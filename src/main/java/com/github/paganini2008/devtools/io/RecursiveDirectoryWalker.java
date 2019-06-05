package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.multithreads.CounterLatch;
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
	 * RootInfo
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	public static class RootInfo extends BasicInfo implements DirectoryInfo, Executable {

		private static final long serialVersionUID = 4103715943927015754L;
		private static final NumberFormat nf = NumberFormat.getPercentInstance();

		RootInfo(File directory, Progressable progressable, CounterLatch lock) {
			super(directory);
			this.progressable = progressable;
			this.lock = lock;
			this.startTime = System.currentTimeMillis();
			ThreadUtils.scheduleAtFixedRate(this, 1, TimeUnit.SECONDS);
		}

		final long startTime;
		final Progressable progressable;
		final CounterLatch lock;
		volatile String processing;

		public boolean execute() {
			if (progressable == null) {
				return false;
			}
			progressable.progress(processing, getFileCount(), getFolderCount(), getLength(), getCompletedRatio(), getElapsed(), null);
			return lock.isLocked();
		}

		public String getCompletedRatio() {
			return "-";
		}

		public long getElapsed() {
			return System.currentTimeMillis() - startTime;
		}

		public String toString() {
			return super.toString() + ", Elapsed: " + getElapsed();
		}

	}

	/**
	 * 
	 * BasicInfo
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	public static class BasicInfo implements DirectoryInfo, Serializable {

		private static final long serialVersionUID = -1704402670961888384L;
		final File directory;
		final AtomicInteger fileCount = new AtomicInteger(0);
		final AtomicInteger folderCount = new AtomicInteger(0);
		final AtomicLong length = new AtomicLong(0);
		final List<DirectoryInfo> childInfos = new ArrayList<DirectoryInfo>();

		BasicInfo(File directory) {
			this.directory = directory;
		}

		public File getDirectory() {
			return directory;
		}

		public int getFileCount() {
			return fileCount.get();
		}

		public int getFileCountRecursively() {
			int total = getFileCount();
			for (DirectoryInfo fileInfo : childInfos) {
				total += fileInfo.getFileCount();
			}
			return total;
		}

		public int getFolderCount() {
			return folderCount.get();
		}

		public int getFolderCountRecursively() {
			int total = getFolderCount();
			for (DirectoryInfo fileInfo : childInfos) {
				total += fileInfo.getFolderCount();
			}
			return total;
		}

		public long getLength() {
			return length.get();
		}

		public long getLengthRecursively() {
			long total = getLength();
			for (DirectoryInfo fileInfo : childInfos) {
				total += fileInfo.getLength();
			}
			return total;
		}

		public String toString() {
			return "File: " + getDirectory() + ", FileCount: " + getFileCount() + ", FolderCount: " + getFolderCount() + ", Length: "
					+ getLength();
		}

	}

	/**
	 * 
	 * DirectoryWalkTask
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	class DirectoryWalkTask extends RecursiveTask<DirectoryInfo> {

		private static final long serialVersionUID = -1911846799071310358L;

		private final RootInfo rootInfo;
		private final BasicInfo fileInfo;
		private final FileFilter fileFilter;
		private final int depth;
		private final CounterLatch lock;

		DirectoryWalkTask(RootInfo rootInfo, File directory, FileFilter fileFilter, int depth, CounterLatch lock) {
			this.rootInfo = rootInfo;
			this.fileInfo = new BasicInfo(directory);
			this.fileFilter = fileFilter;
			this.depth = depth;
			this.lock = lock;
		}

		protected DirectoryInfo compute() {
			final File directory = fileInfo.getDirectory();
			try {
				enterDirectory(directory, depth, rootInfo);
				File[] childFiles = fileFilter != null ? directory.listFiles(fileFilter) : directory.listFiles();
				if (childFiles != null) {
					for (File childFile : childFiles) {
						try {
							if (childFile.isDirectory()) {
								if (shouldHandleDirectory(childFile, depth, rootInfo)) {
									rootInfo.folderCount.incrementAndGet();
									fileInfo.folderCount.incrementAndGet();
									DirectoryWalkTask task = new DirectoryWalkTask(rootInfo, childFile, fileFilter, depth + 1, lock);
									lock.acquire();
									task.fork();
									DirectoryInfo childInfo = task.join();
									fileInfo.childInfos.add(childInfo);
									leaveDirectory(childInfo, depth, rootInfo);
								}
							} else if (shouldHandleFile(childFile, depth, rootInfo)) {
								rootInfo.fileCount.incrementAndGet();
								fileInfo.fileCount.incrementAndGet();
								rootInfo.length.addAndGet(childFile.length());
								fileInfo.length.addAndGet(childFile.length());
								rootInfo.processing = childFile.getAbsolutePath();
								handleFile(childFile, depth, rootInfo);
							}
						} catch (Exception e) {
							handleFileOnError(childFile, depth, rootInfo, e);
						}
					}
				}
			} catch (Exception e) {
				handleFileOnError(directory, depth, rootInfo, e);
			}
			lock.release();
			return fileInfo;
		}
	}

	protected final void walk(File directory, int nThreads, FileFilter fileFilter, Progressable progressable) throws Exception {
		final ForkJoinPool pool = getPool(nThreads);
		final CounterLatch lock = new CounterLatch();
		final RootInfo rootInfo = new RootInfo(directory, progressable, lock);
		try {
			DirectoryWalkTask task = new DirectoryWalkTask(rootInfo, directory, fileFilter, 0, lock);
			lock.acquire();
			Future<DirectoryInfo> future = pool.submit(task);
			leaveDirectory(rootInfo, 0, future.get());
			lock.join();
		} finally {
			ExecutorUtils.gracefulShutdown(pool, 60000);
		}

	}

	protected ForkJoinPool getPool(int nThreads) {
		return new ForkJoinPool(nThreads);
	}

	protected void enterDirectory(File directory, int depth, DirectoryInfo rootInfo) throws Exception {
	}

	protected void leaveDirectory(DirectoryInfo fileInfo, int depth, DirectoryInfo rootInfo) throws Exception {
	}

	protected boolean shouldHandleDirectory(File directory, int depth, DirectoryInfo rootInfo) throws Exception {
		return true;
	}

	protected boolean shouldHandleFile(File file, int depth, DirectoryInfo rootInfo) throws Exception {
		return true;
	}

	protected void handleFile(File file, int depth, DirectoryInfo rootInfo) throws Exception {
	}

	protected void handleFileOnError(File file, int depth, DirectoryInfo rootInfo, Exception error) {
	}

	public static List<File> searchFiles(final File directory, final int nThreads, final FileFilter searcher,
			final Progressable progressable) throws Exception {
		List<File> results = new CopyOnWriteArrayList<File>();
		RecursiveDirectoryWalker directoryWalker = new RecursiveDirectoryWalker() {
			protected void handleFile(File file, int depth, DirectoryInfo rootInfo) throws Exception {
				if (searcher.accept(file)) {
					ThreadUtils.randomSleep(3000L);
					results.add(file);
				}
			}
		};
		directoryWalker.walk(directory, nThreads, null, progressable);
		return results;
	}

	public static List<DirectoryInfo> searchDirectories(final File directory, final int nThreads, final DirectoryFilter searcher,
			final Progressable progressable) throws Exception {
		List<DirectoryInfo> results = new CopyOnWriteArrayList<DirectoryInfo>();
		RecursiveDirectoryWalker directoryWalker = new RecursiveDirectoryWalker() {
			protected void leaveDirectory(DirectoryInfo fileInfo, int depth, DirectoryInfo rootInfo) throws Exception {
				if (searcher.accept(fileInfo)) {
					results.add(fileInfo);
				}
			}

		};
		directoryWalker.walk(directory, nThreads, null, progressable);
		return results;
	}

	// public static void main(String[] args) throws Exception {
	// File directory = new File("C:\\Users");
	// List<File> files = RecursiveDirectoryWalker.searchDirectory(directory, 200,
	// null, new Progressable() {
	// public void progress(int fileCount, int folderCount, long length, long
	// concurrents, String completedRatio, long elapsed) {
	// System.out.println("fileCount: " + fileCount + ", folderCount: " +
	// folderCount + ", length: " + length + ", concurrents: "
	// + concurrents + ", completedRatio: " + completedRatio + ", elapsed: " +
	// elapsed);
	// }
	// });
	// for (File file : files) {
	// System.out.println(file);
	// }
	// }

}
