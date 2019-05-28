package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.multithreads.AtomicSync;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * RecursiveDirectoryWalker
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public abstract class RecursiveDirectoryWalker {

	/**
	 * 
	 * FileInfo
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	public static interface FileInfo {

		File getDirectory();

		int getFileCount();

		int getFolderCount();

		long getLength();
	}

	/**
	 * 
	 * RootInfo
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	static class RootInfo extends BasicFileInfo implements FileInfo, Executable {

		private static final long serialVersionUID = 4103715943927015754L;
		private static final NumberFormat nf = NumberFormat.getPercentInstance();

		RootInfo(File directory, Progressable progressable, AtomicSync counter) {
			super(directory);
			this.progressable = progressable;
			this.counter = counter;
			this.startTime = System.currentTimeMillis();
			ThreadUtils.scheduleAtFixedRate(this, 1, TimeUnit.SECONDS);
		}

		final long startTime;
		final Progressable progressable;
		final AtomicSync counter;

		public boolean execute() {
			if (progressable == null) {
				return false;
			}
			progressable.progress(getFileCount(), getFolderCount(), getLength(), getConcurrents(), getCompletedRatio(),
					getElapsed());
			return counter.isLocked();
		}

		public long getElapsed() {
			return System.currentTimeMillis() - startTime;
		}

		public long getConcurrents() {
			return counter.get();
		}

		public String getCompletedRatio() {
			long remainingCount = getConcurrents();
			int totalCount = getFolderCount();
			return nf.format((float) totalCount / (totalCount + remainingCount));
		}

		public String toString() {
			return super.toString() + ", Concurrents: " + getConcurrents() + ", Elapsed: " + getElapsed();
		}

	}

	/**
	 * 
	 * BasicFileInfo
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	static class BasicFileInfo implements FileInfo, Serializable {

		private static final long serialVersionUID = -1704402670961888384L;
		final File directory;
		final AtomicInteger fileCount = new AtomicInteger(0);
		final AtomicInteger folderCount = new AtomicInteger(0);
		final AtomicLong length = new AtomicLong(0);

		BasicFileInfo(File directory) {
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

	/**
	 * 
	 * DirectoryWalkTask
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	class DirectoryWalkTask extends RecursiveTask<FileInfo> {

		private static final long serialVersionUID = -1911846799071310358L;

		private final BasicFileInfo rootInfo;
		private final BasicFileInfo fileInfo;
		private final FileFilter fileFilter;
		private final int depth;
		private final AtomicSync counter;

		DirectoryWalkTask(BasicFileInfo rootInfo, File directory, FileFilter fileFilter, int depth,
				AtomicSync counter) {
			this.rootInfo = rootInfo;
			this.fileInfo = new BasicFileInfo(directory);
			this.fileFilter = fileFilter;
			this.depth = depth;
			this.counter = counter;
		}

		protected FileInfo compute() {
			final File directory = fileInfo.getDirectory();
			try {
				enterDirectory(rootInfo, depth, directory);
				File[] childFiles = fileFilter != null ? directory.listFiles(fileFilter) : directory.listFiles();
				if (childFiles != null) {
					for (File childFile : childFiles) {
						try {
							if (childFile.isDirectory()) {
								if (shouldHandleDirectory(rootInfo, depth, directory)) {
									rootInfo.folderCount.incrementAndGet();
									fileInfo.folderCount.incrementAndGet();
									DirectoryWalkTask task = new DirectoryWalkTask(rootInfo, childFile, fileFilter,
											depth + 1, counter);
									counter.countUp();
									task.fork();
									leaveDirectory(rootInfo, depth, task.join());
								}
							} else if (shouldHandleFile(rootInfo, depth, childFile)) {
								rootInfo.fileCount.incrementAndGet();
								fileInfo.fileCount.incrementAndGet();
								rootInfo.length.addAndGet(childFile.length());
								fileInfo.length.addAndGet(childFile.length());

								handleFile(rootInfo, depth, childFile);
							}
						} catch (Exception e) {
							handleFileOnError(rootInfo, depth, childFile, e);
						}
					}
				}
			} catch (Exception e) {
				handleFileOnError(rootInfo, depth, directory, e);
			}
			counter.countDown();
			return fileInfo;
		}
	}

	protected final void walk(File directory, int nThreads, FileFilter fileFilter, Progressable progressable)
			throws Exception {
		final ForkJoinPool pool = getPool(nThreads);
		final AtomicSync counter = new AtomicSync();
		final RootInfo rootInfo = new RootInfo(directory, progressable, counter);
		try {
			DirectoryWalkTask task = new DirectoryWalkTask(rootInfo, directory, fileFilter, 0, counter);
			counter.countUp();
			Future<FileInfo> future = pool.submit(task);
			leaveDirectory(rootInfo, 0, future.get());
			counter.join();
		} finally {
			ExecutorUtils.gracefulShutdown(pool, 60000);
		}

	}

	protected ForkJoinPool getPool(int nThreads) {
		return new ForkJoinPool(nThreads);
	}

	protected void enterDirectory(FileInfo rootInfo, int depth, File directory) throws Exception {
	}

	protected void leaveDirectory(FileInfo rootInfo, int depth, FileInfo fileInfo) throws Exception {
	}

	protected boolean shouldHandleDirectory(FileInfo rootInfo, int depth, File directory) throws Exception {
		return true;
	}

	protected boolean shouldHandleFile(FileInfo rootInfo, int depth, File file) throws Exception {
		return true;
	}

	protected void handleFile(FileInfo rootInfo, int depth, File file) throws Exception {
	}

	protected void handleFileOnError(FileInfo rootInfo, int depth, File file, Exception error) {
	}

	public static List<File> searchFiles(final File directory, final int nThreads, final FileFilter searcher,
			final Progressable progressable) throws Exception {
		List<File> results = new CopyOnWriteArrayList<File>();
		RecursiveDirectoryWalker directoryWalker = new RecursiveDirectoryWalker() {
			protected void handleFile(FileInfo rootInfo, int depth, File file) throws Exception {
				if (searcher.accept(file)) {
					results.add(file);
				}
			}
		};
		directoryWalker.walk(directory, nThreads, null, progressable);
		return results;
	}

}
