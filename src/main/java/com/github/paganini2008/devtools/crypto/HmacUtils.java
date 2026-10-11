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
package com.github.paganini2008.devtools.crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import com.github.paganini2008.devtools.codec.HexUtils;

/**
 * Keyed-hash message authentication code (HMAC, RFC 2104) utilities for the MD5, SHA-1, SHA-256 and SHA-512 hash
 * functions. Thread-safe: a new {@link Mac} instance is created for every call.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class HmacUtils {

	public static final String HMAC_MD5 = "HmacMD5";

	public static final String HMAC_SHA1 = "HmacSHA1";

	public static final String HMAC_SHA256 = "HmacSHA256";

	public static final String HMAC_SHA512 = "HmacSHA512";

	/**
	 * Computes the MAC of the data with the named algorithm (e.g. "HmacSHA256"). An empty key is allowed, as
	 * permitted by RFC 2104.
	 *
	 * <pre>
	 * HmacUtils.hmac("HmacSHA256", new byte[0], new byte[0])
	 *     = b613679a0814d9ec772f95d778c35fc5ff1697c493715653c6c712144292c5ad (as bytes)
	 * HmacUtils.hmac("HmacSHA256", null, data)     = IllegalArgumentException
	 * HmacUtils.hmac("HmacSHA256", key, null)      = null
	 * HmacUtils.hmac("HmacNOPE", key, data)        = IllegalArgumentException
	 * </pre>
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the algorithm or key is <code>null</code>, or the algorithm is not
	 *                                  available
	 */
	public static byte[] hmac(String algorithm, byte[] key, byte[] data) {
		Mac mac = getMac(algorithm, key);
		return data != null ? mac.doFinal(data) : null;
	}

	/**
	 * Computes the MAC of the data with the named algorithm as a lowercase hex string.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the algorithm or key is <code>null</code>, or the algorithm is not
	 *                                  available
	 */
	public static String hmacHex(String algorithm, byte[] key, byte[] data) {
		return HexUtils.encodeHex(hmac(algorithm, key, data));
	}

	/**
	 * Returns a new {@link Mac} for the algorithm, initialized with the key.
	 * 
	 * @throws IllegalArgumentException if the algorithm or key is <code>null</code>, or the algorithm is not
	 *                                  available
	 */
	public static Mac getMac(String algorithm, byte[] key) {
		if (algorithm == null) {
			throw new IllegalArgumentException("Mac algorithm must not be null");
		}
		if (key == null) {
			throw new IllegalArgumentException("Mac key must not be null");
		}
		try {
			Mac mac = Mac.getInstance(algorithm);
			mac.init(new RawSecretKey(algorithm, key));
			return mac;
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Unsupported mac algorithm: " + algorithm, e);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException("Invalid mac key: " + e.getMessage(), e);
		}
	}

	/**
	 * Computes the HmacMD5 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacMd5(byte[] key, byte[] data) {
		return hmac(HMAC_MD5, key, data);
	}

	/**
	 * Computes the HmacMD5 of the data with the key; strings are encoded as UTF-8.
	 *
	 * <pre>
	 * HexUtils.encodeHex(HmacUtils.hmacMd5("key", "The quick brown fox jumps over the lazy dog"))
	 *     = "80070713463e7749b90c2dc24911e275"
	 * </pre>
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacMd5(String key, String data) {
		return hmac(HMAC_MD5, utf8(key), utf8(data));
	}

	/**
	 * Computes the HmacMD5 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacMd5(byte[] key, String data) {
		return hmac(HMAC_MD5, key, utf8(data));
	}

	/**
	 * Computes the HmacMD5 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacMd5(String key, byte[] data) {
		return hmac(HMAC_MD5, utf8(key), data);
	}

	/**
	 * Computes the HmacMD5 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacMd5Hex(byte[] key, byte[] data) {
		return HexUtils.encodeHex(hmacMd5(key, data));
	}

	/**
	 * Computes the HmacMD5 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 *
	 * <pre>
	 * HmacUtils.hmacMd5Hex("key", "The quick brown fox jumps over the lazy dog")
	 *     = "80070713463e7749b90c2dc24911e275"
	 * </pre>
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacMd5Hex(String key, String data) {
		return HexUtils.encodeHex(hmacMd5(key, data));
	}

	/**
	 * Computes the HmacMD5 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacMd5Hex(byte[] key, String data) {
		return HexUtils.encodeHex(hmacMd5(key, data));
	}

	/**
	 * Computes the HmacMD5 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacMd5Hex(String key, byte[] data) {
		return HexUtils.encodeHex(hmacMd5(key, data));
	}

	/**
	 * Computes the HmacSHA1 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha1(byte[] key, byte[] data) {
		return hmac(HMAC_SHA1, key, data);
	}

	/**
	 * Computes the HmacSHA1 of the data with the key; strings are encoded as UTF-8.
	 *
	 * <pre>
	 * HexUtils.encodeHex(HmacUtils.hmacSha1("key", "The quick brown fox jumps over the lazy dog"))
	 *     = "de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9"
	 * </pre>
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha1(String key, String data) {
		return hmac(HMAC_SHA1, utf8(key), utf8(data));
	}

	/**
	 * Computes the HmacSHA1 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha1(byte[] key, String data) {
		return hmac(HMAC_SHA1, key, utf8(data));
	}

	/**
	 * Computes the HmacSHA1 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha1(String key, byte[] data) {
		return hmac(HMAC_SHA1, utf8(key), data);
	}

	/**
	 * Computes the HmacSHA1 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha1Hex(byte[] key, byte[] data) {
		return HexUtils.encodeHex(hmacSha1(key, data));
	}

	/**
	 * Computes the HmacSHA1 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 *
	 * <pre>
	 * HmacUtils.hmacSha1Hex("key", "The quick brown fox jumps over the lazy dog")
	 *     = "de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9"
	 * </pre>
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha1Hex(String key, String data) {
		return HexUtils.encodeHex(hmacSha1(key, data));
	}

	/**
	 * Computes the HmacSHA1 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha1Hex(byte[] key, String data) {
		return HexUtils.encodeHex(hmacSha1(key, data));
	}

	/**
	 * Computes the HmacSHA1 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha1Hex(String key, byte[] data) {
		return HexUtils.encodeHex(hmacSha1(key, data));
	}

	/**
	 * Computes the HmacSHA256 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha256(byte[] key, byte[] data) {
		return hmac(HMAC_SHA256, key, data);
	}

	/**
	 * Computes the HmacSHA256 of the data with the key; strings are encoded as UTF-8.
	 *
	 * <pre>
	 * HexUtils.encodeHex(HmacUtils.hmacSha256("key", "The quick brown fox jumps over the lazy dog"))
	 *     = "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
	 * </pre>
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha256(String key, String data) {
		return hmac(HMAC_SHA256, utf8(key), utf8(data));
	}

	/**
	 * Computes the HmacSHA256 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha256(byte[] key, String data) {
		return hmac(HMAC_SHA256, key, utf8(data));
	}

	/**
	 * Computes the HmacSHA256 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha256(String key, byte[] data) {
		return hmac(HMAC_SHA256, utf8(key), data);
	}

	/**
	 * Computes the HmacSHA256 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha256Hex(byte[] key, byte[] data) {
		return HexUtils.encodeHex(hmacSha256(key, data));
	}

	/**
	 * Computes the HmacSHA256 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 *
	 * <pre>
	 * HmacUtils.hmacSha256Hex("key", "The quick brown fox jumps over the lazy dog")
	 *     = "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
	 * </pre>
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha256Hex(String key, String data) {
		return HexUtils.encodeHex(hmacSha256(key, data));
	}

	/**
	 * Computes the HmacSHA256 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha256Hex(byte[] key, String data) {
		return HexUtils.encodeHex(hmacSha256(key, data));
	}

	/**
	 * Computes the HmacSHA256 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha256Hex(String key, byte[] data) {
		return HexUtils.encodeHex(hmacSha256(key, data));
	}

	/**
	 * Computes the HmacSHA512 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha512(byte[] key, byte[] data) {
		return hmac(HMAC_SHA512, key, data);
	}

	/**
	 * Computes the HmacSHA512 of the data with the key; strings are encoded as UTF-8.
	 *
	 * <pre>
	 * HexUtils.encodeHex(HmacUtils.hmacSha512("key", "The quick brown fox jumps over the lazy dog"))
	 *     = "b42af09057bac1e2d41708e48a902e09b5ff7f12ab428a4fe86653c73dd248fb82f948a549f7b791a5b41915ee4d1ec3935357e4e2317250d0372afa2ebeeb3a"
	 * </pre>
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha512(String key, String data) {
		return hmac(HMAC_SHA512, utf8(key), utf8(data));
	}

	/**
	 * Computes the HmacSHA512 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha512(byte[] key, String data) {
		return hmac(HMAC_SHA512, key, utf8(data));
	}

	/**
	 * Computes the HmacSHA512 of the data with the key; strings are encoded as UTF-8.
	 * 
	 * @return the MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static byte[] hmacSha512(String key, byte[] data) {
		return hmac(HMAC_SHA512, utf8(key), data);
	}

	/**
	 * Computes the HmacSHA512 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha512Hex(byte[] key, byte[] data) {
		return HexUtils.encodeHex(hmacSha512(key, data));
	}

	/**
	 * Computes the HmacSHA512 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 *
	 * <pre>
	 * HmacUtils.hmacSha512Hex("key", "The quick brown fox jumps over the lazy dog")
	 *     = "b42af09057bac1e2d41708e48a902e09b5ff7f12ab428a4fe86653c73dd248fb82f948a549f7b791a5b41915ee4d1ec3935357e4e2317250d0372afa2ebeeb3a"
	 * </pre>
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha512Hex(String key, String data) {
		return HexUtils.encodeHex(hmacSha512(key, data));
	}

	/**
	 * Computes the HmacSHA512 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha512Hex(byte[] key, String data) {
		return HexUtils.encodeHex(hmacSha512(key, data));
	}

	/**
	 * Computes the HmacSHA512 of the data with the key as a lowercase hex string; strings are encoded as UTF-8.
	 * 
	 * @return the hex MAC, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	public static String hmacSha512Hex(String key, byte[] data) {
		return HexUtils.encodeHex(hmacSha512(key, data));
	}

	private static byte[] utf8(String str) {
		return str != null ? str.getBytes(StandardCharsets.UTF_8) : null;
	}

	/**
	 * Raw key material. Unlike {@link javax.crypto.spec.SecretKeySpec} it accepts an empty key, which HMAC allows.
	 */
	private static final class RawSecretKey implements SecretKey {

		private static final long serialVersionUID = 1L;

		private final String algorithm;
		private final byte[] key;

		RawSecretKey(String algorithm, byte[] key) {
			this.algorithm = algorithm;
			this.key = key.clone();
		}

		@Override
		public String getAlgorithm() {
			return algorithm;
		}

		@Override
		public String getFormat() {
			return "RAW";
		}

		@Override
		public byte[] getEncoded() {
			return key.clone();
		}

	}

}
