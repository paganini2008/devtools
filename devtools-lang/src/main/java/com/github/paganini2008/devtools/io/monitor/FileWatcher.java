/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.io.monitor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.io.FileUtils;

/**
 * 
 * FileWatcher
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@SuppressWarnings("unchecked")
public class FileWatcher {

	public FileWatcher(File directory, int depth, FileFilter fileFilter) {
		this.directory = directory;
		this.depth = depth;
		this.fileFilter = fileFilter;
	}

	private FileEntry rootEntry;
	private final File directory;
	private final FileFilter fileFilter;
	private final int depth;
	private final List<FileChangeListener> listeners = new CopyOnWriteArrayList<FileChangeListener>();

	public void addListeners(FileChangeListener... l) {
		if (l != null) {
			for (FileChangeListener listener : l) {
				listeners.add(listener);
			}
		}
	}

	public void removeListeners(FileChangeListener... l) {
		if (l != null) {
			for (FileChangeListener listener : l) {
				listeners.remove(listener);
			}
		}
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
		if ((depth < 0 || childEntry.getDepth() <= depth) && (ListUtils.isNotEmpty(childFiles = listFiles(childEntry.getFile())))) {
			final Map<String, FileEntry> childEntries = new LinkedHashMap<String, FileEntry>();
			for (final File file : childFiles) {
				childEntries.put(file.getAbsolutePath(), createFileEntry(childEntry, file));
			}
			childEntry.setChildEntries(childEntries);
		} else {
			childEntry.setChildEntries(Collections.EMPTY_MAP);
		}
		return childEntry;
	}

	private FileEntry createFileEntry(FileEntry parentEntry, File childFile, Map<String, FileEntry> lastChildEntries) {
		FileEntry childEntry = parentEntry.newChildEntry(childFile);
		List<File> listChildFiles;
		if ((depth < 0 || childEntry.getDepth() <= depth) && (ListUtils.isNotEmpty(listChildFiles = listFiles(childEntry.getFile())))) {
			Map<String, FileEntry> childEntries = new LinkedHashMap<String, FileEntry>();
			Map<String, FileEntry> comparedChildEntries = getChildFileChildEntries(childFile, lastChildEntries);
			for (File file : listChildFiles) {
				childEntries.put(file.getAbsolutePath(), createFileEntry(childEntry, file, comparedChildEntries));
			}
			compareAndCall(childEntries, comparedChildEntries);
			childEntry.setChildEntries(childEntries);
		} else {
			childEntry.setChildEntries(Collections.EMPTY_MAP);
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
			return fileEntry != null ? fileEntry.getChildEntries() : Collections.EMPTY_MAP;
		}
		return Collections.EMPTY_MAP;
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
				childEntries.put(childFile.getAbsolutePath(), createFileEntry(rootEntry, childFile, lastRootEntry.getChildEntries()));
			}
			compareAndCall(childEntries, lastRootEntry.getChildEntries());
			rootEntry.setChildEntries(childEntries);
		} else {
			rootEntry.setChildEntries(Collections.EMPTY_MAP);
		}
	}

	public void checkAndNotify() {
		for (FileChangeListener listener : listeners) {
			listener.onStart(this);
		}
		refresh();
		for (FileChangeListener listener : listeners) {
			listener.onEnd(this);
		}
	}

	protected List<File> listFiles(File directory) {
		try {
			return FileUtils.listFiles(directory, fileFilter);
		} catch (IOException e) {
			return ListUtils.emptyList();
		}
	}

	public FileEntry getRootEntry() {
		return rootEntry;
	}

	public File getDirectory() {
		return directory;
	}

}
