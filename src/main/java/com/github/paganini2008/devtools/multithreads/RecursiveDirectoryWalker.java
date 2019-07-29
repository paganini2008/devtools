package com.github.paganini2008.devtools.multithreads;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.io.FileInfo;
import com.github.paganini2008.devtools.io.Progressable;
import com.github.paganini2008.devtools.primitives.Floats;

/**
 * 
 * RecursiveDirectoryWalker
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2016-11
 * @version 1.0
 */
public class RecursiveDirectoryWalker {

	private final File directory;
	private final int maxDepth;
	private final FileFilter fileFilter;

	public RecursiveDirectoryWalker(File directory, int maxDepth, FileFilter fileFilter) {
		this.directory = directory;
		this.maxDepth = maxDepth;
		this.fileFilter = fileFilter;
	}

	/**
	 * 
	 * EnhancedFileInfo
	 *
	 * @author Fred Feng
	 * @revised 2019-07
	 * @created 2016-11
	 */
	public interface EnhancedFileInfo extends FileInfo {

		default int getFileCount() {
			return getFileCount(false);
		}

		default int getFolderCount() {
			return getFolderCount(false);
		}

		default long getLength() {
			return getLength(false);
		}

		int getFileCount(boolean recursive);

		int getFolderCount(boolean recursive);

		long getLength(boolean recursive);

		String getProcessingFile();

		int getDepth();

	}

	/**
	 * 
	 * FileInfoImpl
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 * @created 2016-11
	 * @version 1.0
	 */
	public static class FileInfoImpl implements EnhancedFileInfo, Serializable {

		private static final long serialVersionUID = -1704402670961888384L;
		final AtomicInteger counter = new AtomicInteger();
		final AtomicInteger fileCount = new AtomicInteger(0);
		final AtomicInteger folderCount = new AtomicInteger(0);
		final AtomicLong length = new AtomicLong(0);
		final List<EnhancedFileInfo> childs = new ArrayList<EnhancedFileInfo>();
		final File directory;
		final int depth;
		long startTime;
		volatile long elapsed;
		volatile String processingFile;

		FileInfoImpl(File directory, int depth) {
			this.directory = directory;
			this.depth = depth;
			this.startTime = System.currentTimeMillis();
		}

		public File getFile() {
			return directory;
		}

		public long getStartTime() {
			return startTime;
		}

		public long getElapsed() {
			return counter.get() > 0 ? (elapsed = System.currentTimeMillis() - startTime) : elapsed;
		}

		public String getProcessingFile() {
			return processingFile;
		}

		public int getFileCount(boolean recursive) {
			int total = fileCount.get();
			if (recursive) {
				for (FileInfo fileInfo : childs) {
					total += fileInfo.getFileCount();
				}
			}
			return total;
		}

		public int getFolderCount(boolean recursive) {
			int total = folderCount.get();
			if (recursive) {
				for (FileInfo fileInfo : childs) {
					total += fileInfo.getFolderCount();
				}
			}
			return total;
		}

		public long getLength(boolean recursive) {
			long total = length.get();
			for (FileInfo fileInfo : childs) {
				total += fileInfo.getLength();
			}
			return total;
		}

		public int getDepth() {
			return depth;
		}

	}

