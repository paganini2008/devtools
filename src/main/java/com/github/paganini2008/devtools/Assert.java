package com.github.paganini2008.devtools;

/**
 * Assert
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Assert {

	private Assert() {
	}

	public static void hasNoLength(CharSequence text) {
		hasNoLength(text, "The string type parameter must not be null or empty.");
	}

	public static void hasNoLength(CharSequence text, String msg) {
		hasNoLength(text, msg, null);
	}

	public static void hasNoLength(CharSequence text, String msg, Object[] args) {
		isTrue(StringUtils.isEmpty(text), msg, args);
	}

	public static void hasNoText(CharSequence text) {
		hasNoText(text, "The string type parameter must not be null or empty.");
	}

	public static void hasNoText(CharSequence text, String msg) {
		hasNoText(text, msg, null);
	}

	public static void hasNoText(CharSequence text, String msg, Object[] args) {
		isTrue(StringUtils.isBlank(text), msg, args);
	}

	public static void isNull(Object arg) {
		isNull(arg, "The argument must not be null.");
	}

	public static void isNull(Object arg, String msg) {
		isNull(arg, msg, null);
	}

	public static void isNull(Object arg, String msg, Object[] args) {
		isTrue(arg == null, msg, args);
	}

	public static void isNotArray(Object arg) {
		isNotArray(arg, "The argument type of '%s' is not array.",
				new Object[] { arg != null ? arg.getClass() : null });
	}

	public static void isNotArray(Object arg, String msg) {
		isNotArray(arg, msg, null);
	}

	public static void isNotArray(Object arg, String msg, Object[] args) {
		isTrue(ObjectUtils.isNotArray(arg), msg, args);
	}

	public static void isLt(int a, int b, String msg) {
		isLt(a, b, msg, null);
	}

	public static void isLt(int a, int b, String msg, Object[] args) {
		isTrue(a < b, msg, args);
	}

	public static void isLte(int a, int b, String msg) {
		isLte(a, b, msg, null);
	}

	public static void isLte(int a, int b, String msg, Object[] args) {
		isTrue(a <= b, msg, args);
	}

	public static void isGt(int a, int b, String msg) {
		isGt(a, b, msg, null);
	}

	public static void isGt(int a, int b, String msg, Object[] args) {
		isTrue(a > b, msg, args);
	}

	public static void isGte(int a, int b, String msg) {
		isGte(a, b, msg, null);
	}

	public static void isGte(int a, int b, String msg, Object[] args) {
		isTrue(a >= b, msg, args);
	}

	public static void isNe(int a, int b, String msg) {
		isNe(a, b, msg, null);
	}

	public static void isNe(int a, int b, String msg, Object[] args) {
		isTrue(a != b, msg, args);
	}

	public static void isLt(long a, long b, String msg) {
		isLt(a, b, msg, null);
	}

	public static void isLt(long a, long b, String msg, Object[] args) {
		isTrue(a < b, msg, args);
	}

	public static void isLte(long a, long b, String msg) {
		isLte(a, b, msg, null);
	}

	public static void isLte(long a, long b, String msg, Object[] args) {
		isTrue(a <= b, msg, args);
	}

	public static void isGt(long a, long b, String msg) {
		isGt(a, b, msg, null);
	}

	public static void isGt(long a, long b, String msg, Object[] args) {
		isTrue(a > b, msg, args);
	}

	public static void isGte(long a, long b, String msg) {
		isGte(a, b, msg, null);
	}

	public static void isGte(long a, long b, String msg, Object[] args) {
		isTrue(a >= b, msg, args);
	}

	public static void isNe(long a, long b, String msg) {
		isNe(a, b, msg, null);
	}

	public static void isNe(long a, long b, String msg, Object[] args) {
		isTrue(a != b, msg, args);
	}

	public static void isTrue(boolean result, String msg) {
		isTrue(result, msg, null);
	}

	public static void isTrue(boolean result, String msg, Object[] args) {
		isTrue(result, new IllegalArgumentException(args != null ? String.format(msg, args) : msg));
	}

	public static void isFalse(boolean result, String msg) {
		isFalse(result, msg, null);
	}

	public static void isFalse(boolean result, String msg, Object[] args) {
		isFalse(result, new IllegalArgumentException(args != null ? String.format(msg, args) : msg));
	}

	public static <T extends RuntimeException> void isTrue(boolean result, T e) {
		if (result == true) {
			throw e;
		}
	}

	public static <T extends RuntimeException> void isFalse(boolean result, T e) {
		if (result == false) {
			throw e;
		}
	}

	public static <T extends Exception> void isTrue(boolean result, T e) throws T {
		if (result == true) {
			throw e;
		}
	}

	public static <T extends Exception> void isFalse(boolean result, T e) throws T {
		if (result == false) {
			throw e;
		}
	}

	public static <T extends Error> void isTrue(boolean result, T e) throws T {
		if (result == true) {
			throw e;
		}
	}

	public static <T extends Error> void isFalse(boolean result, T e) throws T {
		if (result == false) {
			throw e;
		}
	}

}
