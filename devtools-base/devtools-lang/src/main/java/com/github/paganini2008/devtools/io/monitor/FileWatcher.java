package com.github.paganini2008.devtools.io.monitor;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.io.FileUtils;

/**
 * FileWatcher
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FileWatcher {

	public FileWatcher(File directory) {
		this.directory = directory;
	}

	private FileEntry rootEntry;

	private File directory;

	private FileFilter fileFilter;

	private int depth = -1;

	private final List<FileChangeListener> listeners = new CopyOnWriteArrayList<FileChangeListener>();

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public List<FileChangeListener> getListeners() {
		return listeners;
	}

	public void start() {
		rootEntry = new FileEntry(directory);
		rootEntry.refresh();
		List<File> rootChildFiles = listFiles(rootEntry.getFile());
		if (ListUtils.isNotEmpty(rootChildFiles)) {
			final Map<String, FileEntry> rootChildEntries = new LinkedHashMap<String, FileEntry>();
			for (final File rootChildFile : rootChildFiles) {
				rootChildEntries.put(rootChildFile.getAbsolutePath(), createFileEntry(rootEntry, rootChildFile));
			}
			rootEntry.setChildEntries(rootChildEntries);
		}
	}

	private FileEntry createFileEntry(FileEntry parentEntry, File childFile) {
		final FileEntry childEntry = parentEntry.newChildEntry(childFile);
		List<File> childFiles;
		if ((depth < 0 || childEntry.getDepth() <= depth)
				&& (ListUtils.isNotEmpty(childFiles = listFiles(childEntry.getFile())))) {
			final Map<String, FileEntry> childEntries = new LinkedHashMap<String, FileEntry>();
			for (final File file : childFiles) {
				childEntries.put(file.getAbsolutePath(), createFileEntry(childEntry, file));
			}
			childEntry.setChildEntries(childEntries);
		} else {
			childEntry.setChildEntries(FileEntry.EMPTY_ENTRIES);
		}
		return childEntry;
	}

	private FileEntry createFileEntry(FileEntry parentEntry, File childFile, Map<String, FileEntry> lastChildEntries) {
		FileEntry childEntry = parentEntry.newChildEntry(childFile);
		List<File> listChildFiles;
		if ((depth < 0 || childEntry.getDepth() <= depth)
				&& (ListUtils.isNotEmpty(listChildFiles = listFiles(childEntry.getFile())))) {
			Map<String, FileEntry> childEntries = new LinkedHashMap<String, FileEntry>();
			Map<String, FileEntry> comparedChildEntries = getChildFileChildEntries(childFile, lastChildEntries);
			for (File file : listChildFiles) {
				childEntries.put(file.getAbsolutePath(), createFileEntry(childEntry, file, comparedChildEntries));
			}
			compareAndCall(childEntries, comparedChildEntries);
			childEntry.setChildEntries(childEntries);
		} else {
			childEntry.setChildEntries(FileEntry.EMPTY_ENTRIES);
		}
		return childEntry;
	}

	private void deleteChildEntries(Map<String, FileEntry> childEntries) {
		FileEntry fileEntry;
		for (Map.Entry<String, FileEntry> en : childEntries.entrySet()) {
			fileEntry = en.getValue();
			for (FileChangeListener listener : listeners) {
				if (fileEntry.isDirectory()) {
					listener.onDirectoryDelete(fileEntry);
				} else {
					listener.onFileDelete(fileEntry);
				}
			}
			deleteChildEntries(fileEntry.getChildEntries());
		}
	}

	private Map<String, FileEntry> getChildFileChildEntries(File childFile, Map<String, FileEntry> lastChildEntries) {
		if (lastChildEntries != null) {
			FileEntry fileEntry = lastChildEntries.get(childFile.getAbsolutePath());
			return fileEntry != null ? fileEntry.getChildEntries() : FileEntry.EMPTY_ENTRIES;
		}
		return FileEntry.EMPTY_ENTRIES;
	}

	private void compareAndCall(Map<String, FileEntry> childEntries, Map<String, FileEntry> lastChildEntries) {
		Map<String, FileEntry> effectedEntries = MapUtils.minus(childEntries, lastChildEntries);
		FileEntry fileEntry;
		for (Map.Entry<String, FileEntry> en : effectedEntries.entrySet()) {
			fileEntry = en.getValue();
			for (FileChangeListener listener : listeners) {
				if (fileEntry.isDirectory()) {
					listener.onDirectoryCreate(fileEntry);
				} else {
					listener.onFileCreate(fileEntry);
				}
			}
		}
		effectedEntries = MapUtils.intersect(childEntries, lastChildEntries);
		FileEntry lastFileEntry;
		for (Map.Entry<String, FileEntry> en : effectedEntries.entrySet()) {
			fileEntry = en.getValue();
			lastFileEntry = lastChildEntries.get(en.getKey());
			if (fileEntry.compareTo(lastFileEntry)) {
				for (FileChangeListener listener : listeners) {
					if (fileEntry.isDirectory()) {
						listener.onDirectoryUpdate(lastFileEntry, fileEntry);
					} else {
						listener.onFileUpdate(lastFileEntry, fileEntry);
					}
				}
			}
		}
		effectedEntries = MapUtils.minus(lastChildEntries, childEntries);
		for (Map.Entry<String, FileEntry> en : effectedEntries.entrySet()) {
			fileEntry = en.getValue();
			for (FileChangeListener listener : listeners) {
				if (fileEntry.isDirectory()) {
					listener.onDirectoryDelete(fileEntry);
				} else {
					listener.onFileDelete(fileEntry);
				}
			}
			deleteChildEntries(fileEntry.getChildEntries());
		}
	}

	public void refresh() {
		final FileEntry lastRootEntry = rootEntry;
		rootEntry = new FileEntry(directory);
		rootEntry.refresh();
		if (!lastRootEntry.isExists() && rootEntry.isExists()) {
			for (FileChangeListener listener : listeners) {
				if (rootEntry.isDirectory()) {
					listener.onDirectoryCreate(rootEntry);
				} else {
					listener.onFileCreate(rootEntry);
				}
			}
		} else if (lastRootEntry.isExists() && !rootEntry.isExists()) {
			for (FileChangeListener listener : listeners) {
				if (lastRootEntry.isDirectory()) {
					listener.onDirectoryDelete(lastRootEntry);
				} else {
					listener.onFileDelete(lastRootEntry);
				}
			}
			deleteChildEntries(lastRootEntry.getChildEntries());
		} else if (lastRootEntry.compareTo(rootEntry)) {
			for (FileChangeListener listener : listeners) {
				if (rootEntry.isDirectory()) {
					listener.onDirectoryUpdate(lastRootEntry, rootEntry);
				} else {
					listener.onFileUpdate(lastRootEntry, rootEntry);
				}
			}
		}
		List<File> childFiles = listFiles(rootEntry.getFile());
		if (ListUtils.isNotEmpty(childFiles)) {
			Map<String, FileEntry> childEntries = new LinkedHashMap<String, FileEntry>();
			for (File childFile : childFiles) {
				childEntries.put(childFile.getAbsolutePath(),
						createFileEntry(rootEntry, childFile, lastRootEntry.getChildEntries()));
			}
			compareAndCall(childEntries, lastRootEntry.getChildEntries());
			rootEntry.setChildEntries(childEntries);
		} else {
			rootEntry.setChildEntries(FileEntry.EMPTY_ENTRIES);
		}
	}

	public void checkAndNotify() {
		for (FileChangeListener listener : listeners) {
			listener.onStart(this);
		}
		refresh();
		for (FileChangeListener listener : listeners) {
			listener.onStop(this);
		}
	}

	protected List<File> listFiles(File directory) {
		return FileUtils.listFiles(directory, fileFilter);
	}

	public FileEntry getRootEntry() {
		return rootEntry;
	}

	public File getDirectory() {
		return directory;
	}

	public void stop() {
		rootEntry = null;
	}

}
