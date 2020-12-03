package com.github.paganini2008.devtools.io.monitor;

/**
 * 
 * FileChangeListener
 * 
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public interface FileChangeListener {

	default void onStart(FileWatcher fileWatcher) {
	}

	default void onEnd(FileWatcher fileWatcher) {
	}

	void onDirectoryCreate(FileEntry fileEntry);

	void onFileCreate(FileEntry fileEntry);

	void onDirectoryDelete(FileEntry fileEntry);

	void onFileDelete(FileEntry fileEntry);

	void onDirectoryUpdate(FileEntry lastFileEntry, FileEntry fileEntry);

	void onFileUpdate(FileEntry lastFileEntry, FileEntry fileEntry);

}
