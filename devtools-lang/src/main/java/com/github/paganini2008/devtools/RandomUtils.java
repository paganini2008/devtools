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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
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

	public static long randomLong(long from, long to) {
		return (long) Math.floor(randomDouble(from, to));
	}

	public static long[] randomLongs(int length, long from, long to) {
		return randomLongs(length, from, to, false);
	}

	public static long[] randomLongs(int length, long from, long to, boolean existsAllowed) {
		long[] results = new long[length];
		long a;
		for (int i = 0; i < length; i++) {
			do {
				a = randomLong(from, to);
			} while (!existsAllowed && Longs.contains(results, a));
			results[i] = a;
		}
		return results;
	}

	private static double nextDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}

	public static float randomFloat(long from, long to) {
		return (float) randomDouble(from, to);
	}

	public static float randomFloat(long from, long to, int scale) {
		float f = randomFloat(from, to);
		return Floats.toFixed(f, 2);
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

	public static byte randomByte(byte from, byte to) {
		return (byte) randomLong(from, to);
	}

	public static short randomShort(short from, short to) {
		return (short) randomLong(from, to);
	}

	public static int randomInt(int from, int to) {
		return (int) randomLong(from, to);
	}

	public static char randomChar(char from, char to) {
		return (char) randomInt(Character.MIN_VALUE, Character.MAX_VALUE);
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

	public static double randomDouble(long from, long to) {
		return nextDouble() * (to - from) + from;
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

	public static boolean randomBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	public static BigDecimal randomBigDecimal(int position, int scale) {
		StringBuilder str = new StringBuilder();
		str.append(randomInt(1, 10));
		int[] ints = randomInts(position - 1 + scale, 0, 9, true);
		str.append(Ints.join(ints, ""));
		return new BigDecimal(str.toString()).divide(BigDecimal.TEN.pow(scale)).setScale(scale, RoundingMode.HALF_UP);
	}

	public static BigInteger randomBigInteger(int position) {
		return randomBigDecimal(position, 0).toBigInteger();
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E randomEnum(Class<E> enumClass) {
		E[] enums = (E[]) MethodUtils.invokeStaticMethod(enumClass, "values");
		return randomChoice(enums);
	}

	public static Year randomYear() {
		return Year.of(randomInt(1970, DateUtils.getYear()));
	}

	public static YearMonth randomMonth(Year year) {
		return year.atMonth(randomInt(1, 13));
	}

	public static LocalDate randomDate() {
		return randomDate(randomYear());
	}

	public static LocalDate randomDate(Year year) {
		return year.atDay(randomInt(1, year.isLeap() ? 366 : 365));
	}

	public static LocalDate randomDate(Year year, Month month) {
		YearMonth yearMonth = year.atMonth(month);
		return yearMonth.atDay(randomInt(1, yearMonth.atEndOfMonth().getDayOfMonth() + 1));
	}

	public static LocalDateTime randomDateTime() {
		return randomDateTime(randomYear());
	}

	public static LocalDateTime randomDateTime(Year year) {
		return randomDate(year).atTime(randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static LocalDateTime randomDateTime(Year year, int dayOfYear) {
		return year.atDay(Math.min(dayOfYear, year.isLeap() ? 366 : 365)).atTime(randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static LocalDateTime randomDateTime(Year year, Month month, int dayOfMonth) {
		YearMonth yearMonth = year.atMonth(month);
		return yearMonth.atDay(dayOfMonth).atTime(randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static LocalTime randomTime() {
		return LocalTime.of(randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

}
