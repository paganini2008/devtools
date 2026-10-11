/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
import com.github.paganini2008.devtools.io.FileUtils;

/**
 * Watches a file or a directory tree and notifies its {@link FileChangeListener}s about the differences between two consecutive snapshots.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 *
 * @since 2.0.1
 */
public class FileWatcher {

	/**
	 * @param directory the file or directory to watch, not null
	 * @param depth the maximum depth to descend, 0 means only the direct children; a negative value means unlimited
	 * @param fileFilter the filter applied to the child files, <code>null</code> to accept all
	 * @throws IllegalArgumentException if <code>directory</code> is <code>null</code>
	 */
	public FileWatcher(File directory, int depth, FileFilter fileFilter) {
		if (directory == null) {
			throw new IllegalArgumentException("Directory must not be null.");
		}
		this.directory = directory;
		this.depth = depth;
		this.fileFilter = fileFilter;
	}

	private volatile FileEntry rootEntry;
	private final File directory;
	private final FileFilter fileFilter;
	private final int depth;
	private final List<FileChangeListener> listeners = new CopyOnWriteArrayList<FileChangeListener>();

	/**
	 * Adds listeners, <code>null</code> is ignored.
	 */
	public void addListeners(FileChangeListener... l) {
		if (l != null) {
			for (FileChangeListener listener : l) {
				listeners.add(listener);
			}
		}
	}

	/**
	 * Removes listeners, <code>null</code> is ignored.
	 */
	public void removeListeners(FileChangeListener... l) {
		if (l != null) {
			for (FileChangeListener listener : l) {
				listeners.remove(listener);
			}
		}
	}

	/**
	 * Takes the initial snapshot. No events are fired.
	 */
	public synchronized void start() {
		rootEntry = createRootEntry();
	}

	private FileEntry createRootEntry() {
		FileEntry entry = new FileEntry(directory);
		entry.refresh();
		entry.setChildEntries(createChildEntries(entry));
		return entry;
	}

	private Map<String, FileEntry> createChildEntries(FileEntry parentEntry) {
		if (!parentEntry.isDirectory() || (depth >= 0 && parentEntry.getDepth() > depth)) {
			return Collections.emptyMap();
		}
		List<File> childFiles = listFiles(parentEntry.getFile());
		if (ListUtils.isEmpty(childFiles)) {
			return Collections.emptyMap();
		}
		final Map<String, FileEntry> childEntries = new LinkedHashMap<String, FileEntry>();
		for (File childFile : childFiles) {
			FileEntry childEntry = parentEntry.newChildEntry(childFile);
			childEntry.setChildEntries(createChildEntries(childEntry));
			childEntries.put(childFile.getAbsolutePath(), childEntry);
		}
		return childEntries;
	}

	private static Map<String, FileEntry> nullSafe(Map<String, FileEntry> entries) {
		return entries != null ? entries : Collections.<String, FileEntry>emptyMap();
	}

	private void compareAndCall(Map<String, FileEntry> childEntries, Map<String, FileEntry> lastChildEntries) {
		childEntries = nullSafe(childEntries);
		lastChildEntries = nullSafe(lastChildEntries);
		FileEntry fileEntry, lastFileEntry;
		for (Map.Entry<String, FileEntry> en : childEntries.entrySet()) {
			fileEntry = en.getValue();
			lastFileEntry = lastChildEntries.get(en.getKey());
			if (lastFileEntry == null) {
				fireCreate(fileEntry);
				compareAndCall(fileEntry.getChildEntries(), null);
			} else {
				if (fileEntry.compareTo(lastFileEntry)) {
					fireUpdate(lastFileEntry, fileEntry);
				}
				compareAndCall(fileEntry.getChildEntries(), lastFileEntry.getChildEntries());
			}
		}
		for (Map.Entry<String, FileEntry> en : lastChildEntries.entrySet()) {
			if (!childEntries.containsKey(en.getKey())) {
				lastFileEntry = en.getValue();
				fireDelete(lastFileEntry);
				compareAndCall(null, lastFileEntry.getChildEntries());
			}
		}
	}

	private void fireCreate(FileEntry fileEntry) {
		for (FileChangeListener listener : listeners) {
			if (fileEntry.isDirectory()) {
				listener.onDirectoryCreate(fileEntry);
			} else {
				listener.onFileCreate(fileEntry);
			}
		}
	}

	private void fireDelete(FileEntry fileEntry) {
		for (FileChangeListener listener : listeners) {
			if (fileEntry.isDirectory()) {
				listener.onDirectoryDelete(fileEntry);
			} else {
				listener.onFileDelete(fileEntry);
			}
		}
	}

	private void fireUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
		for (FileChangeListener listener : listeners) {
			if (fileEntry.isDirectory()) {
				listener.onDirectoryUpdate(lastFileEntry, fileEntry);
			} else {
				listener.onFileUpdate(lastFileEntry, fileEntry);
			}
		}
	}

	/**
	 * Takes a new snapshot and fires events for the differences to the previous one. If {@link #start()} has not been called yet,
	 * only the initial snapshot is taken.
	 */
	public synchronized void refresh() {
		final FileEntry lastRootEntry = rootEntry;
		if (lastRootEntry == null) {
			start();
			return;
		}
		final FileEntry currentRootEntry = createRootEntry();
		rootEntry = currentRootEntry;
		if (!lastRootEntry.isExists() && currentRootEntry.isExists()) {
			fireCreate(currentRootEntry);
		} else if (lastRootEntry.isExists() && !currentRootEntry.isExists()) {
			fireDelete(lastRootEntry);
		} else if (lastRootEntry.compareTo(currentRootEntry)) {
			fireUpdate(lastRootEntry, currentRootEntry);
		}
		compareAndCall(currentRootEntry.getChildEntries(), lastRootEntry.getChildEntries());
	}

	/**
	 * Calls {@link FileChangeListener#onStart(FileWatcher)}, {@link #refresh()} and {@link FileChangeListener#onEnd(FileWatcher)}.
	 */
	public synchronized void checkAndNotify() {
		for (FileChangeListener listener : listeners) {
			listener.onStart(this);
		}
		refresh();
		for (FileChangeListener listener : listeners) {
			listener.onEnd(this);
		}
	}

	/**
	 * Lists the child files of a directory with the file filter, directories first. Returns an empty list if it is not a readable directory.
	 */
	protected List<File> listFiles(File directory) {
		try {
			return FileUtils.listFiles(directory, fileFilter);
		} catch (IOException e) {
			return ListUtils.emptyList();
		}
	}

	/**
	 * Returns the latest snapshot, <code>null</code> before the first check.
	 */
	public FileEntry getRootEntry() {
		return rootEntry;
	}

	public File getDirectory() {
		return directory;
	}

}
