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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.codec.HexUtils;

/**
 * HmacUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class HmacUtilsTest {

	private static final byte[] KEY_25 = new byte[25];
	static {
		for (int i = 0; i < KEY_25.length; i++) {
			KEY_25[i] = (byte) (i + 1);
		}
	}

	private static final String JEFE = "Jefe";
	private static final String JEFE_DATA = "what do ya want for nothing?";
	private static final String FOX = "The quick brown fox jumps over the lazy dog";

	private static byte[] fill(int value, int length) {
		byte[] b = new byte[length];
		Arrays.fill(b, (byte) value);
		return b;
	}

	private static byte[] ascii(String s) {
		return s.getBytes(StandardCharsets.US_ASCII);
	}

	/** All eight overloads of one algorithm. */
	private static final class Overloads {
		final String algorithm;
		final BiFunction<byte[], byte[], byte[]> bb;
		final BiFunction<String, String, byte[]> ss;
		final BiFunction<byte[], String, byte[]> bs;
		final BiFunction<String, byte[], byte[]> sb;
		final BiFunction<byte[], byte[], String> bbHex;
		final BiFunction<String, String, String> ssHex;
		final BiFunction<byte[], String, String> bsHex;
		final BiFunction<String, byte[], String> sbHex;

		Overloads(String algorithm, BiFunction<byte[], byte[], byte[]> bb, BiFunction<String, String, byte[]> ss,
				BiFunction<byte[], String, byte[]> bs, BiFunction<String, byte[], byte[]> sb, BiFunction<byte[], byte[], String> bbHex,
				BiFunction<String, String, String> ssHex, BiFunction<byte[], String, String> bsHex,
				BiFunction<String, byte[], String> sbHex) {
			this.algorithm = algorithm;
			this.bb = bb;
			this.ss = ss;
			this.bs = bs;
			this.sb = sb;
			this.bbHex = bbHex;
			this.ssHex = ssHex;
			this.bsHex = bsHex;
			this.sbHex = sbHex;
		}

		/** Asserts a binary key/data vector through every applicable overload. */
		void check(byte[] key, byte[] data, String expected) {
			byte[] raw = HexUtils.decodeHex(expected);
			assertArrayEquals(raw, bb.apply(key, data));
			assertEquals(expected, bbHex.apply(key, data));
			assertArrayEquals(raw, HmacUtils.hmac(algorithm, key, data));
			assertEquals(expected, HmacUtils.hmacHex(algorithm, key, data));
		}

		/** Asserts an ASCII key/data vector through all eight overloads. */
		void check(String key, String data, String expected) {
			check(ascii(key), ascii(data), expected);
			byte[] raw = HexUtils.decodeHex(expected);
			assertArrayEquals(raw, ss.apply(key, data));
			assertArrayEquals(raw, bs.apply(ascii(key), data));
			assertArrayEquals(raw, sb.apply(key, ascii(data)));
			assertEquals(expected, ssHex.apply(key, data));
			assertEquals(expected, bsHex.apply(ascii(key), data));
			assertEquals(expected, sbHex.apply(key, ascii(data)));
		}

		void checkNulls() {
			assertNull(bb.apply(new byte[1], null));
			assertNull(ss.apply("k", null));
			assertNull(bs.apply(new byte[1], null));
			assertNull(sb.apply("k", null));
			assertNull(bbHex.apply(new byte[1], null));
			assertNull(ssHex.apply("k", null));
			assertNull(bsHex.apply(new byte[1], null));
			assertNull(sbHex.apply("k", null));
			assertThrows(IllegalArgumentException.class, () -> bb.apply(null, new byte[0]));
			assertThrows(IllegalArgumentException.class, () -> ss.apply(null, ""));
			assertThrows(IllegalArgumentException.class, () -> bs.apply(null, ""));
			assertThrows(IllegalArgumentException.class, () -> sb.apply(null, new byte[0]));
			assertThrows(IllegalArgumentException.class, () -> bbHex.apply(null, new byte[0]));
			assertThrows(IllegalArgumentException.class, () -> ssHex.apply(null, ""));
			assertThrows(IllegalArgumentException.class, () -> bsHex.apply(null, ""));
			assertThrows(IllegalArgumentException.class, () -> sbHex.apply(null, new byte[0]));
			// a null key is rejected even if the data is null
			assertThrows(IllegalArgumentException.class, () -> bb.apply(null, null));
		}
	}

	private static final Overloads MD5 = new Overloads("HmacMD5", HmacUtils::hmacMd5, HmacUtils::hmacMd5, HmacUtils::hmacMd5,
			HmacUtils::hmacMd5, HmacUtils::hmacMd5Hex, HmacUtils::hmacMd5Hex, HmacUtils::hmacMd5Hex, HmacUtils::hmacMd5Hex);
	private static final Overloads SHA1 = new Overloads("HmacSHA1", HmacUtils::hmacSha1, HmacUtils::hmacSha1, HmacUtils::hmacSha1,
			HmacUtils::hmacSha1, HmacUtils::hmacSha1Hex, HmacUtils::hmacSha1Hex, HmacUtils::hmacSha1Hex, HmacUtils::hmacSha1Hex);
	private static final Overloads SHA256 = new Overloads("HmacSHA256", HmacUtils::hmacSha256, HmacUtils::hmacSha256,
			HmacUtils::hmacSha256, HmacUtils::hmacSha256, HmacUtils::hmacSha256Hex, HmacUtils::hmacSha256Hex, HmacUtils::hmacSha256Hex,
			HmacUtils::hmacSha256Hex);
	private static final Overloads SHA512 = new Overloads("HmacSHA512", HmacUtils::hmacSha512, HmacUtils::hmacSha512,
			HmacUtils::hmacSha512, HmacUtils::hmacSha512, HmacUtils::hmacSha512Hex, HmacUtils::hmacSha512Hex, HmacUtils::hmacSha512Hex,
			HmacUtils::hmacSha512Hex);

	/** RFC 2202 section 2, test cases 1-4. */
	@Test
	public void hmacMd5_rfc2202() {
		MD5.check(fill(0x0b, 16), ascii("Hi There"), "9294727a3638bb1c13f48ef8158bfc9d");
		MD5.check(JEFE, JEFE_DATA, "750c783e6ab0b503eaa86e310a5db738");
		MD5.check(fill(0xaa, 16), fill(0xdd, 50), "56be34521d144c88dbb8c733f0e8b3f6");
		MD5.check(KEY_25, fill(0xcd, 50), "697eaf0aca3a3aea3a75164746ffaa79");
	}

	/** RFC 2202 section 3, test cases 1-4. */
	@Test
	public void hmacSha1_rfc2202() {
		SHA1.check(fill(0x0b, 20), ascii("Hi There"), "b617318655057264e28bc0b6fb378c8ef146be00");
		SHA1.check(JEFE, JEFE_DATA, "effcdf6ae5eb2fa2d27416d5f184df9c259a7c79");
		SHA1.check(fill(0xaa, 20), fill(0xdd, 50), "125d7342b9ac11cd91a39af48aa17b4f63f175d3");
		SHA1.check(KEY_25, fill(0xcd, 50), "4c9007f4026250c6bc8414f9bf50c86c2d7235da");
	}

	/** RFC 4231 section 4.2-4.5, test cases 1-4. */
	@Test
	public void hmacSha256_rfc4231() {
		SHA256.check(fill(0x0b, 20), ascii("Hi There"), "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7");
		SHA256.check(JEFE, JEFE_DATA, "5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843");
		SHA256.check(fill(0xaa, 20), fill(0xdd, 50), "773ea91e36800e46854db8ebd09181a72959098b3ef8c122d9635514ced565fe");
		SHA256.check(KEY_25, fill(0xcd, 50), "82558a389a443c0ea4cc819899f2083a85f0faa3e578f8077a2e3ff46729665b");
	}

	/** RFC 4231 section 4.2-4.5, test cases 1-4. */
	@Test
	public void hmacSha512_rfc4231() {
		SHA512.check(fill(0x0b, 20), ascii("Hi There"),
				"87aa7cdea5ef619d4ff0b4241a1d6cb02379f4e2ce4ec2787ad0b30545e17cdedaa833b7d6b8a702038b274eaea3f4e4be9d914eeb61f1702e696c203a126854");
		SHA512.check(JEFE, JEFE_DATA,
				"164b7a7bfcf819e2e395fbe73b56e0a387bd64222e831fd610270cd7ea2505549758bf75c05a994a6d034f65f8f0e6fdcaeab1a34d4a6b4b636e070a38bce737");
		SHA512.check(fill(0xaa, 20), fill(0xdd, 50),
				"fa73b0089d56a284efb0f0756c890be9b1b5dbdd8ee81a3655f83e33b2279d39bf3e848279a722c806b485a47e67c807b946a337bee8942674278859e13292fb");
		SHA512.check(KEY_25, fill(0xcd, 50),
				"b0ba465637458c6990e5a8c5f61d4af7e576d97ff94b872de76f8050361ee3dba91ca5c11aa25eb4d679275cc5788063a5f19741120c4f2de2adebeb10a298dd");
	}

	@Test
	public void javadocExamples_quickBrownFox() {
		MD5.check("key", FOX, "80070713463e7749b90c2dc24911e275");
		SHA1.check("key", FOX, "de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9");
		SHA256.check("key", FOX, "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8");
		SHA512.check("key", FOX,
				"b42af09057bac1e2d41708e48a902e09b5ff7f12ab428a4fe86653c73dd248fb82f948a549f7b791a5b41915ee4d1ec3935357e4e2317250d0372afa2ebeeb3a");
	}

	@Test
	public void chineseKeyAndData_useUtf8() {
		byte[] key = "密钥".getBytes(StandardCharsets.UTF_8);
		byte[] data = "中文".getBytes(StandardCharsets.UTF_8);
		assertEquals("ecd9a0b52de17dabc710f2d7687a93d2", HmacUtils.hmacMd5Hex("密钥", "中文"));
		assertEquals("7101a4aa02fe963de5ef04ddf6dcaccb0649ccbb", HmacUtils.hmacSha1Hex("密钥", "中文"));
		assertEquals("57d968db66b4a9c5ec4ca7646baf27da2b515b754c20156e00a2fb3230113913", HmacUtils.hmacSha256Hex("密钥", "中文"));
		assertEquals(
				"ddfcaff130b28c30446339d84d1bedcdf577fe92012087d4f38cc0c5fd5acd4102646a8b3023d00231e35a27c9412880efd585fd93dfca2bfc35239b124c365c",
				HmacUtils.hmacSha512Hex("密钥", "中文"));
		assertEquals(HmacUtils.hmacSha256Hex(key, data), HmacUtils.hmacSha256Hex(key, "中文"));
		assertEquals(HmacUtils.hmacSha256Hex(key, data), HmacUtils.hmacSha256Hex("密钥", data));
	}

	@Test
	public void crossCheckWithJdkMac() throws Exception {
		Random random = new Random(17);
		for (String algorithm : new String[] { "HmacMD5", "HmacSHA1", "HmacSHA256", "HmacSHA512" }) {
			for (int n = 1; n < 300; n += 37) {
				byte[] key = new byte[n];
				byte[] data = new byte[n * 3];
				random.nextBytes(key);
				random.nextBytes(data);
				Mac mac = Mac.getInstance(algorithm);
				mac.init(new SecretKeySpec(key, algorithm));
				assertArrayEquals(mac.doFinal(data), HmacUtils.hmac(algorithm, key, data));
			}
		}
	}

	@Test
	public void emptyKeyAndData_allowed() {
		assertEquals("b613679a0814d9ec772f95d778c35fc5ff1697c493715653c6c712144292c5ad", HmacUtils.hmacSha256Hex("", ""));
		assertEquals("b613679a0814d9ec772f95d778c35fc5ff1697c493715653c6c712144292c5ad",
				HmacUtils.hmacHex("HmacSHA256", new byte[0], new byte[0]));
	}

	@Test
	public void keyIsCopied_notRetained() {
		byte[] key = ascii(JEFE);
		Mac mac = HmacUtils.getMac("HmacSHA256", key);
		Arrays.fill(key, (byte) 0);
		assertEquals("5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843",
				HexUtils.encodeHex(mac.doFinal(ascii(JEFE_DATA))));
		assertNotSame(HmacUtils.getMac("HmacSHA1", key), HmacUtils.getMac("HmacSHA1", key));
	}

	@Test
	public void nullHandling() {
		MD5.checkNulls();
		SHA1.checkNulls();
		SHA256.checkNulls();
		SHA512.checkNulls();
		assertNull(HmacUtils.hmac("HmacSHA256", new byte[1], null));
		assertNull(HmacUtils.hmacHex("HmacSHA256", new byte[1], null));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> HmacUtils.hmac("HmacSHA256", null, new byte[0]));
		assertTrue(e.getMessage().contains("key"));
		assertThrows(IllegalArgumentException.class, () -> HmacUtils.hmac(null, new byte[1], new byte[0]));
		assertThrows(IllegalArgumentException.class, () -> HmacUtils.getMac(null, new byte[1]));
		assertThrows(IllegalArgumentException.class, () -> HmacUtils.getMac("HmacSHA256", null));
	}

	@Test
	public void unknownAlgorithm_throwsIllegalArgument() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> HmacUtils.hmac("HmacNOPE", new byte[1], new byte[1]));
		assertTrue(e.getCause() instanceof NoSuchAlgorithmException);
		assertTrue(e.getMessage().contains("HmacNOPE"));
	}

	@Test
	public void incompatibleKey_throwsIllegalArgument() {
		// a Mac algorithm that only accepts keys of a specific type/length reports InvalidKeyException
		boolean tested = false;
		for (String algorithm : new String[] { "HmacPBESHA256", "PBEWithHmacSHA256", "HmacPBESHA1" }) {
			try {
				Mac.getInstance(algorithm);
			} catch (NoSuchAlgorithmException ex) {
				continue;
			}
			IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
					() -> HmacUtils.hmac(algorithm, new byte[16], new byte[1]));
			assertTrue(e.getMessage().startsWith("Invalid mac key"), e.getMessage());
			tested = true;
			break;
		}
		assertTrue(tested, "no PBE-based Mac available");
	}

	@Test
	public void constants_matchJdkNames() {
		assertEquals("HmacMD5", HmacUtils.HMAC_MD5);
		assertEquals("HmacSHA1", HmacUtils.HMAC_SHA1);
		assertEquals("HmacSHA256", HmacUtils.HMAC_SHA256);
		assertEquals("HmacSHA512", HmacUtils.HMAC_SHA512);
	}

	@Test
	public void largeData() throws Exception {
		byte[] data = new byte[4 << 20];
		new Random(19).nextBytes(data);
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(KEY_25, "HmacSHA256"));
		assertArrayEquals(mac.doFinal(data), HmacUtils.hmacSha256(KEY_25, data));
		// keys longer than the block size are hashed first (RFC 2104)
		byte[] longKey = fill(0xaa, 131);
		mac.init(new SecretKeySpec(longKey, "HmacSHA256"));
		assertArrayEquals(mac.doFinal(data), HmacUtils.hmacSha256(longKey, data));
	}

	@Test
	@Timeout(20)
	public void concurrentUse_producesCorrectResults() throws Exception {
		final int threads = 16;
		final int iterations = 500;
		final byte[][] keys = new byte[32][];
		final byte[][] inputs = new byte[32][];
		final String[] expected = new String[keys.length];
		Random random = new Random(23);
		for (int i = 0; i < keys.length; i++) {
			keys[i] = new byte[1 + random.nextInt(100)];
			inputs[i] = new byte[random.nextInt(2048)];
			random.nextBytes(keys[i]);
			random.nextBytes(inputs[i]);
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(keys[i], "HmacSHA256"));
			expected[i] = HexUtils.encodeHex(mac.doFinal(inputs[i]));
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
							int k = (i * 7 + offset) % keys.length;
							if (expected[k].equals(HmacUtils.hmacSha256Hex(keys[k], inputs[k]))) {
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

}
