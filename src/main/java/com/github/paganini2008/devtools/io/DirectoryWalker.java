package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.Execution;
import com.github.paganini2008.devtools.multithreads.Latch;
import com.github.paganini2008.devtools.multithreads.Latchs;
import com.github.paganini2008.devtools.multithreads.ThreadPool;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * DirectoryWalker
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public abstract class DirectoryWalker {

	public DirectoryWalker(int nThreads) {
		threadPool = ThreadPool.buildThreadPool(nThreads);
	}

	public DirectoryWalker(int nThreads, int maxPermits) {
		threadPool = ThreadPool.buildThreadPool(nThreads, maxPermits);
	}

	public DirectoryWalker(ThreadPool threadPool) {
		this.threadPool = threadPool;
	}

	private final ThreadPool threadPool;

	/**
	 * 
	 * RootInfo
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	static class RootInfo extends FileInfo implements Executable {

		private static final long serialVersionUID = 7955063976098275397L;
		private static final NumberFormat nf = NumberFormat.getPercentInstance();

		RootInfo(File directory, Progressable progressable, ThreadPool threadPool) {
			super(directory);
			this.progressable = progressable;
			this.threadPool = threadPool;
			this.startTime = System.currentTimeMillis();
			ThreadUtils.scheduleAtFixedRate(this, 1, TimeUnit.SECONDS);
		}

		final long startTime;
		final Progressable progressable;
		final Latch latch = Latchs.atomicSyncLatch();
		final ThreadPool threadPool;

		public boolean execute() {
			if (progressable == null) {
				return false;
			}
			progressable.progress(getFileCount(), getFolderCount(), getLength(), threadPool.getQueueSize(),
					getCompletedRatio(), getElapsed());
			return latch.getPermits() > 0;
		}

		public long getElapsed() {
			return System.currentTimeMillis() - startTime;
		}

		public String getCompletedRatio() {
			long remainingCount = latch.getPermits();
			int totalCount = getFolderCount();
			return nf.format((float) totalCount / (totalCount + remainingCount));
		}

		public String toString() {
			return super.toString() + ", Concurrents: " + "100%" + ", Elapsed: " + getElapsed();
		}

	}

	/**
	 * 
	 * FileInfo
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	static class FileInfo implements Serializable {

		private static final long serialVersionUID = -1857113693945072546L;
		final File directory;
		final AtomicInteger fileCount = new AtomicInteger(0);
		final AtomicInteger folderCount = new AtomicInteger(0);
		final AtomicLong length = new AtomicLong(0);
		final Queue<File> directorys = new ConcurrentLinkedQueue<File>();

		FileInfo(File directory) {
			this.directory = directory;
		}

		public File getDirectory() {
			return directory;
		}

		public int getFileCount() {
			return fileCount.get();
		}

		public int getFolderCount() {
			return folderCount.get();
		}

		public long getLength() {
			return length.get();
		}

		public String toString() {
			return "File: " + getDirectory() + ", FileCount: " + getFileCount() + ", FolderCount: " + getFolderCount()
					+ ", Length: " + getLength();
		}

	}

	public void walk(File directory, FileFilter fileFilter, Progressable progressable) {
		final RootInfo rootInfo = new RootInfo(directory, progressable, threadPool);
		rootInfo.latch.acquire();
		threadPool.submit(getExecution(rootInfo, directory, fileFilter));
		rootInfo.latch.join();
	}

	private Execution getExecution(final RootInfo rootInfo, final File file, final FileFilter fileFilter) {
		return new Execution() {
			public FileInfo execute() throws Exception {
				if (file.isDirectory()) {
					FileInfo fileInfo = new FileInfo(file);
					if (shouldHandleDirectory(fileInfo, file)) {
						enterDirectory(fileInfo, file);
						File[] childFiles = fileFilter != null ? file.listFiles(fileFilter) : file.listFiles();
						for (File childFile : childFiles) {
							if (childFile.isDirectory()) {
								rootInfo.folderCount.incrementAndGet();
								fileInfo.folderCount.incrementAndGet();
								fileInfo.directorys.offer(childFile);
							} else {
								rootInfo.fileCount.incrementAndGet();
								fileInfo.fileCount.incrementAndGet();
								rootInfo.length.addAndGet(childFile.length());
								fileInfo.length.addAndGet(childFile.length());
								if (shouldHandleFile(fileInfo, childFile)) {
									handleFile(fileInfo, childFile);
								}
							}
						}
						leaveDirectory(fileInfo, file);
					}
					return fileInfo;
				}
				throw new IllegalStateException("File '" + file + "' is not existed or not a directory.");
			}

			public void onFailure(Exception e, ThreadPool threadPool) {
				e.printStackTrace();
				rootInfo.latch.release();
			}

			public void onSuccess(Object result, ThreadPool threadPool) {
				final FileInfo fileInfo = (FileInfo) result;
				if (fileInfo != null) {
					while (!fileInfo.directorys.isEmpty()) {
						File file = fileInfo.directorys.poll();
						rootInfo.latch.acquire();
						threadPool.submit(getExecution(rootInfo, file, fileFilter));
					}
				}
				rootInfo.latch.release();
			}

		};
	}

	public void close() {
		threadPool.shutdown();
	}

	protected void enterDirectory(FileInfo fileInfo, File dir) throws Exception {
	}

	protected void leaveDirectory(FileInfo fileInfo, File dir) throws Exception {
	}

	protected boolean shouldHandleDirectory(FileInfo fileInfo, File dir) throws Exception {
		return true;
	}

	protected boolean shouldHandleFile(FileInfo fileInfo, File file) throws Exception {
		return true;
	}

	protected void handleFile(FileInfo fileInfo, File file) throws Exception {
	}

	protected void handleFileOnError(FileInfo fileInfo, File fileOrDir, Exception error) throws Exception {
	}

	public static List<File> searchFiles(final File directory, final int nThreads,
			final FileFilter filter, final Progressable progressable) {
		List<File> results = new CopyOnWriteArrayList<File>();
		DirectoryWalker directoryWalker = new DirectoryWalker(nThreads) {

			protected void handleFile(FileInfo fileInfo, File file) throws Exception {
				if (filter.accept(file)) {
					results.add(file);
				}
			}

		};
		directoryWalker.walk(directory, null, progressable);
		directoryWalker.close();
		return results;
	}

}
