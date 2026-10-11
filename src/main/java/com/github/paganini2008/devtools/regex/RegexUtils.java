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
package com.github.paganini2008.devtools.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * RegexUtils
 * 
 * Regular expression helpers with a cache of compiled patterns. Methods taking {@code startFrom}
 * evaluate the regex against the input starting at that index.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class RegexUtils {

	private static final ConcurrentMap<Integer, LruMap<String, Pattern>> cache = new ConcurrentHashMap<Integer, LruMap<String, Pattern>>();

	public static final String BLANK = "[\\s\\p{Zs}]";

	public static final Pattern BLANK_PATTERN = getPattern(BLANK);

	public static final String EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

	public static final Pattern EMAIL_PATTERN = getPattern(EMAIL);

	public static final String URL = "^((((https|http|ftp|rtsp|mms)://)?(([0-9a-zA-Z_!~*'().&=+$%-]+: )?[0-9a-zA-Z_!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([0-9a-zA-Z_!~*'()-]+\\.)*([0-9a-zA-Z][0-9a-zA-Z-]{0,61})?[0-9a-zA-Z]\\.[a-zA-Z]{2,6}|(localhost))(:[0-9]{1,4})?"
			+ "((/?)|(/)|(/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?))|((file:///[a-zA-Z]:)((/?)|(/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)))$";

	public static final Pattern URL_PATTERN = getPattern(URL);

	public static final String ADDRESS = "^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$";

	public static final Pattern ADDRESS_PATTERN = getPattern(ADDRESS);

	public static final String LOCAL_IP = "127(\\.\\d{1,3}){3}$";

	public static final Pattern LOCAL_IP_PATTERN = getPattern(LOCAL_IP);

	/**
	 * A loose pattern for dotted numeric addresses: it does not check the 0-255 range and accepts up to six groups, so
	 * {@code "999.1.1.1"} matches. Use {@link com.github.paganini2008.devtools.validation.ValidationUtils#isIPv4(String)}
	 * for strict validation.
	 */
	public static final String IP = "\\d{1,3}(\\.\\d{1,3}){3,5}$";

	public static final Pattern IP_PATTERN = getPattern(IP);

	/**
	 * Returns the cached compiled pattern for the regex.
	 *
	 * @throws IllegalArgumentException if the regex is null or empty
	 */
	public static Pattern getPattern(String regex) {
		return getPattern(regex, 0);
	}

	/**
	 * Returns the cached compiled pattern for the regex and flags.
	 *
	 * @throws IllegalArgumentException if the regex is null or empty
	 */
	public static Pattern getPattern(String regex, int flags) {
		Assert.hasNoLength(regex, "Regex string must not be empty.");
		String key = regex;
		Map<String, Pattern> m = cache.get(flags);
		if (m == null) {
			cache.putIfAbsent(flags, new LruMap<String, Pattern>(1024));
			m = cache.get(flags);
		}
		Pattern pattern = m.get(key);
		if (pattern == null) {
			m.put(key, Pattern.compile(regex, flags));
			pattern = m.get(key);
		}
		return pattern;
	}

	/**
	 * Returns a matcher of the regex for the input.
	 *
	 * @throws IllegalArgumentException if the input is null
	 */
	public static Matcher getMatcher(CharSequence input, String regex) {
		return getMatcher(input, regex, 0);
	}

	/**
	 * Returns a matcher of the regex for the input.
	 *
	 * @throws IllegalArgumentException if the input is null
	 */
	public static Matcher getMatcher(CharSequence input, String regex, int flags) {
		return getMatcher(input, regex, flags, 0);
	}

	/**
	 * Returns a matcher of the regex for the part of the input starting at {@code startFrom}.
	 *
	 * @throws IllegalArgumentException if the input is null
	 */
	public static Matcher getMatcher(CharSequence input, String regex, int flags, int startFrom) {
		Assert.isNull(input, "Input string must not be null.");
		Pattern p = getPattern(regex, flags);
		return p.matcher(input.subSequence(startFrom, input.length()));
	}

	/**
	 * Checks whether the entire input matches the regex.
	 *
	 * <pre>
	 * RegexUtils.matches("abc", "[a-c]+")  = true
	 * RegexUtils.matches("abcd", "[a-c]+") = false
	 * RegexUtils.matches(null, "a*")       = false
	 * </pre>
	 */
	public static boolean matches(CharSequence input, String regex) {
		return matches(input, regex, 0);
	}

	/**
	 * Checks whether the input from {@code startFrom} on matches the regex; null input returns false.
	 */
	public static boolean matches(CharSequence input, String regex, int startFrom) {
		return matches(input, regex, 0, startFrom);
	}

	/**
	 * Checks whether the input from {@code startFrom} on matches the regex; null input returns false.
	 */
	public static boolean matches(CharSequence input, String regex, int flags, int startFrom) {
		if (input == null) {
			return false;
		}
		Matcher m = getMatcher(input, regex, flags, startFrom);
		return m.matches();
	}

	/**
	 * Returns the first match of the regex in the input.
	 *
	 * <pre>
	 * RegexUtils.match("abc123def", "\\d+") = "123"
	 * RegexUtils.match("abc", "\\d+")       = ""
	 * RegexUtils.match(null, "\\d+")        = ""
	 * </pre>
	 */
	public static String match(CharSequence input, String regex) {
		return match(input, regex, 0);
	}

	/**
	 * Returns the first match of the regex from {@code startFrom} on, or an empty string.
	 */
	public static String match(CharSequence input, String regex, int startFrom) {
		return match(input, regex, 0, startFrom);
	}

	/**
	 * Returns the first match of the regex from {@code startFrom} on, or an empty string.
	 */
	public static String match(CharSequence input, String regex, int flags, int startFrom) {
		return match(input, regex, flags, 0, startFrom);
	}

	/**
	 * Returns the given group of the first match from {@code startFrom} on.
	 *
	 * @return the group (null if the group did not participate in the match), or an empty string if
	 *         nothing matches or the input is null
	 */
	public static String match(CharSequence input, String regex, int flags, int group, int startFrom) {
		if (input == null) {
			return "";
		}
		Matcher m = getMatcher(input, regex, flags, startFrom);
		return m.find() ? m.group(group) : "";
	}

	/**
	 * Checks whether the regex is not found in the input from {@code startFrom} on.
	 */
	public static boolean notFind(CharSequence input, String regex, int startFrom) {
		return !find(input, regex, startFrom);
	}

	/**
	 * Checks whether the regex is not found in the input from {@code startFrom} on.
	 */
	public static boolean notFind(CharSequence input, String regex, int flags, int startFrom) {
		return !find(input, regex, flags, startFrom);
	}

	/**
	 * Checks whether the regex is found in the input from {@code startFrom} on; null input returns false.
	 */
	public static boolean find(CharSequence input, String regex, int startFrom) {
		return find(input, regex, 0, startFrom);
	}

	/**
	 * Checks whether the regex is found in the input from {@code startFrom} on; null input returns false.
	 */
	public static boolean find(CharSequence input, String regex, int flags, int startFrom) {
		if (input == null) {
			return false;
		}
		Matcher m = getMatcher(input, regex, flags, startFrom);
		return m.find();
	}

	/**
	 * Returns the index of the first match of the regex at or after {@code fromIndex}.
	 *
	 * <pre>
	 * RegexUtils.indexOf("abc123", "\\d+", 0) = 3
	 * RegexUtils.indexOf("abc", "\\d+", 0)    = -1
	 * RegexUtils.indexOf(null, *, *)           = -1
	 * </pre>
	 */
	public static int indexOf(String str, String regex, int fromIndex) {
		return indexOf(str, regex, 0, fromIndex);
	}

	/**
	 * Returns the index of the first match at or after {@code fromIndex}, or -1 if not found, the
	 * string is null or the index is out of range.
	 */
	public static int indexOf(String str, String regex, int flags, int fromIndex) {
		if (str == null || fromIndex < 0 || fromIndex > str.length()) {
			return -1;
		}
		Matcher m = getPattern(regex, flags).matcher(str);
		return m.find(fromIndex) ? m.start() : -1;
	}

	/**
	 * Splits the source around matches of the regex, dropping empty tokens.
	 *
	 * <pre>
	 * RegexUtils.split("a b  c", "\\s+") = [a, b, c]
	 * RegexUtils.split(",a,,b", ",")     = [a, b]
	 * RegexUtils.split(null, ",")        = []
	 * </pre>
	 */
	public static List<String> split(CharSequence source, String regex) {
		return split(source, regex, 0);
	}

	/**
	 * Splits the source around matches of the regex, dropping empty tokens.
	 */
	public static List<String> split(CharSequence source, String regex, int flags) {
		return split(source, regex, flags, false);
	}

	/**
	 * Splits the source around matches of the regex, dropping empty tokens and optionally returning
	 * the delimiters as tokens.
	 *
	 * @return the tokens, an empty list for null
	 */
	public static List<String> split(CharSequence source, String regex, int flags, boolean returnDelims) {
		if (source == null) {
			return new ArrayList<String>();
		}
		RegexTokenizer tokenizer = new RegexTokenizer(source, regex, returnDelims);
		tokenizer.setFlags(flags);
		List<String> list = new ArrayList<String>();
		String s;
		while (tokenizer.hasMoreElements()) {
			s = tokenizer.nextElement();
			list.add(s);
		}
		return list;
	}
}
