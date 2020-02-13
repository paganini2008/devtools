package com.github.paganini2008.devtools;

import com.github.paganini2008.devtools.primitives.Chars;

/**
 * 
 * BitUtils
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class BitUtils {

	private BitUtils() {

	}

	public static final int INTEGER_BIT = 2 << 4;

	public static final int LONG_BIT = 2 << 5;

	public static final char AND = '\u0026';

	public static final char OR = '\u007c';

	public static final char XOR = '\u005e';

	public static final char NON = '\u007e';

	public static final String LEFT_SHIFT = "<<";

	public static final String RIGHT_SHIFT = ">>";

	public static final String UNSIGNED_RIGHT_SHIFT = ">>>";

	public static String toBinaryString(int value) {
		String s = Integer.toBinaryString(value);
		if (value >= 0) {
			s = StringUtils.leftPadding(s, "0", INTEGER_BIT);
		}
		return s;
	}

	public static String toBinaryString(long value) {
		String s = Long.toBinaryString(value);
		if (value >= 0) {
			s = StringUtils.leftPadding(s, "0", LONG_BIT);
		}
		return s;
	}

	/**
	 * 无符号右移 比如： -7 >>> -5 = 31
	 * 
	 * @param value
	 * @param n
	 * @return
	 */
	public static int unsignedRightShift(int value, int n) {
		String s = toBinaryString(value);
		char[] c = s.toCharArray();
		unsignedRightShift(c, n);
		return parseInt(c);
	}

	/**
	 * 无符号右移 比如： -7L >>> -5 = 31
	 * 
	 * @param value
	 * @param n
	 * @return
	 */
	public static long unsignedRightShift(long value, int n) {
		String s = toBinaryString(value);
		char[] c = s.toCharArray();
		unsignedRightShift(c, n);
		return parseLong(c);
	}

	/**
	 * 左移 比如：-5 << 2 = 20
	 * 
	 * @param value
	 * @param n
	 * @return
	 */
	public static int leftShift(int value, int n) {
		String s = toBinaryString(value);
		char[] c = s.toCharArray();
		leftShift(c, n);
		return parseInt(c);
	}

	/**
	 * 左移 比如：-5L << 2 = 20
	 * 
	 * @param value
	 * @param n
	 * @return
	 */
	public static long leftShift(long value, int n) {
		String s = toBinaryString(value);
		char[] c = s.toCharArray();
		leftShift(c, n);
		return parseLong(c);
	}

	/**
	 * 右移 比如: -2 >> 3 = -1
	 * 
	 * @param value
	 * @param n
	 * @return
	 */
	public static int rightShift(int value, int n) {
		String s = toBinaryString(value);
		char[] c = s.toCharArray();
		rightShift(c, n);
		return parseInt(c);
	}

	/**
	 * 右移 比如: -2L >> 3 = -1
	 * 
	 * @param value
	 * @param n
	 * @return
	 */
	public static long rightShift(long value, int n) {
		String s = toBinaryString(value);
		char[] c = s.toCharArray();
		rightShift(c, n);
		return parseLong(c);
	}

	private static int parseInt(char[] c) {
		boolean t = c[0] == '1';
		if (t) {
			negated(c);
			add(c);
		}
		int n = Integer.parseInt(new String(c), 2);
		if (t) {
			n *= -1;
		}
		return n;
	}

	private static long parseLong(char[] c) {
		boolean t = c[0] == '1';
		if (t) {
			negated(c);
			add(c);
		}
		long n = Long.parseLong(new String(c), 2);
		if (t) {
			n *= -1;
		}
		return n;
	}

	/**
	 * 二进制字符串转整型数值
	 * 
	 * @param s
	 * @return
	 */
	public static int parseInt(String s) {
		Assert.hasNoText(s);
		return parseInt(s.toCharArray());
	}

	/**
	 * 二进制字符串转长整型数值
	 * 
	 * @param s
	 * @return
	 */
	public static long parseLong(String s) {
		Assert.hasNoText(s);
		return parseLong(s.toCharArray());
	}

	/**
	 * 位与 比如： 5 & 6 = 4
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int and(int a, int b) {
		String s1 = toBinaryString(a);
		String s2 = toBinaryString(b);
		String s3 = new String(compare(s1.toCharArray(), s2.toCharArray(), AND));
		return parseInt(s3);
	}

	/**
	 * 位与 比如： 5L & 6L = 4
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static long and(long a, long b) {
		String s1 = toBinaryString(a);
		String s2 = toBinaryString(b);
		String s3 = new String(compare(s1.toCharArray(), s2.toCharArray(), AND));
		return parseLong(s3);
	}

	/**
	 * 位或 比如： -5 | -6 = -5
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int or(int a, int b) {
		String s1 = toBinaryString(a);
		String s2 = toBinaryString(b);
		String s3 = new String(compare(s1.toCharArray(), s2.toCharArray(), OR));
		return parseInt(s3);
	}

	/**
	 * 位或 比如： -5L | -6L = -5
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static long or(long a, long b) {
		String s1 = toBinaryString(a);
		String s2 = toBinaryString(b);
		String s3 = new String(compare(s1.toCharArray(), s2.toCharArray(), OR));
		return parseLong(s3);
	}

	/**
	 * 位异或 比如： 5 ^ 6 = 3
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int xor(int a, int b) {
		String s1 = toBinaryString(a);
		String s2 = toBinaryString(b);
		String s3 = new String(compare(s1.toCharArray(), s2.toCharArray(), XOR));
		return parseInt(s3);
	}

	/**
	 * 位异或 比如： 5L ^ 6L = 3
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static long xor(long a, long b) {
		String s1 = toBinaryString(a);
		String s2 = toBinaryString(b);
		String s3 = new String(compare(s1.toCharArray(), s2.toCharArray(), XOR));
		return parseLong(s3);
	}

	/**
	 * 位非 比如：~5 = 4
	 * 
	 * @param a
	 * @return
	 */
	public static int non(int a) {
		String s = toBinaryString(a);
		char[] c = s.toCharArray();
		negated(c);
		return parseInt(new String(c));
	}

	/**
	 * 位非 比如：~5L = 4
	 * 
	 * @param a
	 * @return
	 */
	public static long non(long a) {
		String s = toBinaryString(a);
		char[] c = s.toCharArray();
		negated(c);
		return parseLong(new String(c));
	}

	/**
	 * 返回位移量
	 * 
	 * @param n
	 * @param bit
	 * @return
	 */
	public static int getShiftAmount(int n, int bit) {
		return (n >= 0 ? (n % bit) : (n & (bit - 1)));
	}

	private static void add(char[] array) {
		int m, n = 1;
		for (int l = array.length, i = l - 1; i >= 0; i--) {
			m = Integer.parseInt(String.valueOf(array[i]));
			if (m + n == 2) {
				array[i] = '0';
			} else if (m + n == 1) {
				array[i] = '1';
				break;
			}
		}
	}

	private static void negated(char[] array) {
		for (int i = 0, l = array.length; i < l; i++) {
			if (array[i] == '1') {
				array[i] = '0';
			} else {
				array[i] = '1';
			}
		}
	}

	private static void leftShift(char[] array, int n) {
		int l = array.length;
		n = getShiftAmount(n, l);
		Chars.leftScroll(array, n);
		for (int i = n; i > 0; i--) {
			array[l - i] = '0';
		}
	}

	private static void rightShift(char[] array, int n) {
		n = getShiftAmount(n, array.length);
		Chars.rightScroll(array, n);
		boolean b = array[0] == '1';
		for (int i = 0; i < n; i++) {
			array[i] = b ? '1' : '0';
		}
	}

	private static void unsignedRightShift(char[] array, int n) {
		n = getShiftAmount(n, array.length);
		Chars.rightScroll(array, n);
		for (int i = 0; i < n; i++) {
			array[i] = '0';
		}
	}

	private static char[] compare(char[] c1, char[] c2, char t) {
		int l = c1.length;
		char[] c3 = new char[l];
		for (int i = 0; i < l; i++) {
			switch (t) {
			case AND:
				if (c1[i] == '1' && c2[i] == '1') {
					c3[i] = '1';
				} else {
					c3[i] = '0';
				}
				break;
			case OR:
				if (c1[i] == '1' || c2[i] == '1') {
					c3[i] = '1';
				} else {
					c3[i] = '0';
				}
				break;
			case XOR:
				if (Math.abs(c1[i] - c2[i]) == 1) {
					c3[i] = '1';
				} else {
					c3[i] = '0';
				}
				break;
			}
		}
		return c3;
	}

}
