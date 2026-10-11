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
package com.github.paganini2008.devtools.math;

import static com.github.paganini2008.devtools.NumberUtils.format;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.NumberUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * Utility methods for {@link BigInteger}: conversions, parsing and null tolerant arithmetic.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
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

	private static final LruMap<String, BigInteger> cache = new LruMap<String, BigInteger>(1024);

	/**
	 * Clears the cache of parsed strings.
	 */
	public static void clearCache() {
		cache.clear();
	}

	/**
	 * Returns true if the value is outside [min, max].
	 */
	public static boolean notBetween(BigInteger value, Number min, Number max) {
		return !between(value, min, max);
	}

	/**
	 * Returns true if min &lt;= value &lt;= max.
	 *
	 * <pre>
	 * BigIntegerUtils.between(BigInteger.valueOf(5), 5, 10)  = true
	 * BigIntegerUtils.between(BigInteger.valueOf(11), 5, 10) = false
	 * </pre>
	 */
	public static boolean between(BigInteger value, Number min, Number max) {
		BigInteger left = valueOf(min);
		BigInteger right = valueOf(max);
		return value.compareTo(left) >= 0 && value.compareTo(right) <= 0;
	}

	/**
	 * Returns true if the value is not strictly between min and max.
	 */
	public static boolean notIn(BigInteger value, Number min, Number max) {
		return !in(value, min, max);
	}

	/**
	 * Returns true if min &lt; value &lt; max.
	 */
	public static boolean in(BigInteger value, Number min, Number max) {
		BigInteger left = valueOf(min);
		BigInteger right = valueOf(max);
		return value.compareTo(left) > 0 && value.compareTo(right) < 0;
	}

	/**
	 * Adds two numbers. If one operand is null the other one is returned (both null returns null).
	 *
	 * <pre>
	 * BigIntegerUtils.add(1, 2)    = 3
	 * BigIntegerUtils.add(null, 2) = 2
	 * </pre>
	 */
	public static BigInteger add(Number a, Number b) {
		BigInteger m = valueOf(a);
		BigInteger n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.add(n);
	}

	/**
	 * Adds two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank or not a number
	 */
	public static BigInteger add(String a, String b) {
		BigInteger m = parse(a);
		BigInteger n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.add(n);
	}

	/**
	 * Adds two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String addAndFormat(Number a, Number b, String pattern, String defaultValue) {
		BigInteger val = add(a, b);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Adds two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String addAndFormat(String a, String b, String pattern, String defaultValue) {
		BigInteger val = add(a, b);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Multiplies two numbers. If one operand is null the other one is returned (both null returns null).
	 */
	public static BigInteger multiply(Number a, Number b) {
		BigInteger m = valueOf(a);
		BigInteger n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.multiply(n);
	}

	/**
	 * Multiplies two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank or not a number
	 */
	public static BigInteger multiply(String a, String b) {
		BigInteger m = parse(a);
		BigInteger n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.multiply(n);
	}

	/**
	 * Multiplies two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String multiplyAndFormat(String a, String b, String pattern, String defaultValue) {
		BigInteger val = multiply(a, b);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Multiplies two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String multiplyAndFormat(Number a, Number b, String pattern, String defaultValue) {
		BigInteger val = multiply(a, b);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Checks whether a value is even, null returns false.
	 */
	public static boolean isEven(BigInteger value) {
		return value != null ? value.and(BigInteger.ONE).equals(BigInteger.ZERO) : false;
	}

	/**
	 * Checks whether a value is odd, null returns false.
	 *
	 * <pre>
	 * BigIntegerUtils.isOdd(BigInteger.valueOf(3))  = true
	 * BigIntegerUtils.isOdd(BigInteger.valueOf(-3)) = true
	 * BigIntegerUtils.isOdd(null)                   = false
	 * </pre>
	 */
	public static boolean isOdd(BigInteger value) {
		return value != null ? value.testBit(0) : false;
	}

	/**
	 * Converts each element, fractions are truncated.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigInteger[] valueOf(byte[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (byte a : array) {
			result[i++] = BigInteger.valueOf(a);
		}
		return result;
	}

	/**
	 * Converts each element, fractions are truncated.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigInteger[] valueOf(short[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (short a : array) {
			result[i++] = BigInteger.valueOf(a);
		}
		return result;
	}

	/**
	 * Converts each element, fractions are truncated.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigInteger[] valueOf(int[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (int a : array) {
			result[i++] = BigInteger.valueOf(a);
		}
		return result;
	}

	/**
	 * Converts each element, fractions are truncated.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigInteger[] valueOf(long[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (long a : array) {
			result[i++] = BigInteger.valueOf(a);
		}
		return result;
	}

	/**
	 * Converts each element, fractions are truncated.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigInteger[] valueOf(float[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (float a : array) {
			result[i++] = BigDecimal.valueOf(a).toBigInteger();
		}
		return result;
	}

	/**
	 * Converts each element, fractions are truncated.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigInteger[] valueOf(double[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigInteger[] result = new BigInteger[array.length];
		int i = 0;
		for (double a : array) {
			result[i++] = BigDecimal.valueOf(a).toBigInteger();
		}
		return result;
	}

	/**
	 * Converts each number, null elements stay null.
	 */
	public static BigInteger[] valueOf(Number[] numbers) {
		return valueOf(numbers, null);
	}

	/**
	 * Converts each number, null elements become the default value.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigInteger[] valueOf(Number[] numbers, BigInteger defaultValue) {
		Assert.isNull(numbers, "Source array must not be null.");
		BigInteger[] result = new BigInteger[numbers.length];
		int i = 0;
		for (Number number : numbers) {
			result[i++] = valueOf(number, defaultValue);
		}
		return result;
	}

	/**
	 * Converts a number to a {@code BigInteger}, fractions are truncated, null returns null.
	 *
	 * <pre>
	 * BigIntegerUtils.valueOf(new BigDecimal("12.9")) = 12
	 * BigIntegerUtils.valueOf((Number) null)         = null
	 * </pre>
	 */
	public static BigInteger valueOf(Number number) {
		return valueOf(number, null);
	}

	/**
	 * Converts a number to a {@code BigInteger}, fractions are truncated, returning the default value for null.
	 */
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

	/**
	 * Decodes a decimal, hexadecimal ({@code 0x}, {@code 0X}, {@code #}) or octal (leading {@code 0}) string,
	 * optionally prefixed with {@code -}.
	 *
	 * <pre>
	 * BigIntegerUtils.decode("0xff") = 255
	 * BigIntegerUtils.decode("-#ff") = -255
	 * BigIntegerUtils.decode("010")  = 8
	 * </pre>
	 *
	 * @throws NumberFormatException if the string is not a valid number
	 */
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

	/**
	 * Parses a decimal or hexadecimal string, fractions are truncated. Results are cached.
	 *
	 * <pre>
	 * BigIntegerUtils.parse("12.7") = 12
	 * BigIntegerUtils.parse("0xFF") = 255
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the string is blank
	 * @throws NumberFormatException if the string is not a number
	 */
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

	/**
	 * Parses each string, failing on the first invalid one.
	 *
	 * @throws IllegalArgumentException if a string is blank or not a number
	 */
	public static BigInteger[] parseMany(String[] strings) {
		return parseMany(strings, true);
	}

	/**
	 * Parses each string.
	 *
	 * @param thrown true to fail on an invalid string, false to skip it (the result is then shorter)
	 */
	public static BigInteger[] parseMany(String[] strings, boolean thrown) {
		Assert.isNull(strings, "Source array must not be null.");
		BigInteger[] result = new BigInteger[strings.length];
		int i = 0;
		for (String str : strings) {
			try {
				// parse first, so that a failed element does not leave a null hole in the result
				BigInteger value = parse(str);
				result[i++] = value;
			} catch (IllegalArgumentException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return ArrayUtils.ensureCapacity(result, i);
	}

	/**
	 * Returns the code of a character, null returns null.
	 */
	public static BigInteger valueOf(Character c) {
		return valueOf(c, null);
	}

	/**
	 * Returns the code of a character, returning the default value for null.
	 */
	public static BigInteger valueOf(Character c, BigInteger defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return BigInteger.valueOf(c.charValue());
	}

	/**
	 * Converts true to 1 and false to 0, null returns null.
	 */
	public static BigInteger valueOf(Boolean b) {
		return valueOf(b, null);
	}

	/**
	 * Converts true to 1 and false to 0, returning the default value for null.
	 */
	public static BigInteger valueOf(Boolean b, BigInteger defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return b.booleanValue() ? BigInteger.ONE : BigInteger.ZERO;
	}

	/**
	 * Parses a string, returning null if it is blank or not a number.
	 *
	 * <pre>
	 * BigIntegerUtils.valueOf("12")  = 12
	 * BigIntegerUtils.valueOf("abc") = null
	 * BigIntegerUtils.valueOf(" ")   = null
	 * </pre>
	 */
	public static BigInteger valueOf(String str) {
		return valueOf(str, null);
	}

	/**
	 * Parses a string, returning the default value if it is blank or not a number.
	 */
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

	/**
	 * Parses each string, invalid elements become null.
	 */
	public static BigInteger[] valueOf(String[] strings) {
		return valueOf(strings, null);
	}

	/**
	 * Parses each string, invalid elements become the default value.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigInteger[] valueOf(String[] strings, BigInteger defaultValue) {
		Assert.isNull(strings, "Source array must not be null.");
		BigInteger[] result = new BigInteger[strings.length];
		int i = 0;
		for (String str : strings) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

}
