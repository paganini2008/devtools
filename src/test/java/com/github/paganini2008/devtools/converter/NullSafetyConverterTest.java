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
package com.github.paganini2008.devtools.converter;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * NullSafetyConverterTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class NullSafetyConverterTest {

	private static final Class<?>[] SOURCE_TYPES = { String.class, CharSequence.class, Boolean.class, Character.class, Number.class,
			Integer.class, Long.class, Date.class, Calendar.class, Instant.class, LocalDate.class, LocalDateTime.class, LocalTime.class,
			UUID.class, Charset.class, Locale.class, URL.class, Blob.class, Clob.class, InputStream.class, Reader.class, ByteBuffer.class,
			Collection.class, Iterator.class, Enumeration.class, Map.class, Object[].class, Number[].class, String[].class,
			Boolean[].class, Character[].class, Date[].class, Calendar[].class, boolean[].class, char[].class, byte[].class,
			short[].class, int[].class, long[].class, float[].class, double[].class };

	@SuppressWarnings("unchecked")
	@Test
	public void everyRegisteredConverter_isNullSafe() {
		StandardTypeConverter typeConverter = new StandardTypeConverter();
		Class<?>[] targets = { boolean.class, char.class, Character[].class, char[].class, byte.class, Byte[].class, byte[].class,
				short.class, Short[].class, short[].class, int.class, Integer[].class, int[].class, float.class, Float[].class,
				float[].class, double.class, Double[].class, double[].class, long.class, Long[].class, long[].class, BigDecimal.class,
				BigDecimal[].class, BigInteger.class, BigInteger[].class, Date.class, Calendar.class, String.class, String[].class,
				Charset.class, UUID.class, Locale.class, LocalDate.class, LocalTime.class, LocalDateTime.class };
		int checked = 0;
		for (Class<?> target : targets) {
			BasicConverter<Object> converter = (BasicConverter<Object>) typeConverter.lookupType(target);
			for (Class<?> source : SOURCE_TYPES) {
				Converter<Object, Object> c = (Converter<Object, Object>) converter.lookupType(source);
				if (c != null) {
					assertNull(c.convertValue(null, null), target.getSimpleName() + " <- " + source.getSimpleName());
					checked++;
				}
			}
		}
		assertTrue(checked > 200, "checked: " + checked);
	}

}
