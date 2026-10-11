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
package com.github.paganini2008.devtools.codec;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Null-safe URL encoding utilities: form encoding (application/x-www-form-urlencoded), RFC 3986 component encoding
 * and query string building/parsing. UTF-8 is used unless a charset is given.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class URLCodecUtils {

	private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

	/**
	 * Encodes the string in application/x-www-form-urlencoded format using UTF-8 (space becomes '+').
	 *
	 * <pre>
	 * URLCodecUtils.encode(null)          = null
	 * URLCodecUtils.encode("a b&amp;c=d")     = "a+b%26c%3Dd"
	 * URLCodecUtils.encode("中文")         = "%E4%B8%AD%E6%96%87"
	 * </pre>
	 */
	public static String encode(String str) {
		return encode(str, StandardCharsets.UTF_8);
	}

	/**
	 * Encodes the string in application/x-www-form-urlencoded format using the given charset (UTF-8 if
	 * <code>null</code>).
	 *
	 * @return the encoded string, or <code>null</code> if the input is <code>null</code>
	 */
	public static String encode(String str, Charset charset) {
		if (str == null) {
			return null;
		}
		try {
			return URLEncoder.encode(str, charset(charset).name());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Decodes an application/x-www-form-urlencoded string using UTF-8 ('+' becomes space).
	 *
	 * <pre>
	 * URLCodecUtils.decode(null)                 = null
	 * URLCodecUtils.decode("a+b%26c%3Dd")        = "a b&amp;c=d"
	 * URLCodecUtils.decode("%E4%B8%AD%E6%96%87") = "中文"
	 * URLCodecUtils.decode("%G1")                = IllegalArgumentException
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the string contains an illegal escape sequence
	 */
	public static String decode(String str) {
		return decode(str, StandardCharsets.UTF_8);
	}

	/**
	 * Decodes an application/x-www-form-urlencoded string using the given charset (UTF-8 if <code>null</code>).
	 *
	 * @throws IllegalArgumentException if the string contains an illegal escape sequence
	 */
	public static String decode(String str, Charset charset) {
		if (str == null) {
			return null;
		}
		try {
			return URLDecoder.decode(str, charset(charset).name());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Percent-encodes the string as an RFC 3986 URI component using UTF-8: only ALPHA, DIGIT and "-_.~" are left
	 * as is, everything else (including space, '+', '*' and '/') is encoded with uppercase hex digits.
	 *
	 * <pre>
	 * URLCodecUtils.encodeComponent(null)       = null
	 * URLCodecUtils.encodeComponent("a b+c")    = "a%20b%2Bc"
	 * URLCodecUtils.encodeComponent("-_.~*")    = "-_.~%2A"
	 * URLCodecUtils.encodeComponent("中文")      = "%E4%B8%AD%E6%96%87"
	 * </pre>
	 */
	public static String encodeComponent(String str) {
		return encodeComponent(str, StandardCharsets.UTF_8);
	}

	/**
	 * Percent-encodes the string as an RFC 3986 URI component using the given charset (UTF-8 if <code>null</code>).
	 *
	 * @return the encoded string, or <code>null</code> if the input is <code>null</code>
	 */
	public static String encodeComponent(String str, Charset charset) {
		if (str == null) {
			return null;
		}
		final Charset cs = charset(charset);
		final int len = str.length();
		StringBuilder out = null;
		int i = 0;
		while (i < len) {
			char ch = str.charAt(i);
			if (isUnreserved(ch)) {
				if (out != null) {
					out.append(ch);
				}
				i++;
				continue;
			}
			if (out == null) {
				out = new StringBuilder(len * 3);
				out.append(str, 0, i);
			}
			int start = i;
			while (i < len && !isUnreserved(str.charAt(i))) {
				i++;
			}
			byte[] bytes = str.substring(start, i).getBytes(cs);
			for (byte b : bytes) {
				out.append('%').append(HEX_DIGITS[(b >>> 4) & 0x0F]).append(HEX_DIGITS[b & 0x0F]);
			}
		}
		return out != null ? out.toString() : str;
	}

	/**
	 * Decodes a percent-encoded RFC 3986 URI component using UTF-8. Unlike {@link #decode(String)}, '+' is kept as a
	 * literal plus sign.
	 *
	 * <pre>
	 * URLCodecUtils.decodeComponent(null)        = null
	 * URLCodecUtils.decodeComponent("a%20b%2Bc") = "a b+c"
	 * URLCodecUtils.decodeComponent("a+b")       = "a+b"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the string contains an illegal escape sequence
	 */
	public static String decodeComponent(String str) {
		return decodeComponent(str, StandardCharsets.UTF_8);
	}

	/**
	 * Decodes a percent-encoded RFC 3986 URI component using the given charset (UTF-8 if <code>null</code>). '+' is
	 * kept as a literal plus sign.
	 *
	 * @throws IllegalArgumentException if the string contains an illegal escape sequence
	 */
	public static String decodeComponent(String str, Charset charset) {
		if (str == null) {
			return null;
		}
		return decode(str.replace("+", "%2B"), charset);
	}

	/**
	 * Builds a query string ("a=1&amp;b=2") from the map in its iteration order, form-encoding keys and values with
	 * UTF-8. A <code>null</code> value is written as "key=", entries with a <code>null</code> key are skipped.
	 *
	 * <pre>
	 * URLCodecUtils.encodeQuery(null)                      = null
	 * URLCodecUtils.encodeQuery({})                        = ""
	 * URLCodecUtils.encodeQuery({a=1, b=2})                = "a=1&amp;b=2"
	 * URLCodecUtils.encodeQuery({q="a b&amp;c", empty=null})   = "q=a+b%26c&amp;empty="
	 * </pre>
	 */
	public static String encodeQuery(Map<String, ?> params) {
		return encodeQuery(params, StandardCharsets.UTF_8);
	}

	/**
	 * Builds a query string from the map in its iteration order, form-encoding keys and values with the given charset
	 * (UTF-8 if <code>null</code>).
	 *
	 * @return the query string without a leading '?', or <code>null</code> if the map is <code>null</code>
	 */
	public static String encodeQuery(Map<String, ?> params, Charset charset) {
		if (params == null) {
			return null;
		}
		StringBuilder out = new StringBuilder();
		for (Map.Entry<String, ?> entry : params.entrySet()) {
			if (entry.getKey() == null) {
				continue;
			}
			if (out.length() > 0) {
				out.append('&');
			}
			out.append(encode(entry.getKey(), charset)).append('=');
			if (entry.getValue() != null) {
				out.append(encode(String.valueOf(entry.getValue()), charset));
			}
		}
		return out.toString();
	}

	/**
	 * Parses a query string into an insertion-ordered map, form-decoding keys and values with UTF-8. A leading '?' is
	 * ignored, empty segments are skipped, a parameter without '=' gets an empty value, and for repeated names the
	 * first value wins.
	 *
	 * <pre>
	 * URLCodecUtils.parseQuery(null)              = {}
	 * URLCodecUtils.parseQuery("?a=1&amp;b=2&amp;a=3")    = {a=1, b=2}
	 * URLCodecUtils.parseQuery("q=a+b%26c&amp;flag")  = {q="a b&amp;c", flag=""}
	 * </pre>
	 *
	 * @return a new mutable {@link LinkedHashMap}, empty if the input is <code>null</code> or empty
	 * @throws IllegalArgumentException if a key or value contains an illegal escape sequence
	 */
	public static LinkedHashMap<String, String> parseQuery(String query) {
		return parseQuery(query, StandardCharsets.UTF_8);
	}

	/**
	 * Parses a query string into an insertion-ordered map using the given charset (UTF-8 if <code>null</code>). For
	 * repeated names the first value wins.
	 *
	 * @throws IllegalArgumentException if a key or value contains an illegal escape sequence
	 */
	public static LinkedHashMap<String, String> parseQuery(String query, Charset charset) {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		for (String[] pair : splitQuery(query, charset)) {
			if (!result.containsKey(pair[0])) {
				result.put(pair[0], pair[1]);
			}
		}
		return result;
	}

	/**
	 * Parses a query string into an insertion-ordered map keeping every value of repeated names, using UTF-8.
	 *
	 * <pre>
	 * URLCodecUtils.parseQueryMulti(null)           = {}
	 * URLCodecUtils.parseQueryMulti("a=1&amp;b=2&amp;a=3") = {a=[1, 3], b=[2]}
	 * </pre>
	 *
	 * @throws IllegalArgumentException if a key or value contains an illegal escape sequence
	 */
	public static Map<String, List<String>> parseQueryMulti(String query) {
		return parseQueryMulti(query, StandardCharsets.UTF_8);
	}

	/**
	 * Parses a query string into an insertion-ordered map keeping every value of repeated names, using the given
	 * charset (UTF-8 if <code>null</code>).
	 *
	 * @return a new mutable {@link LinkedHashMap}, empty if the input is <code>null</code> or empty
	 * @throws IllegalArgumentException if a key or value contains an illegal escape sequence
	 */
	public static Map<String, List<String>> parseQueryMulti(String query, Charset charset) {
		Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
		for (String[] pair : splitQuery(query, charset)) {
			List<String> values = result.get(pair[0]);
			if (values == null) {
				values = new ArrayList<String>();
				result.put(pair[0], values);
			}
			values.add(pair[1]);
		}
		return result;
	}

	private static List<String[]> splitQuery(String query, Charset charset) {
		List<String[]> pairs = new ArrayList<String[]>();
		if (query == null) {
			return pairs;
		}
		String str = query.startsWith("?") ? query.substring(1) : query;
		int start = 0;
		final int len = str.length();
		while (start <= len) {
			int end = str.indexOf('&', start);
			if (end < 0) {
				end = len;
			}
			if (end > start) {
				String segment = str.substring(start, end);
				int eq = segment.indexOf('=');
				String key = eq >= 0 ? segment.substring(0, eq) : segment;
				String value = eq >= 0 ? segment.substring(eq + 1) : "";
				pairs.add(new String[] { decode(key, charset), decode(value, charset) });
			}
			start = end + 1;
		}
		return pairs;
	}

	private static boolean isUnreserved(char ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '-' || ch == '_'
				|| ch == '.' || ch == '~';
	}

	private static Charset charset(Charset charset) {
		return charset != null ? charset : StandardCharsets.UTF_8;
	}

}
