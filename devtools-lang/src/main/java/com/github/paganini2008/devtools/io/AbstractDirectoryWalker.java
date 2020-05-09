package com.github.paganini2008.devtools.io;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ThreadPool;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.primitives.Floats;

/**
 * 
 * AbstractDirectoryWalker
 *
 * @author Fred Feng
 * @since 1.0
 */
public abstract class AbstractDirectoryWalker implements DirectoryWalker {

	protected AbstractDirectoryWalker(File directory) {
		this.root = directory;
	}

	protected final File root;
	private int threadCount = 8;
	private Progressable progressable;

	@Override
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	@Override
	public void setProgressable(Progressable progressable) {
		this.progressable = progressable;
	}

	@Override
	public FileInfo walk() {
		final Executor threadPool = getThreadPool(threadCount);
		final ProgressBar progressBar = new ProgressBarImpl(root, progressable);
		final FileInfo rootInfo = new FileInfoImpl(root, null);
		walk(threadPool, root, rootInfo, 0, progressBar);
		progressBar.waitForTermination();
		shutdown(threadPool);
		return rootInfo;
	}

	protected abstract void walk(Executor threadPool, File root, FileInfo rootInfo, int depth, ProgressBar progressBar);

	protected abstract Executor getThreadPool(int nThreads);

	protected void shutdown(Executor threadPool) {
		if (threadPool instanceof ThreadPool) {
			((ThreadPool) threadPool).shutdown();
		} else {
			ExecutorUtils.gracefulShutdown(threadPool, 60000L);
		}
	}

	/**
	 * 
	 * ProgressBarImpl
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	static class ProgressBarImpl implements ProgressBar, Executable {

		ProgressBarImpl(File directory, Progressable progressable) {
			this.directory = directory;
			File[] fileArray = directory.listFiles();
			if (fileArray != null) {
				for (File file : fileArray) {
					stack.put(file.getName(), new AtomicInteger(0));
				}
			}
			this.total = stack.size();
			this.progressable = progressable;
			this.startTime = System.currentTimeMillis();
			if (progressable != null) {
				ThreadUtils.scheduleWithFixedDelay(this, progressable.refreshInterval(), TimeUnit.SECONDS);
			}
		}

		private final File directory;
		private final AtomicInteger fileCounter = new AtomicInteger();
		private final AtomicInteger folderCounter = new AtomicInteger();
		private final AtomicLong length = new AtomicLong();
		private final int total;
		private final Map<String, AtomicInteger> stack = new ConcurrentHashMap<String, AtomicInteger>();
		private final long startTime;
		private final Progressable progressable;
		private final AtomicBoolean cancelled = new AtomicBoolean();

		@Override
		public float getCompletionRate() {
			float value = (float) (total - stack.size()) / total;
			return Floats.toFixed(value, 3);
		}

		@Override
		public int getFileCount() {
			return fileCounter.get();
		}

		@Override
		public int getFolderCount() {
			return folderCounter.get();
		}

		@Override
		public long getLength() {
			return length.get();
		}

		@Override
		public long getElapsed() {
			return System.currentTimeMillis() - startTime;
		}

		@Override
		public void waitForTermination() {
			ThreadUtils.wait(this, () -> {
				return isDone();
			});
			if (progressable != null) {
				progressable.progress(getFileCount(), getFolderCount(), getLength(), getCompletionRate(), getElapsed());
			}
		}

		@Override
		public void cancel() {
			cancelled.set(true);
			stack.clear();
			ThreadUtils.notify(this, () -> {
				return isDone();
			});
		}

		@Override
		public boolean isCancelled() {
			return cancelled.get();
		}

		@Override
		public boolean isDone() {
			return stack.isEmpty();
		}

		@Override
		public boolean execute() {
			progressable.progress(getFileCount(), getFolderCount(), getLength(), getCompletionRate(), getElapsed());
			return !isDone();
		}

		@Override
		public void processBegin(File file) {
			if (file.isDirectory()) {
				folderCounter.incrementAndGet();
			} else {
				fileCounter.incrementAndGet();
				length.addAndGet(file.length());
			}
			final String name = getPathName(file);
			AtomicInteger counter = stack.get(name);
			if (counter != null) {
				counter.incrementAndGet();
			}
		}

		@Override
		public void processEnd(File file) {
			final String name = getPathName(file);
			AtomicInteger counter = stack.get(name);
			if (counter != null) {
				if (counter.get() > 0) {
					counter.decrementAndGet();
				}
				if (counter.get() == 0) {
					stack.remove(name);
				}
			}
			ThreadUtils.notify(this, () -> {
				return isDone();
			});
		}

		private String getPathName(File file) {
			String path = file.getAbsolutePath().replace(directory.getAbsolutePath(), "");
			int index = path.indexOf(File.separatorChar, 1);
			return index != -1 ? path.substring(1, index) : path.substring(1);
		}

	}

	/**
	 * 
	 * FileInfoImpl
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	static class FileInfoImpl implements FileInfo {

		private final File file;
		private final FileInfo parent;
		private final AtomicInteger fileCount = new AtomicInteger(0);
		private final AtomicInteger folderCount = new AtomicInteger(0);
		private final AtomicLong length = new AtomicLong(0);
		private final List<FileInfo> children = new CopyOnWriteArrayList<FileInfo>();
		private final AtomicBoolean done = new AtomicBoolean();
		private final long startTime;
		private long elapsed;

		FileInfoImpl(File file, FileInfo parent) {
			this.file = file;
			this.parent = parent;
			this.startTime = System.currentTimeMillis();
		}

		@Override
		public int getFileCount(boolean recursive) {
			int total = fileCount.get();
			if (recursive) {
				for (FileInfo fileInfo : children) {
					total += fileInfo.getFileCount(recursive);
				}
			}
			return total;
		}

		@Override
		public int getFolderCount(boolean recursive) {
			int total = folderCount.get();
			if (recursive) {
				for (FileInfo fileInfo : children) {
					total += fileInfo.getFolderCount(recursive);
				}
			}
			return total;
		}

		@Override
		public long getLength() {
			long total = length.get();
			for (FileInfo fileInfo : children) {
				total += fileInfo.getLength();
			}
			return total;
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public FileInfo[] getChildren() {
			return children.toArray(new FileInfo[children.size()]);
		}

		@Override
		public long getLastModified() {
			long lastModified = file.lastModified();
			for (FileInfo fileInfo : children) {
				lastModified = Math.max(lastModified, fileInfo.getLastModified());
			}
			return lastModified;
		}

		@Override
		public FileInfo newChildInfo(File childFile) {
			FileInfo childFileInfo = new FileInfoImpl(childFile, this);
			children.add(childFileInfo);
			return childFileInfo;
		}

		@Override
		public FileInfo getParent() {
			return parent;
		}

		@Override
		public void sum(File file) {
			if (file.isDirectory()) {
				folderCount.incrementAndGet();
			} else {
				fileCount.incrementAndGet();
				length.addAndGet(file.length());
			}
		}

		@Override
		public void done() {
			done.set(true);
			elapsed = System.currentTimeMillis() - startTime;
		}

		@Override
		public boolean isDone() {
			return done.get();
		}

		@Override
		public long getElapsed() {
			return elapsed;
		}

	}

}
