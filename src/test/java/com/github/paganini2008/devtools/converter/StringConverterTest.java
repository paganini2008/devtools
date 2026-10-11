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
package com.github.paganini2008.devtools.converter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * StringConverterTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class StringConverterTest {

	@TempDir
	Path tempDir;

	private static SimpleDateFormat utcFormat(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf;
	}

	@Test
	public void testScalars() {
		StringConverter c = new StringConverter();
		UUID uuid = UUID.randomUUID();
		assertEquals(uuid.toString(), c.convertValue(uuid, null));
		assertEquals("true", c.convertValue(Boolean.TRUE, null));
		assertEquals("a", c.convertValue('a', null));
		assertEquals("1.5", c.convertValue(1.5d, null));
		assertEquals("3", c.convertValue(3, null));
		assertEquals("1.23", c.convertValue(1.234d, null));
		assertEquals("abc", c.convertValue(new StringBuilder("abc"), null));
		assertEquals("UTF-8", c.convertValue(StandardCharsets.UTF_8, null));
		assertEquals("en_US", c.convertValue(Locale.US, null));
		assertEquals("dft", c.convertValue(null, "dft"));
		assertEquals("dft", c.convertValue(new Object(), "dft"));
		c.setDecimalFormat(new DecimalFormat("0.000"));
		assertEquals("1.500", c.convertValue(1.5d, null));
	}

	@Test
	public void testDates() {
		StringConverter c = new StringConverter();
		c.setDateFormat(utcFormat("yyyy-MM-dd HH:mm:ss"));
		assertEquals("1970-01-01 00:00:01", c.convertValue(new Date(1000L), null));
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(2000L);
		assertEquals("1970-01-01 00:00:02", c.convertValue(cal, null));
	}

	@Test
	public void testArraysAndCollections() {
		StringConverter c = new StringConverter();
		assertEquals("abc", c.convertValue("abc".getBytes(StandardCharsets.UTF_8), null));
		assertEquals("abc", c.convertValue("abc".toCharArray(), null));
		assertEquals("1,2", c.convertValue(new short[] { 1, 2 }, null));
		assertEquals("1,2", c.convertValue(new int[] { 1, 2 }, null));
		assertEquals("1,2", c.convertValue(new long[] { 1, 2 }, null));
		assertTrue(c.convertValue(new float[] { 1.5f, 2 }, null).startsWith("1.5,"));
		assertTrue(c.convertValue(new double[] { 1.5d, 2 }, null).startsWith("1.5,"));
		assertEquals("true,false", c.convertValue(new boolean[] { true, false }, null));
		assertEquals("a,b", c.convertValue(new Object[] { "a", "b" }, null));
		assertEquals("a,b", c.convertValue(Arrays.asList("a", "b"), null));
		assertEquals("a,b", c.convertValue(Arrays.asList("a", "b").iterator(), null));
		assertEquals("a,b", c.convertValue(new Vector<String>(Arrays.asList("a", "b")).elements(), null));
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("k", "v");
		String mapped = c.convertValue(map, null);
		assertTrue(mapped.contains("k") && mapped.contains("v"), mapped);
		c.setDelimiter("|");
		assertEquals("1|2", c.convertValue(new int[] { 1, 2 }, null));
		c.setTrim(true);
		assertEquals("a|b", c.convertValue(new Object[] { " a ", "b" }, null));
	}

	@Test
	public void testStreams() throws Exception {
		StringConverter c = new StringConverter();
		c.setCharset(StandardCharsets.UTF_8);
		assertEquals("中文", c.convertValue(new ByteArrayInputStream("中文".getBytes(StandardCharsets.UTF_8)), null));
		assertEquals("abc", c.convertValue(new StringReader("abc"), null));
		Path file = tempDir.resolve("a.txt");
		Files.write(file, "hello".getBytes(StandardCharsets.UTF_8));
		URL url = file.toUri().toURL();
		assertEquals("hello", c.convertValue(url, null));
		assertEquals("dft", c.convertValue(tempDir.resolve("missing.txt").toUri().toURL(), "dft"));
		Clob clob = new SerialClob("clob-text".toCharArray());
		assertEquals("clob-text", c.convertValue(clob, null));
		assertEquals("dft", c.convertValue(new FailingInputStream(), "dft"));
		assertEquals("dft", c.convertValue(new FailingReader(), "dft"));
	}

	@Test
	public void testThreadSafeFormatting() throws Exception {
		final StringConverter c = new StringConverter();
		c.setDateFormat(utcFormat("yyyy-MM-dd HH:mm:ss"));
		final int threads = 8;
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		final CountDownLatch start = new CountDownLatch(1);
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		try {
			for (int t = 0; t < threads; t++) {
				final long base = t * 86400000L * 400;
				futures.add(executor.submit(() -> {
					start.await();
					SimpleDateFormat expected = utcFormat("yyyy-MM-dd HH:mm:ss");
					for (int i = 0; i < 300; i++) {
						Date d = new Date(base + i * 1000L);
						if (!expected.format(d).equals(c.convertValue(d, null))) {
							return false;
						}
					}
					return true;
				}));
			}
			start.countDown();
			for (Future<Boolean> f : futures) {
				assertTrue(f.get(10, TimeUnit.SECONDS));
			}
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void testStringArrayConverter() {
		StringArrayConverter c = new StringArrayConverter();
		assertArrayEquals(new String[] { "a", "b" }, c.convertValue("a,b", null));
		assertNull(c.convertValue(" ", null));
		assertArrayEquals(new String[] { "true", "false" }, c.convertValue(new boolean[] { true, false }, null));
		assertArrayEquals(new String[] { "a", "b" }, c.convertValue(new char[] { 'a', 'b' }, null));
		assertArrayEquals(new String[] { "1", "2" }, c.convertValue(new byte[] { 1, 2 }, null));
		assertArrayEquals(new String[] { "1", "2" }, c.convertValue(new short[] { 1, 2 }, null));
		assertArrayEquals(new String[] { "1", "2" }, c.convertValue(new int[] { 1, 2 }, null));
		assertArrayEquals(new String[] { "1", "2" }, c.convertValue(new long[] { 1, 2 }, null));
		assertArrayEquals(new String[] { "1.5", "2" }, c.convertValue(new float[] { 1.5f, 2 }, null));
		assertArrayEquals(new String[] { "1.5", "2" }, c.convertValue(new double[] { 1.5d, 2 }, null));
		assertArrayEquals(new String[] { "1.5", "2" }, c.convertValue(new Number[] { 1.5d, 2 }, null));
		// Integer[] must use the most specific Number[] converter rather than Object[]
		assertArrayEquals(new String[] { "1.23" }, c.convertValue(new Double[] { 1.234d }, null));
		assertArrayEquals(new String[] { "a", "1" }, c.convertValue(new Object[] { "a", 1 }, null));
		c.setDateFormat(utcFormat("yyyy-MM-dd"));
		assertArrayEquals(new String[] { "1970-01-01" }, c.convertValue(new Date[] { new Date(1000L) }, null));
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(86400000L);
		assertArrayEquals(new String[] { "1970-01-02" }, c.convertValue(new Calendar[] { cal }, null));
		assertArrayEquals(new String[] { "a", "b" }, c.convertValue(Arrays.asList("a", "b"), null));
		assertArrayEquals(new String[] { "a", "b" }, c.convertValue(Arrays.asList("a", "b").iterator(), null));
		assertArrayEquals(new String[] { "a" }, c.convertValue(Collections.enumeration(Arrays.asList("a")), null));
		c.setDecimalFormat(new DecimalFormat("0.0"));
		assertArrayEquals(new String[] { "2.0" }, c.convertValue(new int[] { 2 }, null));
		c.setDelimiter(";");
		assertArrayEquals(new String[] { "a", "b" }, c.convertValue("a;b", null));
		c.setTrim(true);
		assertArrayEquals(new String[] { "a" }, c.convertValue(new Object[] { " a " }, null));
	}

	@Test
	public void testByteArrayConverter() throws Exception {
		ByteArrayConverter c = new ByteArrayConverter();
		byte[] expected = { 1, 2, 3 };
		for (Object input : new Object[] { new short[] { 1, 2, 3 }, new int[] { 1, 2, 3 }, new long[] { 1, 2, 3 },
				new float[] { 1, 2, 3 }, new double[] { 1, 2, 3 }, new char[] { 1, 2, 3 }, new Number[] { 1, 2, 3 },
				new String[] { "1", "2", "3" }, ByteBuffer.wrap(expected), new ByteArrayInputStream(expected),
				new SerialBlob(expected) }) {
			assertArrayEquals(expected, c.convertValue(input, null), input.getClass().getSimpleName());
		}
		assertArrayEquals(new byte[] { 1, 0 }, c.convertValue(new boolean[] { true, false }, null));
		c.setCharset(StandardCharsets.UTF_8);
		assertArrayEquals("中文".getBytes(StandardCharsets.UTF_8), c.convertValue("中文", null));
		assertArrayEquals("中文".getBytes(StandardCharsets.UTF_8), c.convertValue(new StringReader("中文"), null));
		c.setCharset(StandardCharsets.UTF_16BE);
		assertArrayEquals("ab".getBytes(StandardCharsets.UTF_16BE), c.convertValue("ab", null));

		Path file = tempDir.resolve("b.bin");
		Files.write(file, expected);
		assertArrayEquals(expected, c.convertValue(file.toUri().toURL(), null));
		byte[] dft = {};
		assertSame(dft, c.convertValue(tempDir.resolve("missing").toUri().toURL(), dft));
		assertSame(dft, c.convertValue(new FailingInputStream(), dft));
		assertSame(dft, c.convertValue(new FailingReader(), dft));
		assertSame(dft, c.convertValue(new FailingBlob(), dft));
		assertSame(dft, c.convertValue(null, dft));
	}

	static class FailingInputStream extends InputStream {
		@Override
		public int read() throws IOException {
			throw new IOException("boom");
		}
	}

	static class FailingReader extends Reader {
		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			throw new IOException("boom");
		}

		@Override
		public void close() {
		}
	}

	static class FailingBlob extends SerialBlob {
		private static final long serialVersionUID = 1L;

		FailingBlob() throws SQLException {
			super(new byte[0]);
		}

		@Override
		public InputStream getBinaryStream() throws javax.sql.rowset.serial.SerialException {
			throw new javax.sql.rowset.serial.SerialException("boom");
		}
	}


}
