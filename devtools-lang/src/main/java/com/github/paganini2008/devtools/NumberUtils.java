package com.github.paganini2008.devtools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.math.BigDecimalUtils;

/**
 * NumberUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class NumberUtils {

	private final static LruMap<String, DecimalFormat> formatCache = new LruMap<String, DecimalFormat>(128);

	public static final DecimalFormat DEFAULT_NUMBER_FORMATTER = getDecimalFormat("0.00");

	public static String toPlainString(Number n) {
		return toPlainString(n, "");
	}

	public static String toPlainString(Number n, String defaultValue) {
		return BigDecimalUtils.toPlainString(n, defaultValue);
	}

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

	public static boolean isNotInteger(String value) {
		return !isInteger(value);
	}

	public static boolean isNotNumber(String value) {
		return !isNumber(value);
	}

	public static String read(String value) {
		if (StringUtils.isBlank(value)) {
			return "";
		}
		char s = value.charAt(0);
		if (s == '+') {
			value = value.substring(1);
			s = value.charAt(0);
		}
		if (s == '.') {
			value = "0" + value;
		} else if (s == '-') {
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

	public static Byte toByte(String str) {
		return toByte(str, null);
	}

	public static Byte toByte(String str, Byte defaultValue) {
		try {
			return Byte.valueOf(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static Short toShort(String str) {
		return toShort(str, null);
	}

	public static Short toShort(String str, Short defaultValue) {
		try {
			return Short.valueOf(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static Integer toInteger(String str) {
		return toInteger(str, null);
	}

	public static Integer toInteger(String str, Integer defaultValue) {
		try {
			return Integer.valueOf(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static Long toLong(String str) {
		return toLong(str, null);
	}

	public static Long toLong(String str, Long defaultValue) {
		try {
			return Long.valueOf(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static Double toDouble(String str) {
		return toDouble(str, null);
	}

	public static Double toDouble(String str, Double defaultValue) {
		try {
			return Double.valueOf(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static BigInteger toBigInteger(String str) {
		return toBigInteger(str, null);
	}

	public static BigInteger toBigInteger(String str, BigInteger defaultValue) {
		try {
			return new BigInteger(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static BigDecimal toBigDecimal(String str) {
		return toBigDecimal(str, null);
	}

	public static BigDecimal toBigDecimal(String str, BigDecimal defaultValue) {
		try {
			return new BigDecimal(str);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

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

	public static DecimalFormat getDecimalFormat(String s, int scale) {
		return getDecimalFormat(getDecimalPattern(s, scale));
	}

	public static DecimalFormat getDecimalFormat(String pattern) {
		DecimalFormat df = formatCache.get(pattern);
		if (df == null) {
			formatCache.put(pattern, new DecimalFormat(pattern));
			df = formatCache.get(pattern);
		}
		return df;
	}

	public static String format(Number value, int scale) {
		return format(value, getDecimalPattern("0", scale));
	}

	public static String format(Number value, String pattern) {
		return format(value, pattern, "");
	}

	public static String format(Number value, String pattern, String defaultValue) {
		return format(value, getDecimalFormat(pattern), defaultValue);
	}

	public static String format(Number value) {
		return format(value, DEFAULT_NUMBER_FORMATTER);
	}

	public static String format(Number value, DecimalFormat df) {
		return format(value, df, "");
	}

	public static String format(Number value, DecimalFormat df, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		Assert.isNull(df, "DecimalFormat can not be null.");
		synchronized (NumberUtils.class) {
			return df.format(value);
		}
	}

	public static String[] formatMany(Number[] values) {
		return formatMany(values, DEFAULT_NUMBER_FORMATTER);
	}

	public static String[] formatMany(Number[] values, DecimalFormat df) {
		return formatMany(values, df, null);
	}

	public static String[] formatMany(Number[] values, DecimalFormat df, String defaultValue) {
		int length = values.length;
		String[] array = new String[length];
		int i = 0;
		for (Number value : values) {
			array[i++] = format(value, df, defaultValue);
		}
		return array;
	}

	public static String[] formatMany(Number[] values, String pattern) {
		return formatMany(values, pattern, "");
	}

	public static String[] formatMany(Number[] values, String pattern, String defaultValue) {
		return formatMany(values, getDecimalFormat(pattern), defaultValue);
	}

}
