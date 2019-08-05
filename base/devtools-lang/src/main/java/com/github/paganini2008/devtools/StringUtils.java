package com.github.paganini2008.devtools;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.primitives.Chars;

/**
 * StringUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class StringUtils {

	private StringUtils() {
	}

	public static final String lineSeparator = System.getProperty("line.separator");

	public static final String BLANK = " ";

	public static final String EMPTY = "";

	public static final String UNDEFINED = "undefined";

	public static final String BLANK_CHARACTER = " \t\n\r\f";

	public static final String[] EMPTY_ARRAY = new String[0];

	public static final int INDEX_NOT_FOUND = -1;

	public static String toLetter(int value) {
		StringBuilder str = new StringBuilder();
		while (value >= 0) {
			str.append((char) (value % 26 + 97));
			value = value / 26 - 1;
		}
		return str.reverse().toString();
	}

	public static String reverse(String str) {
		return isNotBlank(str) ? new StringBuilder(str).reverse().toString() : "";
	}

	public static String trimLeft(String input) {
		Assert.isNull(input);
		String s = input.trim();
		return input.substring(input.indexOf(s));
	}

	public static String trimRight(String input) {
		Assert.isNull(input);
		String s = input.trim();
		int i = input.indexOf(s);
		return input.substring(0, i + s.length());
	}

	public static boolean isEmpty(CharSequence str) {
		if ((str == null) || (str.length() == 0)) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(CharSequence str) {
		return !isEmpty(str);
	}

	public static boolean isBlank(CharSequence str) {
		if (isEmpty(str)) {
			return true;
		}
		for (int i = 0; i < str.length(); i++) {
			if (Character.isWhitespace(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(CharSequence str) {
		return !isBlank(str);
	}

	public static String join(String[] array) {
		return join(array, ",");
	}

	public static String join(String[] array, String delim) {
		return join(array, EMPTY, delim);
	}

	public static String join(String[] array, String preAndSuf, String delim) {
		return join(array, preAndSuf, preAndSuf, delim);
	}

	public static String join(String[] array, String prefix, String suffix, String delim) {
		if (array == null) {
			return "";
		}
		StringBuilder str = new StringBuilder();
		for (int i = 0, l = array.length; i < l; i++) {
			str.append(prefix);
			str.append(array[i]);
			str.append(suffix);
			if (i != l - 1) {
				str.append(delim);
			}
		}
		return str.toString();
	}

	public static String padding(int length) {
		return repeat(BLANK, length);
	}

	public static String repeat(String str, int length) {
		return repeat(str, null, length);
	}

	public static String repeat(String str, String delim, int length) {
		Assert.isNull(str, "String must not be null.");
		if (delim == null) {
			delim = "";
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < length; i++) {
			result.append(str);
			if (i != length - 1) {
				result.append(delim);
			}
		}
		return result.toString();
	}

	public static String leftPadding(String str, int length) {
		return leftPadding(str, BLANK, length);
	}

	public static String leftPadding(String str, String place, int length) {
		Assert.isNull(str, "String must not be null.");
		length = Math.max(str.length(), length);
		int n = length - str.length();
		if (n > 0) {
			return repeat(place, n) + str;
		}
		return str;
	}

	public static String rightPadding(String str, int length) {
		return rightPadding(str, BLANK, length);
	}

	public static String rightPadding(String str, String place, int length) {
		Assert.isNull(str, "String must not be null.");
		length = Math.max(str.length(), length);
		int n = length - str.length();
		if (n > 0) {
			return str + repeat(place, n);
		}
		return str;
	}

	public static String toUpperCase(String s, int... indexes) {
		Assert.hasNoText(s);
		StringBuilder result = new StringBuilder(s);
		for (int index : indexes) {
			result.setCharAt(index, Character.toUpperCase(s.charAt(index)));
		}
		return result.toString();
	}

	public static String toLowerCase(String s, int... indexes) {
		Assert.hasNoText(s);
		StringBuilder result = new StringBuilder(s);
		for (int index : indexes) {
			result.setCharAt(index, Character.toLowerCase(s.charAt(index)));
		}
		return result.toString();
	}

	public static String firstCharToLowerCase(String s) {
		return toLowerCase(s, 0);
	}

	public static String firstCharToUpperCase(String s) {
		return toUpperCase(s, 0);
	}

	public static String insert(String str, String substr, int index) {
		Assert.isNull(str, "Source string must not be null.");
		Assert.isNull(substr, "Substring must not be null.");
		return new StringBuilder(str).insert(index, substr).toString();
	}

	public static String thumb(CharSequence cs, int length) {
		Assert.hasNoText(cs);
		String s = cs.subSequence(0, Math.min(cs.length(), length)).toString();
		return cs.length() > length ? s + "..." : s;
	}

	public static String substring(String s, int to) {
		return substring(s, 0, to);
	}

	public static String substring(String s, int from, int to) {
		Assert.hasNoText(s);
		return s.substring(from, Math.min(to, s.length()));
	}

	public static List<String> split(CharSequence source, String example) {
		return split(source, example, false);
	}

	public static List<String> split(CharSequence source, String example, boolean returnDelims) {
		return split(source, example, returnDelims, true);
	}

	public static List<String> split(CharSequence source, String example, boolean returnDelims, boolean returnEmpty) {
		Assert.hasNoText(source);
		if (isBlank(example)) {
			example = BLANK_CHARACTER;
		}
		StringTokenizer tokenizer = new StringTokenizer(source.toString(), example, returnDelims);
		List<String> list = new ArrayList<String>();
		String s;
		while (tokenizer.hasMoreTokens()) {
			s = tokenizer.nextToken();
			if (returnEmpty || isNotBlank(s)) {
				list.add(s);
			}
		}
		return list;
	}

	public static List<String> split(CharSequence source, String[] delimiters) {
		return split(source, delimiters, false);
	}

	public static List<String> split(CharSequence source, String[] delimiters, boolean returnDelim) {
		return split(source, delimiters, returnDelim, true);
	}

	public static List<String> split(CharSequence source, String[] delimiters, boolean returnDelims, boolean returnEmpty) {
		Assert.hasNoText(source);
		Assert.isNull(delimiters, "Delimeters must not be null.");
		SubstrTokenizer tokenizer = new SubstrTokenizer(source, delimiters, returnDelims);
		List<String> list = new ArrayList<String>();
		String s;
		while (tokenizer.hasMoreElements()) {
			s = tokenizer.nextElement();
			if (returnEmpty || isNotBlank(s)) {
				list.add(s);
			}
		}
		return list;
	}

	public static Map<String, String> splitAsMap(CharSequence source, String example) {
		Assert.hasNoText(source);
		if (isBlank(example)) {
			example = BLANK_CHARACTER;
		}
		Map<String, String> result = new LinkedHashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(source.toString(), example, false);
		String s, t = null;
		while (tokenizer.hasMoreTokens()) {
			s = tokenizer.nextToken();
			if (t == null) {
				t = s;
			} else {
				result.put(t, s);
				t = null;
			}
		}
		if (t != null) {
			result.put(t, null);
		}
		return result;
	}

	public static Map<String, String> splitAsMap(CharSequence source, String example, String delimiter) {
		Assert.hasNoText(source);
		Assert.hasNoText(delimiter);
		if (isBlank(example)) {
			example = BLANK_CHARACTER;
		}
		Map<String, String> result = new LinkedHashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(source.toString(), example, false);
		String s;
		int i, n = delimiter.length();
		while (tokenizer.hasMoreTokens()) {
			s = tokenizer.nextToken();
			if (isNotBlank(s)) {
				if ((i = s.indexOf(delimiter)) > 0) {
					result.put(s.substring(0, i), s.substring(i + n));
				} else {
					result.put(s, null);
				}
			}
		}
		return result;
	}

	public static Map<String, String> splitAsMap(CharSequence source, String[] delimiters) {
		Assert.hasNoText(source);
		Assert.isNull(delimiters, "Delimeters must not be null.");
		Map<String, String> result = new LinkedHashMap<String, String>();
		SubstrTokenizer tokenizer = new SubstrTokenizer(source, delimiters, false);
		String s, t = null;
		while (tokenizer.hasMoreTokens()) {
			s = tokenizer.nextToken();
			if (isNotBlank(s)) {
				if (t == null) {
					t = s;
				} else {
					result.put(t, s);
					t = null;
				}
			}
		}
		if (t != null) {
			result.put(t, null);
		}
		return result;
	}

	public static Map<String, String> splitAsMap(CharSequence source, String[] delimiters, String secondDelimiter) {
		Assert.hasNoText(source);
		Assert.isNull(delimiters, "Delimeters must not be null.");
		Assert.hasNoText(secondDelimiter);
		Map<String, String> result = new LinkedHashMap<String, String>();
		SubstrTokenizer tokenizer = new SubstrTokenizer(source, delimiters, false);
		String s;
		int i, n = secondDelimiter.length();
		while (tokenizer.hasMoreTokens()) {
			s = tokenizer.nextToken();
			if (isNotBlank(s)) {
				if (n > 0 && (i = s.indexOf(secondDelimiter)) > 0) {
					result.put(s.substring(0, i), s.substring(i + n));
				} else {
					result.put(s, null);
				}
			}
		}
		return result;
	}

	public static String toCamelCase(String str) {
		return toCamelCase(str, "_");
	}

	public static String toCamelCase(String str, String join) {
		Assert.hasNoText(str);
		Assert.hasNoText(join);
		StringBuilder result = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(str, join, false);
		String token;
		int len;
		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			len = result.length();
			result.append(token);
			if (len > 0) {
				result.setCharAt(len, Character.toUpperCase(token.charAt(0)));
			}
		}
		return result.toString();
	}

	public static String toSegmentCase(String str) {
		return toSegmentCase(str, "_");
	}

	public static String toSegmentCase(String str, String join) {
		Assert.hasNoText(str);
		Assert.isNull(join);
		StringBuilder result = new StringBuilder();
		char[] src = str.toCharArray();
		for (char c : src) {
			if (Character.isUpperCase(c)) {
				result.append(join).append(Character.toLowerCase(c));
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	public static boolean notEquals(String left, String right) {
		return !equals(left, right);
	}

	public static boolean equals(String left, String right) {
		return left == null ? right == null : left.equals(right);
	}

	public static boolean notEqualsStrictly(String left, String right) {
		return !equalsStrictly(left, right);
	}

	public static boolean equalsStrictly(String left, String right) {
		return isBlank(left) ? isBlank(right) : left.equals(right);
	}

	public static boolean notEqualsIgnoreCase(String left, String right) {
		return !equalsIgnoreCase(left, right);
	}

	public static boolean equalsIgnoreCase(String left, String right) {
		return left == null ? right == null : left.equalsIgnoreCase(right);
	}

	public static boolean notEqualsIgnoreCaseStrictly(String left, String right) {
		return !equalsIgnoreCaseStrictly(left, right);
	}

	public static boolean equalsIgnoreCaseStrictly(String left, String right) {
		return isBlank(left) ? isBlank(right) : left.equalsIgnoreCase(right);
	}

	public static boolean hasContent(String left, String right) {
		return isBlank(left) ? isNotBlank(right) : isBlank(right);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(parseText("\\/?1\\~^?2?0", "?", new Object[] { 1, false, "abc" }));
	}

	public static String parseText(String text, String openToken, String closeToken, Map<String, ?> params) {
		Assert.hasNoText(text, "Can't parse empty text.");
		Assert.hasNoText(openToken, "Open token string is required.");
		Assert.hasNoText(closeToken, "Close token string is required.");
		if (params == null || params.size() == 0) {
			return text;
		}
		int offset = 0;
		int start = text.indexOf(openToken, offset);
		if (start == -1) {
			return text;
		}
		StringBuilder builder = new StringBuilder();
		char[] src = text.toCharArray();
		while (start > -1) {
			if (start > 0 && src[start - 1] == '\\') {
				builder.append(src, offset, start - offset - 1).append(openToken);
				offset = start + openToken.length();
			} else {
				int end = text.indexOf(closeToken, start);
				if (end == -1) {
					builder.append(src, offset, src.length - offset);
					offset = src.length;
				} else {
					builder.append(src, offset, start - offset);
					offset = start + openToken.length();
					String key = new String(src, offset, end - offset);// {a}{}{b}
					if (isNotBlank(key)) {
						Object value = params.get(key);
						if (value == null) {
							value = System.getProperty(key);
						}
						builder.append(value);
					} else {
						builder.append(src, start, end - start + closeToken.length());
					}
					offset = end + closeToken.length();
				}
			}
			start = text.indexOf(openToken, offset);
		}
		if (offset < src.length) {
			builder.append(src, offset, src.length - offset);
		}
		return builder.toString();
	}

	public static String parseText(String text, String token, Map<String, ?> params) {
		Assert.hasNoText(text, "Can't parse empty text.");
		Assert.hasNoText(token, "Token string is required.");
		if (params == null || params.size() == 0) {
			return text;
		}
		int offset = 0;
		int start = text.indexOf(token, offset);
		if (start == -1) {
			return text;
		}
		StringBuilder builder = new StringBuilder();
		char[] src = text.toCharArray();
		while (start > -1) {
			if (start > 0 && src[start - 1] == '\\') {
				builder.append(src, offset, start - offset - 1).append(token);
				offset = start + token.length();
			} else {
				int i = start + token.length();
				StringBuilder variable = new StringBuilder();
				while ((i < src.length) && (src[i] == '_' || Character.isLetterOrDigit(src[i]))) {
					variable.append(src[i]);
					i++;
				}
				if (variable.length() == 0) {
					throw new IllegalArgumentException("Bad string format. Index: " + start);
				}
				builder.append(src, offset, start - offset);
				String key = variable.toString();
				Object value = params.get(key);
				if (value == null) {
					value = System.getProperty(key);
				}
				builder.append(value);
				offset = start + token.length() + variable.length();
			}
			start = text.indexOf(token, offset);
		}
		if (offset < src.length) {
			builder.append(src, offset, src.length - offset);
		}
		return builder.toString();
	}

	public static String parseText(String text, String token, Object[] args) {
		Assert.hasNoText(text, "Can't parse empty text.");
		Assert.hasNoText(token, "Token string is required.");
		if (args == null || args.length == 0) {
			return text;
		}
		int offset = 0;
		int start = text.indexOf(token, offset);
		if (start == -1) {
			return text;
		}
		StringBuilder builder = new StringBuilder();
		char[] src = text.toCharArray();
		int n = 0;
		StringBuilder digit = null;
		while (start > -1) {
			if (start > 0 && src[start - 1] == '\\') {
				builder.append(src, offset, start - offset - 1).append(token);
				offset = start + token.length();
			} else {
				builder.append(src, offset, start - offset);
				int i = start + token.length();
				while ((i < src.length) && (Character.isDigit(src[i]))) {
					if (digit == null) {
						digit = new StringBuilder(2);
					}
					digit.append(src[i]);
					i++;
				}
				if (isNotBlank(digit)) {
					if (n > 0) {
						throw new IllegalArgumentException("Bad string format. Index: " + start);
					}
					int index;
					try {
						index = Integer.parseInt(digit.toString());
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Can't parse argument number.");
					}
					if (index > args.length - 1) {
						throw new IllegalArgumentException("Argument number more than parameters' length.");
					}
					builder.append(args[index]);
					offset = start + token.length() + digit.length();
				} else {
					builder.append(args[n]);
					if (n < args.length - 1) {
						n++;
					}
					offset = start + token.length();
				}
			}
			if (digit != null) {
				digit.delete(0, digit.length());
			}
			start = text.indexOf(token, offset);
		}
		if (offset < src.length) {
			builder.append(src, offset, src.length - offset);
		}
		return builder.toString();
	}

	public static String unicodeEscape(String unicodeString) {
		Assert.hasNoText(unicodeString);
		StringBuilder content = new StringBuilder();
		for (String str : split(unicodeString, "\\u")) {
			content.append((char) Integer.parseInt(str, 16));
		}
		return content.toString();
	}

	public static String unicodeString(String str) {
		StringBuilder content = new StringBuilder();
		for (char c : str.toCharArray()) {
			content.append(Chars.toUnicode(c));
		}
		return content.toString();
	}

	public static boolean isAlpha(CharSequence str) {
		if (str == null || str.length() == 0) {
			return false;
		}
		int l = str.length();
		for (int i = 0; i < l; i++) {
			if (Character.isLetter(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumeric(CharSequence str) {
		if (str == null || str.length() == 0) {
			return false;
		}
		int l = str.length();
		for (int i = 0; i < l; i++) {
			if (Character.isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static int count(String str, String substr) {
		int count = 0;
		int idx = 0;
		while (INDEX_NOT_FOUND != (idx = str.indexOf(substr, idx))) {
			count++;
			idx += substr.length();
		}
		return count;
	}

	public static int count(String str, char c) {
		int count = 0;
		int idx = 0;
		while (INDEX_NOT_FOUND != (idx = str.indexOf(c, idx))) {
			count++;
			idx += 1;
		}
		return count;
	}

	public static String replaceFirst(String template, String placeholder, String replacement) {
		Assert.hasNoText(template);
		Assert.hasNoText(placeholder);
		Assert.hasNoText(replacement);
		int loc = template.indexOf(placeholder);
		if (loc < 0) {
			return template;
		} else {
			return template.substring(0, loc).concat(replacement).concat(template.substring(loc + placeholder.length()));
		}
	}

	private final static LruMap<String, MessageFormat> messageFormatCache = new LruMap<String, MessageFormat>(128);

	public static MessageFormat getMessageFormat(String pattern) {
		MessageFormat mf = messageFormatCache.get(pattern);
		if (mf == null) {
			messageFormatCache.put(pattern, new MessageFormat(pattern, Locale.ENGLISH));
			mf = messageFormatCache.get(pattern);
		}
		return mf;
	}

}
