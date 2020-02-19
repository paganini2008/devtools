package com.github.paganini2008.devtools.math;

import static com.github.paganini2008.devtools.NumberUtils.format;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.CompareUtils;
import com.github.paganini2008.devtools.NumberUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * BigIntegerUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class BigIntegerUtils {

	private static class BigIntegerCache {

		static final int high = 2 << 7;

		static final BigInteger[] cache = new BigInteger[high];

		static {
			for (int i = 0; i < high; i++) {
				cache[i] = BigInteger.valueOf(i);
			}
		}

		private BigIntegerCache() {
		}
	}

	public static final BigInteger BYTE_MAX_VALUE = BigInteger.valueOf(Byte.MAX_VALUE);

	public static final BigInteger BYTE_MIN_VALUE = BigInteger.valueOf(Byte.MIN_VALUE);

	public static final BigInteger SHORT_MAX_VALUE = BigInteger.valueOf(Short.MAX_VALUE);

	public static final BigInteger SHORT_MIN_VALUE = BigInteger.valueOf(Short.MIN_VALUE);

	public static final BigInteger INTEGER_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);

	public static final BigInteger INTEGER_MIN_VALUE = BigInteger.valueOf(Integer.MIN_VALUE);

	public static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

	public static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

	public static final BigInteger[] EMPTY_ARRAY = new BigInteger[0];

	private static final LruMap<String, BigInteger> cache = new LruMap<String, BigInteger>(512);

	public static void clearCache() {
		cache.clear();
	}

	public static boolean notBetween(BigInteger value, Number min, Number max) {
		return !between(value, min, max);
	}

	public static boolean between(BigInteger value, Number min, Number max) {
		BigInteger left = valueOf(min);
		BigInteger right = valueOf(max);
		return value.compareTo(left) >= 0 && value.compareTo(right) <= 0;
	}

	public static boolean notIn(BigInteger value, Number min, Number max) {
		return !in(value, min, max);
	}

	public static boolean in(BigInteger value, Number min, Number max) {
		BigInteger left = valueOf(min);
		BigInteger right = valueOf(max);
		return value.compareTo(left) > 0 && value.compareTo(right) < 0;
	}

	public static BigInteger add(Number a, Number b) {
		BigInteger m = valueOf(a);
		BigInteger n = valueOf(b);
		if (m == null || n == null) {
			return CompareUtils.nullOrMax(m, n);
		}
		return m.add(n);
	}

	public static BigInteger add(String a, String b) {
		BigInteger m = parse(a);
		BigInteger n = parse(b);
		if (m == null || n == null) {
			return CompareUtils.nullOrMax(m, n);
		}
		return m.add(n);
	}

	public static String addAndFormat(Number a, Number b, String pattern, String defaultValue) {
		BigInteger val = add(a, b);
		return format(val, pattern, defaultValue);
	}

	public static String addAndFormat(String a, String b, String pattern, String defaultValue) {
		BigInteger val = add(a, b);
		return format(val, pattern, defaultValue);
	}

	public static BigInteger multiply(Number a, Number b) {
		BigInteger m = valueOf(a);
		BigInteger n = valueOf(b);
		if (m == null || n == null) {
			return CompareUtils.nullOrMax(m, n);
		}
		return m.multiply(n);
	}

	public static BigInteger multiply(String a, String b) {
		BigInteger m = parse(a);
		BigInteger n = parse(b);
		if (m == null || n == null) {
			return CompareUtils.nullOrMax(m, n);
		}
		return m.multiply(n);
	}

	public static String multiplyAndFormat(String a, String b, String pattern, String defaultValue) {
		BigInteger val = multiply(a, b);
		return format(val, pattern, defaultValue);
	}

	public static String multiplyAndFormat(Number a, Number b, String pattern, String defaultValue) {
		BigInteger val = multiply(a, b);
		return format(val, pattern, defaultValue);
	}

	public static boolean isEven(BigInteger value) {
		return value != null ? value.and(BigInteger.ONE).equals(BigInteger.ZERO) : false;
	}

	public static boolean isOdd(BigInteger value) {
		return !isEven(value);
	}

	public static BigInteger[] valuesOf(byte[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (byte a : array) {
			result[i++] = BigInteger.valueOf(a);
		}
		return result;
	}

	public static BigInteger[] valuesOf(short[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (short a : array) {
			result[i++] = BigInteger.valueOf(a);
		}
		return result;
	}

	public static BigInteger[] valuesOf(int[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (int a : array) {
			result[i++] = BigInteger.valueOf(a);
		}
		return result;
	}

	public static BigInteger[] valuesOf(long[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (long a : array) {
			result[i++] = BigInteger.valueOf(a);
		}
		return result;
	}

	public static BigInteger[] valuesOf(float[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (float a : array) {
			result[i++] = BigDecimal.valueOf(a).toBigInteger();
		}
		return result;
	}

	public static BigInteger[] valuesOf(double[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (double a : array) {
			result[i++] = BigDecimal.valueOf(a).toBigInteger();
		}
		return result;
	}

	public static BigInteger[] valuesOf(Number[] numbers) {
		return valuesOf(numbers, null);
	}

	public static BigInteger[] valuesOf(Number[] numbers, BigInteger defaultValue) {
		Assert.isNull(numbers, "Source array must not be null.");
		BigInteger[] result = new BigInteger[numbers.length];
		int i = 0;
		for (Number number : numbers) {
			result[i++] = valueOf(number, defaultValue);
		}
		return result;
	}

	public static BigInteger valueOf(Number number) {
		return valueOf(number, null);
	}

	public static BigInteger valueOf(Number number, BigInteger defaultValue) {
		if (number == null) {
			return defaultValue;
		}
		if (number instanceof BigInteger) {
			return (BigInteger) number;
		}
		if (number instanceof BigDecimal) {
			return ((BigDecimal) number).toBigInteger();
		} else {
			long low = number.longValue();
			if (low >= 0 && low < BigIntegerCache.high) {
				return BigIntegerCache.cache[(int) low];
			}
			return BigInteger.valueOf(low);
		}
	}

	public static BigInteger decode(String value) {
		int radix = 10;
		int index = 0;
		boolean negative = false;
		if (value.startsWith("-")) {
			negative = true;
			index++;
		}
		if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
			index += 2;
			radix = 16;
		} else if (value.startsWith("#", index)) {
			index++;
			radix = 16;
		} else if (value.startsWith("0", index) && value.length() > 1 + index) {
			index++;
			radix = 8;
		}
		BigInteger result = new BigInteger(value.substring(index), radix);
		return (negative ? result.negate() : result);
	}

	public static BigInteger parse(String str) {
		Assert.hasNoText(str, "Number string must not be null.");
		if (NumberUtils.isHex(str)) {
			return decode(str);
		}
		BigInteger pooled;
		if ((pooled = cache.get(str)) == null) {
			if (NumberUtils.isNumber(str)) {
				String newStr = NumberUtils.read(str);
				pooled = new BigDecimal(newStr).toBigInteger();
				cache.put(str, pooled);
			} else {
				throw new NumberFormatException("Can not parse string for: " + str);
			}
		}
		return pooled;
	}

	public static BigInteger[] parses(String[] strs) {
		return parses(strs, true);
	}

	public static BigInteger[] parses(String[] strs, boolean thrown) {
		Assert.isNull(strs, "Source array must not be null.");
		BigInteger[] result = new BigInteger[strs.length];
		int i = 0;
		for (String str : strs) {
			try {
				result[i++] = parse(str);
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return ArrayUtils.ensureCapacity(result, i);
	}

	public static BigInteger valueOf(Character c) {
		return valueOf(c, null);
	}

	public static BigInteger valueOf(Character c, BigInteger defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return BigInteger.valueOf(c.charValue());
	}

	public static BigInteger valueOf(Boolean b) {
		return valueOf(b, null);
	}

	public static BigInteger valueOf(Boolean b, BigInteger defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return b.booleanValue() ? BigInteger.ONE : BigInteger.ZERO;
	}

	public static BigInteger valueOf(String str) {
		return valueOf(str, null);
	}

	public static BigInteger valueOf(String str, BigInteger defaultValue) {
		if (StringUtils.isBlank(str)) {
			return defaultValue;
		}
		try {
			return parse(str);
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	public static BigInteger[] valuesOf(String[] strs) {
		return valuesOf(strs, null);
	}

	public static BigInteger[] valuesOf(String[] strs, BigInteger defaultValue) {
		Assert.isNull(strs, "Source array must not be null.");
		BigInteger[] result = new BigInteger[strs.length];
		int i = 0;
		for (String str : strs) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

}
