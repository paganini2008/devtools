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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Null-safe hexadecimal (base 16) encoding and decoding of byte arrays and strings, as described in RFC 4648 section
 * 8.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class HexUtils {

	private static final char[] DIGITS_LOWER = "0123456789abcdef".toCharArray();

	private static final char[] DIGITS_UPPER = "0123456789ABCDEF".toCharArray();

	/**
	 * Encodes the bytes as a lowercase hex string.
	 *
	 * <pre>
	 * HexUtils.encodeHex(null)                       = null
	 * HexUtils.encodeHex(new byte[0])                = ""
	 * HexUtils.encodeHex("foobar".getBytes())        = "666f6f626172"
	 * HexUtils.encodeHex(new byte[] { 0, 15, -1 })   = "000fff"
	 * </pre>
	 */
	public static String encodeHex(byte[] data) {
		return encodeHex(data, false);
	}

	/**
	 * Encodes the bytes as a hex string, using uppercase or lowercase digits.
	 *
	 * <pre>
	 * HexUtils.encodeHex(new byte[] { 0, 15, -1 }, true)  = "000FFF"
	 * HexUtils.encodeHex(new byte[] { 0, 15, -1 }, false) = "000fff"
	 * </pre>
	 *
	 * @return the hex string, or <code>null</code> if the input is <code>null</code>
	 */
	public static String encodeHex(byte[] data, boolean upperCase) {
		if (data == null) {
			return null;
		}
		final char[] digits = upperCase ? DIGITS_UPPER : DIGITS_LOWER;
		char[] out = new char[data.length << 1];
		for (int i = 0, j = 0; i < data.length; i++) {
			out[j++] = digits[(data[i] >>> 4) & 0x0F];
			out[j++] = digits[data[i] & 0x0F];
		}
		return new String(out);
	}

	/**
	 * Encodes the UTF-8 bytes of the string as a lowercase hex string.
	 *
	 * <pre>
	 * HexUtils.encodeHex((String) null) = null
	 * HexUtils.encodeHex("foo")         = "666f6f"
	 * HexUtils.encodeHex("中文")         = "e4b8ade69687"
	 * </pre>
	 */
	public static String encodeHex(String str) {
		return encodeHex(str, StandardCharsets.UTF_8);
	}

	/**
	 * Encodes the bytes of the string in the given charset (UTF-8 if <code>null</code>) as a lowercase hex string.
	 *
	 * @return the hex string, or <code>null</code> if the input is <code>null</code>
	 */
	public static String encodeHex(String str, Charset charset) {
		if (str == null) {
			return null;
		}
		return encodeHex(str.getBytes(charset != null ? charset : StandardCharsets.UTF_8));
	}

	/**
	 * Decodes a hex string into bytes. Both uppercase and lowercase digits are accepted; whitespace and prefixes such
	 * as "0x" are not.
	 *
	 * <pre>
	 * HexUtils.decodeHex(null)           = null
	 * HexUtils.decodeHex("")             = []
	 * HexUtils.decodeHex("000fff")       = [0, 15, -1]
	 * HexUtils.decodeHex("000FFF")       = [0, 15, -1]
	 * HexUtils.decodeHex("abc")          = IllegalArgumentException (odd length)
	 * HexUtils.decodeHex("zz")           = IllegalArgumentException (illegal character)
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the string has an odd length or contains a non-hex character
	 */
	public static byte[] decodeHex(String hex) {
		if (hex == null) {
			return null;
		}
		final int len = hex.length();
		if ((len & 0x01) != 0) {
			throw new IllegalArgumentException("Odd number of characters in hex string: " + len);
		}
		byte[] out = new byte[len >> 1];
		for (int i = 0, j = 0; j < len; i++) {
			int high = toDigit(hex.charAt(j), j);
			j++;
			int low = toDigit(hex.charAt(j), j);
			j++;
			out[i] = (byte) ((high << 4) | low);
		}
		return out;
	}

	/**
	 * Decodes a hex string and converts the bytes to a string using UTF-8.
	 *
	 * <pre>
	 * HexUtils.decodeHexString(null)           = null
	 * HexUtils.decodeHexString("666f6f")       = "foo"
	 * HexUtils.decodeHexString("e4b8ade69687") = "中文"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the string is not valid hex
	 */
	public static String decodeHexString(String hex) {
		return decodeHexString(hex, StandardCharsets.UTF_8);
	}

	/**
	 * Decodes a hex string and converts the bytes to a string using the given charset (UTF-8 if <code>null</code>).
	 *
	 * @throws IllegalArgumentException if the string is not valid hex
	 */
	public static String decodeHexString(String hex, Charset charset) {
		byte[] bytes = decodeHex(hex);
		return bytes != null ? new String(bytes, charset != null ? charset : StandardCharsets.UTF_8) : null;
	}

	private static int toDigit(char ch, int index) {
		if (ch >= '0' && ch <= '9') {
			return ch - '0';
		}
		if (ch >= 'a' && ch <= 'f') {
			return ch - 'a' + 10;
		}
		if (ch >= 'A' && ch <= 'F') {
			return ch - 'A' + 10;
		}
		throw new IllegalArgumentException("Illegal hex character '" + ch + "' at index " + index);
	}

}
