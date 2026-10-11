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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Base64UtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class Base64UtilsTest {

	/** RFC 4648 section 10 test vectors for BASE64. */
	@ParameterizedTest
	@CsvSource(value = { "'',''", "f,Zg==", "fo,Zm8=", "foo,Zm9v", "foob,Zm9vYg==", "fooba,Zm9vYmE=", "foobar,Zm9vYmFy" })
	public void rfc4648Vectors_encodeAndDecode(String plain, String encoded) {
		byte[] bytes = plain.getBytes(StandardCharsets.US_ASCII);
		assertEquals(encoded, Base64Utils.encode(plain));
		assertEquals(encoded, Base64Utils.encodeToString(bytes));
		assertArrayEquals(encoded.getBytes(StandardCharsets.US_ASCII), Base64Utils.encode(bytes));
		assertArrayEquals(bytes, Base64Utils.decode(encoded));
		assertArrayEquals(bytes, Base64Utils.decode(encoded.getBytes(StandardCharsets.US_ASCII)));
		assertEquals(plain, Base64Utils.decodeToString(encoded));
		assertEquals(encoded, Base64Utils.encodeUrlSafe(bytes));
		assertEquals(encoded.replace("=", ""), Base64Utils.encodeUrlSafe(bytes, false));
		assertArrayEquals(bytes, Base64Utils.decodeUrlSafe(encoded));
		assertArrayEquals(bytes, Base64Utils.decodeUrlSafe(encoded.replace("=", "")));
		assertEquals(encoded, Base64Utils.encodeMime(bytes));
		assertArrayEquals(bytes, Base64Utils.decodeMime(encoded));
		assertTrue(Base64Utils.isBase64(encoded));
		// unpadded input is accepted by the basic decoder as well
		assertArrayEquals(bytes, Base64Utils.decode(encoded.replace("=", "")));
	}

	@Test
	public void urlSafeAlphabet_differsFromBasic() {
		byte[] data = { -5, -1 };
		assertEquals("+/8=", Base64Utils.encodeToString(data));
		assertEquals("-_8=", Base64Utils.encodeUrlSafe(data));
		assertEquals("-_8=", Base64Utils.encodeUrlSafe(data, true));
		assertEquals("-_8", Base64Utils.encodeUrlSafe(data, false));
		assertArrayEquals(data, Base64Utils.decodeUrlSafe("-_8="));
		assertArrayEquals(data, Base64Utils.decodeUrlSafe("-_8"));
		assertThrows(IllegalArgumentException.class, () -> Base64Utils.decodeUrlSafe("+/8="));
		assertThrows(IllegalArgumentException.class, () -> Base64Utils.decode("-_8="));
		assertFalse(Base64Utils.isBase64("-_8="));
		assertTrue(Base64Utils.isBase64("+/8="));
	}

	@Test
	public void crossCheckWithJdk() {
		Random random = new Random(42);
		for (int n = 0; n < 300; n++) {
			byte[] data = new byte[n];
			random.nextBytes(data);
			assertEquals(Base64.getEncoder().encodeToString(data), Base64Utils.encodeToString(data));
			assertEquals(Base64.getUrlEncoder().encodeToString(data), Base64Utils.encodeUrlSafe(data));
			assertEquals(Base64.getUrlEncoder().withoutPadding().encodeToString(data), Base64Utils.encodeUrlSafe(data, false));
			assertEquals(Base64.getMimeEncoder().encodeToString(data), Base64Utils.encodeMime(data));
			assertArrayEquals(data, Base64Utils.decode(Base64.getEncoder().encodeToString(data)));
			assertArrayEquals(data, Base64Utils.decodeUrlSafe(Base64.getUrlEncoder().encodeToString(data)));
			assertArrayEquals(data, Base64Utils.decodeMime(Base64.getMimeEncoder().encodeToString(data)));
		}
	}

	@Test
	public void mime_wrapsLinesAt76AndIgnoresSeparators() {
		byte[] data = new byte[100];
		new Random(1).nextBytes(data);
		String mime = Base64Utils.encodeMime(data);
		String[] lines = mime.split("\r\n");
		assertEquals(2, lines.length);
		assertEquals(76, lines[0].length());
		assertFalse(mime.endsWith("\r\n"));
		assertEquals(Base64Utils.encodeToString(data), mime.replace("\r\n", ""));
		assertArrayEquals(data, Base64Utils.decodeMime(mime));
		assertArrayEquals(data, Base64Utils.decodeMime(mime.replace("\r\n", "\n")));
		// the basic decoder rejects line separators
		assertThrows(IllegalArgumentException.class, () -> Base64Utils.decode(mime));
		assertEquals("", Base64Utils.encodeMime(new byte[0]));
	}

	@Test
	public void largeInput_roundTrip() {
		byte[] data = new byte[3 << 20];
		new Random(9).nextBytes(data);
		String encoded = Base64Utils.encodeToString(data);
		assertEquals(4 << 20, encoded.length());
		assertArrayEquals(data, Base64Utils.decode(encoded));
		assertArrayEquals(data, Base64Utils.decodeMime(Base64Utils.encodeMime(data)));
	}

	@Test
	public void chineseText_utf8AndCharset() {
		assertEquals("5Lit5paH", Base64Utils.encode("中文"));
		assertEquals("中文", Base64Utils.decodeToString("5Lit5paH"));
		Charset gbk = Charset.forName("GBK");
		assertEquals("1tDOxA==", Base64Utils.encode("中文", gbk));
		assertEquals("中文", Base64Utils.decodeToString("1tDOxA==", gbk));
		assertEquals("5Lit5paH", Base64Utils.encode("中文", null));
		assertEquals("中文", Base64Utils.decodeToString("5Lit5paH", null));
		assertEquals(Base64.getEncoder().encodeToString("中文".getBytes(StandardCharsets.UTF_8)), Base64Utils.encode("中文"));
	}

	@Test
	public void nullInputs_returnNullOrFalse() {
		assertNull(Base64Utils.encode((byte[]) null));
		assertNull(Base64Utils.decode((byte[]) null));
		assertNull(Base64Utils.encodeToString(null));
		assertNull(Base64Utils.decode((String) null));
		assertNull(Base64Utils.encode((String) null));
		assertNull(Base64Utils.encode(null, StandardCharsets.UTF_8));
		assertNull(Base64Utils.decodeToString(null));
		assertNull(Base64Utils.decodeToString(null, StandardCharsets.UTF_8));
		assertNull(Base64Utils.encodeUrlSafe(null));
		assertNull(Base64Utils.encodeUrlSafe(null, false));
		assertNull(Base64Utils.decodeUrlSafe(null));
		assertNull(Base64Utils.encodeMime(null));
		assertNull(Base64Utils.decodeMime(null));
		assertFalse(Base64Utils.isBase64(null));
	}

	@ParameterizedTest
	@ValueSource(strings = { "Zm9v!", "Zm9vY", "Zm9v YmFy", "Zm9vYg=", "Zm9vYg===", "=Zm9v", "Zg==Zg==x", "中文" })
	public void illegalInput_decodeThrowsAndIsBase64False(String str) {
		assertThrows(IllegalArgumentException.class, () -> Base64Utils.decode(str));
		assertThrows(IllegalArgumentException.class, () -> Base64Utils.decodeToString(str));
		assertThrows(IllegalArgumentException.class, () -> Base64Utils.decode(str.getBytes(StandardCharsets.UTF_8)));
		assertFalse(Base64Utils.isBase64(str));
	}

	@Test
	public void isBase64_examples() {
		assertTrue(Base64Utils.isBase64(""));
		assertTrue(Base64Utils.isBase64("Zm9vYg=="));
		assertTrue(Base64Utils.isBase64("Zm9vYg"));
		assertFalse(Base64Utils.isBase64("Zm9vY"));
		assertFalse(Base64Utils.isBase64("Zm9v YmFy"));
		assertFalse(Base64Utils.isBase64("-_8="));
	}

	@Test
	public void decodeMime_rejectsBrokenPadding() {
		assertThrows(IllegalArgumentException.class, () -> Base64Utils.decodeMime("Zg=x"));
	}

}
