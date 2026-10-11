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
import static org.junit.jupiter.api.Assertions.assertSame;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * CharsetUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class CharsetUtilsTest {

	@Test
	public void testConstants() {
		assertEquals(StandardCharsets.UTF_8, CharsetUtils.UTF_8);
		assertEquals(StandardCharsets.ISO_8859_1, CharsetUtils.ISO_8859_1);
		assertEquals(StandardCharsets.US_ASCII, CharsetUtils.US_ASCII);
		assertEquals(StandardCharsets.UTF_16, CharsetUtils.UTF_16);
		assertEquals(StandardCharsets.UTF_16BE, CharsetUtils.UTF_16BE);
		assertEquals(StandardCharsets.UTF_16LE, CharsetUtils.UTF_16LE);
		assertEquals(Charset.forName("GBK"), CharsetUtils.GBK);
		assertEquals(Charset.forName("Big5"), CharsetUtils.BIG_5);
		assertEquals(Charset.defaultCharset(), CharsetUtils.DEFAULT);
	}

	@Test
	public void testToCharset() {
		assertSame(CharsetUtils.DEFAULT, CharsetUtils.toCharset((Charset) null));
		assertSame(StandardCharsets.UTF_8, CharsetUtils.toCharset(StandardCharsets.UTF_8));
		assertSame(StandardCharsets.UTF_16, CharsetUtils.toCharset((Charset) null, StandardCharsets.UTF_16));
		assertEquals(StandardCharsets.UTF_8, CharsetUtils.toCharset("utf-8"));
		assertEquals(StandardCharsets.UTF_8, CharsetUtils.toCharset("utf-8"));
		assertSame(CharsetUtils.DEFAULT, CharsetUtils.toCharset((String) null));
		assertSame(StandardCharsets.UTF_16, CharsetUtils.toCharset(" ", StandardCharsets.UTF_16));
		assertSame(StandardCharsets.UTF_16, CharsetUtils.toCharset("no-such-charset", StandardCharsets.UTF_16));
		assertSame(StandardCharsets.UTF_16, CharsetUtils.toCharset("bad name!", StandardCharsets.UTF_16));
	}

	@Test
	public void testCoder() {
		CharsetDecoder decoder = CharsetUtils.newDecoder(StandardCharsets.UTF_8);
		assertEquals(CodingErrorAction.REPLACE, decoder.malformedInputAction());
		assertEquals(CodingErrorAction.REPLACE, decoder.unmappableCharacterAction());
		CharsetEncoder encoder = CharsetUtils.newEncoder(StandardCharsets.US_ASCII);
		assertEquals(CodingErrorAction.REPLACE, encoder.malformedInputAction());
		assertEquals(CodingErrorAction.REPLACE, encoder.unmappableCharacterAction());
	}

}
