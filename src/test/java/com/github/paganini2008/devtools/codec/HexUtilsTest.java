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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * HexUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class HexUtilsTest {

	/** RFC 4648 section 10 test vectors for BASE16. */
	@ParameterizedTest
	@CsvSource(value = { "'',''", "f,66", "fo,666F", "foo,666F6F", "foob,666F6F62", "fooba,666F6F6261", "foobar,666F6F626172" })
	public void rfc4648Vectors_encodeAndDecode(String plain, String hex) {
		byte[] bytes = plain.getBytes(StandardCharsets.US_ASCII);
		assertEquals(hex, HexUtils.encodeHex(bytes, true));
		assertEquals(hex.toLowerCase(), HexUtils.encodeHex(bytes));
		assertEquals(hex.toLowerCase(), HexUtils.encodeHex(plain));
		assertArrayEquals(bytes, HexUtils.decodeHex(hex));
		assertArrayEquals(bytes, HexUtils.decodeHex(hex.toLowerCase()));
		assertEquals(plain, HexUtils.decodeHexString(hex));
	}

	@Test
	public void encodeHex_handlesSignedBytesAndCase() {
		byte[] data = { 0, 15, -1 };
		assertEquals("000fff", HexUtils.encodeHex(data));
		assertEquals("000fff", HexUtils.encodeHex(data, false));
		assertEquals("000FFF", HexUtils.encodeHex(data, true));
		assertArrayEquals(data, HexUtils.decodeHex("000fff"));
		assertArrayEquals(data, HexUtils.decodeHex("000FFF"));
		assertArrayEquals(data, HexUtils.decodeHex("000fFf"));
		assertEquals("", HexUtils.encodeHex(new byte[0]));
		assertArrayEquals(new byte[0], HexUtils.decodeHex(""));
	}

	@Test
	public void allByteValues_roundTrip() {
		byte[] all = new byte[256];
		for (int i = 0; i < 256; i++) {
			all[i] = (byte) i;
		}
		String hex = HexUtils.encodeHex(all);
		assertEquals(512, hex.length());
		assertTrue(hex.startsWith("000102") && hex.endsWith("fdfeff"));
		assertArrayEquals(all, HexUtils.decodeHex(hex));
		assertArrayEquals(all, HexUtils.decodeHex(hex.toUpperCase()));
	}

	@Test
	public void crossCheckWithJdk() {
		Random random = new Random(42);
		for (int n = 1; n < 200; n++) {
			byte[] data = new byte[n];
			random.nextBytes(data);
			StringBuilder jdk = new StringBuilder();
			for (byte b : data) {
				jdk.append(String.format("%02x", b & 0xFF));
			}
			assertEquals(jdk.toString(), HexUtils.encodeHex(data));
			// BigInteger with a leading 0x01 marker byte keeps leading zeros
			byte[] marked = new byte[n + 1];
			marked[0] = 1;
			System.arraycopy(data, 0, marked, 1, n);
			assertEquals(new BigInteger(marked).toString(16).substring(1), HexUtils.encodeHex(data));
		}
	}

	@Test
	public void largeInput_roundTrip() {
		byte[] data = new byte[1 << 20];
		new Random(7).nextBytes(data);
		String hex = HexUtils.encodeHex(data);
		assertEquals(2 << 20, hex.length());
		assertArrayEquals(data, HexUtils.decodeHex(hex));
	}

	@Test
	public void chineseText_utf8AndCharset() {
		assertEquals("e4b8ade69687", HexUtils.encodeHex("中文"));
		assertEquals("中文", HexUtils.decodeHexString("e4b8ade69687"));
		assertEquals("E4B8ADE69687", HexUtils.encodeHex("中文".getBytes(StandardCharsets.UTF_8), true));
		Charset gbk = Charset.forName("GBK");
		assertEquals("d6d0cec4", HexUtils.encodeHex("中文", gbk));
		assertEquals("中文", HexUtils.decodeHexString("d6d0cec4", gbk));
		// null charset falls back to UTF-8
		assertEquals("e4b8ade69687", HexUtils.encodeHex("中文", null));
		assertEquals("中文", HexUtils.decodeHexString("e4b8ade69687", null));
	}

	@Test
	public void nullInputs_returnNull() {
		assertNull(HexUtils.encodeHex((byte[]) null));
		assertNull(HexUtils.encodeHex((byte[]) null, true));
		assertNull(HexUtils.encodeHex((String) null));
		assertNull(HexUtils.encodeHex((String) null, StandardCharsets.UTF_8));
		assertNull(HexUtils.decodeHex(null));
		assertNull(HexUtils.decodeHexString(null));
		assertNull(HexUtils.decodeHexString(null, StandardCharsets.UTF_8));
	}

	@Test
	public void decodeHex_rejectsOddLength() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> HexUtils.decodeHex("abc"));
		assertTrue(e.getMessage().contains("Odd"));
		assertThrows(IllegalArgumentException.class, () -> HexUtils.decodeHex("f"));
		assertThrows(IllegalArgumentException.class, () -> HexUtils.decodeHexString("abc"));
	}

	@ParameterizedTest
	@CsvSource(value = { "zz,0", "0g,1", "g0,0", "0G,1", "'0 ',1", "0x00,1", "/0,0", ":0,0", "@0,0", "`0,0" })
	public void decodeHex_rejectsIllegalCharacters(String hex, int index) {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> HexUtils.decodeHex(hex));
		assertTrue(e.getMessage().contains("index " + index), e.getMessage());
	}

}
