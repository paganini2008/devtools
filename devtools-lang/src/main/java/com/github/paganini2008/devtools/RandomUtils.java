/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;

/**
 * RandomUtils
 * 
 * @author Fred Feng
 * @version 1.0
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
		long[] results = new long[length];
		long a;
		for (int i = 0; i < length; i++) {
			do {
				a = randomLong(from, to);
			} while (Longs.contains(results, a));
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
		float[] results = new float[length];
		float a;
		for (int i = 0; i < length; i++) {
			do {
				a = randomFloat(from, to);
			} while (Floats.contains(results, a));
			results[i] = a;
		}
		return results;
	}

	public static int randomInt(int from, int to) {
		return (int) randomLong(from, to);
	}

	public static int[] randomInts(int length, int from, int to) {
		int[] results = new int[length];
		int a;
		for (int i = 0; i < length; i++) {
			do {
				a = randomInt(from, to);
			} while (Ints.contains(results, a));
			results[i] = a;
		}
		return results;
	}

	public static double randomDouble(long from, long to) {
		return nextDouble() * (to - from) + from;
	}

	public static double[] randomDoubles(int length, long from, long to) {
		double[] results = new double[length];
		double d;
		for (int i = 0; i < length; i++) {
			do {
				d = randomDouble(from, to);
			} while (Doubles.contains(results, d));
			results[i] = d;
		}
		return results;
	}

	public static boolean randomBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

}
