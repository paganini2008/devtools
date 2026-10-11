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
 * Base class of the multi-threaded {@link DirectoryWalker} implementations. It tracks the progress of a walk and blocks
 * in {@link #walk()} until every file and sub directory has been processed (or the walk is cancelled).
 *
 * @author Fred Feng
 * @date 2020-05-09
 * @since 2.0.1
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
	public Directory walk() {
		final Executor threadPool = getThreadPool(threadCount);
		final ProgressBar progressBar = new ProgressBarImpl(root, progressable);
		final FileInfo rootInfo = new FileInfoImpl(root, null);
		progressBar.processBegin(root);
		try {
			walk(threadPool, root, rootInfo, 0, progressBar);
			progressBar.waitForTermination();
		} finally {
			shutdown(threadPool);
		}
		return rootInfo;
	}

	/**
	 * Walks the root directory. Implementations must call {@code progressBar.processEnd(root)} exactly once when the
	 * whole walk (including all asynchronous sub tasks) has been submitted and the root directory itself is processed,
	 * otherwise {@link #walk()} never returns.
	 */
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
	 * Default progress bar counting pending entries and the completion of top level entries.
	 *
	 * @author Fred Feng
	 * @since 2.0.1
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
		private final AtomicInteger pending = new AtomicInteger();

		@Override
		public float getCompletionRate() {
			if (isDone()) {
				return 1f;
			}
			if (total == 0) {
				return 0f;
			}
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
			return cancelled.get() || pending.get() <= 0;
		}

		@Override
		public boolean execute() {
			progressable.progress(getFileCount(), getFolderCount(), getLength(), getCompletionRate(), getElapsed());
			return !isDone();
		}

		@Override
		public void processBegin(File file) {
			pending.incrementAndGet();
			if (isRoot(file)) {
				return;
			}
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
			if (!isRoot(file)) {
				stack.computeIfPresent(getPathName(file), (name, counter) -> {
					return counter.decrementAndGet() <= 0 ? null : counter;
				});
			}
			pending.decrementAndGet();
			ThreadUtils.notify(this, () -> {
				return isDone();
			});
		}

		private boolean isRoot(File file) {
			return file.getAbsolutePath().equals(directory.getAbsolutePath());
		}

		private String getPathName(File file) {
			String rootPath = directory.getAbsolutePath();
			String path = file.getAbsolutePath();
			if (path.startsWith(rootPath)) {
				path = path.substring(rootPath.length());
			}
			int begin = 0;
			while (begin < path.length() && path.charAt(begin) == File.separatorChar) {
				begin++;
			}
			int index = path.indexOf(File.separatorChar, begin);
			return index != -1 ? path.substring(begin, index) : path.substring(begin);
		}

	}

	/**
	 * 
	 * Thread safe default {@link FileInfo}.
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	static class FileInfoImpl implements FileInfo {

		private final File file;
		private final Directory parent;
		private final AtomicInteger fileCount = new AtomicInteger(0);
		private final AtomicInteger folderCount = new AtomicInteger(0);
		private final AtomicLong length = new AtomicLong(0);
		private final List<Directory> children = new CopyOnWriteArrayList<Directory>();
		private final AtomicBoolean done = new AtomicBoolean();
		private final long startTime;
		private long elapsed;

		FileInfoImpl(File file, Directory parent) {
			this.file = file;
			this.parent = parent;
			this.startTime = System.currentTimeMillis();
		}

		@Override
		public int getFileCount(boolean recursive) {
			int total = fileCount.get();
			if (recursive) {
				for (Directory fileInfo : children) {
					total += fileInfo.getFileCount(recursive);
				}
			}
			return total;
		}

		@Override
		public int getFolderCount(boolean recursive) {
			int total = folderCount.get();
			if (recursive) {
				for (Directory fileInfo : children) {
					total += fileInfo.getFolderCount(recursive);
				}
			}
			return total;
		}

		@Override
		public long getLength() {
			long total = length.get();
			for (Directory fileInfo : children) {
				total += fileInfo.getLength();
			}
			return total;
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public Directory[] getChildren() {
			return children.toArray(new Directory[children.size()]);
		}

		@Override
		public long getLastModified() {
			long lastModified = file.lastModified();
			for (Directory fileInfo : children) {
				lastModified = Math.max(lastModified, fileInfo.getLastModified());
			}
			return lastModified;
		}

		@Override
		public FileInfo newChildFileInfo(File childFile) {
			FileInfo childFileInfo = new FileInfoImpl(childFile, this);
			children.add(childFileInfo);
			return childFileInfo;
		}

		@Override
		public Directory getParent() {
			return parent;
		}

		@Override
		public void process(File file) {
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
