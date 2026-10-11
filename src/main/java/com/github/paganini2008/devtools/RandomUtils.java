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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.devtools.math.BigDecimalUtils;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.time.TimeAssert;

/**
 * RandomUtils
 * 
 * Random value generators based on {@link java.util.concurrent.ThreadLocalRandom}. Unless stated otherwise,
 * ranges are {@code [from, to)}, and {@code from == to} returns {@code from}. Methods that generate arrays
 * of distinct values throw {@link IllegalArgumentException} when the range is too small.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class RandomUtils {

	/**
	 * Returns a random element of the array.
	 */
	public static byte randomChoice(byte[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	/**
	 * Returns a random element among the first {@code length} elements of the array.
	 */
	public static byte randomChoice(byte[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	/**
	 * Returns a random element of the array.
	 */
	public static short randomChoice(short[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	/**
	 * Returns a random element among the first {@code length} elements of the array.
	 */
	public static short randomChoice(short[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	/**
	 * Returns a random element of the array.
	 */
	public static int randomChoice(int[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	/**
	 * Returns a random element among the first {@code length} elements of the array.
	 */
	public static int randomChoice(int[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	/**
	 * Returns a random element of the array.
	 */
	public static long randomChoice(long[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	/**
	 * Returns a random element among the first {@code length} elements of the array.
	 */
	public static long randomChoice(long[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	/**
	 * Returns a random element of the array.
	 */
	public static float randomChoice(float[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	/**
	 * Returns a random element among the first {@code length} elements of the array.
	 */
	public static float randomChoice(float[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	/**
	 * Returns a random element of the array.
	 */
	public static double randomChoice(double[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	/**
	 * Returns a random element among the first {@code length} elements of the array.
	 */
	public static double randomChoice(double[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	/**
	 * Returns a random element of the array.
	 */
	public static char randomChoice(char[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	/**
	 * Returns a random element among the first {@code length} elements of the array.
	 */
	public static char randomChoice(char[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	/**
	 * Returns a random element of the array.
	 */
	public static boolean randomChoice(boolean[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	/**
	 * Returns a random element among the first {@code length} elements of the array.
	 */
	public static boolean randomChoice(boolean[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	/**
	 * Returns a random element of the array.
	 */
	public static <T> T randomChoice(T[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	/**
	 * Returns a random element among the first {@code length} elements of the array.
	 */
	public static <T> T randomChoice(T[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	/**
	 * Returns a random element of the list.
	 */
	public static <T> T randomChoice(List<T> list) {
		return list.get(randomInt(0, list.size()));
	}

	/**
	 * Returns a random element among the first {@code length} elements of the list.
	 */
	public static <T> T randomChoice(List<T> list, int length) {
		return list.get(randomInt(0, Math.min(length, list.size())));
	}

	/**
	 * Returns a random boolean.
	 */
	public static boolean randomBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	/**
	 * Returns a random value in {@code [from, to)}.
	 */
	public static byte randomByte(byte from, byte to) {
		return (byte) randomInt(from, to);
	}

	/**
	 * Returns a random value in {@code [from, to)}.
	 */
	public static char randomChar(char from, char to) {
		return (char) randomInt(from, to);
	}

	/**
	 * Returns a random value in {@code [from, to)}.
	 */
	public static short randomShort(short from, short to) {
		return (short) randomInt(from, to);
	}

	/**
	 * Returns {@code length} distinct random values in {@code [from, to)}.
	 */
	public static short[] randomShorts(int length, short from, short to) {
		return randomShorts(length, from, to, false);
	}

	/**
	 * Returns {@code length} random values in {@code [from, to)}.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 * @throws IllegalArgumentException if distinct values are required but the range is too small
	 */
	public static short[] randomShorts(int length, short from, short to, boolean existsAllowed) {
		checkDistinct(length, existsAllowed, Math.max(1L, (long) to - from));
		short[] results = new short[length];
		short s;
		for (int i = 0; i < length; i++) {
			do {
				s = randomShort(from, to);
			} while (!existsAllowed && contains(results, i, s));
			results[i] = s;
		}
		return results;
	}

	/**
	 * Returns a random non-negative int.
	 */
	public static int randomInt() {
		return randomInt(0, Integer.MAX_VALUE);
	}

	/**
	 * Returns a random int with exactly {@code precision} digits, e.g. 3 gives a value in {@code [100, 1000)}.
	 *
	 * @throws IllegalArgumentException if the precision exceeds the int range
	 */
	public static int defineInt(int precision) {
		if (Math.log10(Integer.MAX_VALUE) <= precision) {
			throw new IllegalArgumentException("Exceed maximum integer value");
		}
		int from = (int) Math.pow(10, precision - 1);
		int to = (int) Math.pow(10, precision);
		return randomInt(from, to);
	}

	/**
	 * Returns {@code length} distinct random values with {@code precision} digits.
	 */
	public static int[] defineInts(int length, int precision) {
		return defineInts(length, precision, false);
	}

	/**
	 * Returns {@code length} random values with {@code precision} digits.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 */
	public static int[] defineInts(int length, int precision, boolean existsAllowed) {
		checkDistinct(length, existsAllowed, precision <= 0 ? 1L : (precision < 10 ? 9 * (long) Math.pow(10, precision - 1) : Long.MAX_VALUE));
		int[] results = new int[length];
		int a;
		for (int i = 0; i < length; i++) {
			do {
				a = defineInt(precision);
			} while (!existsAllowed && contains(results, i, a));
			results[i] = a;
		}
		return results;
	}

	/**
	 * Returns a random value in {@code [from, to)}.
	 */
	public static int randomInt(int from, int to) {
		return from != to ? ThreadLocalRandom.current().nextInt(from, to) : from;
	}

	/**
	 * Returns {@code length} distinct random values in {@code [from, to)}.
	 */
	public static int[] randomInts(int length, int from, int to) {
		return randomInts(length, from, to, false);
	}

	/**
	 * Returns {@code length} random values in {@code [from, to)}.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 * @throws IllegalArgumentException if distinct values are required but the range is too small
	 */
	public static int[] randomInts(int length, int from, int to, boolean existsAllowed) {
		checkDistinct(length, existsAllowed, Math.max(1L, (long) to - from));
		int[] results = new int[length];
		int a;
		for (int i = 0; i < length; i++) {
			do {
				a = randomInt(from, to);
			} while (!existsAllowed && contains(results, i, a));
			results[i] = a;
		}
		return results;
	}

	/**
	 * Returns a random long with exactly {@code precision} digits.
	 *
	 * @throws IllegalArgumentException if the precision exceeds the long range
	 */
	public static long defineLong(int precision) {
		if (Math.log10(Long.MAX_VALUE) <= precision) {
			throw new IllegalArgumentException("Exceed maximum long value");
		}
		long from = (long) Math.pow(10, precision - 1);
		long to = (long) Math.pow(10, precision);
		return randomLong(from, to);
	}

	/**
	 * Returns {@code length} distinct random values with {@code precision} digits.
	 */
	public static long[] defineLongs(int length, int precision) {
		return defineLongs(length, precision, false);
	}

	/**
	 * Returns {@code length} random values with {@code precision} digits.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 */
	public static long[] defineLongs(int length, int precision, boolean existsAllowed) {
		checkDistinct(length, existsAllowed, precision <= 0 ? 1L : (precision < 19 ? 9 * (long) Math.pow(10, precision - 1) : Long.MAX_VALUE));
		long[] results = new long[length];
		long l;
		for (int i = 0; i < length; i++) {
			do {
				l = defineLong(precision);
			} while (!existsAllowed && contains(results, i, l));
			results[i] = l;
		}
		return results;
	}

	/**
	 * Returns a random value in {@code [from, to)}.
	 */
	public static long randomLong(long from, long to) {
		return from != to ? ThreadLocalRandom.current().nextLong(from, to) : from;
	}

	/**
	 * Returns {@code length} distinct random values in {@code [from, to)}.
	 */
	public static long[] randomLongs(int length, long from, long to) {
		return randomLongs(length, from, to, false);
	}

	/**
	 * Returns {@code length} random values in {@code [from, to)}.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 * @throws IllegalArgumentException if distinct values are required but the range is too small
	 */
	public static long[] randomLongs(int length, long from, long to, boolean existsAllowed) {
		checkDistinct(length, existsAllowed, from == to ? 1L : (to - from > 0 ? to - from : Long.MAX_VALUE));
		long[] results = new long[length];
		long l;
		for (int i = 0; i < length; i++) {
			do {
				l = randomLong(from, to);
			} while (!existsAllowed && contains(results, i, l));
			results[i] = l;
		}
		return results;
	}

	/**
	 * Returns a random float in {@code [0, 1)}.
	 */
	public static float randomFloat() {
		return ThreadLocalRandom.current().nextFloat();
	}

	/**
	 * Returns a random value in {@code [from, to)}.
	 */
	public static float randomFloat(float from, float to) {
		return from != to ? randomFloat() * (to - from) + from : from;
	}

	/**
	 * Returns a random value in {@code [from, to)} rounded to {@code scale} decimal places.
	 */
	public static float randomFloat(float from, float to, int scale) {
		float rand = randomFloat(from, to);
		return Floats.toFixed(rand, scale);
	}

	/**
	 * Returns a random value with {@code precision} integer digits rounded to {@code scale} decimal places.
	 */
	public static float defineFloat(int precision, int scale) {
		long from = (long) Math.pow(10, precision - 1);
		long to = (long) Math.pow(10, precision);
		float rand = randomFloat(from, to);
		return Floats.toFixed(rand, scale);
	}

	/**
	 * Returns {@code length} distinct random values with {@code precision} integer digits, rounded to {@code scale}.
	 */
	public static float[] defineFloats(int length, int precision, int scale) {
		return defineFloats(length, precision, scale, false);
	}

	/**
	 * Returns {@code length} random values with {@code precision} integer digits, rounded to {@code scale}.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 */
	public static float[] defineFloats(int length, int precision, int scale, boolean existsAllowed) {
		float[] results = new float[length];
		float f;
		for (int i = 0; i < length; i++) {
			do {
				f = defineFloat(precision, scale);
			} while (!existsAllowed && contains(results, i, f));
			results[i] = f;
		}
		return results;
	}

	/**
	 * Returns {@code length} distinct random values in {@code [from, to)}.
	 */
	public static float[] randomFloats(int length, float from, float to) {
		return randomFloats(length, from, to, false);
	}

	/**
	 * Returns {@code length} random values in {@code [from, to)}.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 * @throws IllegalArgumentException if distinct values are required but the range is too small
	 */
	public static float[] randomFloats(int length, float from, float to, boolean existsAllowed) {
		checkDistinct(length, existsAllowed, from == to ? 1L : Long.MAX_VALUE);
		float[] results = new float[length];
		float a;
		for (int i = 0; i < length; i++) {
			do {
				a = randomFloat(from, to);
			} while (!existsAllowed && contains(results, i, a));
			results[i] = a;
		}
		return results;
	}

	/**
	 * Returns a random double in {@code [0, 1)}.
	 */
	public static double randomDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}

	/**
	 * Returns a random value in {@code [from, to)}.
	 */
	public static double randomDouble(double from, double to) {
		return from != to ? ThreadLocalRandom.current().nextDouble(from, to) : from;
	}

	/**
	 * Returns a random value in {@code [from, to)} rounded to {@code scale} decimal places.
	 */
	public static double randomDouble(double from, double to, int scale) {
		double rand = randomDouble(from, to);
		return Doubles.toFixed(rand, scale);
	}

	/**
	 * Returns a random value with {@code precision} integer digits rounded to {@code scale} decimal places.
	 */
	public static double defineDouble(int precision, int scale) {
		long from = (long) Math.pow(10, precision - 1);
		long to = (long) Math.pow(10, precision);
		double rand = randomDouble(from, to);
		return Doubles.toFixed(rand, scale);
	}

	/**
	 * Returns {@code length} distinct random values with {@code precision} integer digits, rounded to {@code scale}.
	 */
	public static double[] defineDoubles(int length, int precision, int scale) {
		return defineDoubles(length, precision, scale, false);
	}

	/**
	 * Returns {@code length} random values with {@code precision} integer digits, rounded to {@code scale}.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 */
	public static double[] defineDoubles(int length, int precision, int scale, boolean existsAllowed) {
		double[] results = new double[length];
		double d;
		for (int i = 0; i < length; i++) {
			do {
				d = defineDouble(precision, scale);
			} while (!existsAllowed && contains(results, i, d));
			results[i] = d;
		}
		return results;
	}

	/**
	 * Returns {@code length} distinct random values in {@code [from, to)}.
	 */
	public static double[] randomDoubles(int length, double from, double to) {
		return randomDoubles(length, from, to, false);
	}

	/**
	 * Returns {@code length} random values in {@code [from, to)}.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 * @throws IllegalArgumentException if distinct values are required but the range is too small
	 */
	public static double[] randomDoubles(int length, double from, double to, boolean existsAllowed) {
		checkDistinct(length, existsAllowed, from == to ? 1L : Long.MAX_VALUE);
		double[] results = new double[length];
		double d;
		for (int i = 0; i < length; i++) {
			do {
				d = randomDouble(from, to);
			} while (!existsAllowed && contains(results, i, d));
			results[i] = d;
		}
		return results;
	}

	/**
	 * Returns a random integer in {@code [from, to)}.
	 */
	public static BigInteger randomBigInteger(long from, long to) {
		return randomBigInteger(BigInteger.valueOf(from), BigInteger.valueOf(to));
	}

	/**
	 * Returns a random integer in {@code [from, to)}.
	 */
	public static BigInteger randomBigInteger(BigInteger from, BigInteger to) {
		return randomBigDecimal(new BigDecimal(from), new BigDecimal(to)).toBigInteger();
	}

	/**
	 * Returns a random decimal between {@code from} and {@code to} generated with {@code scale} random digits.
	 */
	public static BigDecimal randomBigDecimal(double from, double to, int scale) {
		return randomBigDecimal(BigDecimal.valueOf(from), BigDecimal.valueOf(to), scale);
	}

	/**
	 * Returns a random decimal between {@code from} and {@code to}.
	 */
	public static BigDecimal randomBigDecimal(BigDecimal from, BigDecimal to) {
		int p = BigDecimalUtils.getPrecision(to);
		int s = BigDecimalUtils.getScale(to);
		return randomBigDecimal(from, to, Math.max(16, p + s));
	}

	/**
	 * Returns a random decimal between {@code from} and {@code to} generated with {@code scale} random digits.
	 * A non-positive scale returns a value in {@code [from, to)} rounded down to that scale (e.g. an integer for 0).
	 */
	public static BigDecimal randomBigDecimal(BigDecimal from, BigDecimal to, int scale) {
		if (scale <= 0) {
			BigDecimal rand = randomBigDecimal(16);
			BigDecimal value = rand.multiply(to.subtract(from)).add(from).setScale(scale, RoundingMode.FLOOR);
			if (value.compareTo(from) < 0) {
				value = from.setScale(scale, RoundingMode.CEILING);
			}
			return value;
		}
		BigDecimal rand = randomBigDecimal(scale);
		return rand.multiply(to.subtract(from)).add(from).stripTrailingZeros();
	}

	/**
	 * Returns a random decimal in {@code [0, 1)} with exactly {@code scale} decimal places.
	 */
	public static BigDecimal randomBigDecimal(int scale) {
		Random r = ThreadLocalRandom.current();
		BigInteger n = BigInteger.TEN.pow(scale);
		BigInteger m;
		do {
			m = new BigInteger(n.bitLength(), r);
		} while (m.compareTo(n) >= 0);
		return new BigDecimal(m, scale);
	}

	/**
	 * Returns a random decimal with {@code precision} integer digits and {@code scale} decimal places.
	 */
	public static BigDecimal defineBigDecimal(int precision, int scale) {
		BigDecimal from = BigDecimal.TEN.pow(precision - 1);
		BigDecimal to = BigDecimal.TEN.pow(precision);
		int newScale = BigDecimalUtils.getPrecision(to) + scale;
		BigDecimal value;
		do {
			value = randomBigDecimal(from, to, newScale);
			if (scale >= 0) {
				value = value.setScale(scale, RoundingMode.HALF_UP);
			}
		} while (value.compareTo(to) >= 0);
		return value;
	}

	/**
	 * Returns {@code length} distinct random values with {@code precision} integer digits, rounded to {@code scale}.
	 */
	public static BigDecimal[] defineBigDecimals(int length, int precision, int scale) {
		return defineBigDecimals(length, precision, scale, false);
	}

	/**
	 * Returns {@code length} random values with {@code precision} integer digits, rounded to {@code scale}.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 */
	public static BigDecimal[] defineBigDecimals(int length, int precision, int scale, boolean existsAllowed) {
		BigDecimal[] results = new BigDecimal[length];
		BigDecimal b;
		for (int i = 0; i < length; i++) {
			do {
				b = defineBigDecimal(precision, scale);
			} while (!existsAllowed && contains(results, i, b));
			results[i] = b;
		}
		return results;
	}

	/**
	 * Returns a random integer with exactly {@code precision} digits.
	 */
	public static BigInteger defineBigInteger(int precision) {
		return defineBigDecimal(precision, 0).toBigInteger();
	}

	/**
	 * Returns {@code length} distinct random values with {@code precision} digits.
	 */
	public static BigInteger[] defineBigIntegers(int length, int precision) {
		return defineBigIntegers(length, precision, false);
	}

	/**
	 * Returns {@code length} random values with {@code precision} digits.
	 *
	 * @param existsAllowed whether duplicated values are allowed
	 */
	public static BigInteger[] defineBigIntegers(int length, int precision, boolean existsAllowed) {
		BigInteger[] results = new BigInteger[length];
		BigInteger b;
		for (int i = 0; i < length; i++) {
			do {
				b = defineBigInteger(precision);
			} while (!existsAllowed && contains(results, i, b));
			results[i] = b;
		}
		return results;
	}

	/**
	 * Returns a random constant of the enum type.
	 */
	public static <E extends Enum<E>> E randomEnum(Class<E> enumClass) {
		E[] enums = enumClass.getEnumConstants();
		return randomChoice(enums);
	}

	/**
	 * Returns a random year in {@code [fromYear, toYear]} (inclusive).
	 *
	 * @throws IllegalArgumentException if a year is out of the supported range
	 */
	public static int randomYear(int fromYear, int toYear) {
		TimeAssert.validateYear(fromYear);
		TimeAssert.validateYear(toYear);
		return randomInt(fromYear, toYear + 1);
	}

	/**
	 * Returns a random month in {@code [fromMonth, toMonth]} (1-based, inclusive).
	 *
	 * @throws IllegalArgumentException if a month is not within 1..12
	 */
	public static int randomMonth(int fromMonth, int toMonth) {
		TimeAssert.validateMonth(fromMonth);
		TimeAssert.validateMonth(toMonth);
		return randomInt(fromMonth, toMonth + 1);
	}

	/**
	 * Returns a random month in {@code [start, end]} (inclusive).
	 */
	public static Month randomMonth(Month start, Month end) {
		return Month.values()[randomMonth(start.ordinal() + 1, end.ordinal() + 1) - 1];
	}

	/**
	 * Returns a random day of year in {@code [fromDayOfYear, toDayOfYear]}; the upper bound is clamped to
	 * the length of the year.
	 */
	public static int randomDayOfYear(Year year, int fromDayOfYear, int toDayOfYear) {
		TimeAssert.validateDayOfYear(year, fromDayOfYear);
		toDayOfYear = Math.min(toDayOfYear, year.isLeap() ? 366 : 365);
		return randomInt(fromDayOfYear, toDayOfYear + 1);
	}

	/**
	 * Returns a random day of month in {@code [fromDayOfMonth, toDayOfMonth]}; the upper bound is clamped
	 * to the length of the month.
	 */
	public static int randomDayOfMonth(YearMonth yearMonth, int fromDayOfMonth, int toDayOfMonth) {
		TimeAssert.validateDayOfMonth(yearMonth, fromDayOfMonth);
		toDayOfMonth = Math.min(toDayOfMonth, yearMonth.atEndOfMonth().getDayOfMonth());
		return randomInt(fromDayOfMonth, toDayOfMonth + 1);
	}

	/**
	 * Returns a random day of month in {@code [fromDayOfMonth, toDayOfMonth]}; the upper bound is clamped
	 * to the length of the month.
	 */
	public static int randomDayOfMonth(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		TimeAssert.validateDayOfMonth(year, month, fromDayOfMonth);
		toDayOfMonth = Math.min(toDayOfMonth, YearMonth.of(year, month).atEndOfMonth().getDayOfMonth());
		return randomInt(fromDayOfMonth, toDayOfMonth + 1);
	}

	/**
	 * Returns a random hour in {@code [fromHourOfDay, toHourOfDay]} (inclusive).
	 */
	public static int randomHourOfDay(int fromHourOfDay, int toHourOfDay) {
		TimeAssert.validateHourOfDay(fromHourOfDay);
		TimeAssert.validateHourOfDay(toHourOfDay);
		return randomInt(fromHourOfDay, toHourOfDay + 1);
	}

	/**
	 * Returns a random minute or second in {@code [from, to]} (inclusive).
	 */
	public static int randomMinuteOrSecond(int from, int to) {
		TimeAssert.validateMinuteOrSecond(from);
		TimeAssert.validateMinuteOrSecond(to);
		return randomInt(from, to + 1);
	}

	private static void checkDistinct(int length, boolean existsAllowed, long rangeSize) {
		if (!existsAllowed && length > rangeSize) {
			throw new IllegalArgumentException("Cannot generate " + length + " distinct values from a range of size " + rangeSize);
		}
	}

	private static boolean contains(short[] array, int end, short value) {
		for (int i = 0; i < end; i++) {
			if (array[i] == value) {
				return true;
			}
		}
		return false;
	}

	private static boolean contains(int[] array, int end, int value) {
		for (int i = 0; i < end; i++) {
			if (array[i] == value) {
				return true;
			}
		}
		return false;
	}

	private static boolean contains(long[] array, int end, long value) {
		for (int i = 0; i < end; i++) {
			if (array[i] == value) {
				return true;
			}
		}
		return false;
	}

	private static boolean contains(float[] array, int end, float value) {
		for (int i = 0; i < end; i++) {
			if (Float.compare(array[i], value) == 0) {
				return true;
			}
		}
		return false;
	}

	private static boolean contains(double[] array, int end, double value) {
		for (int i = 0; i < end; i++) {
			if (Double.compare(array[i], value) == 0) {
				return true;
			}
		}
		return false;
	}

	private static boolean contains(Object[] array, int end, Object value) {
		for (int i = 0; i < end; i++) {
			if (array[i].equals(value)) {
				return true;
			}
		}
		return false;
	}

}
