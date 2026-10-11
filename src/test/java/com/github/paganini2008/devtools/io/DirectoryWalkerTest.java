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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.paganini2008.devtools.io.AbstractDirectoryWalker.ProgressBarImpl;
import com.github.paganini2008.devtools.io.DirectoryWalker.FileInfo;
import com.github.paganini2008.devtools.io.DirectoryWalker.Progressable;

/**
 * DirectoryWalkerTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(value = 20, unit = TimeUnit.SECONDS)
public class DirectoryWalkerTest {

	@TempDir
	File tempDir;

	private File root;
	private Set<String> allFiles;
	private long totalLength;
	private int totalFolders;

	@BeforeEach
	public void setUp() throws IOException {
		root = new File(tempDir, "root");
		allFiles = new HashSet<String>();
		// 4 top level directories, each with 3 sub directories containing 5 files, plus root files
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 5; k++) {
					create("d" + i + "/s" + j + "/f" + k + ".txt", i + j + k + 1);
				}
				totalFolders++;
			}
			create("d" + i + "/top.txt", 2);
			totalFolders++;
		}
		create("r1.txt", 3);
		create("r2.dat", 4);
		new File(root, "emptydir").mkdirs();
		totalFolders++;
	}

	private void create(String path, int size) throws IOException {
		File file = new File(root, path);
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), new byte[size]);
		allFiles.add(file.getAbsolutePath());
		totalLength += size;
	}

	private static class CollectingHandler implements DirectoryWalkerHandler {

		final Set<String> files = ConcurrentHashMap.newKeySet();
		final Set<String> directoriesStarted = ConcurrentHashMap.newKeySet();
		final Set<String> directoriesEnded = ConcurrentHashMap.newKeySet();
		final List<String> depths = new CopyOnWriteArrayList<String>();

		@Override
		public void handleDirectoryStart(File file, int depth) throws IOException {
			directoriesStarted.add(file.getAbsolutePath());
		}

		@Override
		public void handleDirectoryEnd(File file, Directory directory, int depth) {
			directoriesEnded.add(file.getAbsolutePath());
		}

		@Override
		public void handleFile(File file, int depth) throws Exception {
			files.add(file.getAbsolutePath());
			depths.add(file.getName() + "@" + depth);
		}
	}

	private AbstractDirectoryWalker newWalker(String type, DirectoryWalkerHandler handler, int maxDepth, java.io.FileFilter filter) {
		if ("recursive".equals(type)) {
			RecursiveDirectoryWalker walker = new RecursiveDirectoryWalker(root, handler);
			walker.setMaxDepth(maxDepth);
			walker.setFileFilter(filter);
			return walker;
		}
		ForkJoinDirectoryWalker walker = new ForkJoinDirectoryWalker(root, handler);
		walker.setMaxDepth(maxDepth);
		walker.setFileFilter(filter);
		return walker;
	}

	@ParameterizedTest
	@ValueSource(strings = { "recursive", "forkjoin" })
	public void walk_visitsEveryFileExactlyOnceAndAggregatesStatistics(String type) {
		CollectingHandler handler = new CollectingHandler();
		AbstractDirectoryWalker walker = newWalker(type, handler, -1, null);
		walker.setThreadCount(4);
		Directory result = walker.walk();
		assertEquals(allFiles, handler.files);
		assertEquals(allFiles.size(), handler.depths.size());
		assertEquals(totalFolders + 1, handler.directoriesStarted.size());
		assertEquals(handler.directoriesStarted, handler.directoriesEnded);

		assertSame(root, result.getFile());
		assertNull(result.getParent());
		assertEquals(allFiles.size(), result.getFileCount(true));
		assertEquals(2, result.getFileCount(false));
		assertEquals(totalFolders, result.getFolderCount(true));
		assertEquals(5, result.getFolderCount(false));
		assertEquals(totalLength, result.getLength());
		assertEquals(5, result.getChildren().length);
		for (Directory child : result.getChildren()) {
			assertSame(result, child.getParent());
			assertTrue(((FileInfo) child).isDone());
			assertTrue(((FileInfo) child).getElapsed() >= 0);
		}
		assertTrue(((FileInfo) result).isDone());
		assertTrue(result.getLastModified() >= root.lastModified());
	}

	@Test
	public void recursiveWalker_reportsChildDepthForFiles() {
		CollectingHandler handler = new CollectingHandler();
		newWalker("recursive", handler, -1, null).walk();
		assertTrue(handler.depths.contains("r1.txt@1"));
		assertTrue(handler.depths.contains("top.txt@2"));
		assertTrue(handler.depths.contains("f0.txt@3"));
	}

	@Test
	public void forkJoinWalker_reportsParentDepthForFiles() {
		CollectingHandler handler = new CollectingHandler();
		newWalker("forkjoin", handler, -1, null).walk();
		assertTrue(handler.depths.contains("r1.txt@0"));
		assertTrue(handler.depths.contains("top.txt@1"));
		assertTrue(handler.depths.contains("f0.txt@2"));
	}

	@Test
	public void recursiveWalker_maxDepthLimitsListing() {
		CollectingHandler handler = new CollectingHandler();
		newWalker("recursive", handler, 1, null).walk();
		assertEquals(new HashSet<String>(Arrays.asList(new File(root, "r1.txt").getAbsolutePath(), new File(root, "r2.dat").getAbsolutePath())),
				handler.files);

		handler = new CollectingHandler();
		Directory result = newWalker("recursive", handler, 0, null).walk();
		assertTrue(handler.files.isEmpty());
		assertEquals(0, result.getFileCount(true));
	}

	@Test
	public void forkJoinWalker_maxDepthLimitsListing() {
		CollectingHandler handler = new CollectingHandler();
		newWalker("forkjoin", handler, 0, null).walk();
		assertEquals(new HashSet<String>(Arrays.asList(new File(root, "r1.txt").getAbsolutePath(), new File(root, "r2.dat").getAbsolutePath())),
				handler.files);
	}

	@ParameterizedTest
	@ValueSource(strings = { "recursive", "forkjoin" })
	public void walk_terminatesWhenFilterExcludesTopLevelEntries(String type) {
		CollectingHandler handler = new CollectingHandler();
		Directory result = newWalker(type, handler, -1, file -> file.isDirectory() || file.getName().startsWith("f0")).walk();
		assertEquals(12, handler.files.size());
		assertEquals(12, result.getFileCount(true));
	}

	@ParameterizedTest
	@ValueSource(strings = { "recursive", "forkjoin" })
	public void walk_terminatesWhenRootIsSkipped(String type) {
		CollectingHandler handler = new CollectingHandler() {
			@Override
			public boolean shouldHandleDirectory(File directory, int depth) {
				return false;
			}
		};
		Directory result = newWalker(type, handler, -1, null).walk();
		assertTrue(handler.files.isEmpty());
		assertEquals(0, result.getChildren().length);
	}

	@ParameterizedTest
	@ValueSource(strings = { "recursive", "forkjoin" })
	public void walk_emptyDirectory(String type) {
		CollectingHandler handler = new CollectingHandler();
		File empty = new File(root, "emptydir");
		AbstractDirectoryWalker walker = "recursive".equals(type) ? new RecursiveDirectoryWalker(empty, handler)
				: new ForkJoinDirectoryWalker(empty, handler);
		Directory result = walker.walk();
		assertEquals(0, result.getFileCount(true));
		assertEquals(0, result.getLength());
	}

	@ParameterizedTest
	@ValueSource(strings = { "recursive", "forkjoin" })
	public void walk_skipsFilesRejectedByHandler(String type) {
		CollectingHandler handler = new CollectingHandler() {
			@Override
			public boolean shouldHandleFile(File file, int depth) {
				return file.getName().endsWith(".dat");
			}
		};
		Directory result = newWalker(type, handler, -1, null).walk();
		assertEquals(1, handler.files.size());
		assertEquals(allFiles.size(), result.getFileCount(true));
	}

	@ParameterizedTest
	@ValueSource(strings = { "recursive", "forkjoin" })
	public void walk_continuesAfterFileErrorWhenHandlerAllows(String type) {
		final AtomicInteger errors = new AtomicInteger();
		CollectingHandler handler = new CollectingHandler() {
			@Override
			public void handleFile(File file, int depth) throws Exception {
				if (file.getName().equals("f1.txt")) {
					throw new IllegalStateException("bad file");
				}
				super.handleFile(file, depth);
			}

			@Override
			public boolean handleFileOnError(File file, int depth, Throwable e) {
				errors.incrementAndGet();
				return true;
			}
		};
		newWalker(type, handler, -1, null).walk();
		assertEquals(12, errors.get());
		assertEquals(allFiles.size() - 12, handler.files.size());
	}

	@ParameterizedTest
	@ValueSource(strings = { "recursive", "forkjoin" })
	public void walk_cancelsWhenFileErrorHandlerRefuses(String type) {
		CollectingHandler handler = new CollectingHandler() {
			@Override
			public void handleFile(File file, int depth) throws Exception {
				throw new IOException("stop");
			}

			@Override
			public boolean handleFileOnError(File file, int depth, Throwable e) {
				return false;
			}
		};
		Directory result = newWalker(type, handler, -1, null).walk();
		assertTrue(result.getFileCount(true) < allFiles.size());
	}

	@ParameterizedTest
	@ValueSource(strings = { "recursive", "forkjoin" })
	public void walk_handlesDirectoryErrors(String type) {
		final Set<String> failed = ConcurrentHashMap.newKeySet();
		CollectingHandler handler = new CollectingHandler() {
			@Override
			public void handleDirectoryStart(File file, int depth) throws IOException {
				if (file.getName().equals("d1")) {
					throw new IOException("no access");
				}
			}

			@Override
			public boolean handleDirectoryOnError(File file, int depth, Throwable e) {
				failed.add(file.getName());
				return true;
			}
		};
		newWalker(type, handler, -1, null).walk();
		assertEquals(new HashSet<String>(Arrays.asList("d1")), failed);
		assertEquals(allFiles.size() - 16, handler.files.size());
	}

	@ParameterizedTest
	@ValueSource(strings = { "recursive", "forkjoin" })
	public void walk_cancelsWhenDirectoryErrorHandlerRefuses(String type) {
		CollectingHandler handler = new CollectingHandler() {
			@Override
			public void handleDirectoryStart(File file, int depth) throws IOException {
				throw new IOException("no access");
			}

			@Override
			public boolean handleDirectoryOnError(File file, int depth, Throwable e) {
				return false;
			}
		};
		Directory result = newWalker(type, handler, -1, null).walk();
		assertTrue(handler.files.isEmpty());
		assertEquals(0, result.getFileCount(true));
	}

	@ParameterizedTest
	@ValueSource(strings = { "recursive", "forkjoin" })
	public void walk_reportsFinalProgress(String type) {
		final List<Float> rates = new CopyOnWriteArrayList<Float>();
		final AtomicInteger fileCount = new AtomicInteger();
		AbstractDirectoryWalker walker = newWalker(type, new CollectingHandler(), -1, null);
		walker.setProgressable(new Progressable() {
			@Override
			public void progress(int files, int folders, long length, float completedRatio, long elapsed) {
				rates.add(completedRatio);
				fileCount.set(files);
			}
		});
		walker.walk();
		assertFalse(rates.isEmpty());
		assertEquals(1.0f, rates.get(rates.size() - 1), 0.0001f);
		assertEquals(allFiles.size(), fileCount.get());
	}

	@Test
	public void progressable_defaultRefreshIntervalIsOneSecond() {
		Progressable progressable = (a, b, c, d, e) -> {
		};
		assertEquals(1, progressable.refreshInterval());
	}

	@Test
	public void progressBar_tracksBeginAndEnd() {
		ProgressBarImpl bar = new ProgressBarImpl(root, null);
		bar.processBegin(root);
		assertFalse(bar.isDone());
		assertEquals(0f, bar.getCompletionRate(), 0.0001f);
		File r1 = new File(root, "r1.txt");
		File d0 = new File(root, "d0");
		File inner = new File(root, "d0/top.txt");
		bar.processBegin(r1);
		bar.processBegin(d0);
		bar.processBegin(inner);
		assertEquals(1, bar.getFolderCount());
		assertEquals(2, bar.getFileCount());
		assertEquals(5, bar.getLength());
		bar.processEnd(r1);
		bar.processEnd(d0);
		assertFalse(bar.isDone());
		bar.processEnd(inner);
		assertTrue(bar.getCompletionRate() > 0f);
		bar.processEnd(root);
		assertTrue(bar.isDone());
		assertEquals(1f, bar.getCompletionRate(), 0.0001f);
		assertTrue(bar.getElapsed() >= 0);
		bar.waitForTermination();
	}

	@Test
	public void progressBar_cancelMarksDone() {
		ProgressBarImpl bar = new ProgressBarImpl(root, null);
		bar.processBegin(root);
		assertFalse(bar.isCancelled());
		bar.cancel();
		assertTrue(bar.isCancelled());
		assertTrue(bar.isDone());
		bar.waitForTermination();
	}

	@Test
	public void progressBar_emptyDirectoryHasZeroRateUntilDone() {
		ProgressBarImpl bar = new ProgressBarImpl(new File(root, "emptydir"), null);
		bar.processBegin(new File(root, "emptydir"));
		assertEquals(0f, bar.getCompletionRate(), 0.0001f);
		bar.processEnd(new File(root, "emptydir"));
		assertEquals(1f, bar.getCompletionRate(), 0.0001f);
	}

	@Test
	public void progressBar_executeReportsAndStopsWhenDone() {
		final AtomicInteger calls = new AtomicInteger();
		ProgressBarImpl bar = new ProgressBarImpl(new File(root, "emptydir"), new Progressable() {
			@Override
			public void progress(int fileCount, int folderCount, long length, float completedRatio, long elapsed) {
				calls.incrementAndGet();
			}

		});
		bar.processBegin(new File(root, "emptydir"));
		assertTrue(bar.execute());
		bar.processEnd(new File(root, "emptydir"));
		assertFalse(bar.execute());
		assertTrue(calls.get() >= 2);
	}

	@Test
	public void searchUtils_findsMatchingFilesAndDirectories() {
		File[] files = FileSearchUtils.search(root, new DirectoryFilter() {

			@Override
			public boolean accept(Directory directory) {
				return directory.getFile().getName().equals("s2");
			}

			@Override
			public boolean accept(File file) {
				return file.getName().equals("f4.txt");
			}
		}, 4, -1);
		int s2 = 0;
		int f4 = 0;
		for (File file : files) {
			if (file.getName().equals("s2")) {
				s2++;
			} else if (file.getName().equals("f4.txt")) {
				f4++;
			}
		}
		assertEquals(4, s2);
		assertEquals(12, f4);
		assertEquals(16, files.length);
	}

	@Test
	public void directoryFilter_rejectsFilesByDefault() {
		DirectoryFilter filter = directory -> true;
		assertFalse(filter.accept(root));
		assertTrue(filter.accept((Directory) null));
	}

	@Test
	public void walk_handlesConcurrentWalkersOnSameTree() throws Exception {
		final BiFunction<String, Integer, Integer> run = (type, threads) -> {
			CollectingHandler handler = new CollectingHandler();
			AbstractDirectoryWalker walker = newWalker(type, handler, -1, null);
			walker.setThreadCount(threads);
			walker.walk();
			return handler.files.size();
		};
		for (int i = 0; i < 5; i++) {
			assertEquals(allFiles.size(), run.apply("recursive", 1 + i).intValue());
			assertEquals(allFiles.size(), run.apply("forkjoin", 1 + i).intValue());
		}
	}
}
