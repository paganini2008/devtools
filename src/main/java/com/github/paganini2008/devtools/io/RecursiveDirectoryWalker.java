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
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * {@link DirectoryWalker} that walks every sub directory as a separate task of a fixed thread pool. Handler callbacks for
 * files are called with the depth of the file itself (children of the root directory have depth 1).
 *
 * @author Fred Feng
 * @date 2020-05-09
 * @since 2.0.1
 */
public class RecursiveDirectoryWalker extends AbstractDirectoryWalker {

	private int maxDepth = -1;
	private FileFilter fileFilter;
	private final DirectoryWalkerHandler handler;

	public RecursiveDirectoryWalker(File directory, DirectoryWalkerHandler handler) {
		super(directory);
		this.handler = handler;
	}

	/**
	 * Limits the depth of directories whose children are listed; negative for unlimited.
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	/**
	 * Sets a filter applied when listing directories; {@code null} accepts everything.
	 */
	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	@Override
	protected final void walk(Executor threadPool, File root, FileInfo rootInfo, int depth, ProgressBar progressBar) {
		threadPool.execute(() -> {
			try {
				doWalk(threadPool, root, rootInfo, depth, progressBar);
			} catch (IOException e) {
				if (!handler.handleDirectoryOnError(root, depth, e)) {
					progressBar.cancel();
				}
			} catch (CancellationException ignored) {
			} finally {
				rootInfo.done();
				progressBar.processEnd(root);
			}
		});
	}

	@Override
	protected final Executor getThreadPool(int nThreads) {
		final AtomicInteger counter = new AtomicInteger();
		return Executors.newFixedThreadPool(nThreads > 0 ? nThreads : 1, r -> {
			Thread thread = new Thread(r, "RecursiveDirectoryWalker-" + counter.incrementAndGet());
			thread.setDaemon(true);
			return thread;
		});
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
								} catch (CancellationException ignored) {
								} finally {
									childFileInfo.done();
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
}
