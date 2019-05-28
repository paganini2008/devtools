package com.github.paganini2008.devtools.io.monitor;

import java.io.File;

public class TestFileMonitor {

	public static void main(String[] args) throws Exception {
		File directory = new File("d:/sql/testDir");
		FileWatcher watcher = new FileWatcher(directory);
		watcher.getListeners().add(new TestFileChangeListener());
		FileMonitor monitor = new FileMonitor(watcher);
		monitor.setBlocking(true);
		monitor.start();
	}

}
