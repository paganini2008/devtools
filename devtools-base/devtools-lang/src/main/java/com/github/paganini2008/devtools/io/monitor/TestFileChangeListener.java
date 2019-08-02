package com.github.paganini2008.devtools.io.monitor;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * 文件变化监听器，仅供测试
 * 
 * @author yan.feng
 * @version 1.0
 */
public class TestFileChangeListener implements FileChangeListener {

	private static final Log logger = LogFactory.getLog(TestFileChangeListener.class);

	public void onStart(FileWatcher fileMonitor) {
		logger.info("onStart!");
	}

	public void onDirectoryCreate(FileEntry fileEntry) {
		logger.info("onDirectoryCreate: " + fileEntry);
	}

	public void onFileCreate(FileEntry fileEntry) {
		logger.info("onFileCreate: " + fileEntry);
	}

	public void onDirectoryDelete(FileEntry fileEntry) {
		logger.info("onDirectoryDelete: " + fileEntry);
	}

	public void onFileDelete(FileEntry fileEntry) {
		logger.info("onFileDelete: " + fileEntry);
	}

	public void onDirectoryUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
		logger.info("onDirectoryUpdate last: " + lastFileEntry);
		logger.info("onDirectoryUpdate new: " + fileEntry);
	}

	public void onFileUpdate(FileEntry lastFileEntry, FileEntry fileEntry) {
		logger.info("onFileUpdate last: " + lastFileEntry);
		logger.info("onFileUpdate new: " + fileEntry);
	}

	public void onStop(FileWatcher fileMonitor) {
		logger.info("onStop!");
	}

}
