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
package com.github.paganini2008.devtools.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.mock.MockContext.RandomConfig;
import com.github.paganini2008.devtools.mock.MockContext.RandomDateConfig;
import com.github.paganini2008.devtools.mock.MockContext.RandomStringConfig;

/**
 * MockContextTest, also tests {@link DefaultRandomOperations} driven by the
 * configurations of the context
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MockContextTest {

	public static class PlainBean {

		private int i;
		private long l;
		private float f;
		private double d;
		private byte b;
		private short s;
		// primitive char is not used: see the known issue of PropertyUtils/ConvertUtils with char
		private Character c;
		private BigInteger bi;
		private BigDecimal bd;
		private String str;
		private Date date;
		private LocalDate localDate;
		private LocalTime localTime;
		private LocalDateTime localDateTime;
		private Thread.State state;

		public int getI() {
			return i;
		}

		public void setI(int i) {
			this.i = i;
		}

		public long getL() {
			return l;
		}

		public void setL(long l) {
			this.l = l;
		}

		public float getF() {
			return f;
		}

		public void setF(float f) {
			this.f = f;
		}

		public double getD() {
			return d;
		}

		public void setD(double d) {
			this.d = d;
		}

		public byte getB() {
			return b;
		}

		public void setB(byte b) {
			this.b = b;
		}

		public short getS() {
			return s;
		}

		public void setS(short s) {
			this.s = s;
		}

		public Character getC() {
			return c;
		}

		public void setC(Character c) {
			this.c = c;
		}

		public BigInteger getBi() {
			return bi;
		}

		public void setBi(BigInteger bi) {
			this.bi = bi;
		}

		public BigDecimal getBd() {
			return bd;
		}

		public void setBd(BigDecimal bd) {
			this.bd = bd;
		}

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public LocalDate getLocalDate() {
			return localDate;
		}

		public void setLocalDate(LocalDate localDate) {
			this.localDate = localDate;
		}

		public LocalTime getLocalTime() {
			return localTime;
		}

		public void setLocalTime(LocalTime localTime) {
			this.localTime = localTime;
		}

		public LocalDateTime getLocalDateTime() {
			return localDateTime;
		}

		public void setLocalDateTime(LocalDateTime localDateTime) {
			this.localDateTime = localDateTime;
		}

		public Thread.State getState() {
			return state;
		}

		public void setState(Thread.State state) {
			this.state = state;
		}
	}

	@Test
	public void defaultOperations_followRandomConfig() {
		MockContext context = new MockContext();
		RandomConfig config = new RandomConfig();
		config.setMinIntValue(5);
		config.setMaxIntValue(5);
		config.setMinLongValue(6L);
		config.setMaxLongValue(6L);
		config.setMinFloatValue(1.5f);
		config.setMaxFloatValue(1.5f);
		config.setMinDoubleValue(2.5d);
		config.setMaxDoubleValue(2.5d);
		config.setMinByteValue((byte) 3);
		config.setMaxByteValue((byte) 3);
		config.setMinShortValue((short) 4);
		config.setMaxShortValue((short) 4);
		config.setMinCharValue('x');
		config.setMaxCharValue('x');
		context.setRandomConfig(config);
		assertSame(config, context.getRandomConfig());
		assertEquals(5, config.getMaxIntValue());
		assertEquals(6L, config.getMaxLongValue());
		assertEquals(1.5f, config.getMaxFloatValue());
		assertEquals(2.5d, config.getMaxDoubleValue());
		assertEquals(3, config.getMaxByteValue());
		assertEquals(4, config.getMaxShortValue());
		assertEquals('x', config.getMaxCharValue());

		RandomStringConfig stringConfig = new RandomStringConfig();
		stringConfig.setLength(6);
		stringConfig.setDigit(true);
		stringConfig.setLowerCaseLetter(false);
		stringConfig.setUpperCaseLetter(false);
		stringConfig.setPrefix("<");
		stringConfig.setSuffix(">");
		context.setRandomStringConfig(stringConfig);
		assertSame(stringConfig, context.getRandomStringConfig());
		assertEquals(6, stringConfig.getLength());
		assertTrue(stringConfig.isDigit());

		RandomDateConfig dateConfig = new RandomDateConfig();
		dateConfig.setFromYear(2001);
		dateConfig.setToYear(2001);
		dateConfig.setFromMonth(3);
		dateConfig.setToMonth(3);
		dateConfig.setFromDayOfMonth(4);
		dateConfig.setToDayOfMonth(4);
		dateConfig.setFromHourOfDay(5);
		dateConfig.setToHourOfDay(5);
		dateConfig.setFromMinute(6);
		dateConfig.setToMinute(6);
		dateConfig.setFromSecond(7);
		dateConfig.setToSecond(7);
		context.setRandomDateConfig(dateConfig);
		assertSame(dateConfig, context.getRandomDateConfig());

		PlainBean bean = BeanMocker.mockBean(PlainBean.class, context, new DefaultRandomOperations());
		assertEquals(5, bean.getI());
		assertEquals(6L, bean.getL());
		assertEquals(1.5f, bean.getF());
		assertEquals(2.5d, bean.getD());
		assertEquals(3, bean.getB());
		assertEquals(4, bean.getS());
		assertEquals(Character.valueOf('x'), bean.getC());
		assertEquals(BigInteger.valueOf(6), bean.getBi());
		assertEquals(0, new BigDecimal("2.5").setScale(0, java.math.RoundingMode.HALF_UP).compareTo(bean.getBd()));
		assertTrue(bean.getStr().matches("<\\d{6}>"), bean.getStr());
		Calendar c = Calendar.getInstance();
		c.setTime(bean.getDate());
		assertEquals(2001, c.get(Calendar.YEAR));
		assertEquals(Calendar.MARCH, c.get(Calendar.MONTH));
		assertEquals(4, c.get(Calendar.DAY_OF_MONTH));
		assertEquals(5, c.get(Calendar.HOUR_OF_DAY));
		assertEquals(6, c.get(Calendar.MINUTE));
		assertEquals(7, c.get(Calendar.SECOND));
		assertEquals(LocalDate.of(2001, 3, 4), bean.getLocalDate());
		assertEquals(LocalTime.of(5, 6, 7), bean.getLocalTime());
		assertEquals(LocalDateTime.of(2001, 3, 4, 5, 6, 7), bean.getLocalDateTime());
		assertTrue(bean.getState() != null);
	}

	@Test
	public void defaultBigDecimal_isNotConstant() {
		MockContext context = new MockContext();
		context.getRandomConfig().setMinDoubleValue(0);
		context.getRandomConfig().setMaxDoubleValue(1000000);
		DefaultRandomOperations operations = new DefaultRandomOperations();
		java.util.Set<BigDecimal> values = new java.util.HashSet<BigDecimal>();
		for (int i = 0; i < 20; i++) {
			values.add(operations.randomBigDecimal(null, context));
		}
		assertTrue(values.size() > 1);
	}

	@Test
	public void addFirst_customHandlerTakesPrecedence() throws Exception {
		MockContext context = new MockContext();
		context.addFirst((field, operations, ctx) -> field.getType() == String.class ? "custom" : ctx.mock(field, operations));
		PlainBean bean = BeanMocker.mockBean(PlainBean.class, context, new RandomTemplate());
		assertEquals("custom", bean.getStr());

		// a field of unsupported type falls through all handlers
		Field field = MockContextTest.class.getDeclaredField("unsupported");
		context.reset();
		assertNull(context.mock(field, new RandomTemplate()));
	}

	@SuppressWarnings("unused")
	private Object unsupported;

	@Test
	public void suppliers() {
		MockContext context = new MockContext();
		Supplier<String> s = () -> "x";
		context.setStringSupplier("a", s);
		assertSame(s, context.getStringSupplier("a"));
		assertNull(context.getStringSupplier("b"));
		assertNull(context.getIntegerSupplier("a"));
		assertNull(context.getLongSupplier("a"));
		assertNull(context.getFloatSupplier("a"));
		assertNull(context.getDoubleSupplier("a"));
		assertNull(context.getBigDecimalSupplier("a"));
		assertNull(context.getBigIntegerSupplier("a"));
		assertNull(context.getDateSupplier("a"));
		assertNull(context.getLocalDateSupplier("a"));
		assertNull(context.getLocalTimeSupplier("a"));
		assertNull(context.getLocalDateTimeSupplier("a"));
	}

	@Test
	public void randomTemplate_delegatesWithoutAnnotations() {
		MockContext context = new MockContext();
		context.getRandomConfig().setMinIntValue(1);
		context.getRandomConfig().setMaxIntValue(1);
		RandomTemplate template = new RandomTemplate(new DefaultRandomOperations());
		PlainBean bean = BeanMocker.mockBean(PlainBean.class, context, template);
		assertEquals(1, bean.getI());
		assertEquals(32, bean.getStr().length());
	}

}
