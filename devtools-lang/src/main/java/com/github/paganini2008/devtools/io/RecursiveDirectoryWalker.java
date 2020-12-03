package com.github.paganini2008.devtools.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;

import com.github.paganini2008.devtools.NumberUtils;
import com.github.paganini2008.devtools.multithreads.ThreadPoolBuilder;
import com.github.paganini2008.devtools.multithreads.latch.RecursiveLatch;

/**
 * 
 * RecursiveDirectoryWalker
 *
 * @author Jimmy Hoff
 * @since 1.0
 */
public class RecursiveDirectoryWalker extends AbstractDirectoryWalker {

	private int maxDepth = -1;
	private FileFilter fileFilter;
	private final DirectoryWalkerHandler handler;

	public RecursiveDirectoryWalker(File directory, DirectoryWalkerHandler handler) {
		super(directory);
		this.handler = handler;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	@Override
	protected final void walk(Executor threadPool, File root, FileInfo rootInfo, int depth, ProgressBar progressBar) {
		threadPool.execute(() -> {
			try {
				doWalk(threadPool, root, rootInfo, depth, progressBar);
			} catch (IOException e) {
				handler.handleDirectoryOnError(root, depth, e);
			} finally {
				rootInfo.done();
			}
		});
	}

	@Override
	protected final Executor getThreadPool(int nThreads) {
		return ThreadPoolBuilder.common(nThreads).setLatch(new RecursiveLatch(nThreads * 2)).build();
	}

	private void doWalk(final Executor executor, final File directory, final FileInfo directoryInfo, final int depth,
			final ProgressBar progressBar) throws IOException {
		if (handler.shouldHandleDirectory(directory, depth)) {
			handler.handleDirectoryStart(directory, depth);
			final int childDepth = depth + 1;
			if (maxDepth < 0 || childDepth <= maxDepth) {
				File[] childFiles = fileFilter == null ? directory.listFiles() : directory.listFiles(fileFilter);
				if (childFiles != null) {
					for (final File childFile : childFiles) {
						if (progressBar.isCancelled()) {
							throw new CancellationException();
						}
						progressBar.processBegin(childFile);
						directoryInfo.process(childFile);
						if (childFile.isDirectory()) {
							FileInfo childFileInfo = directoryInfo.newChildFileInfo(childFile);
							executor.execute(() -> {
								try {
									doWalk(executor, childFile, childFileInfo, childDepth, progressBar);
								} catch (IOException e) {
									if (!handler.handleDirectoryOnError(childFile, childDepth, e)) {
										progressBar.cancel();
									}
								} finally {
									directoryInfo.done();
									progressBar.processEnd(childFile);
								}
							});
						} else {
							try {
								if (handler.shouldHandleFile(childFile, childDepth)) {
									handler.handleFile(childFile, childDepth);
								}
							} catch (Exception e) {
								if (!handler.handleFileOnError(childFile, childDepth, e)) {
									progressBar.cancel();
								}
							} finally {
								progressBar.processEnd(childFile);
							}
						}
					}
				}
			}
			handler.handleDirectoryEnd(directory, directoryInfo, depth);
		}
	}

	public static void main(String[] args) throws Exception {
		File directory = new File("G:\\test\\nexus-3.0-today\\nexus-3.0");
		List<File> files = new ArrayList<File>();
		RecursiveDirectoryWalker directoryWalker = new RecursiveDirectoryWalker(directory, new DirectoryWalkerHandler() {

			@Override
			public void handleFile(File file, int depth) throws Exception {
				if(file.getName().equals(".classpath") || file.getName().equals(".project")) {
					System.out.println("Delete: " + file);
					file.delete();
				}
			}

			@Override
			public void handleDirectoryEnd(File file, Directory directory, int depth) throws IOException {
				if(file.getName().equals(".settings") ||file.getName().equals("bin") ||  file.getName().equals("logs")) {
					files.add(file);
				}
			}
			
			
		});
		directoryWalker.setThreadCount(100);
		directoryWalker.setProgressable(new Progressable() {

			@Override
			public void progress(int fileCount, int folderCount, long length, float completedRatio, long elapsed) {
				System.out.println("fileCount: " + fileCount + ", folderCount: " + folderCount + ", length: " + length
						+ ", completedRatio: " + NumberUtils.format(completedRatio, "0.00%") + ", elapsed: " + elapsed);
			}
		});
		Directory fileInfo = directoryWalker.walk();
		System.out.println(fileInfo.getLength());
		System.out.println("DirectoryWalker.main()");
		
		for(File file:  files) {
			System.out.println("Delete folder: " + file);
			FileUtils.deleteDirectory(file);
		}
	}

}
