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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Null-safe escaping and unescaping of strings for HTML, XML, JSON, CSV and
 * Java string literals. Every method returns null for a null input.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class EscapeUtils {

	private static final char[] HEX = "0123456789abcdef".toCharArray();

	/**
	 * The longest entity name accepted between '&amp;' and ';'.
	 */
	private static final int MAX_ENTITY_LENGTH = 10;

	private static final Map<String, Integer> XML_ENTITIES;

	private static final Map<String, Integer> HTML_ENTITIES;

	static {
		Map<String, Integer> xml = new HashMap<String, Integer>();
		xml.put("amp", (int) '&');
		xml.put("lt", (int) '<');
		xml.put("gt", (int) '>');
		xml.put("quot", (int) '"');
		xml.put("apos", (int) '\'');
		XML_ENTITIES = Collections.unmodifiableMap(xml);

		Map<String, Integer> html = new HashMap<String, Integer>(xml);
		html.put("nbsp", 0xA0);
		html.put("iexcl", 0xA1);
		html.put("cent", 0xA2);
		html.put("pound", 0xA3);
		html.put("curren", 0xA4);
		html.put("yen", 0xA5);
		html.put("brvbar", 0xA6);
		html.put("sect", 0xA7);
		html.put("uml", 0xA8);
		html.put("copy", 0xA9);
		html.put("ordf", 0xAA);
		html.put("laquo", 0xAB);
		html.put("not", 0xAC);
		html.put("shy", 0xAD);
		html.put("reg", 0xAE);
		html.put("macr", 0xAF);
		html.put("deg", 0xB0);
		html.put("plusmn", 0xB1);
		html.put("sup2", 0xB2);
		html.put("sup3", 0xB3);
		html.put("acute", 0xB4);
		html.put("micro", 0xB5);
		html.put("para", 0xB6);
		html.put("middot", 0xB7);
		html.put("cedil", 0xB8);
		html.put("sup1", 0xB9);
		html.put("ordm", 0xBA);
		html.put("raquo", 0xBB);
		html.put("frac14", 0xBC);
		html.put("frac12", 0xBD);
		html.put("frac34", 0xBE);
		html.put("iquest", 0xBF);
		html.put("times", 0xD7);
		html.put("divide", 0xF7);
		html.put("ndash", 0x2013);
		html.put("mdash", 0x2014);
		html.put("lsquo", 0x2018);
		html.put("rsquo", 0x2019);
		html.put("sbquo", 0x201A);
		html.put("ldquo", 0x201C);
		html.put("rdquo", 0x201D);
		html.put("bdquo", 0x201E);
		html.put("dagger", 0x2020);
		html.put("Dagger", 0x2021);
		html.put("bull", 0x2022);
		html.put("hellip", 0x2026);
		html.put("permil", 0x2030);
		html.put("lsaquo", 0x2039);
		html.put("rsaquo", 0x203A);
		html.put("euro", 0x20AC);
		html.put("trade", 0x2122);
		html.put("larr", 0x2190);
		html.put("uarr", 0x2191);
		html.put("rarr", 0x2192);
		html.put("darr", 0x2193);
		html.put("harr", 0x2194);
		html.put("ensp", 0x2002);
		html.put("emsp", 0x2003);
		html.put("thinsp", 0x2009);
		html.put("zwnj", 0x200C);
		html.put("zwj", 0x200D);
		HTML_ENTITIES = Collections.unmodifiableMap(html);
	}

	// ------------------------------------------------------------------ HTML

	/**
	 * Escapes the five HTML special characters {@code & < > " '}. The single
	 * quote is written as the numeric entity {@code &#39;}, which every HTML
	 * version understands.
	 *
	 * <pre>
	 * EscapeUtils.escapeHtml(null)                    = null
	 * EscapeUtils.escapeHtml("")                      = ""
	 * EscapeUtils.escapeHtml("&lt;a href=\"x\"&gt;Tom &amp; Jerry's&lt;/a&gt;")
	 *     = "&amp;lt;a href=&amp;quot;x&amp;quot;&amp;gt;Tom &amp;amp; Jerry&amp;#39;s&amp;lt;/a&amp;gt;"
	 * EscapeUtils.escapeHtml("中文😀")                 = "中文😀"
	 * </pre>
	 */
	public static String escapeHtml(String str) {
		if (str == null) {
			return null;
		}
		StringBuilder out = null;
		for (int i = 0, n = str.length(); i < n; i++) {
			char c = str.charAt(i);
			String rep;
			switch (c) {
			case '&':
				rep = "&amp;";
				break;
			case '<':
				rep = "&lt;";
				break;
			case '>':
				rep = "&gt;";
				break;
			case '"':
				rep = "&quot;";
				break;
			case '\'':
				rep = "&#39;";
				break;
			default:
				rep = null;
			}
			if (rep != null) {
				if (out == null) {
					out = new StringBuilder(n + 16).append(str, 0, i);
				}
				out.append(rep);
			} else if (out != null) {
				out.append(c);
			}
		}
		return out == null ? str : out.toString();
	}

	/**
	 * Unescapes HTML entities: the five basic named entities ({@code &amp; &lt;
	 * &gt; &quot; &apos;}), common named entities such as {@code &nbsp; &copy;
	 * &reg; &trade; &hellip; &euro;}, decimal ({@code &#65;}) and hexadecimal
	 * ({@code &#x41;}) numeric entities including supplementary characters.
	 * Unknown or malformed entities are kept unchanged. Entity names are case
	 * sensitive.
	 *
	 * <pre>
	 * EscapeUtils.unescapeHtml(null)                 = null
	 * EscapeUtils.unescapeHtml("&amp;lt;b&amp;gt;")          = "&lt;b&gt;"
	 * EscapeUtils.unescapeHtml("&amp;copy; 2026&amp;nbsp;Fred") = "© 2026" + (char) 0xA0 + "Fred"
	 * EscapeUtils.unescapeHtml("&amp;#65;&amp;#x42;&amp;#X43;")    = "ABC"
	 * EscapeUtils.unescapeHtml("&amp;#x1F600;")             = "😀"
	 * EscapeUtils.unescapeHtml("&amp;foo; &amp; &amp;#xZZ;")     = "&amp;foo; &amp; &amp;#xZZ;"
	 * </pre>
	 */
	public static String unescapeHtml(String str) {
		return unescapeEntities(str, HTML_ENTITIES);
	}

	// ------------------------------------------------------------------- XML

	/**
	 * Escapes the five XML 1.0 predefined entities ({@code &amp; &lt; &gt;
	 * &quot; &apos;}) and removes every character that is not allowed in an XML
	 * 1.0 document: control characters other than tab, line feed and carriage
	 * return, unpaired surrogates, U+FFFE and U+FFFF.
	 *
	 * <pre>
	 * EscapeUtils.escapeXml(null)              = null
	 * EscapeUtils.escapeXml("&lt;a b='1'&gt;&amp;&lt;/a&gt;")  = "&amp;lt;a b=&amp;apos;1&amp;apos;&amp;gt;&amp;amp;&amp;lt;/a&amp;gt;"
	 * EscapeUtils.escapeXml("a" + (char) 0 + "b" + (char) 7 + "c\td") = "abc\td"
	 * </pre>
	 */
	public static String escapeXml(String str) {
		if (str == null) {
			return null;
		}
		int n = str.length();
		StringBuilder out = new StringBuilder(n + 16);
		for (int i = 0; i < n; i++) {
			char c = str.charAt(i);
			switch (c) {
			case '&':
				out.append("&amp;");
				break;
			case '<':
				out.append("&lt;");
				break;
			case '>':
				out.append("&gt;");
				break;
			case '"':
				out.append("&quot;");
				break;
			case '\'':
				out.append("&apos;");
				break;
			default:
				if (Character.isHighSurrogate(c)) {
					if (i + 1 < n && Character.isLowSurrogate(str.charAt(i + 1))) {
						out.append(c).append(str.charAt(++i));
					}
				} else if (isXmlChar(c)) {
					out.append(c);
				}
			}
		}
		return out.toString();
	}

	private static boolean isXmlChar(char c) {
		return c == '\t' || c == '\n' || c == '\r' || (c >= 0x20 && c <= 0xD7FF) || (c >= 0xE000 && c <= 0xFFFD);
	}

	/**
	 * Unescapes the five XML predefined entities and decimal / hexadecimal
	 * numeric character references. Any other entity is kept unchanged.
	 *
	 * <pre>
	 * EscapeUtils.unescapeXml(null)                   = null
	 * EscapeUtils.unescapeXml("&amp;lt;a&amp;gt;&amp;amp;&amp;apos;&amp;quot;") = "&lt;a&gt;&amp;'\""
	 * EscapeUtils.unescapeXml("&amp;#x4E2D;&amp;#25991;")       = "中文"
	 * EscapeUtils.unescapeXml("&amp;nbsp;")                 = "&amp;nbsp;"
	 * </pre>
	 */
	public static String unescapeXml(String str) {
		return unescapeEntities(str, XML_ENTITIES);
	}

	private static String unescapeEntities(String str, Map<String, Integer> entities) {
		if (str == null) {
			return null;
		}
		int amp = str.indexOf('&');
		if (amp < 0) {
			return str;
		}
		int n = str.length();
		StringBuilder out = new StringBuilder(n);
		out.append(str, 0, amp);
		int i = amp;
		while (i < n) {
			char c = str.charAt(i);
			if (c != '&') {
				out.append(c);
				i++;
				continue;
			}
			int semi = indexOfSemicolon(str, i + 1, Math.min(n, i + 2 + MAX_ENTITY_LENGTH));
			int codePoint = -1;
			if (semi > i + 1) {
				String name = str.substring(i + 1, semi);
				if (name.charAt(0) == '#') {
					codePoint = parseNumericEntity(name);
				} else {
					Integer value = entities.get(name);
					if (value != null) {
						codePoint = value.intValue();
					}
				}
			}
			if (codePoint >= 0) {
				out.appendCodePoint(codePoint);
				i = semi + 1;
			} else {
				out.append(c);
				i++;
			}
		}
		return out.toString();
	}

	private static int indexOfSemicolon(String str, int from, int to) {
		for (int i = from; i < to; i++) {
			if (str.charAt(i) == ';') {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Parses "#65" or "#x41" / "#X41". Returns -1 if it is not a valid code
	 * point.
	 */
	private static int parseNumericEntity(String name) {
		int radix = 10;
		int start = 1;
		if (name.length() > 1 && (name.charAt(1) == 'x' || name.charAt(1) == 'X')) {
			radix = 16;
			start = 2;
		}
		if (start >= name.length()) {
			return -1;
		}
		int value = 0;
		for (int i = start; i < name.length(); i++) {
			int digit = digit(name.charAt(i), radix);
			if (digit < 0) {
				return -1;
			}
			value = value * radix + digit;
			if (value > Character.MAX_CODE_POINT) {
				return -1;
			}
		}
		return value;
	}

	// ------------------------------------------------------------------ JSON

	/**
	 * Escapes the content of a JSON string (without the surrounding quotes)
	 * according to RFC 8259: {@code "} and {@code \} are backslash escaped,
	 * {@code \b \f \n \r \t} use their short forms and any other control
	 * character below U+0020 is written as a lower-case {@code \}{@code u00xx}. The
	 * forward slash and non-ASCII characters are kept unchanged.
	 *
	 * <pre>
	 * EscapeUtils.escapeJson(null)                  = null
	 * EscapeUtils.escapeJson("He said \"hi\"\n")     = "He said \\\"hi\\\"\\n"
	 * EscapeUtils.escapeJson("a/b\\c")              = "a/b\\\\c"
	 * EscapeUtils.escapeJson((char) 1 + "" + (char) 31) = "\\u0001\\u001f"
	 * EscapeUtils.escapeJson("中文😀")               = "中文😀"
	 * </pre>
	 */
	public static String escapeJson(String str) {
		if (str == null) {
			return null;
		}
		int n = str.length();
		StringBuilder out = new StringBuilder(n + 16);
		for (int i = 0; i < n; i++) {
			char c = str.charAt(i);
			switch (c) {
			case '"':
				out.append("\\\"");
				break;
			case '\\':
				out.append("\\\\");
				break;
			case '\b':
				out.append("\\b");
				break;
			case '\f':
				out.append("\\f");
				break;
			case '\n':
				out.append("\\n");
				break;
			case '\r':
				out.append("\\r");
				break;
			case '\t':
				out.append("\\t");
				break;
			default:
				if (c < 0x20) {
					appendUnicode(out, c);
				} else {
					out.append(c);
				}
			}
		}
		return out.toString();
	}

	/**
	 * Unescapes the content of a JSON string: {@code \" \\ \/ \b \f \n \r \t}
	 * and {@code \}{@code uXXXX} (surrogate pairs such as
	 * {@code \}{@code uD83D\}{@code uDE00} become one supplementary character).
	 *
	 * <pre>
	 * EscapeUtils.unescapeJson(null)                     = null
	 * EscapeUtils.unescapeJson("He said \\\"hi\\\"\\n")   = "He said \"hi\"\n"
	 * EscapeUtils.unescapeJson("\\/\\u4e2D\\ud83d\\ude00") = "/中😀"
	 * EscapeUtils.unescapeJson("\\x")                    -&gt; IllegalArgumentException
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the string contains an unknown escape,
	 *                                  a truncated {@code \}{@code u} escape or
	 *                                  ends with a single backslash
	 */
	public static String unescapeJson(String str) {
		if (str == null) {
			return null;
		}
		int n = str.length();
		if (str.indexOf('\\') < 0) {
			return str;
		}
		StringBuilder out = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			char c = str.charAt(i);
			if (c != '\\') {
				out.append(c);
				continue;
			}
			if (i + 1 >= n) {
				throw new IllegalArgumentException("Unterminated escape sequence at index " + i);
			}
			char e = str.charAt(++i);
			switch (e) {
			case '"':
			case '\\':
			case '/':
				out.append(e);
				break;
			case 'b':
				out.append('\b');
				break;
			case 'f':
				out.append('\f');
				break;
			case 'n':
				out.append('\n');
				break;
			case 'r':
				out.append('\r');
				break;
			case 't':
				out.append('\t');
				break;
			case 'u':
				out.append(parseHex4(str, i + 1));
				i += 4;
				break;
			default:
				throw new IllegalArgumentException("Illegal escape sequence '\\" + e + "' at index " + (i - 1));
			}
		}
		return out.toString();
	}

	// ------------------------------------------------------------------- CSV

	/**
	 * Escapes a single CSV field according to RFC 4180: if the value contains a
	 * comma, a double quote, CR or LF it is wrapped in double quotes and every
	 * double quote is doubled; otherwise it is returned unchanged.
	 *
	 * <pre>
	 * EscapeUtils.escapeCsv(null)           = null
	 * EscapeUtils.escapeCsv("abc")          = "abc"
	 * EscapeUtils.escapeCsv("a,b")          = "\"a,b\""
	 * EscapeUtils.escapeCsv("say \"hi\"")   = "\"say \"\"hi\"\"\""
	 * EscapeUtils.escapeCsv("line1\nline2") = "\"line1\nline2\""
	 * </pre>
	 */
	public static String escapeCsv(String str) {
		if (str == null) {
			return null;
		}
		boolean needQuote = false;
		for (int i = 0, n = str.length(); i < n; i++) {
			char c = str.charAt(i);
			if (c == ',' || c == '"' || c == '\r' || c == '\n') {
				needQuote = true;
				break;
			}
		}
		if (!needQuote) {
			return str;
		}
		return "\"" + str.replace("\"", "\"\"") + "\"";
	}

	/**
	 * Unescapes a single CSV field: a value wrapped in double quotes has the
	 * quotes removed and every {@code ""} turned back into {@code "}; any other
	 * value is returned unchanged.
	 *
	 * <pre>
	 * EscapeUtils.unescapeCsv(null)                   = null
	 * EscapeUtils.unescapeCsv("abc")                  = "abc"
	 * EscapeUtils.unescapeCsv("\"a,b\"")              = "a,b"
	 * EscapeUtils.unescapeCsv("\"say \"\"hi\"\"\"")   = "say \"hi\""
	 * EscapeUtils.unescapeCsv("\"")                   = "\""
	 * </pre>
	 */
	public static String unescapeCsv(String str) {
		if (str == null) {
			return null;
		}
		int n = str.length();
		if (n < 2 || str.charAt(0) != '"' || str.charAt(n - 1) != '"') {
			return str;
		}
		return str.substring(1, n - 1).replace("\"\"", "\"");
	}

	// ------------------------------------------------------------------ Java

	/**
	 * Escapes a string as the content of a Java string literal, keeping
	 * non-ASCII characters unchanged. Same as
	 * {@code escapeJava(str, false)}.
	 *
	 * <pre>
	 * EscapeUtils.escapeJava(null)              = null
	 * EscapeUtils.escapeJava("a\tb\n\"c\"\\")   = "a\\tb\\n\\\"c\\\"\\\\"
	 * EscapeUtils.escapeJava("中文")             = "中文"
	 * </pre>
	 */
	public static String escapeJava(String str) {
		return escapeJava(str, false);
	}

	/**
	 * Escapes a string as the content of a Java string literal: {@code \t \n \r
	 * \b \f \" \\} use their short forms, other control characters (below U+0020
	 * and U+007F) are written as a lower-case {@code \}{@code u00xx}. If
	 * {@code escapeUnicode} is true every char above U+007F is written as
	 * {@code \}{@code uxxxx} (one escape per UTF-16 unit, the same format as
	 * {@link StringUtils#unicodeString(String)}), otherwise it is kept unchanged.
	 * The single quote is not escaped.
	 *
	 * <pre>
	 * EscapeUtils.escapeJava("中文", true)  = "\\u4e2d\\u6587"
	 * EscapeUtils.escapeJava("😀", true)    = "\\ud83d\\ude00"
	 * EscapeUtils.escapeJava(String.valueOf((char) 0), false) = "\\u0000"
	 * </pre>
	 */
	public static String escapeJava(String str, boolean escapeUnicode) {
		if (str == null) {
			return null;
		}
		int n = str.length();
		StringBuilder out = new StringBuilder(n + 16);
		for (int i = 0; i < n; i++) {
			char c = str.charAt(i);
			switch (c) {
			case '\t':
				out.append("\\t");
				break;
			case '\n':
				out.append("\\n");
				break;
			case '\r':
				out.append("\\r");
				break;
			case '\b':
				out.append("\\b");
				break;
			case '\f':
				out.append("\\f");
				break;
			case '"':
				out.append("\\\"");
				break;
			case '\\':
				out.append("\\\\");
				break;
			default:
				if (c < 0x20 || c == 0x7F || (escapeUnicode && c > 0x7F)) {
					appendUnicode(out, c);
				} else {
					out.append(c);
				}
			}
		}
		return out.toString();
	}

	/**
	 * Unescapes the content of a Java string literal: {@code \t \n \r \b \f \s
	 * \" \' \\}, octal escapes ({@code \0} to {@code \377}) and unicode escapes
	 * ({@code \}{@code uXXXX}, the JLS also allows several 'u' such as
	 * {@code \}{@code uu0041}).
	 *
	 * <pre>
	 * EscapeUtils.unescapeJava(null)                    = null
	 * EscapeUtils.unescapeJava("a\\tb\\n\\\"c\\\"\\\\")  = "a\tb\n\"c\"\\"
	 * EscapeUtils.unescapeJava("\\u4e2d\\u6587")        = "中文"
	 * EscapeUtils.unescapeJava("\\101\\0\\'")           = "A" + (char) 0 + "'"
	 * EscapeUtils.unescapeJava("\\q")                   -&gt; IllegalArgumentException
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the string contains an unknown escape,
	 *                                  a truncated unicode escape or ends with a
	 *                                  single backslash
	 */
	public static String unescapeJava(String str) {
		if (str == null) {
			return null;
		}
		if (str.indexOf('\\') < 0) {
			return str;
		}
		int n = str.length();
		StringBuilder out = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			char c = str.charAt(i);
			if (c != '\\') {
				out.append(c);
				continue;
			}
			if (i + 1 >= n) {
				throw new IllegalArgumentException("Unterminated escape sequence at index " + i);
			}
			char e = str.charAt(++i);
			switch (e) {
			case 't':
				out.append('\t');
				break;
			case 'n':
				out.append('\n');
				break;
			case 'r':
				out.append('\r');
				break;
			case 'b':
				out.append('\b');
				break;
			case 'f':
				out.append('\f');
				break;
			case 's':
				out.append(' ');
				break;
			case '"':
			case '\'':
			case '\\':
				out.append(e);
				break;
			case 'u':
				while (i + 1 < n && str.charAt(i + 1) == 'u') {
					i++;
				}
				out.append(parseHex4(str, i + 1));
				i += 4;
				break;
			default:
				if (e >= '0' && e <= '7') {
					// up to 3 octal digits, at most \377
					int value = e - '0';
					int maxDigits = e <= '3' ? 3 : 2;
					int digits = 1;
					while (digits < maxDigits && i + 1 < n && str.charAt(i + 1) >= '0' && str.charAt(i + 1) <= '7') {
						value = value * 8 + (str.charAt(++i) - '0');
						digits++;
					}
					out.append((char) value);
				} else {
					throw new IllegalArgumentException("Illegal escape sequence '\\" + e + "' at index " + (i - 1));
				}
			}
		}
		return out.toString();
	}

	// --------------------------------------------------------------- helpers

	/**
	 * ASCII-only digit value (unlike Character.digit, which also accepts
	 * full-width and other non-ASCII digits).
	 */
	private static int digit(char c, int radix) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		}
		if (radix == 16) {
			if (c >= 'a' && c <= 'f') {
				return c - 'a' + 10;
			}
			if (c >= 'A' && c <= 'F') {
				return c - 'A' + 10;
			}
		}
		return -1;
	}

	private static void appendUnicode(StringBuilder out, char c) {
		out.append("\\u").append(HEX[(c >> 12) & 0xF]).append(HEX[(c >> 8) & 0xF]).append(HEX[(c >> 4) & 0xF]).append(HEX[c & 0xF]);
	}

	private static char parseHex4(String str, int start) {
		if (start + 4 > str.length()) {
			throw new IllegalArgumentException("Truncated unicode escape at index " + (start - 2));
		}
		int value = 0;
		for (int i = start; i < start + 4; i++) {
			int digit = digit(str.charAt(i), 16);
			if (digit < 0) {
				throw new IllegalArgumentException("Illegal unicode escape '" + str.substring(start, start + 4) + "' at index " + (start - 2));
			}
			value = (value << 4) | digit;
		}
		return (char) value;
	}

}
