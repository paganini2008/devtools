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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.Comparables;
import com.github.paganini2008.devtools.NumberUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * Utility methods for {@link BigDecimal}: conversions, parsing, comparison, null tolerant arithmetic and rounding.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class BigDecimalUtils {

	public static final BigDecimal PI = BigDecimal.valueOf(Math.PI);

	public static final BigDecimal E = BigDecimal.valueOf(Math.E);

	public static final BigDecimal BYTE_MAX_VALUE = BigDecimal.valueOf(Byte.MAX_VALUE);

	public static final BigDecimal BYTE_MIN_VALUE = BigDecimal.valueOf(Byte.MIN_VALUE);

	public static final BigDecimal SHORT_MAX_VALUE = BigDecimal.valueOf(Short.MAX_VALUE);

	public static final BigDecimal SHORT_MIN_VALUE = BigDecimal.valueOf(Short.MIN_VALUE);

	public static final BigDecimal INTEGER_MAX_VALUE = BigDecimal.valueOf(Integer.MAX_VALUE);

	public static final BigDecimal INTEGER_MIN_VALUE = BigDecimal.valueOf(Integer.MIN_VALUE);

	public static final BigDecimal LONG_MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE);

	public static final BigDecimal LONG_MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);

	public static final BigDecimal FLOAT_MAX_VALUE = BigDecimal.valueOf(Float.MAX_VALUE);

	public static final BigDecimal FLOAT_MIN_VALUE = BigDecimal.valueOf(Float.MIN_VALUE);

	public static final BigDecimal DOUBLE_MAX_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);

	public static final BigDecimal DOUBLE_MIN_VALUE = BigDecimal.valueOf(Double.MIN_VALUE);

	public static final BigDecimal[] EMPTY_ARRAY = new BigDecimal[0];

	private static final LruMap<String, BigDecimal> parserCache = new LruMap<String, BigDecimal>(1024);

	private static final LruMap<String, String> stringPlainStringCache = new LruMap<String, String>(32);

	private static final LruMap<Number, String> numberPlainStringCache = new LruMap<Number, String>(32);

	/**
	 * Clears the caches of parsed strings and plain strings.
	 */
	public static void clearCache() {
		parserCache.clear();
		stringPlainStringCache.clear();
		numberPlainStringCache.clear();
	}

	/**
	 * Converts each element.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigDecimal[] valueOf(byte[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (byte a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	/**
	 * Converts each element.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigDecimal[] valueOf(short[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (short a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	/**
	 * Converts each element.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigDecimal[] valueOf(int[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (int a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	/**
	 * Converts each element.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigDecimal[] valueOf(long[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (long a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	/**
	 * Converts each element using {@link BigDecimal#valueOf(double)}.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigDecimal[] valueOf(float[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (float a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	/**
	 * Converts each element using {@link BigDecimal#valueOf(double)}.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigDecimal[] valueOf(double[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (double a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	/**
	 * Converts each number, null elements stay null.
	 */
	public static BigDecimal[] valueOf(Number[] numbers) {
		return valueOf(numbers, null);
	}

	/**
	 * Converts each number, null elements become the default value.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigDecimal[] valueOf(Number[] numbers, BigDecimal defaultValue) {
		Assert.isNull(numbers, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[numbers.length];
		int i = 0;
		for (Number number : numbers) {
			result[i++] = valueOf(number, defaultValue);
		}
		return result;
	}

	/**
	 * Converts a number to a {@code BigDecimal} using its string form, null, NaN and infinity return null.
	 *
	 * <pre>
	 * BigDecimalUtils.valueOf(1.1f)       = 1.1
	 * BigDecimalUtils.valueOf(Double.NaN) = null
	 * </pre>
	 */
	public static BigDecimal valueOf(Number number) {
		return valueOf(number, null);
	}

	/**
	 * Converts a number to a {@code BigDecimal}, returning the default value for null, NaN and infinity.
	 */
	public static BigDecimal valueOf(Number number, BigDecimal defaultValue) {
		if (number == null) {
			return defaultValue;
		}
		if (number instanceof BigDecimal) {
			return (BigDecimal) number;
		}
		if (number instanceof BigInteger) {
			return new BigDecimal((BigInteger) number);
		}
		if ((number instanceof Double || number instanceof Float) && !Double.isFinite(number.doubleValue())) {
			return defaultValue;
		}
		return new BigDecimal(number.toString());
	}

	/**
	 * Parses a number string (e.g. {@code ".5"}, {@code "+1"}, {@code "1E+3"}). Results are cached.
	 *
	 * @throws IllegalArgumentException if the string is blank
	 * @throws NumberFormatException if the string is not a number
	 */
	public static BigDecimal parse(String str) {
		Assert.hasNoText(str, "Number string must not be null.");
		BigDecimal pooled;
		if ((pooled = parserCache.get(str)) == null) {
			if (NumberUtils.isNumber(str)) {
				pooled = new BigDecimal(NumberUtils.read(str));
				parserCache.put(str, pooled);
			} else {
				throw new NumberFormatException("Can not parse string for: " + str);
			}
		}
		return pooled;
	}

	/**
	 * Parses each string, failing on the first invalid one.
	 */
	public static BigDecimal[] parseMany(String[] strings) {
		return parseMany(strings, true);
	}

	/**
	 * Parses each string.
	 *
	 * @param thrown true to fail on an invalid string, false to skip it (the result is then shorter)
	 */
	public static BigDecimal[] parseMany(String[] strings, boolean thrown) {
		Assert.isNull(strings, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[strings.length];
		int i = 0;
		for (String str : strings) {
			try {
				// parse first, so that a failed element does not leave a null hole in the result
				BigDecimal value = parse(str);
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
	public static BigDecimal valueOf(Character c) {
		return valueOf(c, null);
	}

	/**
	 * Returns the code of a character, returning the default value for null.
	 */
	public static BigDecimal valueOf(Character c, BigDecimal defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return BigDecimal.valueOf(c.charValue());
	}

	/**
	 * Converts true to 1 and false to 0, null returns null.
	 */
	public static BigDecimal valueOf(Boolean b) {
		return valueOf(b, null);
	}

	/**
	 * Converts true to 1 and false to 0, returning the default value for null.
	 */
	public static BigDecimal valueOf(Boolean b, BigDecimal defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return b.booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
	}

	/**
	 * Parses a string, returning null if it is blank or not a number.
	 */
	public static BigDecimal valueOf(String str) {
		return valueOf(str, null);
	}

	/**
	 * Parses a string, returning the default value if it is blank or not a number.
	 */
	public static BigDecimal valueOf(String str, BigDecimal defaultValue) {
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
	public static BigDecimal[] valueOf(String[] strings) {
		return valueOf(strings, null);
	}

	/**
	 * Parses each string, invalid elements become the default value.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static BigDecimal[] valueOf(String[] strings, BigDecimal defaultValue) {
		Assert.isNull(strings, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[strings.length];
		int i = 0;
		for (String str : strings) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

	/**
	 * Returns true if left &lt; right (compared by value, scale ignored).
	 */
	public static boolean lt(String left, String right) {
		return compareTo(left, right) < 0;
	}

	/**
	 * Returns true if left &lt;= right (compared by value, scale ignored).
	 */
	public static boolean lte(String left, String right) {
		return compareTo(left, right) <= 0;
	}

	/**
	 * Returns true if left &gt; right (compared by value, scale ignored).
	 */
	public static boolean gt(String left, String right) {
		return compareTo(left, right) > 0;
	}

	/**
	 * Returns true if left &gt;= right (compared by value, scale ignored).
	 */
	public static boolean gte(String left, String right) {
		return compareTo(left, right) >= 0;
	}

	/**
	 * Returns true if both are numerically equal, e.g. {@code eq("2", "2.00") = true}.
	 */
	public static boolean eq(String left, String right) {
		return compareTo(left, right) == 0;
	}

	/**
	 * Returns true if left &lt; right (compared by value, scale ignored).
	 */
	public static boolean lt(Number left, Number right) {
		return compareTo(left, right) < 0;
	}

	/**
	 * Returns true if left &lt;= right (compared by value, scale ignored).
	 */
	public static boolean lte(Number left, Number right) {
		return compareTo(left, right) <= 0;
	}

	/**
	 * Returns true if left &gt; right (compared by value, scale ignored).
	 */
	public static boolean gt(Number left, Number right) {
		return compareTo(left, right) > 0;
	}

	/**
	 * Returns true if left &gt;= right (compared by value, scale ignored).
	 */
	public static boolean gte(Number left, Number right) {
		return compareTo(left, right) >= 0;
	}

	/**
	 * Returns true if both are numerically equal, e.g. {@code eq("2", "2.00") = true}.
	 */
	public static boolean eq(Number left, Number right) {
		return compareTo(left, right) == 0;
	}

	/**
	 * Compares two parsed numbers by value.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static int compareTo(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		return Comparables.compareTo(m, n);
	}

	/**
	 * Compares two numbers by value.
	 */
	public static int compareTo(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		return Comparables.compareTo(m, n);
	}

	/**
	 * Returns the minimum of the numbers.
	 *
	 * @throws IllegalArgumentException if the array is null or empty
	 */
	public static BigDecimal min(Number[] array) {
		Assert.isTrue(ArrayUtils.isEmpty(array), "Empty array.");
		BigDecimal min = valueOf(array[0]);
		for (Number arg : array) {
			min = min(min, arg);
		}
		return min;
	}

	/**
	 * Returns the maximum of the numbers.
	 *
	 * @throws IllegalArgumentException if the array is null or empty
	 */
	public static BigDecimal max(Number[] array) {
		Assert.isTrue(ArrayUtils.isEmpty(array), "Empty array.");
		BigDecimal max = null;
		for (Number arg : array) {
			max = max(max, arg);
		}
		return max;
	}

	/**
	 * Returns the average rounded half up to the scale.
	 *
	 * <pre>
	 * BigDecimalUtils.avg(new Number[] { 3, 1.5, 2 }, 2) = 2.17
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the array is null or empty
	 */
	public static BigDecimal avg(Number[] array, int scale) {
		Assert.isTrue(ArrayUtils.isEmpty(array), "Empty array.");
		BigDecimal sum = sum(array);
		return divide(sum, array.length, scale, RoundingMode.HALF_UP);
	}

	/**
	 * Returns the sum of the numbers, null elements are ignored.
	 *
	 * @throws IllegalArgumentException if the array is null or empty
	 */
	public static BigDecimal sum(Number[] array) {
		Assert.isTrue(ArrayUtils.isEmpty(array), "Empty array.");
		BigDecimal sum = BigDecimal.ZERO;
		for (Number arg : array) {
			sum = add(sum, arg);
		}
		return sum;
	}

	/**
	 * Returns true if the value is outside [a, b].
	 */
	public static boolean notBetween(BigDecimal value, Number a, Number b) {
		return !between(value, a, b);
	}

	/**
	 * Returns true if a &lt;= value &lt;= b.
	 */
	public static boolean between(BigDecimal value, Number a, Number b) {
		BigDecimal left = valueOf(a);
		BigDecimal right = valueOf(b);
		return value.compareTo(left) >= 0 && value.compareTo(right) <= 0;
	}

	/**
	 * Returns true if the value is not strictly between a and b.
	 */
	public static boolean notIn(BigDecimal value, Number a, Number b) {
		return !in(value, a, b);
	}

	/**
	 * Returns true if a &lt; value &lt; b.
	 */
	public static boolean in(BigDecimal value, Number a, Number b) {
		BigDecimal left = valueOf(a);
		BigDecimal right = valueOf(b);
		return value.compareTo(left) > 0 && value.compareTo(right) < 0;
	}

	/**
	 * Returns true if the value is outside [a, b].
	 */
	public static boolean notBetween(BigDecimal value, String a, String b) {
		return !between(value, a, b);
	}

	/**
	 * Returns true if a &lt;= value &lt;= b.
	 */
	public static boolean between(BigDecimal value, String a, String b) {
		BigDecimal left = parse(a);
		BigDecimal right = parse(b);
		return value.compareTo(left) >= 0 && value.compareTo(right) <= 0;
	}

	/**
	 * Returns true if the value is not strictly between a and b.
	 */
	public static boolean notIn(BigDecimal value, String a, String b) {
		return !in(value, a, b);
	}

	/**
	 * Returns true if a &lt; value &lt; b.
	 */
	public static boolean in(BigDecimal value, String a, String b) {
		BigDecimal left = parse(a);
		BigDecimal right = parse(b);
		return value.compareTo(left) > 0 && value.compareTo(right) < 0;
	}

	/**
	 * Returns the larger value. If one operand is null the other one is returned.
	 */
	public static BigDecimal max(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.max(n);
	}

	/**
	 * Returns the larger of two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal max(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.max(n);
	}

	/**
	 * Returns the smaller value. If one operand is null, null is returned.
	 */
	public static BigDecimal min(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMin(m, n);
		}
		return m.min(n);
	}

	/**
	 * Returns the smaller of two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal min(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMin(m, n);
		}
		return m.min(n);
	}

	/**
	 * Raises a number to the power with the math context, null returns null.
	 */
	public static BigDecimal pow(Number arg, int n, MathContext mc) {
		BigDecimal value = valueOf(arg);
		return value != null ? value.pow(n, mc) : null;
	}

	/**
	 * Raises a number to the power, null returns null.
	 */
	public static BigDecimal pow(Number arg, int n) {
		BigDecimal value = valueOf(arg);
		return value != null ? value.pow(n) : null;
	}

	/**
	 * Raises a parsed number to the power.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal pow(String arg, int n, MathContext mc) {
		BigDecimal value = parse(arg);
		return value != null ? value.pow(n, mc) : null;
	}

	/**
	 * Raises a parsed number to the power.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal pow(String arg, int n) {
		BigDecimal value = parse(arg);
		return value != null ? value.pow(n) : null;
	}

	/**
	 * Returns the absolute value, null returns null.
	 */
	public static BigDecimal abs(Number arg) {
		BigDecimal value = valueOf(arg);
		return value != null ? value.abs() : null;
	}

	/**
	 * Returns the absolute value of a parsed number.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal abs(String arg) {
		BigDecimal value = parse(arg);
		return value != null ? value.abs() : null;
	}

	/**
	 * Adds two numbers. If one operand is null the other one is returned (both null returns null).
	 *
	 * <pre>
	 * BigDecimalUtils.add(1, 2.5)    = 3.5
	 * BigDecimalUtils.add(null, 2.5) = 2.5
	 * </pre>
	 */
	public static BigDecimal add(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.add(n);
	}

	/**
	 * Adds two numbers with the math context. If one operand is null the other one is returned.
	 */
	public static BigDecimal add(Number a, Number b, MathContext mc) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.add(n, mc);
	}

	/**
	 * Adds two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal add(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.add(n);
	}

	/**
	 * Adds two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal add(String a, String b, MathContext mc) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.add(n, mc);
	}

	/**
	 * Adds two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String addAndFormat(Number a, Number b, String pattern, String defaultValue) {
		BigDecimal val = add(a, b);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Adds two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String addAndFormat(String a, String b, String pattern, String defaultValue) {
		BigDecimal val = add(a, b);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Adds two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String addAndFormat(Number a, Number b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = add(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Adds two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String addAndFormat(String a, String b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = add(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Subtracts b from a. A null operand is ignored: {@code a - null = a} and {@code null - b = -b}.
	 *
	 * <pre>
	 * BigDecimalUtils.subtract(1, 2.5)    = -1.5
	 * BigDecimalUtils.subtract(1, null)   = 1
	 * BigDecimalUtils.subtract(null, 2.5) = -2.5
	 * </pre>
	 */
	public static BigDecimal subtract(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			// a null operand is ignored: a - null = a, null - b = -b
			return m != null ? m : (n != null ? n.negate() : null);
		}
		return m.subtract(n);
	}

	/**
	 * Subtracts two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal subtract(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			// a null operand is ignored: a - null = a, null - b = -b
			return m != null ? m : (n != null ? n.negate() : null);
		}
		return m.subtract(n);
	}

	/**
	 * Subtracts b from a with the math context. A null operand is ignored: {@code a - null = a} and {@code null - b = -b}.
	 */
	public static BigDecimal subtract(Number a, Number b, MathContext mc) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			// a null operand is ignored: a - null = a, null - b = -b
			return m != null ? m : (n != null ? n.negate() : null);
		}
		return m.subtract(n, mc);
	}

	/**
	 * Subtracts two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal subtract(String a, String b, MathContext mc) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			// a null operand is ignored: a - null = a, null - b = -b
			return m != null ? m : (n != null ? n.negate() : null);
		}
		return m.subtract(n, mc);
	}

	/**
	 * Subtracts two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String subtractAndFormat(String a, String b, String pattern, String defaultValue) {
		BigDecimal val = subtract(a, b);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Subtracts two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String subtractAndFormat(Number a, Number b, String pattern, String defaultValue) {
		BigDecimal val = subtract(a, b);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Subtracts two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String subtractAndFormat(String a, String b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = subtract(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Subtracts two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String subtractAndFormat(Number a, Number b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = subtract(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Multiplies two numbers. If one operand is null the other one is returned (both null returns null).
	 */
	public static BigDecimal multiply(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.multiply(n);
	}

	/**
	 * Multiplies two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal multiply(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.multiply(n);
	}

	/**
	 * Multiplies two numbers with the math context. If one operand is null the other one is returned.
	 */
	public static BigDecimal multiply(Number a, Number b, MathContext mc) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.multiply(n, mc);
	}

	/**
	 * Multiplies two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal multiply(String a, String b, MathContext mc) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.multiply(n, mc);
	}

	/**
	 * Multiplies two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String multiplyAndFormat(String a, String b, String pattern, String defaultValue) {
		BigDecimal val = multiply(a, b);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Multiplies two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String multiplyAndFormat(Number a, Number b, String pattern, String defaultValue) {
		BigDecimal val = multiply(a, b);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Multiplies two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String multiplyAndFormat(String a, String b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = multiply(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Multiplies two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String multiplyAndFormat(Number a, Number b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = multiply(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Divides a by b with the math context. A null divisor is ignored ({@code a / null = a}),
	 * a null dividend or a division error (e.g. by zero) returns null.
	 *
	 * <pre>
	 * BigDecimalUtils.divide(1, 3, new MathContext(3)) = 0.333
	 * BigDecimalUtils.divide(1, 0, new MathContext(3)) = null
	 * </pre>
	 */
	public static BigDecimal divide(Number a, Number b, MathContext mc) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			// a null divisor is ignored (a / null = a), a null dividend gives null
			return m;
		}
		try {
			return m.divide(n, mc);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	/**
	 * Divides two parsed numbers, a division error (e.g. by zero) returns null.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal divide(String a, String b, MathContext mc) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			// a null divisor is ignored (a / null = a), a null dividend gives null
			return m;
		}
		try {
			return m.divide(n, mc);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	/**
	 * Divides a by b to the scale. A null divisor is ignored ({@code a / null = a}),
	 * a null dividend or a division error (e.g. by zero) returns null.
	 */
	public static BigDecimal divide(Number a, Number b, int scale, RoundingMode roundingMode) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			// a null divisor is ignored (a / null = a), a null dividend gives null
			return m;
		}
		try {
			return m.divide(n, scale, roundingMode);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	/**
	 * Divides two parsed numbers, a division error (e.g. by zero) returns null.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal divide(String a, String b, int scale, RoundingMode roundingMode) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			// a null divisor is ignored (a / null = a), a null dividend gives null
			return m;
		}
		try {
			return m.divide(n, scale, roundingMode);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	/**
	 * Divides two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String divideAndFormat(String a, String b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = divide(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Divides two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String divideAndFormat(Number a, Number b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = divide(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Divides two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String divideAndFormat(String a, String b, int scale, RoundingMode roundingMode, String pattern, String defaultValue) {
		BigDecimal val = divide(a, b, scale, roundingMode);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Divides two numbers and formats the result with a {@code DecimalFormat} pattern, returning the default value for null.
	 */
	public static String divideAndFormat(Number a, Number b, int scale, RoundingMode roundingMode, String pattern, String defaultValue) {
		BigDecimal val = divide(a, b, scale, roundingMode);
		return format(val, pattern, defaultValue);
	}

	/**
	 * Sets the scale with {@code RoundingMode.HALF_UP}.
	 */
	public static BigDecimal round(String value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_UP);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.HALF_UP}, e.g. {@code round(2.5) = 3}.
	 */
	public static BigInteger round(String value) {
		return setScale(value, RoundingMode.HALF_UP);
	}

	/**
	 * Sets the scale with {@code RoundingMode.HALF_UP}.
	 */
	public static BigDecimal round(Number value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_UP);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.HALF_UP}, e.g. {@code round(2.5) = 3}.
	 */
	public static BigInteger round(Number value) {
		return setScale(value, RoundingMode.HALF_UP);
	}

	/**
	 * Sets the scale with {@code RoundingMode.FLOOR}.
	 */
	public static BigDecimal floor(String value, int scale) {
		return setScale(value, scale, RoundingMode.FLOOR);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.FLOOR}, e.g. {@code floor(-2.1) = -3}.
	 */
	public static BigInteger floor(String value) {
		return setScale(value, RoundingMode.FLOOR);
	}

	/**
	 * Sets the scale with {@code RoundingMode.FLOOR}.
	 */
	public static BigDecimal floor(Number value, int scale) {
		return setScale(value, scale, RoundingMode.FLOOR);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.FLOOR}, e.g. {@code floor(-2.1) = -3}.
	 */
	public static BigInteger floor(Number value) {
		return setScale(value, RoundingMode.FLOOR);
	}

	/**
	 * Sets the scale with {@code RoundingMode.CEILING}.
	 */
	public static BigDecimal ceil(String value, int scale) {
		return setScale(value, scale, RoundingMode.CEILING);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.CEILING}, e.g. {@code ceil(2.1) = 3}.
	 */
	public static BigInteger ceil(String value) {
		return setScale(value, RoundingMode.CEILING);
	}

	/**
	 * Sets the scale with {@code RoundingMode.CEILING}.
	 */
	public static BigDecimal ceil(Number value, int scale) {
		return setScale(value, scale, RoundingMode.CEILING);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.CEILING}, e.g. {@code ceil(2.1) = 3}.
	 */
	public static BigInteger ceil(Number value) {
		return setScale(value, RoundingMode.CEILING);
	}

	/**
	 * Sets the scale with {@code RoundingMode.DOWN}.
	 */
	public static BigDecimal down(String value, int scale) {
		return setScale(value, scale, RoundingMode.DOWN);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.DOWN}, e.g. {@code down(-2.9) = -2}.
	 */
	public static BigInteger down(String value) {
		return setScale(value, RoundingMode.DOWN);
	}

	/**
	 * Sets the scale with {@code RoundingMode.DOWN}.
	 */
	public static BigDecimal down(Number value, int scale) {
		return setScale(value, scale, RoundingMode.DOWN);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.DOWN}, e.g. {@code down(-2.9) = -2}.
	 */
	public static BigInteger down(Number value) {
		return setScale(value, RoundingMode.DOWN);
	}

	/**
	 * Sets the scale with {@code RoundingMode.UP}.
	 */
	public static BigDecimal up(String value, int scale) {
		return setScale(value, scale, RoundingMode.UP);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.UP}, e.g. {@code up(-2.1) = -3}.
	 */
	public static BigInteger up(String value) {
		return setScale(value, RoundingMode.UP);
	}

	/**
	 * Sets the scale with {@code RoundingMode.UP}.
	 */
	public static BigDecimal up(Number value, int scale) {
		return setScale(value, scale, RoundingMode.UP);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.UP}, e.g. {@code up(-2.1) = -3}.
	 */
	public static BigInteger up(Number value) {
		return setScale(value, RoundingMode.UP);
	}

	/**
	 * Sets the scale with {@code RoundingMode.HALF_DOWN}.
	 */
	public static BigDecimal roundDown(String value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_DOWN);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.HALF_DOWN}, e.g. {@code roundDown(2.5) = 2}.
	 */
	public static BigInteger roundDown(String value) {
		return setScale(value, RoundingMode.HALF_DOWN);
	}

	/**
	 * Sets the scale with {@code RoundingMode.HALF_DOWN}.
	 */
	public static BigDecimal roundDown(Number value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_DOWN);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.HALF_DOWN}, e.g. {@code roundDown(2.5) = 2}.
	 */
	public static BigInteger roundDown(Number value) {
		return setScale(value, RoundingMode.HALF_DOWN);
	}

	/**
	 * Sets the scale with {@code RoundingMode.HALF_EVEN}.
	 */
	public static BigDecimal roundEven(String value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_EVEN);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.HALF_EVEN}, e.g. {@code roundEven(2.5) = 2}.
	 */
	public static BigInteger roundEven(String value) {
		return setScale(value, RoundingMode.HALF_EVEN);
	}

	/**
	 * Sets the scale with {@code RoundingMode.HALF_EVEN}.
	 */
	public static BigDecimal roundEven(Number value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_EVEN);
	}

	/**
	 * Rounds to an integer with {@code RoundingMode.HALF_EVEN}, e.g. {@code roundEven(2.5) = 2}.
	 */
	public static BigInteger roundEven(Number value) {
		return setScale(value, RoundingMode.HALF_EVEN);
	}

	/**
	 * Rounds a parsed number to an integer with the rounding mode.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigInteger setScale(String value, RoundingMode roundingMode) {
		BigDecimal val = setScale(value, 0, roundingMode);
		return val != null ? val.toBigInteger() : null;
	}

	/**
	 * Rounds a number to an integer with the rounding mode, null returns null.
	 */
	public static BigInteger setScale(Number value, RoundingMode roundingMode) {
		BigDecimal val = setScale(value, 0, roundingMode);
		return val != null ? val.toBigInteger() : null;
	}

	/**
	 * Sets the scale of a parsed number, a negative scale leaves the value unchanged.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal setScale(String string, int scale, RoundingMode roundingMode) {
		BigDecimal value = parse(string);
		if (value != null) {
			return scale >= 0 ? value.setScale(scale, roundingMode) : value;
		}
		return null;
	}

	/**
	 * Sets the scale of a number, a negative scale leaves the value unchanged, null returns null.
	 */
	public static BigDecimal setScale(Number number, int scale, RoundingMode roundingMode) {
		BigDecimal value = valueOf(number);
		if (value != null) {
			return scale >= 0 ? value.setScale(scale, roundingMode) : value;
		}
		return null;
	}

	/**
	 * Returns the integer quotient and the remainder of two parsed numbers.
	 *
	 * @throws IllegalArgumentException if a string is blank
	 */
	public static BigDecimal[] divideAndRemainder(String a, String b, MathContext mc) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return new BigDecimal[] { m, n };
		}
		return m.divideAndRemainder(n, mc);
	}

	/**
	 * Returns the integer quotient and the remainder, or {@code [a, b]} if any operand is null.
	 */
	public static BigDecimal[] divideAndRemainder(Number a, Number b, MathContext mc) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return new BigDecimal[] { m, n };
		}
		return m.divideAndRemainder(n, mc);
	}

	/**
	 * Converts a number string to plain notation, returning "" if it is not a number.
	 *
	 * <pre>
	 * BigDecimalUtils.toPlainString("1E+3") = "1000"
	 * BigDecimalUtils.toPlainString(".5")   = "0.5"
	 * BigDecimalUtils.toPlainString("abc")  = ""
	 * </pre>
	 */
	public static String toPlainString(String str) {
		return toPlainString(str, "");
	}

	/**
	 * Converts a number string to plain notation, returning the default value if it is not a number.
	 */
	public static String toPlainString(String str, String defaultValue) {
		if (NumberUtils.isNotNumber(str)) {
			return defaultValue;
		}
		String repr;
		if (null == (repr = stringPlainStringCache.get(str))) {
			repr = parse(str).toPlainString();
			stringPlainStringCache.put(str, repr);
		}
		return repr;
	}

	/**
	 * Converts a number to plain notation (no exponent), null returns "".
	 */
	public static String toPlainString(Number n) {
		return toPlainString(n, "");
	}

	/**
	 * Converts a number to plain notation, returning the default value for null, NaN and infinity.
	 */
	public static String toPlainString(Number n, String defaultValue) {
		if (n == null) {
			return defaultValue;
		}
		if (n instanceof Byte || n instanceof Short || n instanceof Integer || n instanceof Long) {
			return n.toString();
		}
		String repr;
		if (null == (repr = numberPlainStringCache.get(n))) {
			BigDecimal value = valueOf(n);
			if (value == null) {
				return defaultValue;
			}
			repr = value.toPlainString();
			numberPlainStringCache.put(n, repr);
		}
		return repr;
	}

	/**
	 * Returns the absolute scale of the value rounded to one significant digit,
	 * e.g. 2 for {@code 123.45} and 3 for {@code 0.00123}.
	 */
	public static int getPrecision(BigDecimal value) {
		return Math.abs(value.round(new MathContext(1)).scale());
	}

	/**
	 * Returns the number of decimal places ignoring trailing zeros, e.g. 2 for {@code 1.2300}.
	 */
	public static int getScale(BigDecimal value) {
		String str = value.stripTrailingZeros().toPlainString();
		int index = str.indexOf(".");
		return index < 0 ? 0 : str.length() - index - 1;
	}

	/**
	 * Merges two maps, summing the values of common keys. Keys of {@code m1} come first.
	 */
	public static <K, V extends Number> Map<K, BigDecimal> mergeAndSum(Map<K, V> m1, Map<K, V> m2) {
		Map<K, BigDecimal> results = new LinkedHashMap<K, BigDecimal>();
		K key;
		BigDecimal value;
		for (Map.Entry<K, V> en : m1.entrySet()) {
			key = en.getKey();
			if (m2.containsKey(key)) {
				value = add(en.getValue(), m2.get(key));
			} else {
				value = valueOf(en.getValue());
			}
			results.put(key, value);
		}
		for (Map.Entry<K, V> en : m2.entrySet()) {
			key = en.getKey();
			if (!results.containsKey(key)) {
				results.put(key, valueOf(en.getValue()));
			}
		}
		return results;
	}

}
