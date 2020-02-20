package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.primitives.Floats;

/**
 * 
 * EnhancedDirectoryWalker
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class EnhancedDirectoryWalker {

	private final File directory;
	private final int maxDepth;
	private final FileFilter fileFilter;

	public EnhancedDirectoryWalker(File directory, int maxDepth, FileFilter fileFilter) {
		this.directory = directory;
		this.maxDepth = maxDepth;
		this.fileFilter = fileFilter;
	}

	/**
	 * 
	 * EnhancedFileInfo
	 *
	 * @author Fred Feng
	 * 
	 * 
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

		int getDepth();

		FileInfo getRootInfo();

	}

	/**
	 * 
	 * RootInfo
	 *
	 * @author Fred Feng
	 * 
	 * 
	 */
	static class RootInfo implements FileInfo {

		final AtomicInteger fileCount = new AtomicInteger(0);
		final AtomicInteger folderCount = new AtomicInteger(0);
		final AtomicLong length = new AtomicLong(0);
		final long startTime;
		final File directory;
		final List<File> files = new CopyOnWriteArrayList<File>();
		final int fileSize;
		volatile long elapsed;

		RootInfo(File directory) {
			this.directory = directory;
			this.files.addAll(Arrays.asList(directory.listFiles(file -> {
				return file.isDirectory();
			})));
			this.fileSize = files.size();
			this.startTime = System.currentTimeMillis();
		}

		public File getFile() {
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

		public long getElapsed() {
			return files.isEmpty() ? elapsed : (elapsed = System.currentTimeMillis() - startTime);
		}

		public boolean isFinished() {
			return files.isEmpty();
		}

		public float getCompletedRatio() {
			System.out.println("fileSize: " + fileSize + ", files.size(): " + files.size());
			float value = (fileSize - files.size()) / (float) fileSize;
			return Floats.toFixed(value, 3);
		}

	}

	/**
	 * 
	 * FileInfoImpl
	 * 
	 * @author Fred Feng
	 * 
	 * 
	 * @version 1.0
	 */
	static class FileInfoImpl implements EnhancedFileInfo {

		final AtomicInteger fileCount = new AtomicInteger(0);
		final AtomicInteger folderCount = new AtomicInteger(0);
		final AtomicLong length = new AtomicLong(0);
		final List<EnhancedFileInfo> childs = new ArrayList<EnhancedFileInfo>();
		final File directory;
		final int depth;
		final long startTime;
		final RootInfo rootInfo;
		final AtomicBoolean finished = new AtomicBoolean(false);
		long elapsed;

		FileInfoImpl(File directory, int depth, RootInfo rootInfo) {
			this.directory = directory;
			this.depth = depth;
			this.rootInfo = rootInfo;
			this.startTime = System.currentTimeMillis();
		}

		public File getFile() {
			return directory;
		}

		void countFiles() {
			fileCount.incrementAndGet();
			rootInfo.fileCount.incrementAndGet();
		}

		void countFolders() {
			folderCount.incrementAndGet();
			rootInfo.folderCount.incrementAndGet();
		}

		void setLength(long length) {
			this.length.addAndGet(length);
			rootInfo.length.addAndGet(length);
		}

		public int getFileCount(boolean recursive) {
			int total = fileCount.get();
			if (recursive) {
				for (EnhancedFileInfo fileInfo : childs) {
					total += fileInfo.getFileCount(recursive);
				}
			}
			return total;
		}

		public int getFolderCount(boolean recursive) {
			int total = folderCount.get();
			if (recursive) {
				for (EnhancedFileInfo fileInfo : childs) {
					total += fileInfo.getFolderCount(recursive);
				}
			}
			return total;
		}

		public long getLength(boolean recursive) {
			long total = length.get();
			for (EnhancedFileInfo fileInfo : childs) {
				total += fileInfo.getLength(recursive);
			}
			return total;
		}

		public int getDepth() {
			return depth;
		}

		public long getElapsed() {
			return finished.get() ? elapsed : (elapsed = System.currentTimeMillis() - startTime);
		}

		public FileInfo getRootInfo() {
			return rootInfo;
		}

		public boolean isFinished() {
			return finished.get();
		}

		public float getCompletedRatio() {
			return rootInfo.getCompletedRatio();
		}

		void finish(File file) {
			rootInfo.files.remove(file);
			finished.set(true);
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
			try {
				if (shouldHandleDirectory(directory, fileInfo)) {
					enterDirectory(directory, fileInfo);
					int childDepth = fileInfo.getDepth();
					if (maxDepth < 0 || childDepth <= maxDepth) {
						File[] childFiles = fileFilter != null ? directory.listFiles(fileFilter) : directory.listFiles();
						if (childFiles != null) {
							for (File childFile : childFiles) {
								if (childFile.isDirectory()) {
									fileInfo.countFolders();
									DirectoryWalkTask task = new DirectoryWalkTask(
											new FileInfoImpl(childFile, childDepth + 1, (RootInfo) fileInfo.getRootInfo()), context);
									task.fork();
									EnhancedFileInfo childInfo = task.join();
									fileInfo.childs.add(childInfo);
								} else if (shouldHandleFile(childFile, fileInfo)) {
									fileInfo.countFiles();
									fileInfo.setLength(childFile.length());
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
			fileInfo.finish(directory);
			return fileInfo;
		}
	}

	public final FileInfo walk(int nThreads, Progressable progressable) throws Exception {
		final RootInfo rootInfo = new RootInfo(directory);
		final FileInfoImpl fileInfo = new FileInfoImpl(directory, 0, rootInfo);
		final ForkJoinPool pool = getThreadPool(nThreads);
		final Map<String, Object> context = new ConcurrentHashMap<String, Object>();
		ThreadUtils.runAsThread(() -> {
			try {
				DirectoryWalkTask task = new DirectoryWalkTask(fileInfo, context);
				Future<EnhancedFileInfo> future = pool.submit(task);
				future.get();
			} catch (Exception e) {
				handleDirectoryIfError(directory, fileInfo, e);
			} finally {
				ThreadUtils.notify(rootInfo, () -> {
					return rootInfo.isFinished();
				});
				ExecutorUtils.gracefulShutdown(pool, 60000L);
			}
		});
		if (progressable != null) {
			ThreadUtils.scheduleAtFixedRate(() -> {
				progressable.progress(rootInfo.getFileCount(), rootInfo.getFolderCount(), rootInfo.getLength(),
						rootInfo.getCompletedRatio(), rootInfo.getElapsed());
				return !rootInfo.isFinished();
			}, 1, TimeUnit.SECONDS);
		}
		ThreadUtils.wait(rootInfo, () -> {
			return rootInfo.isFinished();
		});
		return fileInfo;
	}

	protected ForkJoinPool getThreadPool(int nThreads) {
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
		File directory = new File("D:\\work\\gitlab_0613");
		EnhancedDirectoryWalker walker = new EnhancedDirectoryWalker(directory, -1, null);
		FileInfo fileInfo = walker.walk(10, new Progressable() {

			public void progress(int fileCount, int folderCount, long length, float completedRatio, long elapsed) {
				System.out.println("fileCount: " + fileCount + ", folderCount: " + folderCount + ", length: " + length
						+ ", completedRatio: " + completedRatio + ", elapsed: " + elapsed);
			}
		});
		System.out.println(fileInfo.getElapsed());
		System.out.println(fileInfo);
		System.in.read();
		System.out.println("Completed.");
	}

}
