package com.github.paganini2008.devtools;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * RandomUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class RandomUtils {

	private RandomUtils() {
	}

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

	public static void main(String[] args) throws Exception {
		System.out.println(Doubles.toString(randomDoubles(10, 0, 1000)));
	}

}
