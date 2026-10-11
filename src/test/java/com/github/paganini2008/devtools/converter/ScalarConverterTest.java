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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * ScalarConverterTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ScalarConverterTest {

	@Test
	public void testBooleanConverter() {
		BooleanConverter c = new BooleanConverter();
		assertEquals(Boolean.TRUE, c.convertValue("true", null));
		assertEquals(Boolean.FALSE, c.convertValue("false", null));
		assertEquals(Boolean.TRUE, c.convertValue("", Boolean.TRUE));
		assertEquals(Boolean.TRUE, c.convertValue(1, null));
		assertEquals(Boolean.FALSE, c.convertValue(0L, null));
		c.convertValue('y', null);
		assertEquals(Boolean.FALSE, c.convertValue(null, Boolean.FALSE));
		assertEquals(Boolean.TRUE, c.convertValue(new Object(), Boolean.TRUE));
	}

	@Test
	public void testCharacterConverter() {
		CharacterConverter c = new CharacterConverter();
		assertEquals(Character.valueOf('a'), c.convertValue("abc", null));
		assertEquals(Character.valueOf('x'), c.convertValue(" ", 'x'));
		assertEquals(Character.valueOf('A'), c.convertValue(65, null));
		assertEquals(Character.valueOf('z'), c.convertValue(null, 'z'));
		assertEquals(Character.valueOf('Y'), c.convertValue(Boolean.TRUE, null));
		assertEquals(Character.valueOf('N'), c.convertValue(Boolean.FALSE, null));
	}

	@Test
	public void testIntegralConverters() {
		ByteConverter b = new ByteConverter();
		assertEquals(Byte.valueOf((byte) 12), b.convertValue("12", null));
		assertEquals(Byte.valueOf((byte) 12), b.convertValue(12L, null));
		assertEquals(Byte.valueOf((byte) 1), b.convertValue(Boolean.TRUE, null));
		assertEquals(Byte.valueOf((byte) 0), b.convertValue(Boolean.FALSE, null));
		b.convertValue('1', null);

		ShortConverter s = new ShortConverter();
		assertEquals(Short.valueOf((short) 12), s.convertValue("12", null));
		assertEquals(Short.valueOf((short) 12), s.convertValue(12.0d, null));
		assertEquals(Short.valueOf((short) 1), s.convertValue(Boolean.TRUE, null));
		s.convertValue('1', null);

		IntegerConverter i = new IntegerConverter();
		assertEquals(Integer.valueOf(12), i.convertValue("12", null));
		assertEquals(Integer.valueOf(12), i.convertValue(new BigDecimal("12"), null));
		assertEquals(Integer.valueOf(1), i.convertValue(Boolean.TRUE, null));
		assertEquals(Integer.valueOf(-1), i.convertValue("abc", -1));
		assertEquals(Integer.valueOf(49), i.convertValue('1', null));

		LongConverter l = new LongConverter();
		assertEquals(Long.valueOf(12L), l.convertValue("12", null));
		assertEquals(Long.valueOf(12L), l.convertValue(12, null));
		assertEquals(Long.valueOf(1L), l.convertValue(Boolean.TRUE, null));
		assertEquals(Long.valueOf(1000L), l.convertValue(new Date(1000L), null));
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(2000L);
		assertEquals(Long.valueOf(2000L), l.convertValue(cal, null));
		assertEquals(Long.valueOf(3000L), l.convertValue(Instant.ofEpochMilli(3000L), null));
		// java.sql.Timestamp is a subclass of Date
		assertEquals(Long.valueOf(4000L), l.convertValue(new java.sql.Timestamp(4000L), null));
		assertEquals(Long.valueOf(49L), l.convertValue('1', null));
	}

	@Test
	public void testFloatingConverters() {
		FloatConverter f = new FloatConverter();
		assertEquals(Float.valueOf(1.5f), f.convertValue("1.5", null));
		assertEquals(Float.valueOf(2.0f), f.convertValue(2, null));
		f.convertValue('1', null);

		DoubleConverter d = new DoubleConverter();
		assertEquals(Double.valueOf(1.5d), d.convertValue("1.5", null));
		assertEquals(Double.valueOf(2.0d), d.convertValue(2L, null));
		assertEquals(Double.valueOf(9.0d), d.convertValue("x", 9.0d));
		d.convertValue('1', null);
	}

	@Test
	public void testBigConverters() {
		BigDecimaConverter bd = new BigDecimaConverter();
		assertEquals(new BigDecimal("1.25"), bd.convertValue("1.25", null));
		assertEquals(0, BigDecimal.valueOf(3).compareTo(bd.convertValue(3, null)));
		assertEquals(0, BigDecimal.ONE.compareTo(bd.convertValue(Boolean.TRUE, null)));
		bd.convertValue('1', null);

		BigIntegerConverter bi = new BigIntegerConverter();
		assertEquals(BigInteger.valueOf(123), bi.convertValue("123", null));
		assertEquals(BigInteger.valueOf(5), bi.convertValue(5L, null));
		assertEquals(BigInteger.ONE, bi.convertValue(Boolean.TRUE, null));
		bi.convertValue('1', null);
	}

	@Test
	public void testCharsetLocaleUuid() {
		CharsetConverter cs = new CharsetConverter();
		assertEquals(StandardCharsets.ISO_8859_1, cs.convertValue("ISO-8859-1", null));
		Charset dft = StandardCharsets.UTF_8;
		assertEquals(dft, cs.convertValue(" ", dft));

		LocaleConverter lc = new LocaleConverter();
		assertEquals(Locale.US, lc.convertValue("en_US", null));
		assertEquals(Locale.CHINA, lc.convertValue(null, Locale.CHINA));

		UUIDConverter uc = new UUIDConverter();
		UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		assertEquals(uuid, uc.convertValue("123e4567-e89b-12d3-a456-426614174000", null));
		assertNull(uc.convertValue("", null));
		assertThrows(IllegalArgumentException.class, () -> uc.convertValue("not-a-uuid", null));
	}

}
