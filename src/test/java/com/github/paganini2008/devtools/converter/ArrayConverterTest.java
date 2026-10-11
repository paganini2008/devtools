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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

/**
 * ArrayConverterTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ArrayConverterTest {

	private static final byte[] BYTES = { 1, 2, 3 };
	private static final short[] SHORTS = { 1, 2, 3 };
	private static final int[] INTS = { 1, 2, 3 };
	private static final long[] LONGS = { 1, 2, 3 };
	private static final float[] FLOATS = { 1, 2, 3 };
	private static final double[] DOUBLES = { 1, 2, 3 };
	private static final char[] CHARS = { 1, 2, 3 };
	private static final boolean[] BOOLS = { true, false, true };
	private static final Number[] NUMBERS = { 1, 2L, 3.0d };
	private static final String[] STRINGS = { "1", "2", "3" };

	@Test
	public void testCharacterObjectArrayConverter() {
		CharacterObjectArrayConverter c = new CharacterObjectArrayConverter();
		assertArrayEquals(new Character[] { 'a', 'b' }, c.convertValue(new char[] { 'a', 'b' }, null));
		assertArrayEquals(new Character[] { 'a', 'b' }, c.convertValue("ab", null));
		assertArrayEquals(new Character[] { 'a', 'b' }, c.convertValue(new StringBuilder("ab"), null));
		assertNull(c.convertValue(null, null));
	}

	@Test
	public void testCharArrayConverter() {
		CharArrayConverter c = new CharArrayConverter();
		assertArrayEquals("abc".toCharArray(), c.convertValue("abc", null));
		assertArrayEquals("abc".toCharArray(), c.convertValue(new StringReader("abc"), null));
		c.setCharset(StandardCharsets.UTF_8);
		assertArrayEquals("中文".toCharArray(), c.convertValue(new ByteArrayInputStream("中文".getBytes(StandardCharsets.UTF_8)), null));
		char[] dft = { 'x' };
		assertSame(dft, c.convertValue(new FailingInputStream(), dft));
		assertSame(dft, c.convertValue(new FailingReader(), dft));
	}

	@Test
	public void testByteObjectArrayConverter() {
		ByteObjectArrayConverter c = new ByteObjectArrayConverter();
		Byte[] expected = { 1, 2, 3 };
		assertArrayEquals(expected, c.convertValue(BYTES, null));
		assertArrayEquals(expected, c.convertValue(NUMBERS, null));
		assertArrayEquals(expected, c.convertValue(STRINGS, null));
		assertArrayEquals(expected, c.convertValue(CHARS, null));
		assertArrayEquals(new Byte[] { 1, 0, 1 }, c.convertValue(BOOLS, null));
		c.setCharset(StandardCharsets.ISO_8859_1);
		assertArrayEquals(new Byte[] { 'a', 'b' }, c.convertValue("ab", null));
		assertNull(c.convertValue(" ", null));
	}

	@Test
	public void testShortObjectArrayConverter() {
		ShortObjectArrayConverter c = new ShortObjectArrayConverter();
		Short[] expected = { 1, 2, 3 };
		assertArrayEquals(expected, c.convertValue("1,2,3", null));
		assertArrayEquals(expected, c.convertValue(NUMBERS, null));
		assertArrayEquals(expected, c.convertValue(STRINGS, null));
		assertArrayEquals(expected, c.convertValue(BYTES, null));
		assertArrayEquals(expected, c.convertValue(SHORTS, null));
		assertNull(c.convertValue("", null));
	}

	@Test
	public void testIntegerObjectArrayConverter() {
		IntegerObjectArrayConverter c = new IntegerObjectArrayConverter();
		Integer[] expected = { 1, 2, 3 };
		assertArrayEquals(expected, c.convertValue("1,2,3", null));
		assertArrayEquals(expected, c.convertValue(NUMBERS, null));
		assertArrayEquals(expected, c.convertValue(STRINGS, null));
		assertArrayEquals(expected, c.convertValue(BYTES, null));
		assertArrayEquals(expected, c.convertValue(SHORTS, null));
		assertArrayEquals(expected, c.convertValue(CHARS, null));
		assertArrayEquals(expected, c.convertValue(INTS, null));
		c.setDelimiter(";");
		assertArrayEquals(expected, c.convertValue("1;2;3", null));
		assertNull(c.convertValue(" ", null));
	}

	@Test
	public void testLongObjectArrayConverter() {
		LongObjectArrayConverter c = new LongObjectArrayConverter();
		Long[] expected = { 1L, 2L, 3L };
		assertArrayEquals(expected, c.convertValue("1,2,3", null));
		assertArrayEquals(expected, c.convertValue(NUMBERS, null));
		assertArrayEquals(expected, c.convertValue(STRINGS, null));
		assertArrayEquals(expected, c.convertValue(BYTES, null));
		assertArrayEquals(expected, c.convertValue(SHORTS, null));
		assertArrayEquals(expected, c.convertValue(CHARS, null));
		assertArrayEquals(expected, c.convertValue(INTS, null));
		assertArrayEquals(expected, c.convertValue(LONGS, null));
		assertArrayEquals(new Long[] { 1L, 0L, 1L }, c.convertValue(BOOLS, null));
		assertArrayEquals(new Long[] { 1L, 0L }, c.convertValue(new Boolean[] { true, false }, null));
		assertArrayEquals(new Long[] { 97L }, c.convertValue(new Character[] { 'a' }, null));
		assertArrayEquals(new Long[] { 1000L, 2000L }, c.convertValue(new Date[] { new Date(1000L), new Date(2000L) }, null));
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(5000L);
		assertArrayEquals(new Long[] { 5000L }, c.convertValue(new Calendar[] { cal }, null));
		c.setDelimiter("|");
		assertArrayEquals(expected, c.convertValue("1|2|3", null));
		assertNull(c.convertValue("", null));
	}

	@Test
	public void testFloatObjectArrayConverter() {
		FloatObjectArrayConverter c = new FloatObjectArrayConverter();
		Float[] expected = { 1f, 2f, 3f };
		assertArrayEquals(expected, c.convertValue("1,2,3", null));
		assertArrayEquals(expected, c.convertValue(NUMBERS, null));
		assertArrayEquals(expected, c.convertValue(STRINGS, null));
		assertArrayEquals(expected, c.convertValue(BYTES, null));
		assertArrayEquals(expected, c.convertValue(SHORTS, null));
		assertArrayEquals(expected, c.convertValue(CHARS, null));
		assertArrayEquals(expected, c.convertValue(INTS, null));
		assertArrayEquals(expected, c.convertValue(LONGS, null));
		assertArrayEquals(expected, c.convertValue(FLOATS, null));
		assertNull(c.convertValue("", null));
	}

	@Test
	public void testDoubleObjectArrayConverter() {
		DoubleObjectArrayConverter c = new DoubleObjectArrayConverter();
		Double[] expected = { 1d, 2d, 3d };
		assertArrayEquals(expected, c.convertValue("1,2,3", null));
		assertArrayEquals(expected, c.convertValue(NUMBERS, null));
		assertArrayEquals(expected, c.convertValue(STRINGS, null));
		assertArrayEquals(expected, c.convertValue(BYTES, null));
		assertArrayEquals(expected, c.convertValue(SHORTS, null));
		assertArrayEquals(expected, c.convertValue(CHARS, null));
		assertArrayEquals(expected, c.convertValue(INTS, null));
		assertArrayEquals(expected, c.convertValue(LONGS, null));
		assertArrayEquals(expected, c.convertValue(FLOATS, null));
		assertArrayEquals(expected, c.convertValue(DOUBLES, null));
		c.setDelimiter(" ");
		assertArrayEquals(expected, c.convertValue("1 2 3", null));
		assertNull(c.convertValue("", null));
	}

	@Test
	public void testBigIntegerArrayConverter() {
		BigIntegerArrayConverter c = new BigIntegerArrayConverter();
		BigInteger[] expected = { BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3) };
		assertArrayEquals(expected, c.convertValue(BYTES, null));
		assertArrayEquals(expected, c.convertValue(SHORTS, null));
		assertArrayEquals(expected, c.convertValue(INTS, null));
		assertArrayEquals(expected, c.convertValue(LONGS, null));
		assertArrayEquals(expected, c.convertValue(new Number[] { 1, 2, 3 }, null));
		assertArrayEquals(expected, c.convertValue(STRINGS, null));
		assertNull(c.convertValue(null, null));
	}

	@Test
	public void testBigDecimalArrayConverter() {
		BigDecimalArrayConverter c = new BigDecimalArrayConverter();
		Object[][] inputs = { null };
		for (Object input : new Object[] { BYTES, SHORTS, INTS, LONGS, FLOATS, DOUBLES, NUMBERS, STRINGS }) {
			BigDecimal[] result = c.convertValue(input, null);
			assertEquals(3, result.length);
			for (int i = 0; i < 3; i++) {
				assertEquals(0, BigDecimal.valueOf(i + 1).compareTo(result[i]), input.getClass().getSimpleName());
			}
		}
		assertNull(inputs[0]);
	}

	@Test
	public void testIntArrayConverter() {
		IntArrayConverter c = new IntArrayConverter();
		for (Object input : new Object[] { "1,2,3", NUMBERS, STRINGS, CHARS, BYTES, SHORTS, LONGS, FLOATS, DOUBLES }) {
			assertArrayEquals(INTS, c.convertValue(input, null), input.getClass().getSimpleName());
		}
		assertArrayEquals(new int[] { 1, 0, 1 }, c.convertValue(BOOLS, null));
		assertNull(c.convertValue("", null));
	}

	@Test
	public void testLongArrayConverter() {
		LongArrayConverter c = new LongArrayConverter();
		for (Object input : new Object[] { "1,2,3", NUMBERS, STRINGS, CHARS, BYTES, SHORTS, INTS, FLOATS, DOUBLES }) {
			assertArrayEquals(LONGS, c.convertValue(input, null), input.getClass().getSimpleName());
		}
		assertArrayEquals(new long[] { 1, 0, 1 }, c.convertValue(BOOLS, null));
		c.setDelimiter(";");
		assertArrayEquals(LONGS, c.convertValue("1;2;3", null));
		assertNull(c.convertValue("", null));
	}

	@Test
	public void testShortArrayConverter() {
		ShortArrayConverter c = new ShortArrayConverter();
		for (Object input : new Object[] { "1,2,3", NUMBERS, STRINGS, CHARS, BYTES, INTS, LONGS, FLOATS, DOUBLES }) {
			assertArrayEquals(SHORTS, c.convertValue(input, null), input.getClass().getSimpleName());
		}
		assertArrayEquals(new short[] { 1, 0, 1 }, c.convertValue(BOOLS, null));
		c.setDelimiter(";");
		assertArrayEquals(SHORTS, c.convertValue("1;2;3", null));
		assertNull(c.convertValue("", null));
	}

	@Test
	public void testFloatArrayConverter() {
		FloatArrayConverter c = new FloatArrayConverter();
		for (Object input : new Object[] { "1,2,3", NUMBERS, STRINGS, CHARS, BYTES, INTS, LONGS, DOUBLES }) {
			assertArrayEquals(FLOATS, c.convertValue(input, null), input.getClass().getSimpleName());
		}
		assertArrayEquals(new float[] { 1, 0, 1 }, c.convertValue(BOOLS, null));
		c.setDelimiter(";");
		assertArrayEquals(FLOATS, c.convertValue("1;2;3", null));
		assertNull(c.convertValue("", null));
	}

	@Test
	public void testDoubleArrayConverter() {
		DoubleArrayConverter c = new DoubleArrayConverter();
		for (Object input : new Object[] { "1,2,3", NUMBERS, STRINGS, CHARS, BYTES, INTS, LONGS, FLOATS }) {
			assertArrayEquals(DOUBLES, c.convertValue(input, null), input.getClass().getSimpleName());
		}
		assertArrayEquals(new double[] { 1, 0, 1 }, c.convertValue(BOOLS, null));
		c.setDelimiter(";");
		assertArrayEquals(DOUBLES, c.convertValue("1;2;3", null));
		assertNull(c.convertValue("", null));
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

}