	/**
	 * 
	 * DirectoryWalkTask
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	class DirectoryWalkTask extends RecursiveTask<EnhancedFileInfo> {

		private static final long serialVersionUID = -1911846799071310358L;
		private final FileInfoImpl fileInfo;
		private final Map<String, Object> context;

		DirectoryWalkTask(FileInfoImpl fileInfo, Map<String, Object> context) {
			this.fileInfo = fileInfo;
			this.context = context;
		}

		protected EnhancedFileInfo compute() {
			final File directory = fileInfo.getFile();
			System.out.println("处理目录： " + directory);
			try {
				if (shouldHandleDirectory(directory, fileInfo)) {
					enterDirectory(directory, fileInfo);
					int childDepth = fileInfo.getDepth();
					if (maxDepth < 0 || childDepth <= maxDepth) {
						File[] childFiles = fileFilter != null ? directory.listFiles(fileFilter) : directory.listFiles();
						if (childFiles != null) {
							for (File childFile : childFiles) {
								if (childFile.isDirectory()) {
									fileInfo.folderCount.incrementAndGet();
									fileInfo.counter.incrementAndGet();
									DirectoryWalkTask task = new DirectoryWalkTask(new FileInfoImpl(childFile, childDepth + 1), context);
									task.fork();
									EnhancedFileInfo childInfo = task.join();
									fileInfo.childs.add(childInfo);
								} else if (shouldHandleFile(childFile, fileInfo)) {
									fileInfo.fileCount.incrementAndGet();
									fileInfo.length.addAndGet(childFile.length());
									fileInfo.processingFile = childFile.getAbsolutePath();
									try {
										handleFile(childFile, fileInfo);
									} catch (Exception e) {
										handleFileIfError(childFile, fileInfo, e);
									}
								}

							}
						}
					}
				}
				leaveDirectory(directory, fileInfo);
			} catch (Exception e) {
				handleDirectoryIfError(directory, fileInfo, e);
			}
			fileInfo.counter.decrementAndGet();
			return fileInfo;
		}
	}

	public EnhancedFileInfo walk(int nThreads, Progressable progressable) throws Exception {
		final FileInfoImpl fileInfo = new FileInfoImpl(directory, 0);
		final ForkJoinPool pool = getPool(nThreads);
		final Map<String, Object> context = new ConcurrentHashMap<String, Object>();
		fileInfo.counter.incrementAndGet();
		ThreadUtils.runAsThread(() -> {
			try {
				DirectoryWalkTask task = new DirectoryWalkTask(fileInfo, context);
				Future<EnhancedFileInfo> future = pool.submit(task);
				future.get();
			} catch (Exception e) {
				handleDirectoryIfError(directory, fileInfo, e);
			} finally {
				ThreadUtils.notify(fileInfo, () -> {
					return fileInfo.counter.get() == 0;
				});
				ExecutorUtils.gracefulShutdown(pool, 60000L);
			}
		});
		if (progressable != null) {
			ThreadUtils.scheduleAtFixedRate(() -> {
				progressable.progress(fileInfo.getProcessingFile(), fileInfo.getFileCount(), fileInfo.getFolderCount(),
						fileInfo.getLength(), getCompletedRatio(fileInfo), fileInfo.getElapsed());
				return fileInfo.counter.get() > 0;
			}, 1, TimeUnit.SECONDS);
		}
		ThreadUtils.wait(fileInfo, () -> {
			return fileInfo.counter.get() == 0;
		});
		return fileInfo;
	}

	protected float getCompletedRatio(FileInfo info) {
		int remainingCount = ((FileInfoImpl) info).counter.get();
		int folderCount = info.getFolderCount();
		float value = (float) folderCount / (folderCount + remainingCount);
		return Floats.toFixed(value, 3);
	}

	protected ForkJoinPool getPool(int nThreads) {
		return new ForkJoinPool(nThreads);
	}

	protected void enterDirectory(File directory, FileInfo fileInfo) throws Exception {
	}

	protected void leaveDirectory(File directory, FileInfo fileInfo) throws Exception {
	}

	protected boolean shouldHandleDirectory(File directory, FileInfo fileInfo) throws Exception {
		return true;
	}

	protected boolean shouldHandleFile(File file, FileInfo fileInfo) throws Exception {
		return true;
	}

	protected void handleFile(File file, FileInfo fileInfo) throws Exception {
	}

	protected void handleFileIfError(File file, FileInfo fileInfo, Exception cause) {
	}

	protected void handleDirectoryIfError(File file, FileInfo fileInfo, Exception cause) {
	}

	public static void main(String[] args) throws Exception {
		File directory = new File("d:/sql");
		RecursiveDirectoryWalker walker = new RecursiveDirectoryWalker(directory, -1, null);
		FileInfo fileInfo = walker.walk(10, new Progressable() {

			public void progress(String processingFile, int fileCount, int folderCount, long length, float completedRatio, long elapsed) {
				System.out.println(processingFile + ", fileCount: " + fileCount + ", folderCount: " + folderCount + ", length: " + length
						+ ", completedRatio: " + completedRatio + ", elapsed: " + elapsed);
			}
		});
		System.out.println(DateUtils.formatDurationAsHour(fileInfo.getElapsed()));
		System.out.println(fileInfo);
		System.in.read();
		System.out.println("Completed.");
	}

}
