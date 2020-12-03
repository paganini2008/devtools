package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import com.github.paganini2008.devtools.NumberUtils;

/**
 * 
 * ForkJoinDirectoryWalker
 *
 * @author Jimmy Hoff
 * @since 1.0
 */
public class ForkJoinDirectoryWalker extends AbstractDirectoryWalker {

	public ForkJoinDirectoryWalker(File directory, DirectoryWalkerHandler handler) {
		super(directory);
		this.handler = handler;
	}

	private int maxDepth = -1;
	private FileFilter fileFilter;
	private final DirectoryWalkerHandler handler;

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	/**
	 * 
	 * DirectoryWalkTask
	 * 
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	private class DirectoryWalkTask extends RecursiveTask<FileInfo> {

		private static final long serialVersionUID = -1911846799071310358L;
		private final File directory;
		private final FileInfo directoryInfo;
		private final int depth;
		private final ProgressBar progressBar;

		DirectoryWalkTask(File directory, FileInfo directoryInfo, int depth, ProgressBar progressBar) {
			this.directory = directory;
			this.directoryInfo = directoryInfo;
			this.depth = depth;
			this.progressBar = progressBar;
		}

		protected FileInfo compute() {
			try {
				if (handler.shouldHandleDirectory(directory, depth)) {
					handler.handleDirectoryStart(directory, depth);
					if (maxDepth < 0 || depth <= maxDepth) {
						File[] childFiles = fileFilter != null ? directory.listFiles(fileFilter) : directory.listFiles();
						if (childFiles != null) {
							for (File childFile : childFiles) {
								if (progressBar.isCancelled()) {
									throw new CancellationException();
								}
								progressBar.processBegin(childFile);
								directoryInfo.process(childFile);
								if (childFile.isDirectory()) {
									FileInfo childFileInfo = directoryInfo.newChildFileInfo(childFile);
									DirectoryWalkTask task = new DirectoryWalkTask(childFile, childFileInfo, depth + 1, progressBar);
									task.fork();
									task.join();
								} else if (handler.shouldHandleFile(childFile, depth)) {
									try {
										handler.handleFile(childFile, depth);
									} catch (Exception e) {
										if (!handler.handleFileOnError(childFile, depth, e)) {
											progressBar.cancel();
										}
									}
								}
								progressBar.processEnd(childFile);
							}
						}
					}
					handler.handleDirectoryEnd(directory, directoryInfo, depth);
				}
			} catch (IOException e) {
				if (!handler.handleDirectoryOnError(directory, depth, e)) {
					progressBar.cancel();
				}
			} finally {
				directoryInfo.done();
			}
			return directoryInfo;
		}
	}

	@Override
	protected final void walk(Executor threadPool, File root, FileInfo rootInfo, int depth, ProgressBar progressBar) {
		DirectoryWalkTask task = new DirectoryWalkTask(root, rootInfo, depth, progressBar);
		Future<FileInfo> future = ((ForkJoinPool) threadPool).submit(task);
		try {
			future.get();
		} catch (Exception e) {
			handler.handleDirectoryOnError(root, depth, e);
		}
	}

	protected ForkJoinPool getThreadPool(int nThreads) {
		return new ForkJoinPool(nThreads);
	}

	public static void main(String[] args) throws Exception {
		File directory = new File("E:\\work\\repo_latest\\nexus-3.0");
		ForkJoinDirectoryWalker walker = new ForkJoinDirectoryWalker(directory, new DirectoryWalkerHandler() {
			@Override
			public void handleFile(File file, int depth) throws Exception {

			}
		});
		walker.setProgressable(new Progressable() {

			public void progress(int fileCount, int folderCount, long length, float completedRatio, long elapsed) {
				System.out.println("fileCount: " + fileCount + ", folderCount: " + folderCount + ", length: " + length
						+ ", completedRatio: " + NumberUtils.format(completedRatio, "0.00%") + ", elapsed: " + elapsed);
			}
		});
		Directory fileInfo = walker.walk();
		System.out.println(fileInfo.getLength());
		System.out.println("DirectoryWalker.main()");
		System.in.read();
		System.out.println("Completed.");
	}

}
