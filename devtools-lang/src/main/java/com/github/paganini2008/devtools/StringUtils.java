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
 * @since 2.0.1
 */
public abstract class StringUtils {

	public static final String lineSeparator = System.getProperty("line.separator");

	public static final String BLANK = " ";

	public static final String EMPTY = "";

	public static final String UNDEFINED = "undefined";

	public static final String BLANK_CHARACTER = " \t\n\r\f";

	public static final String[] EMPTY_ARRAY = new String[0];

	public static final int INDEX_NOT_FOUND = -1;

	public static String toString(CharSequence str) {
		return toString(str, EMPTY);
	}

	public static String toString(CharSequence str, String defaultValue) {
		return isNotBlank(str) ? str.toString() : defaultValue;
	}

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

	public static String repeat(char c, int count) {
		return repeat(String.valueOf(c), count);
	}

	public static String repeat(String str, int count) {
		return repeat(str, null, count);
	}

	public static String repeat(String str, String delim, int count) {
		Assert.isNull(str, "String must not be null.");
		Assert.lte(count, 0, "Repeated count must gt 0");
		if (count == 1) {
			return str;
		}
		boolean hasDelim = StringUtils.isNotBlank(delim);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < count; i++) {
			result.append(str);
			if (hasDelim && i != count - 1) {
				result.append(delim);
			}
		}
		return result.toString();
	}

	public static String textRight(String str, int length) {
		return textRight(str, ' ', length);
	}

	public static String textRight(String str, char place, int length) {
		return textRight(str, 0, place, length);
	}

	public static String textRight(String str, int padding, int length) {
		return textRight(str, padding, ' ', length);
	}

	public static String textRight(String str, int padding, char place, int length) {
		Assert.isNull(str, "Filled string must not be null.");
		if (padding > 0) {
			str = str.concat(repeat(place, padding));
		}
		int remaining = length - str.length();
		if (remaining <= 0) {
			return str;
		}
		return repeat(place, remaining).concat(str);
	}

	public static String textLeft(String str, int length) {
		return textLeft(str, ' ', length);
	}

	public static String textLeft(String str, char place, int length) {
		return textLeft(str, 0, place, length);
	}

	public static String textLeft(String str, int padding, int length) {
		return textLeft(str, padding, ' ', length);
	}

	public static String textLeft(String str, int padding, char place, int length) {
		Assert.isNull(str, "Filled string must not be null.");
		if (padding > 0) {
			str = repeat(place, padding).concat(str);
		}
		int remaining = length - str.length();
		if (remaining <= 0) {
			return str;
		}
		return str.concat(repeat(place, remaining));
	}

	public static String textMiddle(String str, int length) {
		return textMiddle(str, ' ', length);
	}

	public static String textMiddle(String str, char place, int length) {
		Assert.isNull(str, "Filled string must not be blank.");
		int remaining = length - str.length();
		if (remaining <= 0) {
			return str;
		}
		if (remaining == 1) {
			return String.valueOf(place).concat(str);
		} else if (remaining == 2) {
			return String.valueOf(place).concat(str).concat(String.valueOf(place));
		}
		int n = remaining / 2;
		int m = remaining % 2;
		return repeat(place, n + m).concat(str).concat(repeat(place, n));
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

	public static String uncapitalize(String str) {
		return toLowerCase(str, 0);
	}

	public static String capitalize(String str) {
		return toUpperCase(str, 0);
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
		if (isEmpty(example)) {
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

	public static String parseText(String text, String prefix, String suffix, Object... args) {
		Assert.hasNoText(text, "Can't parse blank text.");
		Assert.hasNoText(prefix, "Prefix string is required.");
		Assert.hasNoText(suffix, "Suffix string is required.");
		if (args == null || args.length == 0) {
			return text;
		}
		PlaceholderTokenParser placeholderTokenizer = new PlaceholderTokenParser(prefix, suffix);
		return placeholderTokenizer.parse(text, str -> {
			return args[Integer.parseInt(str)];
		});
	}

	public static String parseText(String text, String prefix, String suffix, Map<String, ?> params) {
		Assert.hasNoText(text, "Can't parse blank text.");
		Assert.hasNoText(prefix, "Prefix string is required.");
		Assert.hasNoText(suffix, "Suffix string is required.");
		if (params == null || params.size() == 0) {
			return text;
		}
		PlaceholderTokenParser placeholderTokenizer = new PlaceholderTokenParser(prefix, suffix);
		return placeholderTokenizer.parse(text, key -> {
			return params.get(key);
		});
	}

	public static String parseText(String text, String token, final Map<String, ?> params) {
		Assert.hasNoText(text, "Can't parse blank text.");
		Assert.hasNoText(token, "Token string is required.");
		if (params == null || params.size() == 0) {
			return text;
		}
		PrefixationTokenParser prefixationTokenizer = new PrefixationTokenParser(token);
		return prefixationTokenizer.parse(text, key -> {
			return params.get(key);
		});
	}

	public static String parseText(String text, String token, final Object... args) {
		Assert.hasNoText(text, "Can't parse blank text.");
		Assert.hasNoText(token, "Token string is required.");
		if (args == null || args.length == 0) {
			return text;
		}
		PrefixationTokenParser prefixationTokenizer = new PrefixationTokenParser(token);
		return prefixationTokenizer.parse(text, str -> {
			return args[Integer.parseInt(str)];
		});
	}

	public static String format(String text, String token, final Object... args) {
		Assert.hasNoText(text, "Can't parse blank text.");
		Assert.hasNoText(token, "Token string is required.");
		if (args == null || args.length == 0) {
			return text;
		}
		SimpleTokenParser simpleTokenizer = new SimpleTokenParser(token);
		return simpleTokenizer.parse(text, index -> {
			return args[Integer.min(index, args.length - 1)];
		});
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

	public static void main(String[] args) {
		System.out.println(matchesWildcard("*mapper*com.*", "com.yourcompany.io.mapper"));
	}

	public static boolean matchesWildcard(String pattern, String str) {
		if (pattern.equals("*")) {
			return true;
		}
		if (pattern.contains("*")) {
			List<String> args = split(pattern, "*", false, false);
			if (args.isEmpty()) {
				return true;
			}
			int lastIndex = -1;
			int index;
			for (String arg : args) {
				if ((index = str.indexOf(arg)) < 0 || index < lastIndex) {
					return false;
				}
				lastIndex = index;
			}
			return true;
		}
		return pattern.equals(str);
	}

	public static String[] matchMany(String[] array, String pattern, MatchMode matchMode) {
		List<String> list = new ArrayList<>();
		for (String str : array) {
			if (matchMode.matches(pattern, str)) {
				list.add(str);
			}
		}
		return list.toArray(new String[0]);
	}

}
