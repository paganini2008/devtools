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
 * Null-safe utility methods for {@code String} and {@code CharSequence},
 * such as emptiness checks, padding, splitting, joining and placeholder parsing.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class StringUtils {

	public static final String NEWLINE = System.getProperty("line.separator");

	public static final String BLANK = " ";

	public static final String EMPTY = "";

	public static final String UNDEFINED = "undefined";

	public static final String BLANK_CHARACTER = " \t\n\r\f";

	public static final String[] EMPTY_ARRAY = new String[0];

	public static final int INDEX_NOT_FOUND = -1;

	/**
	 * Returns the string value of a CharSequence, or an empty string if it is blank.
	 *
	 * <pre>
	 * StringUtils.toString(null) = ""
	 * StringUtils.toString(" ")  = ""
	 * StringUtils.toString("a")  = "a"
	 * </pre>
	 */
	public static String toString(CharSequence str) {
		return toString(str, EMPTY);
	}

	/**
	 * Returns the string value of a CharSequence, or {@code defaultValue} if it is
	 * null, empty or whitespace only.
	 */
	public static String toString(CharSequence str, String defaultValue) {
		return isNotBlank(str) ? str.toString() : defaultValue;
	}

	/**
	 * Appends all elements of the boolean array to a new StringBuilder without any
	 * separator. A null array results in an empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(boolean[] array) {
		return toStringBuilder(array, false);
	}

	/**
	 * Appends all elements of the boolean array to a new StringBuilder, optionally
	 * followed by a line separator after each element. A null array results in an
	 * empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(boolean[] array, boolean newline) {
		StringBuilder content = new StringBuilder();
		if (array == null) {
			return content;
		}
		for (boolean b : array) {
			content.append(b);
			if (newline) {
				content.append(NEWLINE);
			}
		}
		return content;
	}

	/**
	 * Appends all elements of the char array to a new StringBuilder without any
	 * separator. A null array results in an empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(char[] array) {
		return toStringBuilder(array, false);
	}

	/**
	 * Appends all elements of the char array to a new StringBuilder, optionally
	 * followed by a line separator after each element. A null array results in an
	 * empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(char[] array, boolean newline) {
		StringBuilder content = new StringBuilder();
		if (array == null) {
			return content;
		}
		for (char c : array) {
			content.append(c);
			if (newline) {
				content.append(NEWLINE);
			}
		}
		return content;
	}

	/**
	 * Appends all elements of the int array to a new StringBuilder without any
	 * separator. A null array results in an empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(int[] array) {
		return toStringBuilder(array, false);
	}

	/**
	 * Appends all elements of the int array to a new StringBuilder, optionally
	 * followed by a line separator after each element. A null array results in an
	 * empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(int[] array, boolean newline) {
		StringBuilder content = new StringBuilder();
		if (array == null) {
			return content;
		}
		for (int i : array) {
			content.append(i);
			if (newline) {
				content.append(NEWLINE);
			}
		}
		return content;
	}

	/**
	 * Appends all elements of the float array to a new StringBuilder without any
	 * separator. A null array results in an empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(float[] array) {
		return toStringBuilder(array, false);
	}

	/**
	 * Appends all elements of the float array to a new StringBuilder, optionally
	 * followed by a line separator after each element. A null array results in an
	 * empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(float[] array, boolean newline) {
		StringBuilder content = new StringBuilder();
		if (array == null) {
			return content;
		}
		for (float f : array) {
			content.append(f);
			if (newline) {
				content.append(NEWLINE);
			}
		}
		return content;
	}

	/**
	 * Appends all elements of the double array to a new StringBuilder without any
	 * separator. A null array results in an empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(double[] array) {
		return toStringBuilder(array, false);
	}

	/**
	 * Appends all elements of the double array to a new StringBuilder, optionally
	 * followed by a line separator after each element. A null array results in an
	 * empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(double[] array, boolean newline) {
		StringBuilder content = new StringBuilder();
		if (array == null) {
			return content;
		}
		for (double d : array) {
			content.append(d);
			if (newline) {
				content.append(NEWLINE);
			}
		}
		return content;
	}

	/**
	 * Appends all elements of the long array to a new StringBuilder without any
	 * separator. A null array results in an empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(long[] array) {
		return toStringBuilder(array, false);
	}

	/**
	 * Appends all elements of the long array to a new StringBuilder, optionally
	 * followed by a line separator after each element. A null array results in an
	 * empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(long[] array, boolean newline) {
		StringBuilder content = new StringBuilder();
		if (array == null) {
			return content;
		}
		for (long l : array) {
			content.append(l);
			if (newline) {
				content.append(NEWLINE);
			}
		}
		return content;
	}

	/**
	 * Appends all elements of the Object array to a new StringBuilder without any
	 * separator. A null array results in an empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(Object[] array) {
		return toStringBuilder(array, false);
	}

	/**
	 * Appends all elements of the Object array to a new StringBuilder, optionally
	 * followed by a line separator after each element. A null array results in an
	 * empty StringBuilder.
	 */
	public static StringBuilder toStringBuilder(Object[] array, boolean newline) {
		StringBuilder content = new StringBuilder();
		if (array == null) {
			return content;
		}
		for (Object obj : array) {
			content.append(obj);
			if (newline) {
				content.append(NEWLINE);
			}
		}
		return content;
	}

	/**
	 * Converts a zero-based index to a spreadsheet-like column name.
	 *
	 * <pre>
	 * StringUtils.toLetter(0)   = "a"
	 * StringUtils.toLetter(25)  = "z"
	 * StringUtils.toLetter(26)  = "aa"
	 * StringUtils.toLetter(702) = "aaa"
	 * StringUtils.toLetter(-1)  = ""
	 * </pre>
	 */
	public static String toLetter(int value) {
		StringBuilder str = new StringBuilder();
		while (value >= 0) {
			str.append((char) (value % 26 + 97));
			value = value / 26 - 1;
		}
		return str.reverse().toString();
	}

	/**
	 * Reverses a CharSequence. A blank input returns an empty string.
	 *
	 * <pre>
	 * StringUtils.reverse(null)  = ""
	 * StringUtils.reverse(" ")   = ""
	 * StringUtils.reverse("abc") = "cba"
	 * </pre>
	 */
	public static String reverse(CharSequence str) {
		return isNotBlank(str) ? new StringBuilder(str).reverse().toString() : "";
	}

	/**
	 * Removes leading control characters and spaces (chars &lt;= ' ').
	 *
	 * <pre>
	 * StringUtils.trimLeft("  a b  ") = "a b  "
	 * StringUtils.trimLeft("   ")     = ""
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the input is null
	 */
	public static String trimLeft(String input) {
		Assert.isNull(input);
		int i = 0, l = input.length();
		while (i < l && input.charAt(i) <= ' ') {
			i++;
		}
		return input.substring(i);
	}

	/**
	 * Removes trailing control characters and spaces (chars &lt;= ' ').
	 *
	 * <pre>
	 * StringUtils.trimRight("  a b  ") = "  a b"
	 * StringUtils.trimRight("   ")     = ""
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the input is null
	 */
	public static String trimRight(String input) {
		Assert.isNull(input);
		int i = input.length();
		while (i > 0 && input.charAt(i - 1) <= ' ') {
			i--;
		}
		return input.substring(0, i);
	}

	/**
	 * Checks if a CharSequence is empty ("") or null.
	 *
	 * <pre>
	 * StringUtils.isEmpty(null) = true
	 * StringUtils.isEmpty("")   = true
	 * StringUtils.isEmpty(" ")  = false
	 * </pre>
	 */
	public static boolean isEmpty(CharSequence str) {
		if ((str == null) || (str.length() == 0)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a CharSequence is neither empty ("") nor null.
	 */
	public static boolean isNotEmpty(CharSequence str) {
		return !isEmpty(str);
	}

	/**
	 * Checks if a CharSequence is empty (""), null or whitespace only.
	 *
	 * <pre>
	 * StringUtils.isBlank(null)  = true
	 * StringUtils.isBlank("")    = true
	 * StringUtils.isBlank(" ")   = true
	 * StringUtils.isBlank("bob") = false
	 * </pre>
	 */
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

	/**
	 * Checks if a CharSequence is not empty (""), not null and not whitespace only.
	 */
	public static boolean isNotBlank(CharSequence str) {
		return !isBlank(str);
	}

	/**
	 * Joins the array with a comma. A null array returns an empty string.
	 */
	public static String join(String[] array) {
		return join(array, ",");
	}

	/**
	 * Joins the array with the given delimiter. A null array returns an empty string.
	 */
	public static String join(String[] array, String delim) {
		return join(array, EMPTY, delim);
	}

	/**
	 * Joins the array, wrapping each element with the same prefix and suffix.
	 *
	 * <pre>
	 * StringUtils.join(["a", "b"], "'", ",") = "'a','b'"
	 * </pre>
	 */
	public static String join(String[] array, String preAndSuf, String delim) {
		return join(array, preAndSuf, preAndSuf, delim);
	}

	/**
	 * Joins the array, wrapping each element with the prefix and suffix.
	 *
	 * <pre>
	 * StringUtils.join(null, "[", "]", "|")       = ""
	 * StringUtils.join(["a", "b"], "[", "]", "|") = "[a]|[b]"
	 * </pre>
	 */
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

	/**
	 * Repeats a character {@code count} times.
	 *
	 * @throws IllegalArgumentException if count &lt;= 0
	 */
	public static String repeat(char c, int count) {
		return repeat(String.valueOf(c), count);
	}

	/**
	 * Repeats a string {@code count} times.
	 *
	 * @throws IllegalArgumentException if str is null or count &lt;= 0
	 */
	public static String repeat(String str, int count) {
		return repeat(str, null, count);
	}

	/**
	 * Repeats a string {@code count} times, separated by the delimiter (ignored if
	 * blank).
	 *
	 * <pre>
	 * StringUtils.repeat("ab", ",", 2) = "ab,ab"
	 * StringUtils.repeat("x", null, 1) = "x"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if str is null or count &lt;= 0
	 */
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

	/**
	 * Right-aligns the string by left-padding it with spaces up to {@code length}.
	 */
	public static String textRight(String str, int length) {
		return textRight(str, ' ', length);
	}

	/**
	 * Right-aligns the string by left-padding it with the given char up to {@code length}.
	 */
	public static String textRight(String str, char place, int length) {
		return textRight(str, 0, place, length);
	}

	/**
	 * Right-aligns the string with spaces, see {@link #textRight(String, int, char, int)}.
	 */
	public static String textRight(String str, int padding, int length) {
		return textRight(str, padding, ' ', length);
	}

	/**
	 * Right-aligns the string: first appends {@code padding} fill chars to the
	 * right, then left-pads with fill chars up to {@code length}. A string that is
	 * already long enough is returned as it is.
	 *
	 * <pre>
	 * StringUtils.textRight("ab", 5)          = "   ab"
	 * StringUtils.textRight("ab", 2, '*', 5)  = "*ab**"
	 * StringUtils.textRight("abcdef", 3)      = "abcdef"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if str is null
	 */
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

	/**
	 * Left-aligns the string by right-padding it with spaces up to {@code length}.
	 */
	public static String textLeft(String str, int length) {
		return textLeft(str, ' ', length);
	}

	/**
	 * Left-aligns the string by right-padding it with the given char up to {@code length}.
	 */
	public static String textLeft(String str, char place, int length) {
		return textLeft(str, 0, place, length);
	}

	/**
	 * Left-aligns the string with spaces, see {@link #textLeft(String, int, char, int)}.
	 */
	public static String textLeft(String str, int padding, int length) {
		return textLeft(str, padding, ' ', length);
	}

	/**
	 * Left-aligns the string: first prepends {@code padding} fill chars, then
	 * right-pads with fill chars up to {@code length}.
	 *
	 * <pre>
	 * StringUtils.textLeft("ab", 5)          = "ab   "
	 * StringUtils.textLeft("ab", 2, '*', 5)  = "**ab*"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if str is null
	 */
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

	/**
	 * Centers the string with spaces, see {@link #textMiddle(String, char, int)}.
	 */
	public static String textMiddle(String str, int length) {
		return textMiddle(str, ' ', length);
	}

	/**
	 * Centers the string within {@code length} chars. When the padding cannot be
	 * split evenly, the extra char goes to the left.
	 *
	 * <pre>
	 * StringUtils.textMiddle("ab", '*', 4) = "*ab*"
	 * StringUtils.textMiddle("ab", '*', 7) = "***ab**"
	 * StringUtils.textMiddle("abc", 2)     = "abc"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if str is null
	 */
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

	/**
	 * Upper-cases the chars at the given indexes.
	 *
	 * <pre>
	 * StringUtils.toUpperCase("hello", 0, 4) = "HellO"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if s is blank
	 */
	public static String toUpperCase(String s, int... indexes) {
		Assert.hasNoText(s);
		StringBuilder result = new StringBuilder(s);
		for (int index : indexes) {
			result.setCharAt(index, Character.toUpperCase(s.charAt(index)));
		}
		return result.toString();
	}

	/**
	 * Lower-cases the chars at the given indexes.
	 *
	 * @throws IllegalArgumentException if s is blank
	 */
	public static String toLowerCase(String s, int... indexes) {
		Assert.hasNoText(s);
		StringBuilder result = new StringBuilder(s);
		for (int index : indexes) {
			result.setCharAt(index, Character.toLowerCase(s.charAt(index)));
		}
		return result.toString();
	}

	/**
	 * Lower-cases the first char, e.g. "ABC" becomes "aBC".
	 *
	 * @throws IllegalArgumentException if str is blank
	 */
	public static String uncapitalize(String str) {
		return toLowerCase(str, 0);
	}

	/**
	 * Upper-cases the first char, e.g. "abc" becomes "Abc".
	 *
	 * @throws IllegalArgumentException if str is blank
	 */
	public static String capitalize(String str) {
		return toUpperCase(str, 0);
	}

	/**
	 * Inserts {@code substr} at the given index, e.g. insert("abc", "X", 1) = "aXbc".
	 *
	 * @throws IllegalArgumentException if str or substr is null
	 */
	public static String insert(String str, String substr, int index) {
		Assert.isNull(str, "Source string must not be null.");
		Assert.isNull(substr, "Substring must not be null.");
		return new StringBuilder(str).insert(index, substr).toString();
	}

	/**
	 * Abbreviates the text to at most {@code length} chars followed by "...".
	 *
	 * <pre>
	 * StringUtils.thumb("abcdef", 3) = "abc..."
	 * StringUtils.thumb("ab", 3)     = "ab"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the text is blank
	 */
	public static String thumb(CharSequence cs, int length) {
		Assert.hasNoText(cs);
		String s = cs.subSequence(0, Math.min(cs.length(), length)).toString();
		return cs.length() > length ? s + "..." : s;
	}

	/**
	 * Returns the first {@code to} chars (or the whole string if shorter).
	 *
	 * @throws IllegalArgumentException if s is blank
	 */
	public static String substring(String s, int to) {
		return substring(s, 0, to);
	}

	/**
	 * Returns the substring between {@code from} and {@code to}, where {@code to}
	 * is truncated to the string length.
	 *
	 * @throws IllegalArgumentException if s is blank
	 */
	public static String substring(String s, int from, int to) {
		Assert.hasNoText(s);
		return s.substring(from, Math.min(to, s.length()));
	}

	/**
	 * Splits the text by any char of {@code example} (whitespace chars if empty),
	 * like {@link java.util.StringTokenizer}.
	 *
	 * <pre>
	 * StringUtils.split("a b  c", " ") = [a, b, c]
	 * </pre>
	 *
	 * @throws IllegalArgumentException if source is blank
	 */
	public static List<String> split(CharSequence source, String example) {
		return split(source, example, false);
	}

	/**
	 * Splits the text by any char of {@code example}, optionally returning the
	 * delimiters as tokens.
	 */
	public static List<String> split(CharSequence source, String example, boolean returnDelims) {
		return split(source, example, returnDelims, true);
	}

	/**
	 * Splits the text by any char of {@code example}.
	 *
	 * <pre>
	 * StringUtils.split("a,b", ",", true)           = [a, ",", b]
	 * StringUtils.split("a, ,b", ",", false, false) = [a, b]
	 * </pre>
	 *
	 * @param returnDelims whether the delimiters are also returned as tokens
	 * @param returnEmpty whether blank tokens are kept
	 * @throws IllegalArgumentException if source is blank
	 */
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

	/**
	 * Splits the text by whole-word delimiters, see {@link SubstrTokenizer}.
	 */
	public static List<String> split(CharSequence source, String[] delimiters) {
		return split(source, delimiters, false);
	}

	/**
	 * Splits the text by whole-word delimiters, optionally returning the delimiters.
	 */
	public static List<String> split(CharSequence source, String[] delimiters, boolean returnDelim) {
		return split(source, delimiters, returnDelim, true);
	}

	/**
	 * Splits the text by whole-word delimiters.
	 *
	 * <pre>
	 * StringUtils.split("a::b,,c", ["::", ","])       = [a, b, c]
	 * StringUtils.split("a::b,,c", ["::", ","], true) = [a, "::", b, ",", ",", c]
	 * </pre>
	 *
	 * @throws IllegalArgumentException if source is blank or delimiters is null
	 */
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

	/**
	 * Splits the text and pairs the tokens as key and value in order. A trailing
	 * key without value is mapped to null.
	 *
	 * <pre>
	 * StringUtils.splitAsMap("a 1 b 2 c", " ") = {a=1, b=2, c=null}
	 * </pre>
	 */
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

	/**
	 * Splits the text into entries, then splits each entry into key and value at the
	 * first {@code delimiter}.
	 *
	 * <pre>
	 * StringUtils.splitAsMap("a=1&amp;b=2&amp;c", "&amp;", "=") = {a=1, b=2, c=null}
	 * </pre>
	 */
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

	/**
	 * Splits the text by whole-word delimiters and pairs the tokens as key and value.
	 */
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

	/**
	 * Splits the text into entries by whole-word delimiters, then splits each entry
	 * into key and value at the first {@code secondDelimiter}.
	 */
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

	/**
	 * Converts an underscore separated string to camel case, e.g. "user_name_id"
	 * becomes "userNameId".
	 */
	public static String toCamelCase(String str) {
		return toCamelCase(str, "_");
	}

	/**
	 * Converts a string separated by any char of {@code join} to camel case.
	 *
	 * @throws IllegalArgumentException if str or join is blank
	 */
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

	/**
	 * Converts a camel case string to underscore separated, e.g. "userNameId"
	 * becomes "user_name_id".
	 */
	public static String toSegmentCase(String str) {
		return toSegmentCase(str, "_");
	}

	/**
	 * Converts a camel case string to a string separated by {@code join}.
	 */
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

	/**
	 * Negation of {@link #equals(String, String)}.
	 */
	public static boolean notEquals(String left, String right) {
		return !equals(left, right);
	}

	/**
	 * Compares two strings, returning true if both are null or equal.
	 */
	public static boolean equals(String left, String right) {
		return left == null ? right == null : left.equals(right);
	}

	/**
	 * Negation of {@link #equalsStrictly(String, String)}.
	 */
	public static boolean notEqualsStrictly(String left, String right) {
		return !equalsStrictly(left, right);
	}

	/**
	 * Compares two strings, treating all blank strings (including null) as equal.
	 */
	public static boolean equalsStrictly(String left, String right) {
		return isBlank(left) ? isBlank(right) : left.equals(right);
	}

	/**
	 * Negation of {@link #equalsIgnoreCase(String, String)}.
	 */
	public static boolean notEqualsIgnoreCase(String left, String right) {
		return !equalsIgnoreCase(left, right);
	}

	/**
	 * Compares two strings ignoring case, returning true if both are null.
	 */
	public static boolean equalsIgnoreCase(String left, String right) {
		return left == null ? right == null : left.equalsIgnoreCase(right);
	}

	/**
	 * Negation of {@link #equalsIgnoreCaseStrictly(String, String)}.
	 */
	public static boolean notEqualsIgnoreCaseStrictly(String left, String right) {
		return !equalsIgnoreCaseStrictly(left, right);
	}

	/**
	 * Compares two strings ignoring case, treating all blank strings as equal.
	 */
	public static boolean equalsIgnoreCaseStrictly(String left, String right) {
		return isBlank(left) ? isBlank(right) : left.equalsIgnoreCase(right);
	}

	/**
	 * Returns true if exactly one of the two strings is not blank.
	 */
	public static boolean hasContent(String left, String right) {
		return isBlank(left) ? isNotBlank(right) : isBlank(right);
	}

	/**
	 * Replaces indexed placeholders such as "${0}" with the arguments. If no
	 * argument is given or the text is blank, the text is returned as it is. A
	 * placeholder that is not a valid index is kept as it is.
	 *
	 * <pre>
	 * StringUtils.parseText("Hello ${0}, ${1}", "${", "}", "a", "b") = "Hello a, b"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if prefix or suffix is blank
	 */
	public static String parseText(String text, String prefix, String suffix, Object... args) {
		if (isBlank(text)) {
			return text;
		}
		Assert.hasNoText(prefix, "Prefix string is required.");
		Assert.hasNoText(suffix, "Suffix string is required.");
		if (args == null || args.length == 0) {
			return text;
		}
		PlaceholderTokenParser placeholderTokenizer = new PlaceholderTokenParser(prefix, suffix);
		return placeholderTokenizer.parse(text, str -> {
			int index = toIndex(str.trim(), args.length);
			return index >= 0 ? args[index] : prefix + str + suffix;
		});
	}

	/**
	 * Replaces named placeholders such as "${name}" with values of the map. A
	 * missing key falls back to the system property of the same name.
	 *
	 * @throws IllegalArgumentException if prefix or suffix is blank
	 */
	public static String parseText(String text, String prefix, String suffix, Map<String, ?> params) {
		if (isBlank(text)) {
			return text;
		}
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

	/**
	 * Replaces prefixed variables such as ":name" with values of the map. A blank
	 * text is returned as it is.
	 *
	 * <pre>
	 * StringUtils.parseText("Hi :name!", ":", {name=Tom}) = "Hi Tom!"
	 * </pre>
	 */
	public static String parseText(String text, String token, final Map<String, ?> params) {
		if (isBlank(text)) {
			return text;
		}
		Assert.hasNoText(token, "Token string is required.");
		if (params == null || params.size() == 0) {
			return text;
		}
		PrefixationTokenParser prefixationTokenizer = new PrefixationTokenParser(token);
		return prefixationTokenizer.parse(text, key -> {
			return params.get(key);
		});
	}

	/**
	 * Replaces indexed variables such as "{}0" with the argument at that index and
	 * a bare token such as "{}" with the next argument in order. Tokens that
	 * cannot be resolved (index out of range or no more arguments) are kept as
	 * they are, and a blank text is returned as it is.
	 *
	 * <pre>
	 * StringUtils.parseText(":0 and :1", ":", new Object[] { "a", "b" })   = "a and b"
	 * StringUtils.parseText("{} and {}", "{}", new Object[] { "a", "b" })  = "a and b"
	 * StringUtils.parseText("{}9 {}x", "{}", new Object[] { "a" })         = "{}9 ax"
	 * </pre>
	 *
	 * Note that a call with exactly two String arguments resolves to
	 * {@link #parseText(String, String, String, Object...)}; pass an
	 * {@code Object[]} to call this method explicitly.
	 */
	public static String parseText(String text, String token, final Object... args) {
		if (isBlank(text)) {
			return text;
		}
		Assert.hasNoText(token, "Token string is required.");
		if (args == null || args.length == 0) {
			return text;
		}
		return parseIndexedText(text, token, args);
	}

	private static int toIndex(String str, int length) {
		if (!isNumeric(str) || str.length() > 9) {
			return INDEX_NOT_FOUND;
		}
		int index = Integer.parseInt(str);
		return index < length ? index : INDEX_NOT_FOUND;
	}

	/**
	 * Replaces "token + digits" with the argument at that index and a bare token
	 * with the next argument in order. Unresolvable tokens are kept literally and
	 * an escaped token ("\\" + token) is output without the backslash.
	 */
	private static String parseIndexedText(String text, String token, Object[] args) {
		StringBuilder builder = new StringBuilder();
		int offset = 0, length = text.length(), tokenLength = token.length(), sequence = 0;
		int start = text.indexOf(token);
		while (start > -1) {
			if (start > 0 && text.charAt(start - 1) == '\\') {
				builder.append(text, offset, start - 1).append(token);
				offset = start + tokenLength;
			} else {
				builder.append(text, offset, start);
				int i = start + tokenLength;
				while (i < length && Character.isDigit(text.charAt(i))) {
					i++;
				}
				int index;
				if (i > start + tokenLength) {
					index = toIndex(text.substring(start + tokenLength, i), args.length);
				} else {
					index = sequence < args.length ? sequence : INDEX_NOT_FOUND;
					sequence++;
				}
				if (index >= 0) {
					builder.append(args[index]);
				} else {
					builder.append(text, start, i);
				}
				offset = i;
			}
			start = text.indexOf(token, offset);
		}
		builder.append(text, offset, length);
		return builder.toString();
	}

	/**
	 * Replaces each occurrence of the token with the arguments in order. Extra tokens
	 * reuse the last argument and an escaped token ("\\{}") is kept literally. A
	 * blank text is returned as it is.
	 *
	 * <pre>
	 * StringUtils.format("{} + {} = {}", "{}", 1, 2) = "1 + 2 = 2"
	 * </pre>
	 */
	public static String format(String text, String token, final Object... args) {
		if (isBlank(text)) {
			return text;
		}
		Assert.hasNoText(token, "Token string is required.");
		if (args == null || args.length == 0) {
			return text;
		}
		SimpleTokenParser simpleTokenizer = new SimpleTokenParser(token);
		return simpleTokenizer.parse(text, index -> {
			return args[Integer.min(index, args.length - 1)];
		});
	}

	/**
	 * Decodes a string of unicode escapes such as "\\u4e2d\\u6587" into chars.
	 */
	public static String unicodeEscape(String unicodeString) {
		Assert.hasNoText(unicodeString);
		StringBuilder content = new StringBuilder();
		for (String str : split(unicodeString, "\\u")) {
			content.append((char) Integer.parseInt(str, 16));
		}
		return content.toString();
	}

	/**
	 * Encodes every char of the string as a unicode escape. A null input returns an
	 * empty string.
	 */
	public static String unicodeString(String str) {
		if (str == null) {
			return "";
		}
		StringBuilder content = new StringBuilder();
		for (char c : str.toCharArray()) {
			content.append(Chars.toUnicode(c));
		}
		return content.toString();
	}

	/**
	 * Checks if the text contains only letters. Null or empty returns false.
	 */
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

	/**
	 * Checks if the text contains only digits. Null or empty returns false.
	 */
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

	/**
	 * Counts non-overlapping occurrences of a substring.
	 *
	 * <pre>
	 * StringUtils.count(null, "a")      = 0
	 * StringUtils.count("abc", "")      = 0
	 * StringUtils.count("abcabc", "bc") = 2
	 * StringUtils.count("aaa", "aa")    = 1
	 * </pre>
	 */
	public static int count(String str, String substr) {
		if (isEmpty(str) || isEmpty(substr)) {
			return 0;
		}
		int count = 0;
		int idx = 0;
		while (INDEX_NOT_FOUND != (idx = str.indexOf(substr, idx))) {
			count++;
			idx += substr.length();
		}
		return count;
	}

	/**
	 * Counts occurrences of a char. Null or empty returns 0.
	 */
	public static int count(String str, char c) {
		if (isEmpty(str)) {
			return 0;
		}
		int count = 0;
		int idx = 0;
		while (INDEX_NOT_FOUND != (idx = str.indexOf(c, idx))) {
			count++;
			idx += 1;
		}
		return count;
	}

	/**
	 * Replaces the first occurrence of {@code placeholder} literally (no regex).
	 *
	 * @throws IllegalArgumentException if any argument is blank
	 */
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

	/**
	 * Returns a MessageFormat for the pattern. The parsed pattern is cached, but
	 * MessageFormat is not thread-safe, so a copy of the cached instance is
	 * returned to each caller.
	 */
	public static MessageFormat getMessageFormat(String pattern) {
		MessageFormat mf = messageFormatCache.get(pattern);
		if (mf == null) {
			mf = new MessageFormat(pattern, Locale.ENGLISH);
			messageFormatCache.put(pattern, mf);
		}
		return (MessageFormat) mf.clone();
	}

	/**
	 * Tests whether the whole string matches the wildcard pattern, where '*'
	 * matches any sequence of characters (including an empty one).
	 *
	 * <pre>
	 * StringUtils.matchesWildcard("*", "")       = true
	 * StringUtils.matchesWildcard("a*", "abc")   = true
	 * StringUtils.matchesWildcard("a*", "ba")    = false
	 * StringUtils.matchesWildcard("a*c", "abc")  = true
	 * StringUtils.matchesWildcard("*c", "abcd")  = false
	 * StringUtils.matchesWildcard("a*", null)    = false
	 * </pre>
	 * 
	 * @param pattern the wildcard pattern
	 * @param str the string to test
	 * @return true if matched
	 * @throws IllegalArgumentException if the pattern is blank
	 */
	public static boolean matchesWildcard(String pattern, String str) {
		Assert.hasNoText(pattern, "Empty pattern");
		if (str == null) {
			return false;
		}
		if (pattern.indexOf('*') < 0) {
			return pattern.equals(str);
		}
		String[] parts = pattern.split("\\*", -1);
		String first = parts[0];
		String last = parts[parts.length - 1];
		if (!str.startsWith(first)) {
			return false;
		}
		int position = first.length();
		int end = str.length() - last.length();
		if (end < position || !str.endsWith(last)) {
			return false;
		}
		int index;
		for (int i = 1; i < parts.length - 1; i++) {
			String part = parts[i];
			if (part.isEmpty()) {
				continue;
			}
			index = str.indexOf(part, position);
			if (index < 0 || index + part.length() > end) {
				return false;
			}
			position = index + part.length();
		}
		return true;
	}

	/**
	 * Returns the elements matching the pattern with the given mode. A null array
	 * returns an empty array.
	 *
	 * <pre>
	 * StringUtils.matchMany(["apple", "banana"], "ap", MatchMode.START) = ["apple"]
	 * </pre>
	 */
	public static String[] matchMany(String[] array, String pattern, MatchMode matchMode) {
		List<String> list = new ArrayList<>();
		if (array == null) {
			return EMPTY_ARRAY;
		}
		for (String str : array) {
			if (matchMode.matches(str, pattern)) {
				list.add(str);
			}
		}
		return list.toArray(new String[0]);
	}
}
