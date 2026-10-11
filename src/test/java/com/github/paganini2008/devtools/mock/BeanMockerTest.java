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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * BeanMockerTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class BeanMockerTest {

	public static class SuppliedBean {

		@IntRange("i")
		private int i;
		@LongRange("l")
		private long l;
		@FloatRange("f")
		private float f;
		@DoubleRange("d")
		private double d;
		@LongRange("bi")
		private BigInteger bi;
		@DoubleRange("bd")
		private BigDecimal bd;
		@NumberRange("nbi")
		private BigInteger nbi;
		@NumberRange("nbd")
		private BigDecimal nbd;
		@DateRange("dt")
		private Date date;
		@TimeRange("tm")
		private Date time;
		@DateRange("ld")
		private LocalDate localDate;
		@TimeRange("lt")
		private LocalTime localTime;
		@DateRange("ld")
		@TimeRange("lt")
		private LocalDateTime localDateTime;

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

		public BigInteger getNbi() {
			return nbi;
		}

		public void setNbi(BigInteger nbi) {
			this.nbi = nbi;
		}

		public BigDecimal getNbd() {
			return nbd;
		}

		public void setNbd(BigDecimal nbd) {
			this.nbd = nbd;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public Date getTime() {
			return time;
		}

		public void setTime(Date time) {
			this.time = time;
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
	}

	private static Calendar calendar(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	private static boolean between(BigDecimal value, String from, String to) {
		return value.compareTo(new BigDecimal(from)) >= 0 && value.compareTo(new BigDecimal(to)) <= 0;
	}

	@Test
	public void mockBeans_respectAnnotations() {
		MockContext context = new MockContext();
		context.setStringSupplier("username", () -> "Petter");
		context.setStringSupplier("nick", () -> "Nick");
		List<MockBean> beans = BeanMocker.mockBeans(30, MockBean.class, context);
		assertEquals(30, beans.size());
		LocalDate today = LocalDate.now();
		for (MockBean bean : beans) {
			assertTrue(bean.getIntValue() >= 10 && bean.getIntValue() < 20, "" + bean.getIntValue());
			assertTrue(bean.getLongValue() >= 1 && bean.getLongValue() < 5);
			assertTrue(bean.getFloatValue() >= 1 && bean.getFloatValue() <= 2);
			assertTrue(bean.getFloatPrecision() >= 100 && bean.getFloatPrecision() < 1000, "" + bean.getFloatPrecision());
			assertTrue(bean.getDoubleValue() >= 5 && bean.getDoubleValue() <= 6);
			assertTrue(bean.getDoublePrecision() >= 10 && bean.getDoublePrecision() < 100, "" + bean.getDoublePrecision());
			assertTrue(between(new BigDecimal(bean.getBigInteger()), "0", "10"));
			assertTrue(between(bean.getBigDecimal(), "50", "200"), "" + bean.getBigDecimal());
			assertTrue(between(bean.getBigDecimalPrecision(), "100", "1000"), "" + bean.getBigDecimalPrecision());
			assertTrue(between(bean.getNumberDecimal(), "1", "100"));
			assertTrue(bean.getNumberDecimal().scale() == 1);
			assertTrue(between(new BigDecimal(bean.getNumberInteger()), "1", "100"));
			assertEquals(4, bean.getNumberIntegerPrecision().toString().length());
			assertTrue(between(bean.getNumberDecimalPrecision(), "100", "1000"));
			assertEquals(2, bean.getNumberDecimalPrecision().scale());

			assertTrue(bean.getCode().matches("P_\\d{8}_S"), bean.getCode());
			assertTrue(bean.getStyled().matches("[ab]{5}"), bean.getStyled());
			assertEquals("Petter", bean.getName());
			assertEquals("Nick", bean.getNick());
			assertEquals(32, bean.getPlain().length());

			assertEquals(2020, calendar(bean.getDate()).get(Calendar.YEAR));
			int hour = calendar(bean.getTime()).get(Calendar.HOUR_OF_DAY);
			assertTrue(hour == 10 || hour == 11, "" + hour);
			Calendar dateTime = calendar(bean.getDateTime());
			assertEquals(2020, dateTime.get(Calendar.YEAR));
			assertEquals(Calendar.JANUARY, dateTime.get(Calendar.MONTH));
			assertTrue(dateTime.get(Calendar.HOUR_OF_DAY) == 8 || dateTime.get(Calendar.HOUR_OF_DAY) == 9);
			assertTrue(bean.getPlainDate().before(new Date(System.currentTimeMillis() + 366L * 24 * 3600 * 1000)));

			assertTrue(!bean.getLocalDate().isBefore(LocalDate.of(2020, 1, 1)) && !bean.getLocalDate().isAfter(LocalDate.of(2020, 1, 10)));
			assertTrue(!bean.getOpenLocalDate().isBefore(LocalDate.of(2020, 1, 1)) && !bean.getOpenLocalDate().isAfter(today));
			assertNotNull(bean.getPlainLocalDate());
			assertTrue(!bean.getLocalTime().isBefore(LocalTime.of(10, 0)) && !bean.getLocalTime().isAfter(LocalTime.of(10, 30)));
			assertNotNull(bean.getPlainLocalTime());

			LocalDateTime ldt = bean.getLocalDateTime();
			assertTrue(ldt.getDayOfMonth() <= 10 && ldt.getYear() == 2020);
			assertTrue(ldt.getHour() == 8 || ldt.getHour() == 9);
			assertTrue(bean.getLocalDateTimeOnlyTime().getHour() == 8 || bean.getLocalDateTimeOnlyTime().getHour() == 9);
			assertEquals(LocalTime.MIDNIGHT, bean.getLocalDateTimeOnlyDate().toLocalTime());
			assertNotNull(bean.getPlainLocalDateTime());

			assertNotNull(bean.getFlag2());
			assertNotNull(bean.getSh());
			assertNotNull(bean.getStyle());
			assertNotNull(bean.getInner());
			assertTrue(bean.getInner().getCode().matches("\\d{4}"));
			// no handler for Object
			assertNull(bean.getOther());
		}
	}

	@Test
	public void mockBean_usesSuppliersOfContext() {
		MockContext context = new MockContext();
		Date date = new Date(0);
		Date time = new Date(3600 * 1000);
		context.setIntegerSupplier("i", () -> 7);
		context.setLongSupplier("l", () -> 8L);
		context.setFloatSupplier("f", () -> 1.5f);
		context.setDoubleSupplier("d", () -> 2.5d);
		context.setBigIntegerSupplier("bi", () -> BigInteger.TEN);
		context.setBigDecimalSupplier("bd", () -> BigDecimal.ONE);
		context.setBigIntegerSupplier("nbi", () -> BigInteger.ONE);
		context.setBigDecimalSupplier("nbd", () -> BigDecimal.TEN);
		context.setDateSupplier("dt", () -> date);
		context.setDateSupplier("tm", () -> time);
		context.setLocalDateSupplier("ld", () -> LocalDate.of(2000, 2, 3));
		context.setLocalTimeSupplier("lt", () -> LocalTime.of(4, 5, 6));
		context.setLocalDateTimeSupplier("ldt", () -> LocalDateTime.of(2000, 1, 1, 0, 0));
		assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0), context.getLocalDateTimeSupplier("ldt").get());

		SuppliedBean bean = BeanMocker.mockBean(SuppliedBean.class, context, new RandomTemplate());
		assertEquals(7, bean.getI());
		assertEquals(8L, bean.getL());
		assertEquals(1.5f, bean.getF());
		assertEquals(2.5d, bean.getD());
		assertEquals(BigInteger.TEN, bean.getBi());
		assertEquals(BigDecimal.ONE, bean.getBd());
		assertEquals(BigInteger.ONE, bean.getNbi());
		assertEquals(BigDecimal.TEN, bean.getNbd());
		assertNotNull(bean.getDate());
		assertNotNull(bean.getTime());
		assertEquals(LocalDate.of(2000, 2, 3), bean.getLocalDate());
		assertEquals(LocalTime.of(4, 5, 6), bean.getLocalTime());
		assertEquals(LocalDateTime.of(2000, 2, 3, 4, 5, 6), bean.getLocalDateTime());
	}

}
