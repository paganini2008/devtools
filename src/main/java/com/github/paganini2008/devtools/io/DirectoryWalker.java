package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.multithreads.ThreadPool;
import com.github.paganini2008.devtools.multithreads.ThreadPoolBuilder;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.multithreads.latch.RecursiveLatch;
import com.github.paganini2008.devtools.primitives.Floats;

/**
 * 
 * DirectoryWalker
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2013-06
 */
public class DirectoryWalker {

	private final File directory;
	private final int maxDepth;
	private final FileFilter fileFilter;

	public DirectoryWalker(File directory, int maxDepth, FileFilter fileFilter) {
		this.directory = directory;
		this.maxDepth = maxDepth;
		this.fileFilter = fileFilter;
	}

	/**
	 * 
	 * FileInfoImpl
	 *
	 * @author Fred Feng
	 * @revised 2019-07
	 * @created 2013-06
	 */
	static class FileInfoImpl implements FileInfo {

		final AtomicInteger counter = new AtomicInteger(0);
		final AtomicInteger fileCount = new AtomicInteger(0);
		final AtomicInteger folderCount = new AtomicInteger(0);
		final AtomicLong length = new AtomicLong(0);
		final File file;
		final long startTime;
		long elapsed;

		FileInfoImpl(File file) {
			this.startTime = System.currentTimeMillis();
			this.file = file;
		}

		public File getFile() {
			return file;
		}

		public long getStartTime() {
			return startTime;
		}

		public long getElapsed() {
			return counter.get() > 0 ? (elapsed = System.currentTimeMillis() - startTime) : elapsed;
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

	}

	public FileInfo walk(int nThreads, final Progressable progressable) {
		final ThreadPool threadPool = ThreadPoolBuilder.common(nThreads).setLatch(new RecursiveLatch(nThreads * 2)).build();
		try {
			return walk(threadPool, progressable);
		} finally {
			threadPool.shutdown();
		}
	}

	public FileInfo walk(final Executor executor, final Progressable progressable) {
		final FileInfoImpl info = new FileInfoImpl(directory);
		final Map<String, Object> context = new ConcurrentHashMap<String, Object>();
		info.counter.incrementAndGet();
		executor.execute(() -> {
			try {
				walk(directory, 0, context, executor, info);
			} catch (IOException e) {
				handleDirectoryIfError(directory, 0, e, context);
			} finally {
				info.counter.decrementAndGet();
				ThreadUtils.notify(info, () -> {
					return info.counter.get() == 0;
				});
			}
		});
		if (progressable != null) {
			ThreadUtils.scheduleAtFixedRate(() -> {
				progressable.progress("-", info.getFileCount(), info.getFolderCount(), info.getLength(), getCompletedRatio(info),
						info.getElapsed());
				return info.counter.get() > 0;
			}, 1, TimeUnit.SECONDS);
		}
		ThreadUtils.wait(info, () -> {
			return info.counter.get() == 0;
		});
		return info;
	}

	protected float getCompletedRatio(FileInfo info) {
		int remainingCount = ((FileInfoImpl) info).counter.get();
		int folderCount = info.getFolderCount();
		float value = (float) folderCount / (folderCount + remainingCount);
		return Floats.toFixed(value, 3);
	}

	private void walk(final File directory, final int depth, final Map<String, Object> context, final Executor executor,
			final FileInfoImpl info) throws IOException {
		if (shouldHandleDirectory(directory, depth, context)) {
			enterDirectory(directory, depth, context);
			int childDepth = depth + 1;
			if (maxDepth < 0 || childDepth <= maxDepth) {
				File[] childFiles = fileFilter == null ? directory.listFiles() : directory.listFiles(fileFilter);
				if (childFiles != null) {
					for (File childFile : childFiles) {
						if (childFile.isDirectory()) {
							info.folderCount.incrementAndGet();
							info.counter.incrementAndGet();
							executor.execute(() -> {
								try {
									walk(childFile, childDepth, context, executor, info);
								} catch (IOException e) {
									handleDirectoryIfError(childFile, childDepth, e, context);
								} finally {
									info.counter.decrementAndGet();
								}
							});
						} else {
							if (shouldHandleFile(childFile, childDepth, context)) {
								info.fileCount.incrementAndGet();
								info.length.addAndGet(childFile.length());
								try {
									handleFile(childFile, childDepth, context);
								} catch (Exception e) {
									handleFileIfError(childFile, childDepth, e, context);
								}
							}
						}
					}
				}
			}
		}
		leaveDirectory(directory, depth, context);
	}

	public static void main(String[] args) throws IOException {
		File directory = new File("H:\\my project");
		DirectoryWalker walker = new DirectoryWalker(directory, -1, null);
		ThreadPool threadPool = ThreadPoolBuilder.common(10).setLatch(new RecursiveLatch(10)).build();
		FileInfo fileInfo = walker.walk(threadPool, new Progressable() {

			public void progress(String processingFile, int fileCount, int folderCount, long length, float completedRatio, long elapsed) {
				System.out.println(processingFile + ", fileCount: " + fileCount + ", folderCount: " + folderCount + ", length: " + length
						+ ", completedRatio: " + completedRatio + ", elapsed: " + elapsed);
			}
		});
		System.out.println(DateUtils.formatDurationAsHour(fileInfo.getElapsed()));
		System.out.println(fileInfo);
		System.in.read();
		threadPool.shutdown();
		System.out.println("Completed.");
	}

	protected void enterDirectory(File file, int depth, Map<String, Object> context) throws IOException {
	}

	protected void leaveDirectory(File file, int depth, Map<String, Object> context) throws IOException {
	}

	protected void handleDirectoryIfError(File file, int depth, Exception cause, Map<String, Object> context) {
	}

	protected boolean shouldHandleDirectory(File directory, int depth, Map<String, Object> context) throws IOException {
		return true;
	}

	protected boolean shouldHandleFile(File file, int depth, Map<String, Object> context) throws IOException {
		return true;
	}

	protected void handleFile(File file, int depth, Map<String, Object> context) throws IOException {
	}

	protected void handleFileIfError(File file, int depth, Exception cause, Map<String, Object> context) {
	}

}