/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.devtools.date.TimeAsserts;
import com.github.paganini2008.devtools.math.BigDecimalUtils;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;
import com.github.paganini2008.devtools.reflection.MethodUtils;

/**
 * RandomUtils
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class RandomUtils {

	public static byte randomChoice(byte[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	public static byte randomChoice(byte[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	public static short randomChoice(short[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	public static short randomChoice(short[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	public static int randomChoice(int[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	public static int randomChoice(int[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	public static long randomChoice(long[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	public static long randomChoice(long[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	public static float randomChoice(float[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	public static float randomChoice(float[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	public static double randomChoice(double[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	public static double randomChoice(double[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	public static char randomChoice(char[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	public static char randomChoice(char[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	public static boolean randomChoice(boolean[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	public static boolean randomChoice(boolean[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	public static <T> T randomChoice(T[] choice) {
		return choice[randomInt(0, choice.length)];
	}

	public static <T> T randomChoice(T[] choice, int length) {
		return choice[randomInt(0, Math.min(length, choice.length))];
	}

	public static <T> T randomChoice(List<T> list) {
		return list.get(randomInt(0, list.size()));
	}

	public static <T> T randomChoice(List<T> list, int length) {
		return list.get(randomInt(0, Math.min(length, list.size())));
	}

	public static boolean randomBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	public static byte randomByte(byte from, byte to) {
		return (byte) randomInt(from, to);
	}

	public static char randomChar(char from, char to) {
		return (char) randomInt(from, to);
	}

	public static short randomShort(short from, short to) {
		return (short) randomInt(from, to);
	}

	public static short[] randomShorts(int length, short from, short to) {
		return randomShorts(length, from, to, false);
	}

	public static short[] randomShorts(int length, short from, short to, boolean existsAllowed) {
		short[] results = new short[length];
		short s;
		for (int i = 0; i < length; i++) {
			do {
				s = randomShort(from, to);
			} while (!existsAllowed && Shorts.contains(results, s));
			results[i] = s;
		}
		return results;
	}

	public static int randomInt() {
		return randomInt(0, Integer.MAX_VALUE);
	}

	public static int defineInt(int precision) {
		if (Math.log10(Integer.MAX_VALUE) <= precision) {
			throw new IllegalArgumentException("Exceed maximum integer value");
		}
		int from = (int) Math.pow(10, precision - 1);
		int to = (int) Math.pow(10, precision);
		return randomInt(from, to);
	}

	public static int[] defineInts(int length, int precision) {
		return defineInts(length, precision, false);
	}

	public static int[] defineInts(int length, int precision, boolean existsAllowed) {
		int[] results = new int[length];
		int a;
		for (int i = 0; i < length; i++) {
			do {
				a = defineInt(precision);
			} while (!existsAllowed && Ints.contains(results, a));
			results[i] = a;
		}
		return results;
	}

	public static int randomInt(int from, int to) {
		return from != to ? ThreadLocalRandom.current().nextInt(from, to) : from;
	}

	public static int[] randomInts(int length, int from, int to) {
		return randomInts(length, from, to, false);
	}

	public static int[] randomInts(int length, int from, int to, boolean existsAllowed) {
		int[] results = new int[length];
		int a;
		for (int i = 0; i < length; i++) {
			do {
				a = randomInt(from, to);
			} while (!existsAllowed && Ints.contains(results, a));
			results[i] = a;
		}
		return results;
	}

	public static long defineLong(int precision) {
		if (Math.log10(Long.MAX_VALUE) <= precision) {
			throw new IllegalArgumentException("Exceed maximum long value");
		}
		long from = (long) Math.pow(10, precision - 1);
		long to = (long) Math.pow(10, precision);
		return randomLong(from, to);
	}

	public static long[] defineLongs(int length, int precision) {
		return defineLongs(length, precision, false);
	}

	public static long[] defineLongs(int length, int precision, boolean existsAllowed) {
		long[] results = new long[length];
		long l;
		for (int i = 0; i < length; i++) {
			do {
				l = defineLong(precision);
			} while (!existsAllowed && Longs.contains(results, l));
			results[i] = l;
		}
		return results;
	}

	public static long randomLong(long from, long to) {
		return from != to ? ThreadLocalRandom.current().nextLong(from, to) : from;
	}

	public static long[] randomLongs(int length, long from, long to) {
		return randomLongs(length, from, to, false);
	}

	public static long[] randomLongs(int length, long from, long to, boolean existsAllowed) {
		long[] results = new long[length];
		long l;
		for (int i = 0; i < length; i++) {
			do {
				l = randomLong(from, to);
			} while (!existsAllowed && Longs.contains(results, l));
			results[i] = l;
		}
		return results;
	}

	public static float randomFloat() {
		return ThreadLocalRandom.current().nextFloat();
	}

	public static float randomFloat(float from, float to) {
		return from != to ? randomFloat() * (to - from) + from : from;
	}

	public static float randomFloat(float from, float to, int scale) {
		float rand = randomFloat(from, to);
		return Floats.toFixed(rand, scale);
	}

	public static float defineFloat(int precision, int scale) {
		long from = (long) Math.pow(10, precision - 1);
		long to = (long) Math.pow(10, precision);
		float rand = randomFloat(from, to);
		return Floats.toFixed(rand, scale);
	}

	public static float[] defineFloats(int length, int precision, int scale) {
		return defineFloats(length, precision, scale, false);
	}

	public static float[] defineFloats(int length, int precision, int scale, boolean existsAllowed) {
		float[] results = new float[length];
		float f;
		for (int i = 0; i < length; i++) {
			do {
				f = defineFloat(precision, scale);
			} while (!existsAllowed && Floats.contains(results, f));
			results[i] = f;
		}
		return results;
	}

	public static float[] randomFloats(int length, float from, float to) {
		return randomFloats(length, from, to, false);
	}

	public static float[] randomFloats(int length, float from, float to, boolean existsAllowed) {
		float[] results = new float[length];
		float a;
		for (int i = 0; i < length; i++) {
			do {
				a = randomFloat(from, to);
			} while (!existsAllowed && Floats.contains(results, a));
			results[i] = a;
		}
		return results;
	}

	public static double randomDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}

	public static double randomDouble(double from, double to) {
		return from != to ? ThreadLocalRandom.current().nextDouble(from, to) : from;
	}

	public static double randomDouble(double from, double to, int scale) {
		double rand = randomDouble(from, to);
		return Doubles.toFixed(rand, scale);
	}

	public static double defineDouble(int precision, int scale) {
		long from = (long) Math.pow(10, precision - 1);
		long to = (long) Math.pow(10, precision);
		double rand = randomDouble(from, to);
		return Doubles.toFixed(rand, scale);
	}

	public static double[] defineDoubles(int length, int precision, int scale) {
		return defineDoubles(length, precision, scale, false);
	}

	public static double[] defineDoubles(int length, int precision, int scale, boolean existsAllowed) {
		double[] results = new double[length];
		double d;
		for (int i = 0; i < length; i++) {
			do {
				d = defineDouble(precision, scale);
			} while (!existsAllowed && Doubles.contains(results, d));
			results[i] = d;
		}
		return results;
	}

	public static double[] randomDoubles(int length, double from, double to) {
		return randomDoubles(length, from, to, false);
	}

	public static double[] randomDoubles(int length, double from, double to, boolean existsAllowed) {
		double[] results = new double[length];
		double d;
		for (int i = 0; i < length; i++) {
			do {
				d = randomDouble(from, to);
			} while (!existsAllowed && Doubles.contains(results, d));
			results[i] = d;
		}
		return results;
	}

	public static BigInteger randomBigInteger(long from, long to) {
		return randomBigInteger(BigInteger.valueOf(from), BigInteger.valueOf(to));
	}

	public static BigInteger randomBigInteger(BigInteger from, BigInteger to) {
		return randomBigDecimal(new BigDecimal(from), new BigDecimal(to)).toBigInteger();
	}

	public static BigDecimal randomBigDecimal(double from, double to, int scale) {
		return randomBigDecimal(BigDecimal.valueOf(from), BigDecimal.valueOf(to), scale);
	}

	public static BigDecimal randomBigDecimal(BigDecimal from, BigDecimal to) {
		int p = BigDecimalUtils.getPrecision(to);
		int s = BigDecimalUtils.getScale(to);
		return randomBigDecimal(from, to, Math.max(16, p + s));
	}

	public static BigDecimal randomBigDecimal(BigDecimal from, BigDecimal to, int scale) {
		BigDecimal rand = randomBigDecimal(scale);
		return rand.multiply(to.subtract(from)).add(from).stripTrailingZeros();
	}

	public static BigDecimal randomBigDecimal(int scale) {
		Random r = ThreadLocalRandom.current();
		BigInteger n = BigInteger.TEN.pow(scale);
		BigInteger m;
		do {
			m = new BigInteger(n.bitLength(), r);
		} while (m.compareTo(n) >= 0);
		return new BigDecimal(m, scale);
	}

	public static void main(String[] args) {
		// System.out.println(defineBigInteger(10));
		// System.out.println(BigDecimal.valueOf(randomFloat(100,
		// 1200)).toPlainString());
		// System.out.println(randomBigDecimal(BigDecimal.ONE, new BigDecimal("100")));
		// System.out.println(randomDouble(10, 1000));
	}

	public static BigDecimal defineBigDecimal(int precision, int scale) {
		BigDecimal from = BigDecimal.TEN.pow(precision - 1);
		BigDecimal to = BigDecimal.TEN.pow(precision);
		int newScale = BigDecimalUtils.getPrecision(to) + scale;
		BigDecimal value = randomBigDecimal(from, to, newScale);
		return scale >= 0 ? value.setScale(scale, RoundingMode.HALF_UP) : value;
	}

	public static BigDecimal[] defineBigDecimals(int length, int precision, int scale) {
		return defineBigDecimals(length, precision, scale, false);
	}

	public static BigDecimal[] defineBigDecimals(int length, int precision, int scale, boolean existsAllowed) {
		BigDecimal[] results = new BigDecimal[length];
		BigDecimal b;
		for (int i = 0; i < length; i++) {
			do {
				b = defineBigDecimal(precision, scale);
			} while (!existsAllowed && ArrayUtils.contains(results, b));
			results[i] = b;
		}
		return results;
	}

	public static BigInteger defineBigInteger(int precision) {
		return defineBigDecimal(precision, 0).toBigInteger();
	}

	public static BigInteger[] defineBigIntegers(int length, int precision) {
		return defineBigIntegers(length, precision, false);
	}

	public static BigInteger[] defineBigIntegers(int length, int precision, boolean existsAllowed) {
		BigInteger[] results = new BigInteger[length];
		BigInteger b;
		for (int i = 0; i < length; i++) {
			do {
				b = defineBigInteger(precision);
			} while (!existsAllowed && ArrayUtils.contains(results, b));
			results[i] = b;
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E randomEnum(Class<E> enumClass) {
		E[] enums = (E[]) MethodUtils.invokeStaticMethod(enumClass, "values");
		return randomChoice(enums);
	}

	public static int randomYear(int fromYear, int toYear) {
		TimeAsserts.validateYear(fromYear);
		TimeAsserts.validateYear(toYear);
		return randomInt(fromYear, toYear + 1);
	}

	public static int randomMonth(int fromMonth, int toMonth) {
		TimeAsserts.validateMonth(fromMonth);
		TimeAsserts.validateMonth(toMonth);
		return randomInt(fromMonth, toMonth + 1);
	}

	public static int randomDayOfYear(Year year, int fromDayOfYear, int toDayOfYear) {
		TimeAsserts.validateDayOfYear(year, fromDayOfYear);
		toDayOfYear = Math.min(toDayOfYear, year.isLeap() ? 366 : 365);
		return randomInt(fromDayOfYear, toDayOfYear + 1);
	}

	public static int randomDayOfMonth(YearMonth yearMonth, int fromDayOfMonth, int toDayOfMonth) {
		TimeAsserts.validateDayOfMonth(yearMonth, fromDayOfMonth);
		toDayOfMonth = Math.min(toDayOfMonth, yearMonth.atEndOfMonth().getDayOfMonth());
		return randomInt(fromDayOfMonth, toDayOfMonth + 1);
	}

	public static int randomDayOfMonth(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		TimeAsserts.validateDayOfMonth(year, month, fromDayOfMonth);
		toDayOfMonth = Math.min(toDayOfMonth, YearMonth.of(year, month).atEndOfMonth().getDayOfMonth());
		return randomInt(fromDayOfMonth, toDayOfMonth + 1);
	}

	public static int randomHourOfDay(int fromHourOfDay, int toHourOfDay) {
		TimeAsserts.validateHourOfDay(fromHourOfDay);
		TimeAsserts.validateHourOfDay(toHourOfDay);
		return randomInt(fromHourOfDay, toHourOfDay + 1);
	}

	public static int randomMinuteOrSecond(int from, int to) {
		TimeAsserts.validateMinuteOrSecond(from);
		TimeAsserts.validateMinuteOrSecond(to);
		return randomInt(from, to + 1);
	}

}
