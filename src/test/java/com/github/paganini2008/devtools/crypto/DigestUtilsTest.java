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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.github.paganini2008.devtools.codec.HexUtils;

/**
 * DigestUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class DigestUtilsTest {

	private static final String M448 = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";

	@TempDir
	File tempDir;

	/** RFC 1321 appendix A.5 test suite. */
	@ParameterizedTest
	@CsvSource(value = { "'',d41d8cd98f00b204e9800998ecf8427e", "a,0cc175b9c0f1b6a831c399e269772661",
			"abc,900150983cd24fb0d6963f7d28e17f72", "message digest,f96b697d7cb7938d525a2f31aaf161d0",
			"abcdefghijklmnopqrstuvwxyz,c3fcd3d76192e4007dfb496cca67e13b",
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,d174ab98d277d9f5a5611c2c9f419d9f",
			"12345678901234567890123456789012345678901234567890123456789012345678901234567890,57edf4a22be3c955ac49da2e2107b67a" })
	public void md5_rfc1321Vectors(String input, String expected) throws IOException {
		byte[] bytes = input.getBytes(StandardCharsets.US_ASCII);
		assertEquals(expected, DigestUtils.md5Hex(input));
		assertEquals(expected, DigestUtils.md5Hex(bytes));
		assertEquals(expected, DigestUtils.md5Hex(new ByteArrayInputStream(bytes)));
		assertEquals(expected, DigestUtils.md5Hex(writeFile(bytes)));
		assertArrayEquals(HexUtils.decodeHex(expected), DigestUtils.md5(input));
		assertArrayEquals(HexUtils.decodeHex(expected), DigestUtils.md5(bytes));
		assertArrayEquals(HexUtils.decodeHex(expected), DigestUtils.md5(new ByteArrayInputStream(bytes)));
		assertArrayEquals(HexUtils.decodeHex(expected), DigestUtils.md5(writeFile(bytes)));
		assertEquals(expected, DigestUtils.digestHex("MD5", bytes));
	}

	/** FIPS 180-2 / NIST CSRC examples: "abc", the empty string and the 448-bit message. */
	@Test
	public void sha_fips180Vectors() throws IOException {
		assertSha("SHA-1", "abc", "a9993e364706816aba3e25717850c26c9cd0d89d");
		assertSha("SHA-1", "", "da39a3ee5e6b4b0d3255bfef95601890afd80709");
		assertSha("SHA-1", M448, "84983e441c3bd26ebaae4aa1f95129e5e54670f1");
		assertSha("SHA-256", "abc", "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
		assertSha("SHA-256", "", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
		assertSha("SHA-256", M448, "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1");
		assertSha("SHA-384", "abc",
				"cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7");
		assertSha("SHA-384", "",
				"38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b");
		assertSha("SHA-384", M448,
				"3391fdddfc8dc7393707a65b1b4709397cf8b1d162af05abfe8f450de5f36bc6b0455a8520bc4e6f5fe95b1fe3c8452b");
		assertSha("SHA-512", "abc",
				"ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f");
		assertSha("SHA-512", "",
				"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
		assertSha("SHA-512", M448,
				"204a8fc6dda82f0a0ced7beb8e08a41657c16ef468b228a8279be331a703c33596fd15c13b1b07f9aa1d3bea57789ca031ad85c7a71dd70354ec631238ca3445");
	}

	private void assertSha(String algorithm, String input, String expected) throws IOException {
		byte[] bytes = input.getBytes(StandardCharsets.US_ASCII);
		byte[] raw = HexUtils.decodeHex(expected);
		File file = writeFile(bytes);
		assertEquals(expected, DigestUtils.digestHex(algorithm, bytes));
		if ("SHA-1".equals(algorithm)) {
			assertEquals(expected, DigestUtils.sha1Hex(input));
			assertEquals(expected, DigestUtils.sha1Hex(bytes));
			assertEquals(expected, DigestUtils.sha1Hex(new ByteArrayInputStream(bytes)));
			assertEquals(expected, DigestUtils.sha1Hex(file));
			assertArrayEquals(raw, DigestUtils.sha1(input));
			assertArrayEquals(raw, DigestUtils.sha1(bytes));
			assertArrayEquals(raw, DigestUtils.sha1(new ByteArrayInputStream(bytes)));
			assertArrayEquals(raw, DigestUtils.sha1(file));
		} else if ("SHA-256".equals(algorithm)) {
			assertEquals(expected, DigestUtils.sha256Hex(input));
			assertEquals(expected, DigestUtils.sha256Hex(bytes));
			assertEquals(expected, DigestUtils.sha256Hex(new ByteArrayInputStream(bytes)));
			assertEquals(expected, DigestUtils.sha256Hex(file));
			assertArrayEquals(raw, DigestUtils.sha256(input));
			assertArrayEquals(raw, DigestUtils.sha256(bytes));
			assertArrayEquals(raw, DigestUtils.sha256(new ByteArrayInputStream(bytes)));
			assertArrayEquals(raw, DigestUtils.sha256(file));
		} else if ("SHA-384".equals(algorithm)) {
			assertEquals(expected, DigestUtils.sha384Hex(input));
			assertEquals(expected, DigestUtils.sha384Hex(bytes));
			assertEquals(expected, DigestUtils.sha384Hex(new ByteArrayInputStream(bytes)));
			assertEquals(expected, DigestUtils.sha384Hex(file));
			assertArrayEquals(raw, DigestUtils.sha384(input));
			assertArrayEquals(raw, DigestUtils.sha384(bytes));
			assertArrayEquals(raw, DigestUtils.sha384(new ByteArrayInputStream(bytes)));
			assertArrayEquals(raw, DigestUtils.sha384(file));
		} else {
			assertEquals(expected, DigestUtils.sha512Hex(input));
			assertEquals(expected, DigestUtils.sha512Hex(bytes));
			assertEquals(expected, DigestUtils.sha512Hex(new ByteArrayInputStream(bytes)));
			assertEquals(expected, DigestUtils.sha512Hex(file));
			assertArrayEquals(raw, DigestUtils.sha512(input));
			assertArrayEquals(raw, DigestUtils.sha512(bytes));
			assertArrayEquals(raw, DigestUtils.sha512(new ByteArrayInputStream(bytes)));
			assertArrayEquals(raw, DigestUtils.sha512(file));
		}
	}

	@Test
	public void chineseText_usesUtf8() throws Exception {
		byte[] utf8 = "中文".getBytes(StandardCharsets.UTF_8);
		assertEquals("a7bac2239fcdcb3a067903d8077c4a07", DigestUtils.md5Hex("中文"));
		assertArrayEquals(MessageDigest.getInstance("MD5").digest(utf8), DigestUtils.md5("中文"));
		assertArrayEquals(MessageDigest.getInstance("SHA-256").digest(utf8), DigestUtils.sha256("中文"));
		assertEquals(DigestUtils.sha1Hex(utf8), DigestUtils.sha1Hex("中文"));
		assertEquals(DigestUtils.sha384Hex(utf8), DigestUtils.sha384Hex("中文"));
		assertEquals(DigestUtils.sha512Hex(utf8), DigestUtils.sha512Hex("中文"));
		CRC32 crc = new CRC32();
		crc.update(utf8, 0, utf8.length);
		assertEquals(crc.getValue(), DigestUtils.crc32("中文"));
	}

	@Test
	public void crc32_checkValue() throws IOException {
		byte[] bytes = "123456789".getBytes(StandardCharsets.US_ASCII);
		assertEquals(0xCBF43926L, DigestUtils.crc32("123456789"));
		assertEquals(3421780262L, DigestUtils.crc32(bytes));
		assertEquals(0xCBF43926L, DigestUtils.crc32(new ByteArrayInputStream(bytes)));
		assertEquals(0L, DigestUtils.crc32(new byte[0]));
		assertEquals(0L, DigestUtils.crc32(""));
		assertEquals(0L, DigestUtils.crc32(new ByteArrayInputStream(new byte[0])));
		assertEquals(0L, DigestUtils.crc32((byte[]) null));
		assertEquals(0L, DigestUtils.crc32((String) null));
		assertEquals(0L, DigestUtils.crc32((InputStream) null));
	}

	@Test
	public void nullInputs_returnNull() throws IOException {
		assertNull(DigestUtils.md5((byte[]) null));
		assertNull(DigestUtils.md5((String) null));
		assertNull(DigestUtils.md5((InputStream) null));
		assertNull(DigestUtils.md5((File) null));
		assertNull(DigestUtils.md5Hex((byte[]) null));
		assertNull(DigestUtils.md5Hex((String) null));
		assertNull(DigestUtils.md5Hex((InputStream) null));
		assertNull(DigestUtils.md5Hex((File) null));
		assertNull(DigestUtils.sha1((byte[]) null));
		assertNull(DigestUtils.sha1Hex((String) null));
		assertNull(DigestUtils.sha1((File) null));
		assertNull(DigestUtils.sha256((String) null));
		assertNull(DigestUtils.sha256Hex((InputStream) null));
		assertNull(DigestUtils.sha256((File) null));
		assertNull(DigestUtils.sha384((byte[]) null));
		assertNull(DigestUtils.sha384Hex((File) null));
		assertNull(DigestUtils.sha512((InputStream) null));
		assertNull(DigestUtils.sha512Hex((byte[]) null));
		assertNull(DigestUtils.digest("MD5", (byte[]) null));
		assertNull(DigestUtils.digest("MD5", (InputStream) null));
		assertNull(DigestUtils.digest("MD5", (File) null));
		assertNull(DigestUtils.digestHex("MD5", null));
	}

	@Test
	public void unknownOrNullAlgorithm_throwsIllegalArgument() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> DigestUtils.digest("NO-SUCH-ALGO", new byte[0]));
		assertTrue(e.getCause() instanceof NoSuchAlgorithmException);
		assertTrue(e.getMessage().contains("NO-SUCH-ALGO"));
		assertThrows(IllegalArgumentException.class, () -> DigestUtils.digest(null, new byte[0]));
		assertThrows(IllegalArgumentException.class, () -> DigestUtils.digest("NOPE", (byte[]) null));
		assertThrows(IllegalArgumentException.class, () -> DigestUtils.digest("NOPE", new ByteArrayInputStream(new byte[0])));
		assertThrows(IllegalArgumentException.class, () -> DigestUtils.digest("NOPE", (File) null));
		assertThrows(IllegalArgumentException.class, () -> DigestUtils.getDigest(null));
		assertNotSame(DigestUtils.getDigest("MD5"), DigestUtils.getDigest("MD5"));
	}

	@Test
	public void inputStream_isReadFullyButNotClosed() throws IOException {
		byte[] data = new byte[100000];
		new Random(5).nextBytes(data);
		CloseTrackingInputStream in = new CloseTrackingInputStream(new ByteArrayInputStream(data));
		assertEquals(DigestUtils.sha256Hex(data), DigestUtils.sha256Hex(in));
		assertFalse(in.closed);
		assertEquals(-1, in.read());

		in = new CloseTrackingInputStream(new ByteArrayInputStream(data));
		DigestUtils.crc32(in);
		assertFalse(in.closed);
		assertEquals(-1, in.read());

		// only the remaining content is digested
		in = new CloseTrackingInputStream(new ByteArrayInputStream("xabc".getBytes(StandardCharsets.US_ASCII)));
		assertEquals('x', in.read());
		assertEquals("900150983cd24fb0d6963f7d28e17f72", DigestUtils.md5Hex(in));
		assertFalse(in.closed);
	}

	@Test
	public void inputStream_propagatesIOException() {
		InputStream broken = new InputStream() {
			@Override
			public int read() throws IOException {
				throw new IOException("boom");
			}
		};
		assertEquals("boom", assertThrows(IOException.class, () -> DigestUtils.md5(broken)).getMessage());
		assertEquals("boom", assertThrows(IOException.class, () -> DigestUtils.crc32(broken)).getMessage());
	}

	@Test
	public void file_missingOrDirectoryThrowsIOException() {
		File missing = new File(tempDir, "missing.bin");
		assertThrows(FileNotFoundException.class, () -> DigestUtils.md5(missing));
		assertThrows(FileNotFoundException.class, () -> DigestUtils.sha256Hex(missing));
		assertThrows(IOException.class, () -> DigestUtils.sha1(tempDir));
	}

	@Test
	public void file_isClosedAfterDigest() throws IOException {
		File file = writeFile("abc".getBytes(StandardCharsets.US_ASCII));
		for (int i = 0; i < 2000; i++) {
			assertEquals("900150983cd24fb0d6963f7d28e17f72", DigestUtils.md5Hex(file));
		}
		// an open handle would prevent deletion on some platforms
		assertTrue(file.delete());
	}

	@Test
	public void largeInput_streamingMatchesInMemory() throws Exception {
		byte[] data = new byte[(5 << 20) + 123];
		new Random(11).nextBytes(data);
		File file = writeFile(data);
		for (String algorithm : new String[] { "MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512" }) {
			byte[] expected = MessageDigest.getInstance(algorithm).digest(data);
			assertArrayEquals(expected, DigestUtils.digest(algorithm, data));
			assertArrayEquals(expected, DigestUtils.digest(algorithm, new ByteArrayInputStream(data)));
			assertArrayEquals(expected, DigestUtils.digest(algorithm, file));
		}
		CRC32 crc = new CRC32();
		crc.update(data, 0, data.length);
		assertEquals(crc.getValue(), DigestUtils.crc32(data));
		assertEquals(crc.getValue(), DigestUtils.crc32(new ByteArrayInputStream(data)));
	}

	@Test
	public void constants_matchJdkNames() {
		assertEquals("MD5", DigestUtils.MD5);
		assertEquals("SHA-1", DigestUtils.SHA1);
		assertEquals("SHA-256", DigestUtils.SHA256);
		assertEquals("SHA-384", DigestUtils.SHA384);
		assertEquals("SHA-512", DigestUtils.SHA512);
	}

	@Test
	@Timeout(20)
	public void concurrentUse_producesCorrectResults() throws Exception {
		final int threads = 16;
		final int iterations = 500;
		final byte[][] inputs = new byte[64][];
		final String[] md5 = new String[inputs.length];
		final String[] sha256 = new String[inputs.length];
		final long[] crc = new long[inputs.length];
		Random random = new Random(13);
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = new byte[random.nextInt(4096)];
			random.nextBytes(inputs[i]);
			md5[i] = HexUtils.encodeHex(MessageDigest.getInstance("MD5").digest(inputs[i]));
			sha256[i] = HexUtils.encodeHex(MessageDigest.getInstance("SHA-256").digest(inputs[i]));
			CRC32 c = new CRC32();
			c.update(inputs[i], 0, inputs[i].length);
			crc[i] = c.getValue();
		}
		final CountDownLatch start = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
			for (int t = 0; t < threads; t++) {
				final int offset = t;
				futures.add(executor.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						start.await();
						int ok = 0;
						for (int i = 0; i < iterations; i++) {
							int k = (i + offset) % inputs.length;
							if (md5[k].equals(DigestUtils.md5Hex(inputs[k])) && sha256[k].equals(DigestUtils.sha256Hex(inputs[k]))
									&& crc[k] == DigestUtils.crc32(inputs[k])) {
								ok++;
							}
						}
						return ok;
					}
				}));
			}
			start.countDown();
			for (Future<Integer> future : futures) {
				assertEquals(iterations, future.get(15, TimeUnit.SECONDS).intValue());
			}
		} finally {
			executor.shutdownNow();
		}
	}

	private int fileSeq;

	private File writeFile(byte[] content) throws IOException {
		File file = new File(tempDir, "data-" + (fileSeq++) + ".bin");
		Files.write(file.toPath(), content);
		return file;
	}

	private static class CloseTrackingInputStream extends FilterInputStream {

		boolean closed;

		CloseTrackingInputStream(InputStream in) {
			super(in);
		}

		@Override
		public void close() throws IOException {
			closed = true;
			super.close();
		}
	}

}
