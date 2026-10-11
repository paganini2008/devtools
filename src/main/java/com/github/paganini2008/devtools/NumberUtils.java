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
package com.github.paganini2008.devtools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.math.BigDecimalUtils;

/**
 * Utility methods for numbers: validating numeric strings, null-safe parsing
 * with default values and formatting with cached DecimalFormat patterns.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class NumberUtils {

	private final static LruMap<String, DecimalFormat> formatCache = new LruMap<String, DecimalFormat>(128);

	/**
	 * Returns the plain (non-scientific) string of a number, or "" if it is null.
	 */
	public static String toPlainString(Number n) {
		return toPlainString(n, "");
	}

	/**
	 * Returns the plain (non-scientific) string of a number, or the default value if
	 * it is null.
	 */
	public static String toPlainString(Number n, String defaultValue) {
		return BigDecimalUtils.toPlainString(n, defaultValue);
	}

	/**
	 * Checks whether the string is an integer literal with an optional sign and an
	 * optional 'l'/'L' suffix.
	 *
	 * <pre>
	 * NumberUtils.isInteger(null)  = false
	 * NumberUtils.isInteger("-12") = true
	 * NumberUtils.isInteger("12L") = true
	 * NumberUtils.isInteger("1.2") = false
	 * </pre>
	 */
	public static boolean isInteger(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		char[] chars = str.toCharArray();
		int length = chars.length;
		int start = (chars[0] == '-' || chars[0] == '+') ? 1 : 0;
		length--;
		int i = start;
		boolean foundDigit = false;
		while (i < length || (i < length + 1 && !foundDigit)) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				foundDigit = true;
			} else {
				return false;
			}
			i++;
		}
		if (i < chars.length) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				return true;
			}
			if (chars[i] == 'l' || chars[i] == 'L') {
				return foundDigit;
			}
			return false;
		}
		return foundDigit;
	}

	/**
	 * Checks whether the string is a valid Java number literal (decimal point,
	 * exponent and type suffix allowed).
	 *
	 * <pre>
	 * NumberUtils.isNumber(null)      = false
	 * NumberUtils.isNumber(".5")      = true
	 * NumberUtils.isNumber("-1.5e10") = true
	 * NumberUtils.isNumber("1.5f")    = true
	 * NumberUtils.isNumber("1.5L")    = false
	 * NumberUtils.isNumber("1e")      = false
	 * </pre>
	 */
	public static boolean isNumber(String str) {
		if (StringUtils.isBlank(str)) {
			return false;
		}
		char[] chars = str.toCharArray();
		int length = chars.length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;
		int start = (chars[0] == '-' || chars[0] == '+') ? 1 : 0;
		length--;
		int i = start;
		while (i < length || (i < length + 1 && allowSigns && !foundDigit)) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				foundDigit = true;
				allowSigns = false;
			} else if (chars[i] == '.') {
				if (hasDecPoint || hasExp) {
					return false;
				}
				hasDecPoint = true;
			} else if (chars[i] == 'e' || chars[i] == 'E') {
				if (hasExp) {
					return false;
				}
				if (!foundDigit) {
					return false;
				}
				hasExp = true;
				allowSigns = true;
			} else if (chars[i] == '+' || chars[i] == '-') {
				if (!allowSigns) {
					return false;
				}
				allowSigns = false;
				foundDigit = false;
			} else {
				return false;
			}
			i++;
		}
		if (i < chars.length) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				return true;
			}
			if (chars[i] == 'e' || chars[i] == 'E') {
				return false;
			}
			if (chars[i] == '.') {
				if (hasDecPoint || hasExp) {
					return false;
				}
				return foundDigit;
			}
			if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
				return foundDigit;
			}
			if (chars[i] == 'l' || chars[i] == 'L') {
				return foundDigit && !hasExp && !hasDecPoint;
			}
			return false;
		}
		return !allowSigns && foundDigit;
	}

	/**
	 * Checks whether the string is a hex number starting with "0x", "0X" or "#",
	 * with an optional leading minus sign.
	 *
	 * <pre>
	 * NumberUtils.isHex("0x1F")  = true
	 * NumberUtils.isHex("#fff")  = true
	 * NumberUtils.isHex("0x")    = false
	 * NumberUtils.isHex("123")   = false
	 * </pre>
	 */
	public static boolean isHex(String value) {
		if (StringUtils.isBlank(value)) {
			return false;
		}
		int start = value.startsWith("-") ? 1 : 0;
		int offset = 0;
		if (value.startsWith("0x", start) || value.startsWith("0X", start)) {
			offset = 2;
		} else if (value.startsWith("#", start)) {
			offset = 1;
		}
		if (offset == 0) {
			return false;
		}
		char[] chars = value.toCharArray();
		int i = start + offset;
		if (i == chars.length) {
			return false;
		}
		for (; i < chars.length; i++) {
			if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Negation of {@link #isInteger(String)}.
	 */
	public static boolean isNotInteger(String value) {
		return !isInteger(value);
	}

	/**
	 * Negation of {@link #isNumber(String)}.
	 */
	public static boolean isNotNumber(String value) {
		return !isNumber(value);
	}

	/**
	 * Normalizes a number literal: removes a leading '+', adds a missing leading
	 * zero and strips type suffixes.
	 *
	 * <pre>
	 * NumberUtils.read(null)    = ""
	 * NumberUtils.read("+1.5")  = "1.5"
	 * NumberUtils.read("-.5")   = "-0.5"
	 * NumberUtils.read("10L")   = "10"
	 * NumberUtils.read("1.5f")  = "1.5"
	 * </pre>
	 *
	 * @throws NumberFormatException if the value is a bare sign ("+" or "-")
	 */
	public static String read(String value) {
		if (StringUtils.isBlank(value)) {
			return "";
		}
		char s = value.charAt(0);
		if (s == '+') {
			value = value.substring(1);
			if (value.isEmpty()) {
				throw new NumberFormatException("For input string: \"+\"");
			}
			s = value.charAt(0);
		}
		if (s == '.') {
			value = "0" + value;
		} else if (s == '-') {
			if (value.length() == 1) {
				throw new NumberFormatException("For input string: \"-\"");
			}
			if (value.charAt(1) == '.') {
				value = s + "0" + value.substring(1);
			}
		}
		int index = value.indexOf('.');
		char lastChar = value.charAt(value.length() - 1);
		if ((index < 0 && lastChar == 'L') || (index < 0 && lastChar == 'l') || lastChar == 'D' || lastChar == 'd' || lastChar == 'F'
				|| lastChar == 'f') {
			value = value.substring(0, value.length() - 1);
		}
		return value;
	}

	/**
	 * Parses the string as Byte, returning null if it is null or invalid.
	 */
	public static Byte toByte(String str) {
		return toByte(str, null);
	}

	/**
	 * Parses the string as Byte, returning the default value if it is null or
	 * invalid.
	 */
	public static Byte toByte(String str, Byte defaultValue) {
		try {
			return Byte.valueOf(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Parses the string as Short, returning null if it is null or invalid.
	 */
	public static Short toShort(String str) {
		return toShort(str, null);
	}

	/**
	 * Parses the string as Short, returning the default value if it is null or
	 * invalid.
	 */
	public static Short toShort(String str, Short defaultValue) {
		try {
			return Short.valueOf(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Parses the string as Integer, returning null if it is null or invalid.
	 */
	public static Integer toInteger(String str) {
		return toInteger(str, null);
	}

	/**
	 * Parses the string as Integer, returning the default value if it is null or
	 * invalid.
	 */
	public static Integer toInteger(String str, Integer defaultValue) {
		try {
			return Integer.valueOf(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Parses the string as Long, returning null if it is null or invalid.
	 */
	public static Long toLong(String str) {
		return toLong(str, null);
	}

	/**
	 * Parses the string as Long, returning the default value if it is null or
	 * invalid.
	 */
	public static Long toLong(String str, Long defaultValue) {
		try {
			return Long.valueOf(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Parses the string as Double, returning null if it is null or invalid.
	 */
	public static Double toDouble(String str) {
		return toDouble(str, null);
	}

	/**
	 * Parses the string as Double, returning the default value if it is null or
	 * invalid.
	 */
	public static Double toDouble(String str, Double defaultValue) {
		try {
			return Double.valueOf(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Parses the string as BigInteger, returning null if it is null or invalid.
	 */
	public static BigInteger toBigInteger(String str) {
		return toBigInteger(str, null);
	}

	/**
	 * Parses the string as BigInteger, returning the default value if it is null or
	 * invalid.
	 */
	public static BigInteger toBigInteger(String str, BigInteger defaultValue) {
		try {
			return new BigInteger(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Parses the string as BigDecimal, returning null if it is null or invalid.
	 */
	public static BigDecimal toBigDecimal(String str) {
		return toBigDecimal(str, null);
	}

	/**
	 * Parses the string as BigDecimal, returning the default value if it is null or
	 * invalid.
	 */
	public static BigDecimal toBigDecimal(String str, BigDecimal defaultValue) {
		try {
			return new BigDecimal(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Formats each number with the DecimalFormat, or as a plain string if the format
	 * is null.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static String[] toStringArray(Number[] numbers, DecimalFormat df) {
		Assert.isNull(numbers, "Number array can not be null.");
		String[] results = new String[numbers.length];
		int i = 0;
		for (Number number : numbers) {
			results[i++] = df != null ? df.format(number) : toPlainString(number, null);
		}
		return results;
	}

	private static String getDecimalPattern(String s, int scale) {
		if (scale > 0) {
			s += "." + StringUtils.repeat("#", scale);
		} else if (scale < 0) {
			s += "." + StringUtils.repeat("0", -scale);
		}
		return s;
	}

	/**
	 * Returns a DecimalFormat for the integer pattern {@code s} with {@code scale}
	 * optional fraction digits ('#'); a negative scale means mandatory fraction
	 * digits ('0'). Each call returns a new copy, since DecimalFormat is not
	 * thread-safe. The format uses the symbols of the default Locale.
	 */
	public static DecimalFormat getDecimalFormat(String s, int scale) {
		return (DecimalFormat) getDecimalFormat(getDecimalPattern(s, scale)).clone();
	}

	private static DecimalFormat getDecimalFormat(String pattern) {
		DecimalFormat df = formatCache.get(pattern);
		if (df == null) {
			df = new DecimalFormat(pattern);
			formatCache.put(pattern, df);
		}
		return df;
	}

	/**
	 * Formats the number with the given scale, see
	 * {@link #getDecimalFormat(String, int)}. Like all {@code format} methods
	 * taking a pattern, the output depends on the default Locale (e.g. the decimal
	 * separator is ',' under Locale.GERMANY).
	 *
	 * <pre>
	 * NumberUtils.format(1.234, 2) = "1.23"
	 * NumberUtils.format(1.0, 2)   = "1"
	 * NumberUtils.format(1.2, -2)  = "1.20"
	 * NumberUtils.format(null, 2)  = ""
	 * </pre>
	 */
	public static String format(Number value, int scale) {
		return format(value, getDecimalPattern("0", scale));
	}

	/**
	 * Formats the number with the pattern "0.00" using the symbols of the default
	 * Locale. A null value returns "".
	 */
	public static String format(Number value) {
		return format(value, "0.00");
	}

	/**
	 * Formats the number with a DecimalFormat pattern, using the symbols of the
	 * default Locale. A null value returns "".
	 */
	public static String format(Number value, String pattern) {
		return format(value, pattern, "");
	}

	/**
	 * Formats the number with a DecimalFormat pattern, returning the default value
	 * if the number is null.
	 */
	public static String format(Number value, String pattern, String defaultValue) {
		return format(value, getDecimalFormat(pattern), defaultValue);
	}

	/**
	 * Formats the number with the DecimalFormat. A null value returns "".
	 */
	public static String format(Number value, DecimalFormat df) {
		return format(value, df, "");
	}

	/**
	 * Formats the number with the DecimalFormat, returning the default value if the
	 * number is null.
	 *
	 * @throws IllegalArgumentException if the number is not null and df is null
	 */
	public static String format(Number value, DecimalFormat df, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		Assert.isNull(df, "DecimalFormat can not be null.");
		return doFormat(value, df);
	}

	/**
	 * Formats each number with "0.00"; nulls become "". A null array returns an
	 * empty array.
	 */
	public static String[] formatMany(Number[] values) {
		return formatMany(values, "0.00");
	}

	/**
	 * Formats each number with the DecimalFormat; nulls stay null.
	 */
	public static String[] formatMany(Number[] values, DecimalFormat df) {
		return formatMany(values, df, null);
	}

	/**
	 * Formats each number with the DecimalFormat, using the default value for nulls.
	 * A null array returns an empty array.
	 */
	public static String[] formatMany(Number[] values, DecimalFormat df, String defaultValue) {
		if (values == null) {
			return StringUtils.EMPTY_ARRAY;
		}
		int length = values.length;
		String[] array = new String[length];
		int i = 0;
		for (Number value : values) {
			array[i++] = format(value, df, defaultValue);
		}
		return array;
	}

	/**
	 * Formats each number with the pattern; nulls become "".
	 */
	public static String[] formatMany(Number[] values, String pattern) {
		return formatMany(values, pattern, "");
	}

	/**
	 * Formats each number with the pattern, using the default value for nulls.
	 */
	public static String[] formatMany(Number[] values, String pattern, String defaultValue) {
		return formatMany(values, getDecimalFormat(pattern), defaultValue);
	}

	private static String doFormat(Number value, DecimalFormat df) {
		synchronized (NumberUtils.class) {
			return df.format(value);
		}
	}
}
