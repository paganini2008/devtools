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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * ProgressWrappersTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class ProgressWrappersTest {

	private final RecordingRenderer renderer = new RecordingRenderer();
	private PrintStream systemOut;

	@BeforeEach
	public void silenceConsole() {
		systemOut = System.out;
		System.setOut(new PrintStream(new ByteArrayOutputStream(), true));
	}

	@AfterEach
	public void restoreConsole() {
		System.setOut(systemOut);
	}

	private ProgressBar bar() {
		return ProgressBar.builder().taskName("Job").renderer(renderer).build();
	}

	private static final byte[] DATA = new byte[10000];
	static {
		for (int i = 0; i < DATA.length; i++) {
			DATA[i] = (byte) i;
		}
	}

	@Test
	public void manualStep_inPlainForLoop() {
		int n = 5;
		ProgressBar ref;
		try (ProgressBar bar = ProgressBar.builder().taskName("Import").total(n).renderer(renderer).build()) {
			ref = bar;
			for (int i = 0; i < n; i++) {
				bar.step();
			}
		}
		assertEquals(5L, ref.getCompleted());
		assertEquals(ProgressState.COMPLETED, ref.getState());
		assertEquals(100d, ref.getPercent());
	}

	@Test
	public void wrapList_completesWithSizeAsTotal() {
		List<String> list = Arrays.asList("a", "b", "c");
		ProgressBar bar = bar();
		List<String> seen = new ArrayList<String>();
		for (String s : bar.wrap(list)) {
			assertEquals(ProgressState.RUNNING, bar.getState());
			seen.add(s);
		}
		assertEquals(list, seen);
		assertEquals(3L, bar.getTotal());
		assertEquals(3L, bar.getCompleted());
		assertEquals(ProgressState.COMPLETED, bar.getState());
		assertEquals(1, renderer.finished.size());
	}

	@Test
	public void wrapIterable_keepsExplicitTotalAndDetectsCollections() {
		ProgressBar bar = ProgressBar.builder().total(10).renderer(renderer).build();
		Iterable<Integer> iterable = new LinkedHashSet<Integer>(Arrays.asList(1, 2));
		int sum = 0;
		for (int i : bar.wrap(iterable)) {
			sum += i;
		}
		assertEquals(3, sum);
		assertEquals(10L, bar.getTotal(), "explicit total is kept");
		assertTrue(bar.isFinished());

		ProgressBar other = bar();
		Iterable<Integer> plain = () -> Arrays.asList(1, 2, 3).iterator();
		for (Integer ignored : other.wrap(plain)) {
			assertTrue(ignored > 0);
		}
		assertEquals(-1L, other.getTotal(), "a plain iterable has no size");
		assertEquals(3L, other.getCompleted());
		assertTrue(other.isFinished());

		ProgressBar collection = bar();
		collection.wrap((Iterable<String>) Arrays.asList("x", "y"));
		assertEquals(2L, collection.getTotal());
	}

	@Test
	public void wrapNulls_areEmpty() {
		ProgressBar a = bar();
		assertFalse(a.wrap((Iterable<String>) null).iterator().hasNext());
		assertTrue(a.isFinished());
		ProgressBar b = bar();
		assertFalse(b.wrap((Collection<String>) null).iterator().hasNext());
		assertEquals(-1L, b.getTotal());
		ProgressBar c = bar();
		Iterator<String> it = c.wrap((Iterator<String>) null);
		assertThrows(NoSuchElementException.class, it::next);
		assertThrows(IllegalStateException.class, it::remove);
		assertFalse(it.hasNext());
		assertFalse(bar().wrap((String[]) null).iterator().hasNext());
		assertFalse(bar().wrap((int[]) null).iterator().hasNext());
		assertFalse(bar().wrap((long[]) null).iterator().hasNext());
		assertFalse(bar().wrap((double[]) null).iterator().hasNext());
		assertEquals(0L, bar().wrap((Stream<String>) null).count());
	}

	@Test
	public void wrapIterator_supportsRemove() {
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
		ProgressBar bar = bar();
		Iterator<Integer> it = bar.wrap(list.iterator());
		it.next();
		it.remove();
		assertEquals(Arrays.asList(2, 3), list);
		assertEquals(1L, bar.getCompleted());
	}

	@Test
	public void wrapArrays_totalIsLength() {
		ProgressBar objects = bar();
		StringBuilder str = new StringBuilder();
		for (String s : objects.wrap(new String[] { "a", "b" })) {
			str.append(s);
		}
		assertEquals("ab", str.toString());
		assertEquals(2L, objects.getTotal());
		assertTrue(objects.isFinished());

		ProgressBar ints = bar();
		int sum = 0;
		for (int i : ints.wrap(new int[] { 1, 2, 3 })) {
			sum += i;
		}
		assertEquals(6, sum);
		assertEquals(3L, ints.getCompleted());
		assertEquals(3L, ints.getTotal());

		ProgressBar longs = bar();
		long lsum = 0;
		for (long l : longs.wrap(new long[] { 10L, 20L })) {
			lsum += l;
		}
		assertEquals(30L, lsum);
		assertEquals(2L, longs.getTotal());

		ProgressBar doubles = bar();
		double dsum = 0;
		for (double d : doubles.wrap(new double[] { 0.5, 0.25 })) {
			dsum += d;
		}
		assertEquals(0.75d, dsum);
		assertTrue(doubles.isFinished());

		Iterator<Integer> exhausted = bar().wrap(new int[0]).iterator();
		assertFalse(exhausted.hasNext());
		assertThrows(NoSuchElementException.class, exhausted::next);
	}

	@Test
	public void range_indexLoop() {
		List<Integer> seen = new ArrayList<Integer>();
		Iterable<Integer> range = ProgressBar.range("Process", 3, 7);
		for (int i : range) {
			seen.add(i);
		}
		assertEquals(Arrays.asList(3, 4, 5, 6), seen);

		ProgressBar bar = bar();
		int count = 0;
		for (int i : bar.range(0, 5)) {
			assertEquals(count++, i);
		}
		assertEquals(5L, bar.getTotal());
		assertEquals(5L, bar.getCompleted());
		assertEquals(ProgressState.COMPLETED, bar.getState());

		ProgressBar empty = bar();
		assertFalse(empty.range(5, 5).iterator().hasNext());
		assertTrue(empty.isFinished());
		assertEquals(-1L, empty.getTotal());
		Iterator<Integer> backwards = bar().range(5, 1).iterator();
		assertFalse(backwards.hasNext());
		assertThrows(NoSuchElementException.class, backwards::next);
	}

	@Test
	public void range_longVersion() {
		long sum = 0;
		for (long i : ProgressBar.range("Process", 1L, 4L)) {
			sum += i;
		}
		assertEquals(6L, sum);

		ProgressBar bar = ProgressBar.builder().total(100).renderer(renderer).build();
		Iterator<Long> it = bar.range(10L, 12L).iterator();
		assertEquals(Long.valueOf(10L), it.next());
		assertEquals(Long.valueOf(11L), it.next());
		assertThrows(NoSuchElementException.class, it::next);
		assertFalse(it.hasNext());
		assertEquals(100L, bar.getTotal(), "explicit total is kept");
		assertEquals(2L, bar.getCompleted());
		assertTrue(bar.isFinished());

		ProgressBar empty = bar();
		assertFalse(empty.range(3L, 1L).iterator().hasNext());
		assertEquals(-1L, empty.getTotal());
	}

	@Test
	public void staticRangeAndForEach_renderOnConsoleByDefault() {
		PrintStream old = System.out;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		System.setOut(new PrintStream(buffer, true));
		try {
			for (int i : ProgressBar.range("Quiet", 0, 2)) {
				assertTrue(i >= 0);
			}
			ProgressBar.forEach("Each", Arrays.asList(1, 2), i -> {
			});
		} finally {
			System.setOut(old);
		}
		String out = buffer.toString();
		assertTrue(out.contains("Quiet"));
		assertTrue(out.contains("Each"));
		assertTrue(out.contains("Done in"));
	}

	@Test
	public void wrapStream_countsAndCompletesOnClose() {
		ProgressBar bar = bar();
		List<Integer> result;
		try (Stream<Integer> stream = bar.wrap(Stream.of(1, 2, 3, 4), 4)) {
			result = stream.map(i -> i * 2).collect(Collectors.toList());
			assertEquals(ProgressState.RUNNING, bar.getState());
		}
		assertEquals(Arrays.asList(2, 4, 6, 8), result);
		assertEquals(4L, bar.getTotal());
		assertEquals(4L, bar.getCompleted());
		assertEquals(ProgressState.COMPLETED, bar.getState());
	}

	@Test
	public void wrapStream_parallelCountIsExact() {
		ProgressBar bar = ProgressBar.builder().renderer(renderer).refreshInterval(0, TimeUnit.MILLISECONDS).build();
		long sum;
		try (Stream<Integer> stream = bar.wrap(IntStream.range(0, 20000).boxed().parallel())) {
			sum = stream.mapToLong(Integer::longValue).sum();
		}
		assertEquals(199990000L, sum);
		assertEquals(20000L, bar.getCompleted());
		assertTrue(bar.isFinished());
		assertEquals(0, renderer.overlaps.get());
	}

	@Test
	public void forEach_completesAfterLastItem() {
		ProgressBar bar = bar();
		List<String> seen = new ArrayList<String>();
		bar.forEach(Arrays.asList("a", "b"), seen::add);
		assertEquals(Arrays.asList("a", "b"), seen);
		assertEquals(2L, bar.getTotal());
		assertEquals(2L, bar.getCompleted());
		assertEquals(ProgressState.COMPLETED, bar.getState());

		ProgressBar plain = bar();
		AtomicInteger count = new AtomicInteger();
		plain.forEach((Iterable<String>) () -> Arrays.asList("x").iterator(), s -> count.incrementAndGet());
		assertEquals(1, count.get());
		assertEquals(-1L, plain.getTotal());

		ProgressBar none = bar();
		none.forEach(null, s -> count.incrementAndGet());
		assertEquals(ProgressState.COMPLETED, none.getState());
		ProgressBar.forEach("Nothing", null, s -> count.incrementAndGet());
		assertEquals(1, count.get());
	}

	@Test
	public void forEach_failsAndRethrows() {
		ProgressBar bar = ProgressBar.builder().total(10).renderer(renderer).build();
		IllegalStateException boom = new IllegalStateException("boom");
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> bar.forEach(Arrays.asList(1, 2, 3), i -> {
			if (i == 2) {
				throw boom;
			}
		}));
		assertSame(boom, e);
		assertEquals(ProgressState.FAILED, bar.getState());
		assertEquals(1L, bar.getCompleted());
		assertSame(boom, renderer.last().getFailure());
		assertEquals(10L, bar.getTotal(), "explicit total is kept");
		assertThrows(IllegalArgumentException.class, () -> bar().forEach(Arrays.asList(1), null));
		assertThrows(AssertionError.class, () -> bar().forEach(Arrays.asList(1), i -> {
			throw new AssertionError();
		}));
	}

	@Test
	public void inputStream_countsBytesAndCompletesAtEof() throws IOException {
		ProgressBar bar = ProgressBar.builder().unit("B").total(DATA.length).renderer(renderer).build();
		ByteArrayOutputStream copy = new ByteArrayOutputStream();
		try (ProgressInputStream in = bar.wrap(new ByteArrayInputStream(DATA))) {
			assertSame(bar, in.getProgressBar());
			copy.write(in.read());
			assertEquals(5L, in.skip(5));
			byte[] buf = new byte[333];
			assertEquals(0, in.read(buf, 0, 0));
			int n;
			while ((n = in.read(buf, 0, buf.length)) != -1) {
				copy.write(buf, 0, n);
			}
			assertEquals(ProgressState.COMPLETED, bar.getState());
			assertEquals(-1, in.read());
			assertEquals(0L, in.skip(5));
		}
		assertEquals(DATA.length, bar.getCompleted());
		assertEquals(DATA.length - 5, copy.size());
	}

	@Test
	public void inputStream_closeCompletesAndFailuresFail() throws IOException {
		ProgressBar bar = bar();
		ProgressInputStream in = new ProgressInputStream(new ByteArrayInputStream(DATA), bar);
		assertEquals(0, in.read());
		in.close();
		assertEquals(ProgressState.COMPLETED, bar.getState());

		for (int mode = 0; mode < 3; mode++) {
			ProgressBar failing = bar();
			ProgressInputStream broken = failing.wrap(new BrokenInputStream());
			final int m = mode;
			IOException e = assertThrows(IOException.class, () -> {
				if (m == 0) {
					broken.read();
				} else if (m == 1) {
					broken.read(new byte[4], 0, 4);
				} else {
					broken.skip(4);
				}
			});
			assertEquals("broken", e.getMessage());
			assertEquals(ProgressState.FAILED, failing.getState());
			assertSame(e, failing.snapshot().getFailure());
		}
		assertThrows(IllegalArgumentException.class, () -> new ProgressInputStream(null, bar()));
		assertThrows(IllegalArgumentException.class, () -> new ProgressInputStream(new ByteArrayInputStream(DATA), null));
	}

	@Test
	public void outputStream_countsBytesAndCompletesOnClose() throws IOException {
		ProgressBar bar = ProgressBar.builder().unit("B").renderer(renderer).build();
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (ProgressOutputStream out = bar.wrap(target)) {
			assertSame(bar, out.getProgressBar());
			out.write(1);
			out.write(DATA, 0, 100);
			out.write(DATA, 0, 0);
			out.write(DATA);
			assertEquals(ProgressState.RUNNING, bar.getState());
		}
		assertEquals(1 + 100 + DATA.length, bar.getCompleted());
		assertEquals(1 + 100 + DATA.length, target.size());
		assertEquals(ProgressState.COMPLETED, bar.getState());
		byte[] written = target.toByteArray();
		assertArrayEquals(Arrays.copyOfRange(DATA, 0, 100), Arrays.copyOfRange(written, 1, 101));
	}

	@Test
	public void outputStream_failures() {
		for (int mode = 0; mode < 2; mode++) {
			ProgressBar failing = bar();
			ProgressOutputStream out = failing.wrap(new BrokenOutputStream());
			final int m = mode;
			assertThrows(IOException.class, () -> {
				if (m == 0) {
					out.write(1);
				} else {
					out.write(new byte[3], 0, 3);
				}
			});
			assertEquals(ProgressState.FAILED, failing.getState());
		}
		assertThrows(IllegalArgumentException.class, () -> new ProgressOutputStream(null, bar()));
		assertThrows(IllegalArgumentException.class, () -> new ProgressOutputStream(new ByteArrayOutputStream(), null));
	}

	static class BrokenInputStream extends InputStream {
		@Override
		public int read() throws IOException {
			throw new IOException("broken");
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			throw new IOException("broken");
		}

		@Override
		public long skip(long n) throws IOException {
			throw new IOException("broken");
		}
	}

	static class BrokenOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			throw new IOException("broken");
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			throw new IOException("broken");
		}
	}

}
