package com.github.paganini2008.devtools.io.monitor;

/**
 * 文件变化监听器，对文件的变化进行对应的操作
 * 
 * @author yan.feng
 * @version 1.0
 */
public interface FileChangeListener {

	/**
	 * 检查前的操作
	 * 
	 * @param fileWatcher
	 */
	void onStart(FileWatcher fileWatcher);

	/**
	 * 文件夹新建事件
	 * 
	 * @param fileEntry
	 */
	void onDirectoryCreate(FileEntry fileEntry);

	/**
	 * 文件新建事件
	 * 
	 * @param fileEntry
	 */
	void onFileCreate(FileEntry fileEntry);

	/**
	 * 文件夹删除事件
	 * 
	 * @param fileEntry
	 */
	void onDirectoryDelete(FileEntry fileEntry);

	/**
	 * 文件删除事件
	 * 
	 * @param fileEntry
	 */
	void onFileDelete(FileEntry fileEntry);

	/**
	 * 文件夹更新事件
	 * 
	 * @param lastFileEntry
	 * @param fileEntry
	 */
	void onDirectoryUpdate(FileEntry lastFileEntry, FileEntry fileEntry);

	/**
	 * 文件更新事件
	 * 
	 * @param lastFileEntry
	 *            上一个版本的快照信息
	 * @param fileEntry
	 *            本次快照信息
	 */
	void onFileUpdate(FileEntry lastFileEntry, FileEntry fileEntry);

	/**
	 * 检查结束后操作
	 * 
	 * @param fileWatcher
	 */
	void onStop(FileWatcher fileWatcher);

}
