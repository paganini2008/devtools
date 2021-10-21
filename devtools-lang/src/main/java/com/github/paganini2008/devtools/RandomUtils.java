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
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.devtools.date.DateUtils;
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

	public static byte randomByte() {
		return randomByte((byte) 0, Byte.MAX_VALUE);
	}

	public static byte randomByte(byte from, byte to) {
		return (byte) randomInt(from, to);
	}

	public static char randomChar() {
		return randomChar(Character.MIN_VALUE, Character.MAX_VALUE);
	}

	public static char randomChar(char from, char to) {
		return (char) randomInt(from, to);
	}

	public static long randomLong() {
		return randomLong(0, Long.MAX_VALUE);
	}

	public static long randomLong(int precision) {
		if (Math.log10(Long.MAX_VALUE) <= precision) {
			throw new IllegalArgumentException("Exceed maximum long value");
		}
		long from = (long) Math.pow(10, precision - 1);
		long to = (long) Math.pow(10, precision);
		return randomLong(from, to);
	}

	public static long[] randomLongs(int length, int precision) {
		return randomLongs(length, precision, false);
	}

	public static long[] randomLongs(int length, int precision, boolean existsAllowed) {
		long[] results = new long[length];
		long l;
		for (int i = 0; i < length; i++) {
			do {
				l = randomLong(precision);
			} while (!existsAllowed && Longs.contains(results, l));
			results[i] = l;
		}
		return results;
	}

	public static long randomLong(long from, long to) {
		return ThreadLocalRandom.current().nextLong(from, to);
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

	public static float randomFloat(long from, long to) {
		return randomFloat() * (to - from) + from;
	}

	public static float[] randomFloats(int length, long from, long to) {
		return randomFloats(length, from, to, false);
	}

	public static float[] randomFloats(int length, long from, long to, boolean existsAllowed) {
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

	public static short randomShort() {
		return randomShort((short) 0, Short.MAX_VALUE);
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

	public static int randomInt(int precision) {
		if (Math.log10(Integer.MAX_VALUE) <= precision) {
			throw new IllegalArgumentException("Exceed maximum integer value");
		}
		int from = (int) Math.pow(10, precision - 1);
		int to = (int) Math.pow(10, precision);
		return randomInt(from, to);
	}

	public static int[] randomInts(int length, int precision) {
		return randomInts(length, length, precision, false);
	}

	public static int[] randomInts(int length, int precision, boolean existsAllowed) {
		int[] results = new int[length];
		int a;
		for (int i = 0; i < length; i++) {
			do {
				a = randomInt(precision);
			} while (!existsAllowed && Ints.contains(results, a));
			results[i] = a;
		}
		return results;
	}

	public static int randomInt(int from, int to) {
		return ThreadLocalRandom.current().nextInt(from, to);
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

	private static double randomDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}

	public static double randomDouble(long from, long to) {
		return randomDouble() * (to - from) + from;
	}

	public static double[] randomDoubles(int length, long from, long to) {
		return randomDoubles(length, from, to, false);
	}

	public static double[] randomDoubles(int length, long from, long to, boolean existsAllowed) {
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

	public static BigDecimal randomBigDecimal(long from, long to) {
		return randomBigDecimal(BigDecimal.valueOf(from), BigDecimal.valueOf(to));
	}

	public static BigDecimal randomBigDecimal(BigDecimal from, BigDecimal to) {
		int scale = to.round(new MathContext(1)).scale();
		return randomBigDecimal(Math.abs(Math.min(-16, scale)), from, to);
	}

	private static BigDecimal randomBigDecimal(int scale, BigDecimal from, BigDecimal to) {
		BigDecimal rand = randomBigDecimal(scale);
		return rand.multiply(to.subtract(from)).add(from);
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
		System.out.println(randomBigDecimal(11, 6));
	}

	public static BigDecimal randomBigDecimal(int precision, int scale) {
		BigDecimal from = BigDecimal.TEN.pow(precision - 1);
		BigDecimal to = BigDecimal.TEN.pow(precision);
		scale = Math.abs(to.round(new MathContext(1)).scale()) + scale;
		BigDecimal value = randomBigDecimal(scale, from, to);
		return value.setScale(scale, RoundingMode.HALF_UP);
	}

	public static BigDecimal[] randomBigDecimals(int length, int precision, int scale) {
		return randomBigDecimals(length, precision, scale, false);
	}

	public static BigDecimal[] randomBigDecimals(int length, int precision, int scale, boolean existsAllowed) {
		BigDecimal[] results = new BigDecimal[length];
		BigDecimal b;
		for (int i = 0; i < length; i++) {
			do {
				b = randomBigDecimal(precision, scale);
			} while (!existsAllowed && ArrayUtils.contains(results, b));
			results[i] = b;
		}
		return results;
	}

	public static BigInteger randomBigInteger(int precision) {
		return randomBigDecimal(precision, 0).toBigInteger();
	}

	public static BigInteger[] randomBigIntegers(int length, int precision) {
		return randomBigIntegers(length, precision, false);
	}

	public static BigInteger[] randomBigIntegers(int length, int precision, boolean existsAllowed) {
		BigInteger[] results = new BigInteger[length];
		BigInteger b;
		for (int i = 0; i < length; i++) {
			do {
				b = randomBigInteger(precision);
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

	public static Year randomYear() {
		return Year.of(randomInt(1970, DateUtils.getYear() + 1));
	}

	public static YearMonth randomMonth(Year year) {
		return year.atMonth(randomInt(1, 13));
	}

	public static LocalDate randomLocalDate() {
		return randomLocalDate(Year.now());
	}

	public static LocalDate randomLocalDate(Year year) {
		return year.atDay(randomInt(1, (year.isLeap() ? 366 : 365) + 1));
	}

	public static LocalDate randomLocalDate(Year year, Month month) {
		YearMonth yearMonth = year.atMonth(month);
		return yearMonth.atDay(randomInt(1, yearMonth.atEndOfMonth().getDayOfMonth() + 1));
	}

	public static LocalDateTime randomLocalDateTime() {
		return randomLocalDateTime(Year.now());
	}

	public static LocalDateTime randomLocalDateTime(Year year) {
		return randomLocalDateTime(year, randomInt(1, (year.isLeap() ? 366 : 365) + 1));
	}

	public static LocalDateTime randomLocalDateTime(Year year, int dayOfYear) {
		return year.atDay(Math.min(dayOfYear, year.isLeap() ? 366 : 365)).atTime(randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month) {
		return randomLocalDateTime(year, month, randomInt(1, month.maxLength() + 1));
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month, int dayOfMonth) {
		YearMonth yearMonth = year.atMonth(month);
		return yearMonth.atDay(dayOfMonth).atTime(randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static LocalTime randomLocalTime() {
		return LocalTime.of(randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static Date randomDate() {
		return randomDate(DateUtils.getYear());
	}

	public static Date randomDate(int year) {
		return randomDate(year, randomInt(1, 13));
	}

	public static Date randomDate(int year, int month) {
		Date current = DateUtils.valueOf(year, month, 1);
		int date = randomInt(1, DateUtils.getLastDay(current) + 1);
		return DateUtils.setDay(current, date);
	}

	public static Date randomDateTime() {
		return randomDateTime(DateUtils.getYear());
	}

	public static Date randomDateTime(int year) {
		return randomDateTime(year, randomInt(1, 13));
	}

	public static Date randomDateTime(int year, int month) {
		Date current = DateUtils.valueOf(year, month, 1);
		int dayOfMonth = randomInt(1, DateUtils.getLastDay(current) + 1);
		return DateUtils.valueOf(year, month, dayOfMonth, randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static Date randomDateTime(int year, int month, int dayOfMonth) {
		Date current = DateUtils.valueOf(year, month, 1);
		dayOfMonth = Math.min(dayOfMonth, DateUtils.getLastDay(current));
		return DateUtils.valueOf(year, month, dayOfMonth, randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static void main2(String[] args) {
		for (int i = 1; i <= 100; i++)
			System.out.println(randomLocalDateTime(Year.now(), Month.AUGUST));
	}

}
