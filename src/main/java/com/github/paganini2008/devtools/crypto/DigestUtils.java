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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

import com.github.paganini2008.devtools.codec.HexUtils;

/**
 * Null-safe message digest (MD5, SHA-1, SHA-2) and CRC32 checksum utilities for byte arrays, strings, streams and
 * files. Thread-safe: a new {@link MessageDigest} instance is created for every call.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class DigestUtils {

	public static final String MD5 = "MD5";

	public static final String SHA1 = "SHA-1";

	public static final String SHA256 = "SHA-256";

	public static final String SHA384 = "SHA-384";

	public static final String SHA512 = "SHA-512";

	private static final int BUFFER_SIZE = 8192;

	/**
	 * Returns a new {@link MessageDigest} for the algorithm.
	 * 
	 * @throws IllegalArgumentException if the algorithm is <code>null</code> or not available (wrapping the
	 *                                  {@link NoSuchAlgorithmException})
	 */
	public static MessageDigest getDigest(String algorithm) {
		if (algorithm == null) {
			throw new IllegalArgumentException("Digest algorithm must not be null");
		}
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Unsupported digest algorithm: " + algorithm, e);
		}
	}

	/**
	 * Computes the digest of the bytes with the named algorithm (e.g. "SHA-256").
	 *
	 * <pre>
	 * DigestUtils.digest("MD5", null)          = null
	 * DigestUtils.digest("MD5", new byte[0])   = d41d8cd98f00b204e9800998ecf8427e (as bytes)
	 * DigestUtils.digest("NOPE", new byte[0])  = IllegalArgumentException
	 * </pre>
	 * 
	 * @return the digest, or <code>null</code> if the data is <code>null</code>
	 * @throws IllegalArgumentException if the algorithm is <code>null</code> or not available
	 */
	public static byte[] digest(String algorithm, byte[] data) {
		MessageDigest md = getDigest(algorithm);
		return data != null ? md.digest(data) : null;
	}

	/**
	 * Computes the digest of the remaining content of the stream with the named algorithm. The stream is read to the
	 * end but not closed.
	 * 
	 * @return the digest, or <code>null</code> if the stream is <code>null</code>
	 * @throws IllegalArgumentException if the algorithm is <code>null</code> or not available
	 * @throws IOException              if reading fails
	 */
	public static byte[] digest(String algorithm, InputStream in) throws IOException {
		MessageDigest md = getDigest(algorithm);
		if (in == null) {
			return null;
		}
		byte[] buffer = new byte[BUFFER_SIZE];
		int n;
		while ((n = in.read(buffer)) != -1) {
			md.update(buffer, 0, n);
		}
		return md.digest();
	}

	/**
	 * Computes the digest of the file content with the named algorithm. The file is opened and closed by this method.
	 * 
	 * @return the digest, or <code>null</code> if the file is <code>null</code>
	 * @throws IllegalArgumentException if the algorithm is <code>null</code> or not available
	 * @throws IOException              if the file does not exist or cannot be read
	 */
	public static byte[] digest(String algorithm, File file) throws IOException {
		getDigest(algorithm);
		if (file == null) {
			return null;
		}
		try (InputStream in = new FileInputStream(file)) {
			return digest(algorithm, in);
		}
	}

	/**
	 * Computes the digest of the bytes with the named algorithm as a lowercase hex string, <code>null</code> for
	 * <code>null</code>.
	 * 
	 * @throws IllegalArgumentException if the algorithm is <code>null</code> or not available
	 */
	public static String digestHex(String algorithm, byte[] data) {
		return HexUtils.encodeHex(digest(algorithm, data));
	}

	/**
	 * Computes the MD5 digest of the bytes.
	 * 
	 * @return the digest, or <code>null</code> if the input is <code>null</code>
	 */
	public static byte[] md5(byte[] data) {
		return digest(MD5, data);
	}

	/**
	 * Computes the MD5 digest of the UTF-8 bytes of the string, <code>null</code> for <code>null</code>.
	 */
	public static byte[] md5(String data) {
		return digest(MD5, utf8(data));
	}

	/**
	 * Computes the MD5 digest of the remaining content of the stream, which is read to the end but not closed.
	 * 
	 * @return the digest, or <code>null</code> if the stream is <code>null</code>
	 * @throws IOException if reading fails
	 */
	public static byte[] md5(InputStream in) throws IOException {
		return digest(MD5, in);
	}

	/**
	 * Computes the MD5 digest of the file content. The file is opened and closed by this method.
	 * 
	 * @return the digest, or <code>null</code> if the file is <code>null</code>
	 * @throws IOException if the file does not exist or cannot be read
	 */
	public static byte[] md5(File file) throws IOException {
		return digest(MD5, file);
	}

	/**
	 * Computes the MD5 digest of the bytes as a lowercase hex string, <code>null</code> for <code>null</code>.
	 */
	public static String md5Hex(byte[] data) {
		return HexUtils.encodeHex(md5(data));
	}

	/**
	 * Computes the MD5 digest of the UTF-8 bytes of the string as a lowercase hex string, <code>null</code> for
	 * <code>null</code>.
	 *
	 * <pre>
	 * DigestUtils.md5Hex("abc") = "900150983cd24fb0d6963f7d28e17f72"
	 * </pre>
	 */
	public static String md5Hex(String data) {
		return HexUtils.encodeHex(md5(data));
	}

	/**
	 * Computes the MD5 digest of the stream content as a lowercase hex string. The stream is not closed.
	 * 
	 * @throws IOException if reading fails
	 */
	public static String md5Hex(InputStream in) throws IOException {
		return HexUtils.encodeHex(md5(in));
	}

	/**
	 * Computes the MD5 digest of the file content as a lowercase hex string.
	 * 
	 * @throws IOException if the file does not exist or cannot be read
	 */
	public static String md5Hex(File file) throws IOException {
		return HexUtils.encodeHex(md5(file));
	}

	/**
	 * Computes the SHA-1 digest of the bytes.
	 * 
	 * @return the digest, or <code>null</code> if the input is <code>null</code>
	 */
	public static byte[] sha1(byte[] data) {
		return digest(SHA1, data);
	}

	/**
	 * Computes the SHA-1 digest of the UTF-8 bytes of the string, <code>null</code> for <code>null</code>.
	 */
	public static byte[] sha1(String data) {
		return digest(SHA1, utf8(data));
	}

	/**
	 * Computes the SHA-1 digest of the remaining content of the stream, which is read to the end but not closed.
	 * 
	 * @return the digest, or <code>null</code> if the stream is <code>null</code>
	 * @throws IOException if reading fails
	 */
	public static byte[] sha1(InputStream in) throws IOException {
		return digest(SHA1, in);
	}

	/**
	 * Computes the SHA-1 digest of the file content. The file is opened and closed by this method.
	 * 
	 * @return the digest, or <code>null</code> if the file is <code>null</code>
	 * @throws IOException if the file does not exist or cannot be read
	 */
	public static byte[] sha1(File file) throws IOException {
		return digest(SHA1, file);
	}

	/**
	 * Computes the SHA-1 digest of the bytes as a lowercase hex string, <code>null</code> for <code>null</code>.
	 */
	public static String sha1Hex(byte[] data) {
		return HexUtils.encodeHex(sha1(data));
	}

	/**
	 * Computes the SHA-1 digest of the UTF-8 bytes of the string as a lowercase hex string, <code>null</code> for
	 * <code>null</code>.
	 *
	 * <pre>
	 * DigestUtils.sha1Hex("abc") = "a9993e364706816aba3e25717850c26c9cd0d89d"
	 * </pre>
	 */
	public static String sha1Hex(String data) {
		return HexUtils.encodeHex(sha1(data));
	}

	/**
	 * Computes the SHA-1 digest of the stream content as a lowercase hex string. The stream is not closed.
	 * 
	 * @throws IOException if reading fails
	 */
	public static String sha1Hex(InputStream in) throws IOException {
		return HexUtils.encodeHex(sha1(in));
	}

	/**
	 * Computes the SHA-1 digest of the file content as a lowercase hex string.
	 * 
	 * @throws IOException if the file does not exist or cannot be read
	 */
	public static String sha1Hex(File file) throws IOException {
		return HexUtils.encodeHex(sha1(file));
	}

	/**
	 * Computes the SHA-256 digest of the bytes.
	 * 
	 * @return the digest, or <code>null</code> if the input is <code>null</code>
	 */
	public static byte[] sha256(byte[] data) {
		return digest(SHA256, data);
	}

	/**
	 * Computes the SHA-256 digest of the UTF-8 bytes of the string, <code>null</code> for <code>null</code>.
	 */
	public static byte[] sha256(String data) {
		return digest(SHA256, utf8(data));
	}

	/**
	 * Computes the SHA-256 digest of the remaining content of the stream, which is read to the end but not closed.
	 * 
	 * @return the digest, or <code>null</code> if the stream is <code>null</code>
	 * @throws IOException if reading fails
	 */
	public static byte[] sha256(InputStream in) throws IOException {
		return digest(SHA256, in);
	}

	/**
	 * Computes the SHA-256 digest of the file content. The file is opened and closed by this method.
	 * 
	 * @return the digest, or <code>null</code> if the file is <code>null</code>
	 * @throws IOException if the file does not exist or cannot be read
	 */
	public static byte[] sha256(File file) throws IOException {
		return digest(SHA256, file);
	}

	/**
	 * Computes the SHA-256 digest of the bytes as a lowercase hex string, <code>null</code> for <code>null</code>.
	 */
	public static String sha256Hex(byte[] data) {
		return HexUtils.encodeHex(sha256(data));
	}

	/**
	 * Computes the SHA-256 digest of the UTF-8 bytes of the string as a lowercase hex string, <code>null</code> for
	 * <code>null</code>.
	 *
	 * <pre>
	 * DigestUtils.sha256Hex("abc") = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
	 * </pre>
	 */
	public static String sha256Hex(String data) {
		return HexUtils.encodeHex(sha256(data));
	}

	/**
	 * Computes the SHA-256 digest of the stream content as a lowercase hex string. The stream is not closed.
	 * 
	 * @throws IOException if reading fails
	 */
	public static String sha256Hex(InputStream in) throws IOException {
		return HexUtils.encodeHex(sha256(in));
	}

	/**
	 * Computes the SHA-256 digest of the file content as a lowercase hex string.
	 * 
	 * @throws IOException if the file does not exist or cannot be read
	 */
	public static String sha256Hex(File file) throws IOException {
		return HexUtils.encodeHex(sha256(file));
	}

	/**
	 * Computes the SHA-384 digest of the bytes.
	 * 
	 * @return the digest, or <code>null</code> if the input is <code>null</code>
	 */
	public static byte[] sha384(byte[] data) {
		return digest(SHA384, data);
	}

	/**
	 * Computes the SHA-384 digest of the UTF-8 bytes of the string, <code>null</code> for <code>null</code>.
	 */
	public static byte[] sha384(String data) {
		return digest(SHA384, utf8(data));
	}

	/**
	 * Computes the SHA-384 digest of the remaining content of the stream, which is read to the end but not closed.
	 * 
	 * @return the digest, or <code>null</code> if the stream is <code>null</code>
	 * @throws IOException if reading fails
	 */
	public static byte[] sha384(InputStream in) throws IOException {
		return digest(SHA384, in);
	}

	/**
	 * Computes the SHA-384 digest of the file content. The file is opened and closed by this method.
	 * 
	 * @return the digest, or <code>null</code> if the file is <code>null</code>
	 * @throws IOException if the file does not exist or cannot be read
	 */
	public static byte[] sha384(File file) throws IOException {
		return digest(SHA384, file);
	}

	/**
	 * Computes the SHA-384 digest of the bytes as a lowercase hex string, <code>null</code> for <code>null</code>.
	 */
	public static String sha384Hex(byte[] data) {
		return HexUtils.encodeHex(sha384(data));
	}

	/**
	 * Computes the SHA-384 digest of the UTF-8 bytes of the string as a lowercase hex string, <code>null</code> for
	 * <code>null</code>.
	 *
	 * <pre>
	 * DigestUtils.sha384Hex("abc") = "cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7"
	 * </pre>
	 */
	public static String sha384Hex(String data) {
		return HexUtils.encodeHex(sha384(data));
	}

	/**
	 * Computes the SHA-384 digest of the stream content as a lowercase hex string. The stream is not closed.
	 * 
	 * @throws IOException if reading fails
	 */
	public static String sha384Hex(InputStream in) throws IOException {
		return HexUtils.encodeHex(sha384(in));
	}

	/**
	 * Computes the SHA-384 digest of the file content as a lowercase hex string.
	 * 
	 * @throws IOException if the file does not exist or cannot be read
	 */
	public static String sha384Hex(File file) throws IOException {
		return HexUtils.encodeHex(sha384(file));
	}

	/**
	 * Computes the SHA-512 digest of the bytes.
	 * 
	 * @return the digest, or <code>null</code> if the input is <code>null</code>
	 */
	public static byte[] sha512(byte[] data) {
		return digest(SHA512, data);
	}

	/**
	 * Computes the SHA-512 digest of the UTF-8 bytes of the string, <code>null</code> for <code>null</code>.
	 */
	public static byte[] sha512(String data) {
		return digest(SHA512, utf8(data));
	}

	/**
	 * Computes the SHA-512 digest of the remaining content of the stream, which is read to the end but not closed.
	 * 
	 * @return the digest, or <code>null</code> if the stream is <code>null</code>
	 * @throws IOException if reading fails
	 */
	public static byte[] sha512(InputStream in) throws IOException {
		return digest(SHA512, in);
	}

	/**
	 * Computes the SHA-512 digest of the file content. The file is opened and closed by this method.
	 * 
	 * @return the digest, or <code>null</code> if the file is <code>null</code>
	 * @throws IOException if the file does not exist or cannot be read
	 */
	public static byte[] sha512(File file) throws IOException {
		return digest(SHA512, file);
	}

	/**
	 * Computes the SHA-512 digest of the bytes as a lowercase hex string, <code>null</code> for <code>null</code>.
	 */
	public static String sha512Hex(byte[] data) {
		return HexUtils.encodeHex(sha512(data));
	}

	/**
	 * Computes the SHA-512 digest of the UTF-8 bytes of the string as a lowercase hex string, <code>null</code> for
	 * <code>null</code>.
	 *
	 * <pre>
	 * DigestUtils.sha512Hex("abc") = "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f"
	 * </pre>
	 */
	public static String sha512Hex(String data) {
		return HexUtils.encodeHex(sha512(data));
	}

	/**
	 * Computes the SHA-512 digest of the stream content as a lowercase hex string. The stream is not closed.
	 * 
	 * @throws IOException if reading fails
	 */
	public static String sha512Hex(InputStream in) throws IOException {
		return HexUtils.encodeHex(sha512(in));
	}

	/**
	 * Computes the SHA-512 digest of the file content as a lowercase hex string.
	 * 
	 * @throws IOException if the file does not exist or cannot be read
	 */
	public static String sha512Hex(File file) throws IOException {
		return HexUtils.encodeHex(sha512(file));
	}

	/**
	 * Computes the CRC32 checksum of the bytes.
	 *
	 * <pre>
	 * DigestUtils.crc32((byte[]) null)          = 0
	 * DigestUtils.crc32(new byte[0])            = 0
	 * DigestUtils.crc32("123456789".getBytes()) = 0xCBF43926L
	 * </pre>
	 * 
	 * @return the checksum as an unsigned 32-bit value, 0 if the input is <code>null</code> or empty
	 */
	public static long crc32(byte[] data) {
		if (data == null) {
			return 0L;
		}
		CRC32 crc = new CRC32();
		crc.update(data, 0, data.length);
		return crc.getValue();
	}

	/**
	 * Computes the CRC32 checksum of the UTF-8 bytes of the string, 0 for <code>null</code>.
	 *
	 * <pre>
	 * DigestUtils.crc32("123456789") = 3421780262L (0xCBF43926)
	 * </pre>
	 */
	public static long crc32(String data) {
		return crc32(utf8(data));
	}

	/**
	 * Computes the CRC32 checksum of the remaining content of the stream, which is read to the end but not closed.
	 * 
	 * @return the checksum, 0 if the stream is <code>null</code> or empty
	 * @throws IOException if reading fails
	 */
	public static long crc32(InputStream in) throws IOException {
		CRC32 crc = new CRC32();
		if (in == null) {
			return 0L;
		}
		byte[] buffer = new byte[BUFFER_SIZE];
		int n;
		while ((n = in.read(buffer)) != -1) {
			crc.update(buffer, 0, n);
		}
		return crc.getValue();
	}

	private static byte[] utf8(String str) {
		return str != null ? str.getBytes(StandardCharsets.UTF_8) : null;
	}

}
