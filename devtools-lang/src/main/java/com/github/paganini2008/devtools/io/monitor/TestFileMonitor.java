package com.github.paganini2008.devtools.io.monitor;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TestFileMonitor {

	public static void main(String[] args) throws Exception {
		File directory = new File("d:/sql/testDir");
		FileWatcher watcher = new FileWatcher(directory, -1, null);
		watcher.addListeners(new LoggingFileChangeListener());
		FileMonitor monitor = new FileMonitor(5, TimeUnit.SECONDS);
		monitor.addWatchers(watcher);
		monitor.start();
		System.in.read();
		monitor.stop();
	}

}
