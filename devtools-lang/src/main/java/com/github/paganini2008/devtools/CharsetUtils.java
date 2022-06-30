/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

import com.github.paganini2008.devtools.collection.LruMap;

/**
 * 
 * CharsetUtils
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class CharsetUtils {

	private static final LruMap<String, Charset> cache = new LruMap<String, Charset>(16);

	public static final String ISO_8859_1_NAME = "ISO-8859-1";

	public static final Charset ISO_8859_1 = toCharset(ISO_8859_1_NAME);

	public static final String US_ASCII_NAME = "US-ASCII";

	public static final Charset US_ASCII = toCharset(US_ASCII_NAME);

	public static final String UTF_16_NAME = "UTF-16";

	public static final Charset UTF_16 = toCharset(UTF_16_NAME);

	public static final String UTF_16BE_NAME = "UTF-16BE";

	public static final Charset UTF_16BE = toCharset(UTF_16BE_NAME);

	public static final String UTF_16LE_NAME = "UTF-16LE";

	public static final Charset UTF_16LE = toCharset(UTF_16LE_NAME);

	public static final String UTF_8_NAME = "UTF-8";

	public static final Charset UTF_8 = toCharset(UTF_8_NAME);

	public static final String GBK_NAME = "GBK";

	public static final Charset GBK = toCharset(GBK_NAME);

	public static final String GB_2312_NAME = "GB2312";

	public static final Charset GB_2312 = toCharset(GB_2312_NAME);

	public static final String BIG_5_NAME = "BIG_5";

	public static final Charset BIG_5 = toCharset(BIG_5_NAME);

	public static final Charset DEFAULT = Charset.defaultCharset();

	public static final byte[] BOM_UTF_8 = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

	public static final byte[] BOM_UTF_16LE = new byte[] { (byte) 0xFF, (byte) 0xFE };

	public static final byte[] BOM_UTF_16BE = new byte[] { (byte) 0xFE, (byte) 0xFF };

	public static Charset toCharset(Charset charset) {
		return toCharset(charset, DEFAULT);
	}

	public static Charset toCharset(Charset charset, Charset defaultCharset) {
		return charset == null ? defaultCharset : charset;
	}

	public static Charset toCharset(String charset) {
		return toCharset(charset, DEFAULT);
	}

	public static Charset toCharset(String charset, Charset defaultCharset) {
		if (StringUtils.isBlank(charset)) {
			return defaultCharset;
		}
		try {
			Charset instance = cache.get(charset);
			if (instance == null) {
				cache.put(charset, Charset.forName(charset));
				instance = cache.get(charset);
			}
			return instance;
		} catch (IllegalArgumentException e) {
			return defaultCharset;
		}
	}

	public static CharsetDecoder newDecoder(Charset charset) {
		CharsetDecoder decoder = charset.newDecoder();
		decoder.reset();
		decoder.onMalformedInput(CodingErrorAction.REPLACE);
		decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		return decoder;
	}

	public static CharsetEncoder newEncoder(Charset charset) {
		CharsetEncoder encoder = charset.newEncoder();
		encoder.reset();
		encoder.onMalformedInput(CodingErrorAction.REPLACE);
		encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		return encoder;
	}

}
