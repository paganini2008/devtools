package com.github.paganini2008.devtools.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * RegexUtils
 * 
 * @author Fred Feng
 * @version 1.0
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

	public static final String IP = "\\d{1,3}(\\.\\d{1,3}){3,5}$";

	public static final Pattern IP_PATTERN = getPattern(IP);

	public static Pattern getPattern(String regex) {
		return getPattern(regex, 0);
	}

	public static Pattern getPattern(String regex, int flags) {
		Assert.hasNoText(regex, "Regex string must not be empty.");
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

	public static Matcher getMatcher(CharSequence input, String regex) {
		return getMatcher(input, regex, 0);
	}

	public static Matcher getMatcher(CharSequence input, String regex, int flags) {
		return getMatcher(input, regex, flags, 0);
	}

	public static Matcher getMatcher(CharSequence input, String regex, int flags, int startFrom) {
		Assert.hasNoText(input, "Input string must not be empty.");
		Pattern p = getPattern(regex, flags);
		return p.matcher(input.subSequence(startFrom, input.length()));
	}

	public static boolean matches(CharSequence input, String regex) {
		return matches(input, regex, 0);
	}

	public static boolean matches(CharSequence input, String regex, int startFrom) {
		return matches(input, regex, 0, startFrom);
	}

	public static boolean matches(CharSequence input, String regex, int flags, int startFrom) {
		Matcher m = getMatcher(input, regex, flags, startFrom);
		return m.matches();
	}

	public static String match(CharSequence input, String regex) {
		return match(input, regex, 0);
	}

	public static String match(CharSequence input, String regex, int startFrom) {
		return match(input, regex, 0, startFrom);
	}

	public static String match(CharSequence input, String regex, int flags, int startFrom) {
		return match(input, regex, flags, 0, startFrom);
	}

	public static String match(CharSequence input, String regex, int flags, int group, int startFrom) {
		Matcher m = getMatcher(input, regex, flags, startFrom);
		return m.find() ? m.group(group) : "";
	}

	public static boolean notFind(CharSequence input, String regex, int startFrom) {
		return !find(input, regex, startFrom);
	}

	public static boolean notFind(CharSequence input, String regex, int flags, int startFrom) {
		return !find(input, regex, flags, startFrom);
	}

	public static boolean find(CharSequence input, String regex, int startFrom) {
		return find(input, regex, 0, startFrom);
	}

	public static boolean find(CharSequence input, String regex, int flags, int startFrom) {
		Matcher m = getMatcher(input, regex, flags, startFrom);
		return m.find();
	}

	public static int indexOf(String str, String regex, int fromIndex) {
		return indexOf(str, regex, 0, fromIndex);
	}

	public static int indexOf(String str, String regex, int flags, int fromIndex) {
		String result = match(str, regex, flags, fromIndex);
		return StringUtils.isBlank(result) ? -1 : str.indexOf(result, fromIndex);
	}

	public static List<String> split(CharSequence source, String regex) {
		return split(source, regex, 0);
	}

	public static List<String> split(CharSequence source, String regex, int flags) {
		return split(source, regex, flags, false);
	}

	public static List<String> split(CharSequence source, String regex, int flags, boolean returnDelims) {
		Assert.hasNoText(source);
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
