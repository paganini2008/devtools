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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * EscapeUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class EscapeUtilsTest {

	private static final String EMOJI = new String(Character.toChars(0x1F600));

	private static final String[] POOL = { "a", "Z", "0", " ", "&", "<", ">", "\"", "'", "\\", "/", ",", ";", "#", "x", "u",
			"\t", "\n", "\r", "\b", "\f", "\u0000", "\u0001", "\u001f", "\u007f", " ", "é", "中", "文",
			"。", EMOJI, new String(Character.toChars(0x20BB7)), "&amp;", "&#65;", "\\u0041", "\"\"" };

	private static final String[] XML_POOL = { "a", " ", "&", "<", ">", "\"", "'", "\t", "\n", "\r", "中", EMOJI, "&lt;",
			"©", "퟿", "", "�" };

	private static String random(Random random, String[] pool, int maxTokens) {
		StringBuilder sb = new StringBuilder();
		int n = random.nextInt(maxTokens + 1);
		for (int i = 0; i < n; i++) {
			sb.append(pool[random.nextInt(pool.length)]);
		}
		return sb.toString();
	}

	// ------------------------------------------------------------------ null / empty

	@Test
	public void allMethods_returnNullForNull() {
		assertNull(EscapeUtils.escapeHtml(null));
		assertNull(EscapeUtils.unescapeHtml(null));
		assertNull(EscapeUtils.escapeXml(null));
		assertNull(EscapeUtils.unescapeXml(null));
		assertNull(EscapeUtils.escapeJson(null));
		assertNull(EscapeUtils.unescapeJson(null));
		assertNull(EscapeUtils.escapeCsv(null));
		assertNull(EscapeUtils.unescapeCsv(null));
		assertNull(EscapeUtils.escapeJava(null));
		assertNull(EscapeUtils.escapeJava(null, true));
		assertNull(EscapeUtils.unescapeJava(null));
	}

	@Test
	public void allMethods_returnEmptyForEmpty() {
		assertEquals("", EscapeUtils.escapeHtml(""));
		assertEquals("", EscapeUtils.unescapeHtml(""));
		assertEquals("", EscapeUtils.escapeXml(""));
		assertEquals("", EscapeUtils.unescapeXml(""));
		assertEquals("", EscapeUtils.escapeJson(""));
		assertEquals("", EscapeUtils.unescapeJson(""));
		assertEquals("", EscapeUtils.escapeCsv(""));
		assertEquals("", EscapeUtils.unescapeCsv(""));
		assertEquals("", EscapeUtils.escapeJava(""));
		assertEquals("", EscapeUtils.unescapeJava(""));
	}

	// ------------------------------------------------------------------ HTML

	@Test
	public void escapeHtml_escapesFiveSpecialChars() {
		assertEquals("&lt;a href=&quot;x&quot;&gt;Tom &amp; Jerry&#39;s&lt;/a&gt;",
				EscapeUtils.escapeHtml("<a href=\"x\">Tom & Jerry's</a>"));
		assertEquals("中文" + EMOJI, EscapeUtils.escapeHtml("中文" + EMOJI));
	}

	@Test
	public void escapeHtml_returnsSameInstanceWhenNothingToEscape() {
		String s = "plain text 中";
		assertSame(s, EscapeUtils.escapeHtml(s));
	}

	@Test
	public void unescapeHtml_namedEntities() {
		assertEquals("<b>", EscapeUtils.unescapeHtml("&lt;b&gt;"));
		assertEquals("&\"'", EscapeUtils.unescapeHtml("&amp;&quot;&apos;"));
		assertEquals("© 2026 Fred", EscapeUtils.unescapeHtml("&copy; 2026&nbsp;Fred"));
		// WHATWG HTML named character references
		assertEquals("®™…€—–«»×÷",
				EscapeUtils.unescapeHtml("&reg;&trade;&hellip;&euro;&mdash;&ndash;&laquo;&raquo;&times;&divide;"));
		assertEquals("‘’“”•·°±",
				EscapeUtils.unescapeHtml("&lsquo;&rsquo;&ldquo;&rdquo;&bull;&middot;&deg;&plusmn;"));
		assertEquals("†‡", EscapeUtils.unescapeHtml("&dagger;&Dagger;"));
	}

	@Test
	public void unescapeHtml_numericEntities() {
		assertEquals("ABC", EscapeUtils.unescapeHtml("&#65;&#x42;&#X43;"));
		assertEquals(EMOJI, EscapeUtils.unescapeHtml("&#x1F600;"));
		assertEquals(EMOJI, EscapeUtils.unescapeHtml("&#128512;"));
		assertEquals("中文", EscapeUtils.unescapeHtml("&#x4e2d;&#25991;"));
		assertEquals("\u0000", EscapeUtils.unescapeHtml("&#0;"));
		assertEquals(new String(Character.toChars(0x10FFFF)), EscapeUtils.unescapeHtml("&#x10FFFF;"));
	}

	@Test
	public void unescapeHtml_keepsUnknownOrMalformedEntities() {
		assertEquals("&foo; & &#xZZ;", EscapeUtils.unescapeHtml("&foo; & &#xZZ;"));
		assertEquals("&;", EscapeUtils.unescapeHtml("&;"));
		assertEquals("&#;", EscapeUtils.unescapeHtml("&#;"));
		assertEquals("&#x;", EscapeUtils.unescapeHtml("&#x;"));
		assertEquals("&#12a;", EscapeUtils.unescapeHtml("&#12a;"));
		assertEquals("&#x110000;", EscapeUtils.unescapeHtml("&#x110000;"));
		assertEquals("&#99999999;", EscapeUtils.unescapeHtml("&#99999999;"));
		assertEquals("&amp", EscapeUtils.unescapeHtml("&amp"));
		assertEquals("&AMP;", EscapeUtils.unescapeHtml("&AMP;"));
		// full-width digits are not ASCII digits
		assertEquals("&#６５;", EscapeUtils.unescapeHtml("&#６５;"));
		// name too long to be an entity, the later "&lt;" is still decoded
		assertEquals("&averyveryverylongname;<", EscapeUtils.unescapeHtml("&averyveryverylongname;&lt;"));
		assertEquals("&&<", EscapeUtils.unescapeHtml("&&&lt;"));
		assertEquals("&#/; &#x`; &#x@; &#xg;", EscapeUtils.unescapeHtml("&#/; &#x`; &#x@; &#xg;"));
	}

	@Test
	public void unescapeHtml_returnsSameInstanceWithoutAmpersand() {
		String s = "no entities here";
		assertSame(s, EscapeUtils.unescapeHtml(s));
	}

	@Test
	public void html_roundTrip() {
		Random random = new Random(20261011L);
		for (int i = 0; i < 2000; i++) {
			String s = random(random, POOL, 30);
			assertEquals(s, EscapeUtils.unescapeHtml(EscapeUtils.escapeHtml(s)), s);
		}
	}

	// ------------------------------------------------------------------- XML

	@Test
	public void escapeXml_predefinedEntities() {
		assertEquals("&lt;a b=&apos;1&apos; c=&quot;2&quot;&gt;&amp;&lt;/a&gt;",
				EscapeUtils.escapeXml("<a b='1' c=\"2\">&</a>"));
	}

	@Test
	public void escapeXml_removesInvalidXmlChars() {
		assertEquals("abc\td", EscapeUtils.escapeXml("a" + (char) 0 + "b" + (char) 7 + "c\td"));
		assertEquals("\t\n\r", EscapeUtils.escapeXml("\t\n\r\013\014\037"));
		assertEquals("ab", EscapeUtils.escapeXml("a￾￿b"));
		// unpaired surrogates are removed, valid pairs kept
		assertEquals("ab" + EMOJI, EscapeUtils.escapeXml("a\ud83db\ude00" + EMOJI));
		assertEquals("a", EscapeUtils.escapeXml("a\ud83d"));
		assertEquals("\u007f\u0080퟿�", EscapeUtils.escapeXml("\u007f\u0080퟿�"));
	}

	@Test
	public void unescapeXml_onlyPredefinedAndNumeric() {
		assertEquals("<a>&'\"", EscapeUtils.unescapeXml("&lt;a&gt;&amp;&apos;&quot;"));
		assertEquals("中文", EscapeUtils.unescapeXml("&#x4E2D;&#25991;"));
		assertEquals("&nbsp;&copy;", EscapeUtils.unescapeXml("&nbsp;&copy;"));
	}

	@Test
	public void xml_roundTrip() {
		Random random = new Random(42L);
		for (int i = 0; i < 2000; i++) {
			String s = random(random, XML_POOL, 30);
			assertEquals(s, EscapeUtils.unescapeXml(EscapeUtils.escapeXml(s)), s);
		}
	}

	// ------------------------------------------------------------------ JSON

	@Test
	public void escapeJson_fixedSamples() {
		assertEquals("He said \\\"hi\\\"\\n", EscapeUtils.escapeJson("He said \"hi\"\n"));
		assertEquals("a/b\\\\c", EscapeUtils.escapeJson("a/b\\c"));
		assertEquals("\\u0001\\u001f", EscapeUtils.escapeJson((char) 1 + "" + (char) 31));
		assertEquals("\\b\\f\\n\\r\\t\\u0000", EscapeUtils.escapeJson("\b\f\n\r\t\u0000"));
		assertEquals("中文" + EMOJI + "\u007f", EscapeUtils.escapeJson("中文" + EMOJI + "\u007f"));
	}

	@Test
	public void unescapeJson_fixedSamples() {
		assertEquals("He said \"hi\"\n", EscapeUtils.unescapeJson("He said \\\"hi\\\"\\n"));
		assertEquals("/中" + EMOJI, EscapeUtils.unescapeJson("\\/\\u4e2D\\ud83d\\ude00"));
		assertEquals("\b\f\n\r\t\\\"", EscapeUtils.unescapeJson("\\b\\f\\n\\r\\t\\\\\\\""));
		// RFC 8259 section 7: G clef U+1D11E as a surrogate pair
		assertEquals(new String(Character.toChars(0x1D11E)), EscapeUtils.unescapeJson("\\uD834\\uDD1E"));
		// RFC 8259 section 7: reverse solidus
		assertEquals("\\", EscapeUtils.unescapeJson("\\u005C"));
		String s = "no escapes";
		assertSame(s, EscapeUtils.unescapeJson(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "\\x", "\\", "abc\\", "\\u12", "\\u12G4", "\\'", "\\a", "\\u" })
	public void unescapeJson_rejectsIllegalEscapes(String input) {
		assertThrows(IllegalArgumentException.class, () -> EscapeUtils.unescapeJson(input));
	}

	@Test
	public void unescapeJson_errorMessageMentionsSequence() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> EscapeUtils.unescapeJson("ab\\x"));
		assertEquals("Illegal escape sequence '\\x' at index 2", e.getMessage());
		e = assertThrows(IllegalArgumentException.class, () -> EscapeUtils.unescapeJson("\\uZZZZ"));
		assertTrue(e.getMessage().startsWith("Illegal unicode escape 'ZZZZ'"));
	}

	@Test
	public void json_roundTrip() {
		Random random = new Random(7L);
		for (int i = 0; i < 2000; i++) {
			String s = random(random, POOL, 30);
			String escaped = EscapeUtils.escapeJson(s);
			for (int j = 0; j < escaped.length(); j++) {
				assertTrue(escaped.charAt(j) >= 0x20, "control char must be escaped");
			}
			assertEquals(s, EscapeUtils.unescapeJson(escaped), s);
		}
	}

	// ------------------------------------------------------------------- CSV

	@Test
	public void escapeCsv_rfc4180() {
		assertEquals("abc", EscapeUtils.escapeCsv("abc"));
		assertEquals("\"a,b\"", EscapeUtils.escapeCsv("a,b"));
		assertEquals("\"say \"\"hi\"\"\"", EscapeUtils.escapeCsv("say \"hi\""));
		assertEquals("\"line1\nline2\"", EscapeUtils.escapeCsv("line1\nline2"));
		assertEquals("\"a\rb\"", EscapeUtils.escapeCsv("a\rb"));
		assertEquals("\"\"\"\"", EscapeUtils.escapeCsv("\""));
		assertEquals(" a ; b ", EscapeUtils.escapeCsv(" a ; b "));
		String s = "中文";
		assertSame(s, EscapeUtils.escapeCsv(s));
	}

	@Test
	public void unescapeCsv_rfc4180() {
		assertEquals("abc", EscapeUtils.unescapeCsv("abc"));
		assertEquals("a,b", EscapeUtils.unescapeCsv("\"a,b\""));
		assertEquals("say \"hi\"", EscapeUtils.unescapeCsv("\"say \"\"hi\"\"\""));
		assertEquals("\"", EscapeUtils.unescapeCsv("\""));
		assertEquals("", EscapeUtils.unescapeCsv("\"\""));
		assertEquals("\"abc", EscapeUtils.unescapeCsv("\"abc"));
		assertEquals("abc\"", EscapeUtils.unescapeCsv("abc\""));
	}

	@Test
	public void csv_roundTrip() {
		Random random = new Random(99L);
		for (int i = 0; i < 2000; i++) {
			String s = random(random, POOL, 30);
			assertEquals(s, EscapeUtils.unescapeCsv(EscapeUtils.escapeCsv(s)), s);
		}
	}

	// ------------------------------------------------------------------ Java

	@Test
	public void escapeJava_fixedSamples() {
		assertEquals("a\\tb\\n\\\"c\\\"\\\\", EscapeUtils.escapeJava("a\tb\n\"c\"\\"));
		assertEquals("\\r\\b\\f'/", EscapeUtils.escapeJava("\r\b\f'/"));
		assertEquals("中文", EscapeUtils.escapeJava("中文"));
		assertEquals("\\u0000\\u001f\\u007f", EscapeUtils.escapeJava("\u0000\u001f\u007f"));
		assertEquals("\\u0000", EscapeUtils.escapeJava(String.valueOf((char) 0), false));
	}

	@Test
	public void escapeJava_withUnicodeEscaping() {
		assertEquals("\\u4e2d\\u6587", EscapeUtils.escapeJava("中文", true));
		assertEquals("\\ud83d\\ude00", EscapeUtils.escapeJava(EMOJI, true));
		assertEquals("a\\u00e9\\n", EscapeUtils.escapeJava("aé\n", true));
		assertEquals("~", EscapeUtils.escapeJava("~", true));
		// same format as the existing StringUtils.unicodeString for non-ASCII text
		assertEquals(StringUtils.unicodeString("中文é"), EscapeUtils.escapeJava("中文é", true));
	}

	@Test
	public void unescapeJava_fixedSamples() {
		assertEquals("a\tb\n\"c\"\\", EscapeUtils.unescapeJava("a\\tb\\n\\\"c\\\"\\\\"));
		assertEquals("中文", EscapeUtils.unescapeJava("\\u4e2d\\u6587"));
		assertEquals("A" + (char) 0 + "'", EscapeUtils.unescapeJava("\\101\\0\\'"));
		assertEquals("\r\b\f ", EscapeUtils.unescapeJava("\\r\\b\\f\\s"));
		// JLS 3.3: several 'u' are allowed
		assertEquals("A", EscapeUtils.unescapeJava("\\uuu0041"));
		// JLS 3.10.7 octal escapes: \377 is the maximum, \400 is "\40" + "0"
		assertEquals("ÿ", EscapeUtils.unescapeJava("\\377"));
		assertEquals(" 0", EscapeUtils.unescapeJava("\\400"));
		assertEquals("\u00078", EscapeUtils.unescapeJava("\\78"));
		assertEquals("\u0001x", EscapeUtils.unescapeJava("\\1x"));
		assertEquals("\u0001", EscapeUtils.unescapeJava("\\1"));
		String s = "plain";
		assertSame(s, EscapeUtils.unescapeJava(s));
	}

	@ParameterizedTest
	@ValueSource(strings = { "\\q", "\\", "x\\", "\\u12", "\\uXYZW", "\\8", "\\/", "\\uu", "\\u00/0", "\\u00`0", "\\u00@0", "\\u00G0" })
	public void unescapeJava_rejectsIllegalEscapes(String input) {
		assertThrows(IllegalArgumentException.class, () -> EscapeUtils.unescapeJava(input));
	}

	@Test
	public void java_roundTrip() {
		Random random = new Random(123L);
		for (int i = 0; i < 2000; i++) {
			String s = random(random, POOL, 30);
			assertEquals(s, EscapeUtils.unescapeJava(EscapeUtils.escapeJava(s)), s);
			String ascii = EscapeUtils.escapeJava(s, true);
			for (int j = 0; j < ascii.length(); j++) {
				char c = ascii.charAt(j);
				assertTrue(c >= 0x20 && c < 0x7f, "output must be printable ASCII");
			}
			assertEquals(s, EscapeUtils.unescapeJava(ascii), s);
		}
	}

}
