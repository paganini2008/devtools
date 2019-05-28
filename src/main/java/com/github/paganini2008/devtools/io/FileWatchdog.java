package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FileWatchdog
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FileWatchdog extends TimerTask {

	private static final int STATE_NONE = 0;

	private static final int STATE_CREATE = 1;
	private static final int STATE_DELETE = 2;
	private static final int STATE_UPDATE = 3;
	private static final long DEFAULT_CHECKED_INTERVAL = 3L * 1000;

	public FileWatchdog(File... files) {
		addFiles(files);
	}

	private final List<FileWatcher> watchers = new CopyOnWriteArrayList<FileWatcher>();
	private final List<FileEntry> fileEntries = new CopyOnWriteArrayList<FileEntry>();
	private Timer timer;
	private long interval = DEFAULT_CHECKED_INTERVAL;
	private volatile boolean running = false;

	/**
	 * FileEntry
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	static class FileEntry {

		final File file;
		boolean exists;
		long length;
		long lastModified;

		FileEntry(File file) {
			exists = file.exists();
			length = exists ? file.length() : 0;
			lastModified = exists ? file.lastModified() : 0;
			this.file = file;
		}

		public int changed() {
			boolean otherExists = this.exists;
			this.exists = file.exists();
			long otherLastModified = this.lastModified;
			this.lastModified = file.lastModified();
			long otherLength = this.length;
			this.length = file.length();
			if (!otherExists && exists) {
				return STATE_CREATE;
			} else if (otherExists && !exists) {
				return STATE_DELETE;
			} else {
				return (otherLength != length) || (lastModified > otherLastModified) ? STATE_UPDATE : STATE_NONE;
			}
		}

		public File getFile() {
			return file;
		}

		public boolean isExists() {
			return exists;
		}

		public long getLastModified() {
			return lastModified;
		}

		public String toString() {
			return "FileEntry [file=" + file + ", exists=" + exists + ", length=" + length + ", lastModified=" + lastModified + "]";
		}

	}

	public boolean isRunning() {
		return running;
	}

	public void setInterval(long interval) {
		if (interval > DEFAULT_CHECKED_INTERVAL) {
			this.interval = interval;
		}
	}

	public void addFiles(File... files) {
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				fileEntries.add(new FileEntry(files[i]));
			}
		}
	}

	public void addWatcher(FileWatcher fileWatcher) {
		if (fileWatcher != null) {
			watchers.add(fileWatcher);
		}
	}

	public synchronized void start() {
		if (running) {
			throw new IllegalStateException("FileWatchdog is running now.");
		}
		timer = new Timer("FileWatchdog@" + System.currentTimeMillis(), false);
		timer.schedule(this, interval, interval);
		running = true;
	}

	public void stop() {
		if (!running) {
			throw new IllegalStateException("FileWatchdog is not running.");
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		running = false;
	}

	public void run() {
		for (FileEntry fileEntry : fileEntries) {
			try {
				switch (fileEntry.changed()) {
				case STATE_CREATE:
					for (FileWatcher watcher : watchers) {
						watcher.onCreate(fileEntry.getFile());
					}
					break;
				case STATE_DELETE:
					for (FileWatcher watcher : watchers) {
						watcher.onDelete(fileEntry.getFile());
					}
					break;
				case STATE_UPDATE:
					for (FileWatcher watcher : watchers) {
						watcher.onUpdate(fileEntry.getFile());
					}
					break;
				}
			} catch (IOException e) {
				if (!onError(fileEntry.getFile(), e)) {
					break;
				}
			}
		}
	}

	protected boolean onError(File file, IOException e) {
		return true;
	}

}
