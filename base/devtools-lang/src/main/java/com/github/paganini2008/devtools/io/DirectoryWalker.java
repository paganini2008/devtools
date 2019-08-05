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
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
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

		final AtomicInteger fileCount = new AtomicInteger(0);
		final AtomicInteger folderCount = new AtomicInteger(0);
		final AtomicLong length = new AtomicLong(0);
		final File directory;
		final long startTime;
		final Map<String, AtomicInteger> files = new ConcurrentHashMap<String, AtomicInteger>();
		final int fileSize;
		long elapsed;

		FileInfoImpl(File directory) {
			this.directory = directory;
			File[] fileArray = directory.listFiles(file -> {
				return file.isDirectory();
			});
			if (fileArray != null) {
				for (File file : fileArray) {
					files.put(file.getName(), new AtomicInteger(-1));
				}
			}
			this.fileSize = files.size();
			this.startTime = System.currentTimeMillis();
		}

		public File getFile() {
			return directory;
		}

		public long getElapsed() {
			return files.size() > 0 ? (elapsed = System.currentTimeMillis() - startTime) : elapsed;
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

		public boolean isFinished() {
			return files.isEmpty();
		}

		public float getCompletedRatio() {
			float value = (fileSize - files.size()) / (float) fileSize;
			return Floats.toFixed(value, 3);
		}

		void startScan(File file) {
			final String name = getPathName(file);
			AtomicInteger counter = files.get(name);
			if (counter != null) {
				counter.incrementAndGet();
			}
		}

		void finishScan(File file) {
			final String name = getPathName(file);
			AtomicInteger counter = files.get(name);
			if (counter != null) {
				if (counter.get() == 0) {
					files.remove(name);
				} else if (counter.get() > 0) {
					counter.decrementAndGet();
				}
			}
		}

		private String getPathName(File file) {
			String path = file.getAbsolutePath().replace(directory.getAbsolutePath(), "");
			int index = path.indexOf(File.separatorChar, 1);
			return index != -1 ? path.substring(1, index) : path.substring(1);
		}

	}

	public FileInfo walk(int nThreads, final Progressable progressable) {
		final Executor executor = getThreadPool(nThreads);
		try {
			return walk(executor, progressable);
		} finally {
			if (executor instanceof ThreadPool) {
				((ThreadPool) executor).shutdown();
			} else {
				ExecutorUtils.gracefulShutdown(executor, 60000L);
			}
		}
	}

	protected final FileInfo walk(final Executor executor, final Progressable progressable) {
		final FileInfoImpl info = new FileInfoImpl(directory);
		final Map<String, Object> context = new ConcurrentHashMap<String, Object>();
		executor.execute(() -> {
			try {
				walk(directory, 0, context, executor, info);
			} catch (IOException e) {
				handleDirectoryIfError(directory, 0, e, context);
			} finally {
				ThreadUtils.notify(info, () -> {
					return info.isFinished();
				});
			}
		});
		if (progressable != null) {
			ThreadUtils.scheduleAtFixedRate(() -> {
				progressable.progress(info.getFileCount(), info.getFolderCount(), info.getLength(), info.getCompletedRatio(),
						info.getElapsed());
				return !info.isFinished();
			}, 1, TimeUnit.SECONDS);
		}
		ThreadUtils.wait(info, () -> {
			return info.isFinished();
		});
		return info;
	}

	protected Executor getThreadPool(int nThreads) {
		return ThreadPoolBuilder.common(nThreads).setLatch(new RecursiveLatch(nThreads * 2)).build();
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
							info.startScan(childFile);
							executor.execute(() -> {
								try {
									walk(childFile, childDepth, context, executor, info);
								} catch (IOException e) {
									handleDirectoryIfError(childFile, childDepth, e, context);
								} finally {
									info.finishScan(childFile);
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
								} finally {
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
		File directory = new File("H:\\JiYong2018");
		DirectoryWalker walker = new DirectoryWalker(directory, -1, null);
		FileInfo fileInfo = walker.walk(10, new Progressable() {

			public void progress(int fileCount, int folderCount, long length, float completedRatio, long elapsed) {
				System.out.println("fileCount: " + fileCount + ", folderCount: " + folderCount + ", length: " + length
						+ ", completedRatio: " + completedRatio + ", elapsed: " + elapsed);
			}
		});
		System.out.println(DateUtils.formatDurationAsHour(fileInfo.getElapsed()));
		System.out.println(fileInfo);
		System.in.read();
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