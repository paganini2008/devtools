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
import java.util.Base64;

/**
 * Null-safe Base64 encoding and decoding built on {@link java.util.Base64}, supporting the basic (RFC 4648 section 4),
 * URL-safe (RFC 4648 section 5) and MIME (RFC 2045) variants.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class Base64Utils {

	/**
	 * Encodes the bytes using the basic Base64 alphabet with padding, returning the encoded bytes (ISO-8859-1/ASCII).
	 *
	 * @return the encoded bytes, or <code>null</code> if the input is <code>null</code>
	 */
	public static byte[] encode(byte[] data) {
		return data != null ? Base64.getEncoder().encode(data) : null;
	}

	/**
	 * Decodes bytes encoded with the basic Base64 alphabet. Padding is optional.
	 *
	 * @return the decoded bytes, or <code>null</code> if the input is <code>null</code>
	 * @throws IllegalArgumentException if the input is not valid Base64
	 */
	public static byte[] decode(byte[] data) {
		return data != null ? Base64.getDecoder().decode(data) : null;
	}

	/**
	 * Encodes the bytes as a Base64 string using the basic alphabet with padding.
	 *
	 * <pre>
	 * Base64Utils.encodeToString(null)                 = null
	 * Base64Utils.encodeToString(new byte[0])          = ""
	 * Base64Utils.encodeToString("foobar".getBytes())  = "Zm9vYmFy"
	 * Base64Utils.encodeToString(new byte[] { -5, -1 }) = "+/8="
	 * </pre>
	 */
	public static String encodeToString(byte[] data) {
		return data != null ? Base64.getEncoder().encodeToString(data) : null;
	}

	/**
	 * Decodes a Base64 string encoded with the basic alphabet. Padding is optional.
	 *
	 * <pre>
	 * Base64Utils.decode((String) null) = null
	 * Base64Utils.decode("Zm9vYg==")    = "foob" bytes
	 * Base64Utils.decode("Zm9vYg")      = "foob" bytes
	 * Base64Utils.decode("Zm9v!")       = IllegalArgumentException
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the input is not valid Base64
	 */
	public static byte[] decode(String str) {
		return str != null ? Base64.getDecoder().decode(str) : null;
	}

	/**
	 * Encodes the UTF-8 bytes of the string as a Base64 string.
	 *
	 * <pre>
	 * Base64Utils.encode((String) null) = null
	 * Base64Utils.encode("")            = ""
	 * Base64Utils.encode("f")           = "Zg=="
	 * Base64Utils.encode("fo")          = "Zm8="
	 * Base64Utils.encode("foo")         = "Zm9v"
	 * Base64Utils.encode("中文")         = "5Lit5paH"
	 * </pre>
	 */
	public static String encode(String str) {
		return encode(str, StandardCharsets.UTF_8);
	}

	/**
	 * Encodes the bytes of the string in the given charset (UTF-8 if <code>null</code>) as a Base64 string.
	 *
	 * @return the encoded string, or <code>null</code> if the input is <code>null</code>
	 */
	public static String encode(String str, Charset charset) {
		return str != null ? encodeToString(str.getBytes(charset(charset))) : null;
	}

	/**
	 * Decodes a Base64 string and converts the bytes to a string using UTF-8.
	 *
	 * <pre>
	 * Base64Utils.decodeToString(null)       = null
	 * Base64Utils.decodeToString("Zm9vYmFy") = "foobar"
	 * Base64Utils.decodeToString("5Lit5paH") = "中文"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the input is not valid Base64
	 */
	public static String decodeToString(String str) {
		return decodeToString(str, StandardCharsets.UTF_8);
	}

	/**
	 * Decodes a Base64 string and converts the bytes to a string using the given charset (UTF-8 if <code>null</code>).
	 *
	 * @throws IllegalArgumentException if the input is not valid Base64
	 */
	public static String decodeToString(String str, Charset charset) {
		return str != null ? new String(decode(str), charset(charset)) : null;
	}

	/**
	 * Encodes the bytes using the URL and filename safe alphabet (RFC 4648 section 5, '-' and '_' instead of '+' and
	 * '/') with padding.
	 *
	 * <pre>
	 * Base64Utils.encodeUrlSafe(new byte[] { -5, -1 }) = "-_8="
	 * </pre>
	 */
	public static String encodeUrlSafe(byte[] data) {
		return encodeUrlSafe(data, true);
	}

	/**
	 * Encodes the bytes using the URL and filename safe alphabet, with or without trailing '=' padding.
	 *
	 * <pre>
	 * Base64Utils.encodeUrlSafe(new byte[] { -5, -1 }, true)  = "-_8="
	 * Base64Utils.encodeUrlSafe(new byte[] { -5, -1 }, false) = "-_8"
	 * </pre>
	 *
	 * @return the encoded string, or <code>null</code> if the input is <code>null</code>
	 */
	public static String encodeUrlSafe(byte[] data, boolean padding) {
		if (data == null) {
			return null;
		}
		Base64.Encoder encoder = Base64.getUrlEncoder();
		return (padding ? encoder : encoder.withoutPadding()).encodeToString(data);
	}

	/**
	 * Decodes a string encoded with the URL and filename safe alphabet. Padding is optional.
	 *
	 * <pre>
	 * Base64Utils.decodeUrlSafe(null)   = null
	 * Base64Utils.decodeUrlSafe("-_8=") = [-5, -1]
	 * Base64Utils.decodeUrlSafe("-_8")  = [-5, -1]
	 * Base64Utils.decodeUrlSafe("+/8=") = IllegalArgumentException
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the input is not valid URL-safe Base64
	 */
	public static byte[] decodeUrlSafe(String str) {
		return str != null ? Base64.getUrlDecoder().decode(str) : null;
	}

	/**
	 * Encodes the bytes using the MIME variant: lines of at most 76 characters separated by "\r\n", with no trailing
	 * line separator.
	 *
	 * @return the encoded string, or <code>null</code> if the input is <code>null</code>
	 */
	public static String encodeMime(byte[] data) {
		return data != null ? Base64.getMimeEncoder().encodeToString(data) : null;
	}

	/**
	 * Decodes a MIME Base64 string. Line separators and any other characters outside the Base64 alphabet are ignored.
	 *
	 * @return the decoded bytes, or <code>null</code> if the input is <code>null</code>
	 * @throws IllegalArgumentException if the padding is invalid
	 */
	public static byte[] decodeMime(String str) {
		return str != null ? Base64.getMimeDecoder().decode(str) : null;
	}

	/**
	 * Checks whether the string can be decoded with the basic Base64 alphabet (padding optional, no whitespace).
	 *
	 * <pre>
	 * Base64Utils.isBase64(null)       = false
	 * Base64Utils.isBase64("")         = true
	 * Base64Utils.isBase64("Zm9vYg==") = true
	 * Base64Utils.isBase64("Zm9vYg")   = true
	 * Base64Utils.isBase64("Zm9vY")    = false
	 * Base64Utils.isBase64("Zm9v YmFy") = false
	 * Base64Utils.isBase64("-_8=")     = false
	 * </pre>
	 */
	public static boolean isBase64(String str) {
		if (str == null) {
			return false;
		}
		try {
			Base64.getDecoder().decode(str);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private static Charset charset(Charset charset) {
		return charset != null ? charset : StandardCharsets.UTF_8;
	}

}
