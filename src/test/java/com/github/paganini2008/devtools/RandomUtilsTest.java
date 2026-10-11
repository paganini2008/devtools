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
package com.github.paganini2008.devtools;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * RandomUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class RandomUtilsTest {

	@Test
	public void testRandomChoice() {
		assertEquals((byte) 7, RandomUtils.randomChoice(new byte[] { 7 }));
		assertEquals((byte) 1, RandomUtils.randomChoice(new byte[] { 1, 2, 3 }, 1));
		assertEquals((short) 7, RandomUtils.randomChoice(new short[] { 7 }));
		assertEquals((short) 1, RandomUtils.randomChoice(new short[] { 1, 2 }, 1));
		assertEquals(7, RandomUtils.randomChoice(new int[] { 7 }));
		assertEquals(1, RandomUtils.randomChoice(new int[] { 1, 2 }, 1));
		assertEquals(7L, RandomUtils.randomChoice(new long[] { 7L }));
		assertEquals(1L, RandomUtils.randomChoice(new long[] { 1L, 2L }, 1));
		assertEquals(7F, RandomUtils.randomChoice(new float[] { 7F }));
		assertEquals(1F, RandomUtils.randomChoice(new float[] { 1F, 2F }, 1));
		assertEquals(7D, RandomUtils.randomChoice(new double[] { 7D }));
		assertEquals(1D, RandomUtils.randomChoice(new double[] { 1D, 2D }, 1));
		assertEquals('x', RandomUtils.randomChoice(new char[] { 'x' }));
		assertEquals('a', RandomUtils.randomChoice(new char[] { 'a', 'b' }, 1));
		assertTrue(RandomUtils.randomChoice(new boolean[] { true }));
		assertFalse(RandomUtils.randomChoice(new boolean[] { false, true }, 1));
		assertEquals("a", RandomUtils.randomChoice(new String[] { "a" }));
		assertEquals("a", RandomUtils.randomChoice(new String[] { "a", "b" }, 1));
		assertEquals("a", RandomUtils.randomChoice(Collections.singletonList("a")));
		assertEquals("a", RandomUtils.randomChoice(Arrays.asList("a", "b"), 1));
		// length larger than the array is clamped
		String s = RandomUtils.randomChoice(new String[] { "a", "b" }, 100);
		assertTrue(s.equals("a") || s.equals("b"));
	}

	@RepeatedTest(5)
	public void testPrimitiveRanges() {
		RandomUtils.randomBoolean();
		byte b = RandomUtils.randomByte((byte) 1, (byte) 5);
		assertTrue(b >= 1 && b < 5);
		assertEquals('a', RandomUtils.randomChar('a', 'b'));
		short sh = RandomUtils.randomShort((short) 10, (short) 20);
		assertTrue(sh >= 10 && sh < 20);
		assertTrue(RandomUtils.randomInt() >= 0);
		assertEquals(3, RandomUtils.randomInt(3, 3));
		long l = RandomUtils.randomLong(-5L, 5L);
		assertTrue(l >= -5L && l < 5L);
		assertEquals(9L, RandomUtils.randomLong(9L, 9L));
		float f = RandomUtils.randomFloat();
		assertTrue(f >= 0F && f < 1F);
		assertEquals(2F, RandomUtils.randomFloat(2F, 2F));
		f = RandomUtils.randomFloat(1F, 2F, 2);
		assertTrue(f >= 1F && f <= 2F);
		double d = RandomUtils.randomDouble();
		assertTrue(d >= 0D && d < 1D);
		assertEquals(2D, RandomUtils.randomDouble(2D, 2D));
		d = RandomUtils.randomDouble(1D, 2D, 3);
		assertTrue(d >= 1D && d <= 2D);
		assertEquals(d, BigDecimal.valueOf(d).setScale(3, java.math.RoundingMode.HALF_UP).doubleValue());
	}

	@RepeatedTest(5)
	public void testDistinctArraysIncludeZero() {
		int[] ints = RandomUtils.randomInts(5, 0, 5);
		Arrays.sort(ints);
		assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, ints);
		short[] shorts = RandomUtils.randomShorts(3, (short) 0, (short) 3);
		Arrays.sort(shorts);
		assertArrayEquals(new short[] { 0, 1, 2 }, shorts);
		long[] longs = RandomUtils.randomLongs(4, 0L, 4L);
		Arrays.sort(longs);
		assertArrayEquals(new long[] { 0L, 1L, 2L, 3L }, longs);
	}

	@Test
	public void testArraysWithDuplicatesAllowed() {
		assertArrayEquals(new int[] { 1, 1, 1 }, RandomUtils.randomInts(3, 1, 1, true));
		assertArrayEquals(new short[] { 1, 1 }, RandomUtils.randomShorts(2, (short) 1, (short) 1, true));
		assertArrayEquals(new long[] { 1L, 1L }, RandomUtils.randomLongs(2, 1L, 1L, true));
		assertArrayEquals(new float[] { 1F, 1F }, RandomUtils.randomFloats(2, 1F, 1F, true));
		assertArrayEquals(new double[] { 1D, 1D }, RandomUtils.randomDoubles(2, 1D, 1D, true));
	}

	@Test
	public void testDistinctArraysRejectTooSmallRange() {
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.randomInts(6, 0, 5));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.randomShorts(4, (short) 0, (short) 3));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.randomLongs(5, 0L, 4L));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.randomFloats(2, 1F, 1F));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.randomDoubles(2, 1D, 1D));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.defineInts(10, 1));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.defineLongs(10, 1));
	}

	@Test
	public void testFloatAndDoubleArrays() {
		float[] floats = RandomUtils.randomFloats(10, 0F, 1F);
		assertEquals(10, distinct(floats).size());
		for (float f : floats) {
			assertTrue(f >= 0F && f < 1F);
		}
		double[] doubles = RandomUtils.randomDoubles(10, 0D, 1D);
		assertEquals(10, distinct(doubles).size());
		float[] defined = RandomUtils.defineFloats(5, 3, 1);
		assertEquals(5, distinct(defined).size());
		for (float f : defined) {
			assertTrue(f >= 100F && f <= 1000F);
		}
		double[] definedDoubles = RandomUtils.defineDoubles(5, 3, 2);
		assertEquals(5, distinct(definedDoubles).size());
		for (double d : definedDoubles) {
			assertTrue(d >= 100D && d <= 1000D);
		}
		float f = RandomUtils.defineFloat(2, 1);
		assertTrue(f >= 10F && f <= 100F);
		double d = RandomUtils.defineDouble(2, 1);
		assertTrue(d >= 10D && d <= 100D);
	}

	@RepeatedTest(3)
	public void testDefineIntegers() {
		for (int precision = 1; precision <= 9; precision++) {
			int value = RandomUtils.defineInt(precision);
			assertEquals(precision, String.valueOf(value).length());
		}
		for (int precision = 1; precision <= 18; precision++) {
			long value = RandomUtils.defineLong(precision);
			assertEquals(precision, String.valueOf(value).length());
		}
		int[] ints = RandomUtils.defineInts(9, 1);
		Arrays.sort(ints);
		assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, ints);
		long[] longs = RandomUtils.defineLongs(9, 1);
		Arrays.sort(longs);
		assertArrayEquals(new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, longs);
		assertEquals(3, RandomUtils.defineInts(3, 1, true).length);
		assertEquals(0, RandomUtils.defineInt(0));
		assertEquals(0L, RandomUtils.defineLong(0));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.defineInt(10));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.defineLong(19));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.defineInts(2, 0));
	}

	@RepeatedTest(3)
	public void testBigNumbers() {
		BigInteger bi = RandomUtils.randomBigInteger(10L, 20L);
		assertTrue(bi.compareTo(BigInteger.valueOf(10)) >= 0 && bi.compareTo(BigInteger.valueOf(20)) < 0);
		BigDecimal bd = RandomUtils.randomBigDecimal(1.0D, 2.0D, 5);
		assertTrue(bd.compareTo(BigDecimal.ONE) >= 0 && bd.compareTo(BigDecimal.valueOf(2)) <= 0);
		bd = RandomUtils.randomBigDecimal(3);
		assertEquals(3, bd.scale());
		assertTrue(bd.compareTo(BigDecimal.ZERO) >= 0 && bd.compareTo(BigDecimal.ONE) < 0);
		bd = RandomUtils.defineBigDecimal(3, 2);
		assertEquals(2, bd.scale());
		assertTrue(bd.compareTo(BigDecimal.valueOf(100)) >= 0 && bd.compareTo(BigDecimal.valueOf(1000)) < 0);
		BigInteger defined = RandomUtils.defineBigInteger(4);
		assertEquals(4, defined.toString().length());
		BigDecimal[] decimals = RandomUtils.defineBigDecimals(5, 2, 0);
		assertEquals(5, new HashSet<BigDecimal>(Arrays.asList(decimals)).size());
		BigInteger[] integers = RandomUtils.defineBigIntegers(9, 1);
		Set<BigInteger> set = new HashSet<BigInteger>(Arrays.asList(integers));
		assertEquals(9, set.size());
		assertFalse(set.contains(BigInteger.ZERO));
	}

	@Test
	public void testRandomBigDecimalWithZeroScale() {
		Set<BigDecimal> seen = new HashSet<BigDecimal>();
		for (int i = 0; i < 200; i++) {
			BigDecimal bd = RandomUtils.randomBigDecimal(BigDecimal.valueOf(10), BigDecimal.valueOf(20), 0);
			assertEquals(0, bd.scale());
			assertTrue(bd.compareTo(BigDecimal.valueOf(10)) >= 0 && bd.compareTo(BigDecimal.valueOf(20)) < 0);
			seen.add(bd);
		}
		// used to always return "from"
		assertTrue(seen.size() > 1);
		BigDecimal bd = RandomUtils.randomBigDecimal(BigDecimal.valueOf(100), BigDecimal.valueOf(1000), -1);
		assertTrue(bd.compareTo(BigDecimal.valueOf(100)) >= 0 && bd.compareTo(BigDecimal.valueOf(1000)) < 0);
		assertEquals(BigDecimal.valueOf(5), RandomUtils.randomBigDecimal(BigDecimal.valueOf(5), BigDecimal.valueOf(5), 0));
	}

	@Test
	public void testRandomEnum() {
		assertNotNull(RandomUtils.randomEnum(TimeUnit.class));
		assertEquals(Single.ONLY, RandomUtils.randomEnum(Single.class));
	}

	@RepeatedTest(5)
	public void testTimeFields() {
		int year = RandomUtils.randomYear(2000, 2001);
		assertTrue(year == 2000 || year == 2001);
		int month = RandomUtils.randomMonth(12, 12);
		assertEquals(12, month);
		Month m = RandomUtils.randomMonth(Month.MARCH, Month.MAY);
		assertTrue(m.compareTo(Month.MARCH) >= 0 && m.compareTo(Month.MAY) <= 0);
		int dayOfYear = RandomUtils.randomDayOfYear(Year.of(2021), 360, 400);
		assertTrue(dayOfYear >= 360 && dayOfYear <= 365);
		int dayOfMonth = RandomUtils.randomDayOfMonth(YearMonth.of(2021, 2), 27, 31);
		assertTrue(dayOfMonth == 27 || dayOfMonth == 28);
		dayOfMonth = RandomUtils.randomDayOfMonth(2020, 2, 29, 31);
		assertEquals(29, dayOfMonth);
		assertEquals(5, RandomUtils.randomHourOfDay(5, 5));
		int ms = RandomUtils.randomMinuteOrSecond(0, 59);
		assertTrue(ms >= 0 && ms <= 59);
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.randomMonth(0, 12));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.randomHourOfDay(0, 24));
		assertThrows(IllegalArgumentException.class, () -> RandomUtils.randomMinuteOrSecond(-1, 2));
	}

	private static Set<Float> distinct(float[] array) {
		Set<Float> set = new HashSet<Float>();
		for (float f : array) {
			set.add(f);
		}
		return set;
	}

	private static Set<Double> distinct(double[] array) {
		Set<Double> set = new HashSet<Double>();
		for (double d : array) {
			set.add(d);
		}
		return set;
	}

	static enum Single {
		ONLY;
	}

}
