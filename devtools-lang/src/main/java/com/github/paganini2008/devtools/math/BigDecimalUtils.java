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
 * BigDecimalUtils
 * 
 * @author Fred Feng
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

	public static void clearCache() {
		parserCache.clear();
		stringPlainStringCache.clear();
		numberPlainStringCache.clear();
	}

	public static BigDecimal[] valueOf(byte[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (byte a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	public static BigDecimal[] valueOf(short[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (short a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	public static BigDecimal[] valueOf(int[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (int a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	public static BigDecimal[] valueOf(long[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (long a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	public static BigDecimal[] valueOf(float[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (float a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	public static BigDecimal[] valueOf(double[] array) {
		Assert.isNull(array, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[array.length];
		int i = 0;
		for (double a : array) {
			result[i++] = BigDecimal.valueOf(a);
		}
		return result;
	}

	public static BigDecimal[] valueOf(Number[] numbers) {
		return valueOf(numbers, null);
	}

	public static BigDecimal[] valueOf(Number[] numbers, BigDecimal defaultValue) {
		Assert.isNull(numbers, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[numbers.length];
		int i = 0;
		for (Number number : numbers) {
			result[i++] = valueOf(number, defaultValue);
		}
		return result;
	}

	public static BigDecimal valueOf(Number number) {
		return valueOf(number, null);
	}

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
		return new BigDecimal(number.toString());
	}

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

	public static BigDecimal[] parseMany(String[] strings) {
		return parseMany(strings, true);
	}

	public static BigDecimal[] parseMany(String[] strings, boolean thrown) {
		Assert.isNull(strings, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[strings.length];
		int i = 0;
		for (String str : strings) {
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

	public static BigDecimal valueOf(Character c) {
		return valueOf(c, null);
	}

	public static BigDecimal valueOf(Character c, BigDecimal defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return BigDecimal.valueOf(c.charValue());
	}

	public static BigDecimal valueOf(Boolean b) {
		return valueOf(b, null);
	}

	public static BigDecimal valueOf(Boolean b, BigDecimal defaultValue) {
		if (b == null) {
			return defaultValue;
		}
		return b.booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
	}

	public static BigDecimal valueOf(String str) {
		return valueOf(str, null);
	}

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

	public static BigDecimal[] valueOf(String[] strings) {
		return valueOf(strings, null);
	}

	public static BigDecimal[] valueOf(String[] strings, BigDecimal defaultValue) {
		Assert.isNull(strings, "Source array must not be null.");
		BigDecimal[] result = new BigDecimal[strings.length];
		int i = 0;
		for (String str : strings) {
			result[i++] = valueOf(str, defaultValue);
		}
		return result;
	}

	public static boolean lt(String left, String right) {
		return compareTo(left, right) < 0;
	}

	public static boolean lte(String left, String right) {
		return compareTo(left, right) <= 0;
	}

	public static boolean gt(String left, String right) {
		return compareTo(left, right) > 0;
	}

	public static boolean gte(String left, String right) {
		return compareTo(left, right) >= 0;
	}

	public static boolean eq(String left, String right) {
		return compareTo(left, right) == 0;
	}

	public static boolean lt(Number left, Number right) {
		return compareTo(left, right) < 0;
	}

	public static boolean lte(Number left, Number right) {
		return compareTo(left, right) <= 0;
	}

	public static boolean gt(Number left, Number right) {
		return compareTo(left, right) > 0;
	}

	public static boolean gte(Number left, Number right) {
		return compareTo(left, right) >= 0;
	}

	public static boolean eq(Number left, Number right) {
		return compareTo(left, right) == 0;
	}

	public static int compareTo(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		return Comparables.compareTo(m, n);
	}

	public static int compareTo(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		return Comparables.compareTo(m, n);
	}

	public static BigDecimal min(Number[] array) {
		Assert.isTrue(ArrayUtils.isEmpty(array), "Empty array.");
		BigDecimal min = valueOf(array[0]);
		for (Number arg : array) {
			min = min(min, arg);
		}
		return min;
	}

	public static BigDecimal max(Number[] array) {
		Assert.isTrue(ArrayUtils.isEmpty(array), "Empty array.");
		BigDecimal max = null;
		for (Number arg : array) {
			max = max(max, arg);
		}
		return max;
	}

	public static BigDecimal avg(Number[] array, int scale) {
		Assert.isTrue(ArrayUtils.isEmpty(array), "Empty array.");
		BigDecimal sum = sum(array);
		return divide(sum, array.length, scale, RoundingMode.HALF_UP);
	}

	public static BigDecimal sum(Number[] array) {
		Assert.isTrue(ArrayUtils.isEmpty(array), "Empty array.");
		BigDecimal sum = BigDecimal.ZERO;
		for (Number arg : array) {
			sum = add(sum, arg);
		}
		return sum;
	}

	public static boolean notBetween(BigDecimal value, Number a, Number b) {
		return !between(value, a, b);
	}

	public static boolean between(BigDecimal value, Number a, Number b) {
		BigDecimal left = valueOf(a);
		BigDecimal right = valueOf(b);
		return value.compareTo(left) >= 0 && value.compareTo(right) <= 0;
	}

	public static boolean notIn(BigDecimal value, Number a, Number b) {
		return !in(value, a, b);
	}

	public static boolean in(BigDecimal value, Number a, Number b) {
		BigDecimal left = valueOf(a);
		BigDecimal right = valueOf(b);
		return value.compareTo(left) > 0 && value.compareTo(right) < 0;
	}

	public static boolean notBetween(BigDecimal value, String a, String b) {
		return !between(value, a, b);
	}

	public static boolean between(BigDecimal value, String a, String b) {
		BigDecimal left = parse(a);
		BigDecimal right = parse(b);
		return value.compareTo(left) >= 0 && value.compareTo(right) <= 0;
	}

	public static boolean notIn(BigDecimal value, String a, String b) {
		return !in(value, a, b);
	}

	public static boolean in(BigDecimal value, String a, String b) {
		BigDecimal left = parse(a);
		BigDecimal right = parse(b);
		return value.compareTo(left) > 0 && value.compareTo(right) < 0;
	}

	public static BigDecimal max(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.max(n);
	}

	public static BigDecimal max(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.max(n);
	}

	public static BigDecimal min(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMin(m, n);
		}
		return m.min(n);
	}

	public static BigDecimal min(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMin(m, n);
		}
		return m.min(n);
	}

	public static BigDecimal pow(Number arg, int n, MathContext mc) {
		BigDecimal value = valueOf(arg);
		return value != null ? value.pow(n, mc) : null;
	}

	public static BigDecimal pow(Number arg, int n) {
		BigDecimal value = valueOf(arg);
		return value != null ? value.pow(n) : null;
	}

	public static BigDecimal pow(String arg, int n, MathContext mc) {
		BigDecimal value = parse(arg);
		return value != null ? value.pow(n, mc) : null;
	}

	public static BigDecimal pow(String arg, int n) {
		BigDecimal value = parse(arg);
		return value != null ? value.pow(n) : null;
	}

	public static BigDecimal abs(Number arg) {
		BigDecimal value = valueOf(arg);
		return value != null ? value.abs() : null;
	}

	public static BigDecimal abs(String arg) {
		BigDecimal value = parse(arg);
		return value != null ? value.abs() : null;
	}

	public static BigDecimal add(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.add(n);
	}

	public static BigDecimal add(Number a, Number b, MathContext mc) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.add(n, mc);
	}

	public static BigDecimal add(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.add(n);
	}

	public static BigDecimal add(String a, String b, MathContext mc) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.add(n, mc);
	}

	public static String addAndFormat(Number a, Number b, String pattern, String defaultValue) {
		BigDecimal val = add(a, b);
		return format(val, pattern, defaultValue);
	}

	public static String addAndFormat(String a, String b, String pattern, String defaultValue) {
		BigDecimal val = add(a, b);
		return format(val, pattern, defaultValue);
	}

	public static String addAndFormat(Number a, Number b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = add(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	public static String addAndFormat(String a, String b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = add(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	public static BigDecimal subtract(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.subtract(n);
	}

	public static BigDecimal subtract(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.subtract(n);
	}

	public static BigDecimal subtract(Number a, Number b, MathContext mc) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.subtract(n, mc);
	}

	public static BigDecimal subtract(String a, String b, MathContext mc) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.subtract(n, mc);
	}

	public static String subtractAndFormat(String a, String b, String pattern, String defaultValue) {
		BigDecimal val = subtract(a, b);
		return format(val, pattern, defaultValue);
	}

	public static String subtractAndFormat(Number a, Number b, String pattern, String defaultValue) {
		BigDecimal val = subtract(a, b);
		return format(val, pattern, defaultValue);
	}

	public static String subtractAndFormat(String a, String b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = subtract(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	public static String subtractAndFormat(Number a, Number b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = subtract(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	public static BigDecimal multiply(Number a, Number b) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.multiply(n);
	}

	public static BigDecimal multiply(String a, String b) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.multiply(n);
	}

	public static BigDecimal multiply(Number a, Number b, MathContext mc) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.multiply(n, mc);
	}

	public static BigDecimal multiply(String a, String b, MathContext mc) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		return m.multiply(n, mc);
	}

	public static String multiplyAndFormat(String a, String b, String pattern, String defaultValue) {
		BigDecimal val = multiply(a, b);
		return format(val, pattern, defaultValue);
	}

	public static String multiplyAndFormat(Number a, Number b, String pattern, String defaultValue) {
		BigDecimal val = multiply(a, b);
		return format(val, pattern, defaultValue);
	}

	public static String multiplyAndFormat(String a, String b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = multiply(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	public static String multiplyAndFormat(Number a, Number b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = multiply(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	public static BigDecimal divide(Number a, Number b, MathContext mc) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		try {
			return m.divide(n, mc);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	public static BigDecimal divide(String a, String b, MathContext mc) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		try {
			return m.divide(n, mc);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	public static BigDecimal divide(Number a, Number b, int scale, RoundingMode roundingMode) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		try {
			return m.divide(n, scale, roundingMode);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	public static BigDecimal divide(String a, String b, int scale, RoundingMode roundingMode) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return Comparables.nullOrMax(m, n);
		}
		try {
			return m.divide(n, scale, roundingMode);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	public static String divideAndFormat(String a, String b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = divide(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	public static String divideAndFormat(Number a, Number b, MathContext mc, String pattern, String defaultValue) {
		BigDecimal val = divide(a, b, mc);
		return format(val, pattern, defaultValue);
	}

	public static String divideAndFormat(String a, String b, int scale, RoundingMode roundingMode, String pattern, String defaultValue) {
		BigDecimal val = divide(a, b, scale, roundingMode);
		return format(val, pattern, defaultValue);
	}

	public static String divideAndFormat(Number a, Number b, int scale, RoundingMode roundingMode, String pattern, String defaultValue) {
		BigDecimal val = divide(a, b, scale, roundingMode);
		return format(val, pattern, defaultValue);
	}

	public static BigDecimal round(String value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_UP);
	}

	public static BigInteger round(String value) {
		return setScale(value, RoundingMode.HALF_UP);
	}

	public static BigDecimal round(Number value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_UP);
	}

	public static BigInteger round(Number value) {
		return setScale(value, RoundingMode.HALF_UP);
	}

	public static BigDecimal floor(String value, int scale) {
		return setScale(value, scale, RoundingMode.FLOOR);
	}

	public static BigInteger floor(String value) {
		return setScale(value, RoundingMode.FLOOR);
	}

	public static BigDecimal floor(Number value, int scale) {
		return setScale(value, scale, RoundingMode.FLOOR);
	}

	public static BigInteger floor(Number value) {
		return setScale(value, RoundingMode.FLOOR);
	}

	public static BigDecimal ceil(String value, int scale) {
		return setScale(value, scale, RoundingMode.CEILING);
	}

	public static BigInteger ceil(String value) {
		return setScale(value, RoundingMode.CEILING);
	}

	public static BigDecimal ceil(Number value, int scale) {
		return setScale(value, scale, RoundingMode.CEILING);
	}

	public static BigInteger ceil(Number value) {
		return setScale(value, RoundingMode.CEILING);
	}

	public static BigDecimal down(String value, int scale) {
		return setScale(value, scale, RoundingMode.DOWN);
	}

	public static BigInteger down(String value) {
		return setScale(value, RoundingMode.DOWN);
	}

	public static BigDecimal down(Number value, int scale) {
		return setScale(value, scale, RoundingMode.DOWN);
	}

	public static BigInteger down(Number value) {
		return setScale(value, RoundingMode.DOWN);
	}

	public static BigDecimal up(String value, int scale) {
		return setScale(value, scale, RoundingMode.UP);
	}

	public static BigInteger up(String value) {
		return setScale(value, RoundingMode.UP);
	}

	public static BigDecimal up(Number value, int scale) {
		return setScale(value, scale, RoundingMode.UP);
	}

	public static BigInteger up(Number value) {
		return setScale(value, RoundingMode.UP);
	}

	public static BigDecimal roundDown(String value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_DOWN);
	}

	public static BigInteger roundDown(String value) {
		return setScale(value, RoundingMode.HALF_DOWN);
	}

	public static BigDecimal roundDown(Number value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_DOWN);
	}

	public static BigInteger roundDown(Number value) {
		return setScale(value, RoundingMode.HALF_DOWN);
	}

	public static BigDecimal roundEven(String value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_EVEN);
	}

	public static BigInteger roundEven(String value) {
		return setScale(value, RoundingMode.HALF_EVEN);
	}

	public static BigDecimal roundEven(Number value, int scale) {
		return setScale(value, scale, RoundingMode.HALF_EVEN);
	}

	public static BigInteger roundEven(Number value) {
		return setScale(value, RoundingMode.HALF_EVEN);
	}

	public static BigInteger setScale(String value, RoundingMode roundingMode) {
		BigDecimal val = setScale(value, 0, roundingMode);
		return val != null ? val.toBigInteger() : null;
	}

	public static BigInteger setScale(Number value, RoundingMode roundingMode) {
		BigDecimal val = setScale(value, 0, roundingMode);
		return val != null ? val.toBigInteger() : null;
	}

	public static BigDecimal setScale(String string, int scale, RoundingMode roundingMode) {
		BigDecimal value = parse(string);
		if (value != null) {
			return scale > 0 ? value.setScale(scale, roundingMode) : value;
		}
		return null;
	}

	public static BigDecimal setScale(Number number, int scale, RoundingMode roundingMode) {
		BigDecimal value = valueOf(number);
		if (value != null) {
			return scale > 0 ? value.setScale(scale, roundingMode) : value;
		}
		return null;
	}

	public static BigDecimal[] divideAndRemainder(String a, String b, MathContext mc) {
		BigDecimal m = parse(a);
		BigDecimal n = parse(b);
		if (m == null || n == null) {
			return new BigDecimal[] { m, n };
		}
		return m.divideAndRemainder(n, mc);
	}

	public static BigDecimal[] divideAndRemainder(Number a, Number b, MathContext mc) {
		BigDecimal m = valueOf(a);
		BigDecimal n = valueOf(b);
		if (m == null || n == null) {
			return new BigDecimal[] { m, n };
		}
		return m.divideAndRemainder(n, mc);
	}

	public static String toPlainString(String str) {
		return toPlainString(str, "");
	}

	public static String toPlainString(String str, String defaultValue) {
		if (NumberUtils.isNotNumber(str)) {
			return defaultValue;
		}
		String repr;
		if (null == (repr = stringPlainStringCache.get(str))) {
			repr = new BigDecimal(str).toPlainString();
			stringPlainStringCache.put(str, repr);
		}
		return repr;
	}

	public static String toPlainString(Number n) {
		return toPlainString(n, "");
	}

	public static String toPlainString(Number n, String defaultValue) {
		if (n == null) {
			return defaultValue;
		}
		if (n instanceof Byte || n instanceof Short || n instanceof Integer || n instanceof Long) {
			return n.toString();
		}
		String repr;
		if (null == (repr = numberPlainStringCache.get(n))) {
			repr = valueOf(n).toPlainString();
			numberPlainStringCache.put(n, repr);
		}
		return repr;
	}

	public static int getPrecision(BigDecimal value) {
		return Math.abs(value.round(new MathContext(1)).scale());
	}

	public static int getScale(BigDecimal value) {
		String str = value.stripTrailingZeros().toPlainString();
		int index = str.indexOf(".");
		return index < 0 ? 0 : str.length() - index - 1;
	}

	public static <K, V extends Number> Map<K, BigDecimal> mergeAndSum(Map<K, V> m1, Map<K, V> m2) {
		Map<K, BigDecimal> results = new LinkedHashMap<K, BigDecimal>();
		K key;
		BigDecimal total;
		BigDecimal value;
		for (Map.Entry<K, V> en : m1.entrySet()) {
			key = en.getKey();
			if (m2.containsKey(key)) {
				value = add(en.getValue(), m2.get(key));
			} else {
				value = valueOf(en.getValue());
			}
			if (results.containsKey(key)) {
				total = results.get(key);
				total = add(total, value);
			} else {
				results.put(key, value);
			}
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
