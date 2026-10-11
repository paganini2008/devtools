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
package com.github.paganini2008.devtools.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.io.Directory;
import com.github.paganini2008.devtools.io.DirectoryWalker;
import com.github.paganini2008.devtools.io.RecursiveDirectoryWalker;

/**
 * ProgressTrackTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class ProgressTrackTest {

	private PrintStream systemOut;
	private ByteArrayOutputStream console;

	@TempDir
	Path tempDir;

	@BeforeEach
	public void captureConsole() {
		systemOut = System.out;
		console = new ByteArrayOutputStream();
		System.setOut(new PrintStream(console, true));
	}

	@AfterEach
	public void restoreConsole() {
		System.setOut(systemOut);
	}

	private String lastLine() {
		String[] lines = console.toString().replace("\r", "\n").split("\n");
		return lines[lines.length - 1].trim();
	}

	@Test
	public void trackCollection_totalIsSize() {
		List<String> seen = new ArrayList<String>();
		for (String s : ProgressBar.track("Import", Arrays.asList("a", "b", "c"))) {
			seen.add(s);
		}
		assertEquals(Arrays.asList("a", "b", "c"), seen);
		assertTrue(lastLine().startsWith("Import [========================================] 100% 3/3"), lastLine());
		assertTrue(lastLine().contains("Done in"));
	}

	@Test
	public void trackIterable_unknownTotal() {
		Iterable<Integer> plain = () -> Arrays.asList(1, 2).iterator();
		int sum = 0;
		for (int i : ProgressBar.track("Plain", plain)) {
			sum += i;
		}
		assertEquals(3, sum);
		assertTrue(lastLine().startsWith("Plain 2  "), lastLine());
		assertTrue(lastLine().contains("Done in"));
		for (Object ignored : ProgressBar.track("Null", (Iterable<Object>) null)) {
			throw new AssertionError();
		}
	}

	@Test
	public void trackIterator_withAndWithoutTotal() {
		Iterable<String> once = ProgressBar.track("Iter", Arrays.asList("x", "y").iterator(), 2);
		assertSame(once.iterator(), once.iterator(), "single use iterable");
		List<String> seen = new ArrayList<String>();
		for (String s : once) {
			seen.add(s);
		}
		assertEquals(Arrays.asList("x", "y"), seen);
		assertTrue(lastLine().startsWith("Iter [========================================] 100% 2/2"), lastLine());
		int count = 0;
		for (String ignored : ProgressBar.track("Unknown", Arrays.asList("x").iterator())) {
			count++;
		}
		assertEquals(1, count);
		assertTrue(lastLine().startsWith("Unknown 1  "), lastLine());
	}

	@Test
	public void trackIterator_failureFailsAndRethrows() {
		Iterator<String> brokenNext = new Iterator<String>() {
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public String next() {
				throw new IllegalStateException("next broken");
			}
		};
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
			for (String ignored : ProgressBar.track("Broken", brokenNext)) {
				throw new AssertionError(ignored);
			}
		});
		assertEquals("next broken", e.getMessage());
		assertTrue(lastLine().contains("Failed after 00:00:00 (IllegalStateException: next broken)"), lastLine());

		Iterator<String> brokenHasNext = new Iterator<String>() {
			@Override
			public boolean hasNext() {
				throw new IllegalArgumentException("hasNext broken");
			}

			@Override
			public String next() {
				return null;
			}
		};
		assertThrows(IllegalArgumentException.class, () -> ProgressBar.track("Broken", brokenHasNext).iterator().hasNext());
		assertTrue(lastLine().contains("(IllegalArgumentException: hasNext broken)"), lastLine());
	}

	@Test
	public void trackArrays() {
		StringBuilder str = new StringBuilder();
		for (String s : ProgressBar.track("Objects", new String[] { "a", "b" })) {
			str.append(s);
		}
		assertEquals("ab", str.toString());
		assertTrue(lastLine().startsWith("Objects [========================================] 100% 2/2"), lastLine());
		int sum = 0;
		for (int i : ProgressBar.track("Ints", new int[] { 1, 2, 3 })) {
			sum += i;
		}
		assertEquals(6, sum);
		assertTrue(lastLine().startsWith("Ints [========================================] 100% 3/3"), lastLine());
		long lsum = 0;
		for (long l : ProgressBar.track("Longs", new long[] { 5L, 6L })) {
			lsum += l;
		}
		assertEquals(11L, lsum);
		assertTrue(lastLine().startsWith("Longs [========================================] 100% 2/2"), lastLine());
	}

	@Test
	public void trackStream_completesOnClose() {
		List<Integer> result;
		try (Stream<Integer> s = ProgressBar.track("Stream", Stream.of(1, 2, 3), 3)) {
			result = s.map(i -> i + 1).collect(Collectors.toList());
		}
		assertEquals(Arrays.asList(2, 3, 4), result);
		assertTrue(lastLine().startsWith("Stream [========================================] 100% 3/3"), lastLine());
	}

	private static void write(File file, int size) throws IOException {
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), new byte[size]);
	}

	@Test
	public void walk_returnsDirectoryAndRendersPercent() throws IOException {
		File root = tempDir.toFile();
		write(new File(root, "a/1.txt"), 10);
		write(new File(root, "a/b/2.txt"), 20);
		write(new File(root, "c.txt"), 30);
		Directory dir = ProgressBar.walk("Scan", new RecursiveDirectoryWalker(root, (file, depth) -> {
		}));
		assertEquals(3, dir.getFileCount(true));
		assertEquals(2, dir.getFolderCount(true));
		assertEquals("Scan [========================================] 100%  Done in 00:00:00  files=3 dirs=2 size=60 B",
				lastLine().replaceAll("Done in \\d\\d:\\d\\d:\\d\\d", "Done in 00:00:00"));
	}

	static class FakeWalker implements DirectoryWalker {
		Throwable error;
		DirectoryWalker.Progressable progressable;

		@Override
		public void setThreadCount(int threadCount) {
		}

		@Override
		public void setProgressable(DirectoryWalker.Progressable progressable) {
			this.progressable = progressable;
		}

		@Override
		public Directory walk() throws IOException {
			if (error instanceof IOException) {
				throw (IOException) error;
			}
			if (error instanceof RuntimeException) {
				throw (RuntimeException) error;
			}
			return null;
		}
	}

	@Test
	public void walk_failuresFailAndRethrow() {
		FakeWalker io = new FakeWalker();
		io.error = new IOException("denied");
		IOException e = assertThrows(IOException.class, () -> ProgressBar.walk("Scan", io));
		assertEquals("denied", e.getMessage());
		assertTrue(lastLine().contains("Failed after 00:00:00 (IOException: denied)"), lastLine());
		FakeWalker runtime = new FakeWalker();
		runtime.error = new IllegalStateException("cancelled");
		assertThrows(IllegalStateException.class, () -> ProgressBar.walk("Scan", runtime));
		assertTrue(lastLine().contains("(IllegalStateException: cancelled)"), lastLine());
		assertThrows(IllegalArgumentException.class, () -> ProgressBar.walk("Scan", null));
	}

	@Test
	public void walk_completesEvenWithoutFinalReport() throws IOException {
		RecordingRenderer renderer = new RecordingRenderer();
		ProgressBar bar = ProgressBar.builder().renderer(renderer).build();
		FakeWalker walker = new FakeWalker();
		ProgressBar.walk(bar, walker);
		assertEquals(ProgressState.COMPLETED, bar.getState());
		assertTrue(walker.progressable != null);
		ProgressBar half = ProgressBar.builder().renderer(renderer).build();
		FakeWalker reporting = new FakeWalker() {
			@Override
			public Directory walk() throws IOException {
				progressable.progress(1, 0, 1, 0.5f, 0);
				return null;
			}
		};
		ProgressBar.walk(half, reporting);
		assertEquals(100L, half.getCompleted());
		assertEquals(ProgressState.COMPLETED, half.getState());
	}

}
