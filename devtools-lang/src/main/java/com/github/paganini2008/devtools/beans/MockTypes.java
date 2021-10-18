/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.beans;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.paganini2008.devtools.RandomStringUtils;
import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * MockTypes
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class MockTypes {

	public static class MockByte implements MockType<Byte> {

		private final byte from;
		private final byte to;

		public MockByte(byte from, byte to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type getType() {
			return Byte.class;
		}

		@Override
		public Byte randomize() {
			return RandomUtils.randomByte(from, to);
		}

	};

	public static class MockShort implements MockType<Short> {

		private final short from;
		private final short to;

		public MockShort(short from, short to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type getType() {
			return Short.class;
		}

		@Override
		public Short randomize() {
			return RandomUtils.randomShort(from, to);
		}

	};

	public static class MockInteger implements MockType<Integer> {

		private final int from;
		private final int to;

		public MockInteger(int from, int to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type getType() {
			return Integer.class;
		}

		@Override
		public Integer randomize() {
			return RandomUtils.randomInt(from, to);
		}

	};

	public static class MockLong implements MockType<Long> {

		private final long from;
		private final long to;

		public MockLong(long from, long to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type getType() {
			return Long.class;
		}

		@Override
		public Long randomize() {
			return RandomUtils.randomLong(from, to);
		}

	};

	public static class MockFloat implements MockType<Float> {

		private final long from;
		private final long to;

		public MockFloat(long from, long to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type getType() {
			return Float.class;
		}

		@Override
		public Float randomize() {
			return RandomUtils.randomFloat(from, to);
		}

	};

	public static class MockDouble implements MockType<Double> {

		private final long from;
		private final long to;

		public MockDouble(long from, long to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type getType() {
			return Double.class;
		}

		@Override
		public Double randomize() {
			return RandomUtils.randomDouble(from, to);
		}

	};

	public static class MockBoolean implements MockType<Boolean> {

		@Override
		public Type getType() {
			return Boolean.class;
		}

		@Override
		public Boolean randomize() {
			return RandomUtils.randomBoolean();
		}

	};

	public static class MockCharacter implements MockType<Character> {

		private final boolean digits;
		private final boolean lowerCaseLetters;
		private final boolean upperCaseLetters;

		public MockCharacter(boolean digits, boolean lowerCaseLetters, boolean upperCaseLetters) {
			this.digits = digits;
			this.lowerCaseLetters = lowerCaseLetters;
			this.upperCaseLetters = upperCaseLetters;
		}

		@Override
		public Type getType() {
			return Character.class;
		}

		@Override
		public Character randomize() {
			return RandomStringUtils.randomString(1, digits, lowerCaseLetters, upperCaseLetters).charAt(0);
		}

	}

	public static class MockLocalDate implements MockType<LocalDate> {

		private final Year year;
		private final Month month;

		public MockLocalDate(Year year, Month month) {
			this.year = year;
			this.month = month;
		}

		@Override
		public Type getType() {
			return LocalDate.class;
		}

		@Override
		public LocalDate randomize() {
			return RandomUtils.randomLocalDate(year, month);
		}

	}

	public static class MockLocalDateTime implements MockType<LocalDateTime> {

		private final Year year;
		private final Month month;
		private final int dayOfMonth;

		public MockLocalDateTime(Year year, Month month, int dayOfMonth) {
			this.year = year;
			this.month = month;
			this.dayOfMonth = dayOfMonth;
		}

		@Override
		public Type getType() {
			return LocalDateTime.class;
		}

		@Override
		public LocalDateTime randomize() {
			return RandomUtils.randomLocalDateTime(year, month, dayOfMonth);
		}

	}

	public static class MockLocalTime implements MockType<LocalTime> {

		@Override
		public Type getType() {
			return LocalTime.class;
		}

		@Override
		public LocalTime randomize() {
			return RandomUtils.randomLocalTime();
		}

	}

	public static class MockDate implements MockType<Date> {

		private final int year;
		private final int month;

		public MockDate(int year, int month) {
			this.year = year;
			this.month = month;
		}

		@Override
		public Type getType() {
			return Date.class;
		}

		@Override
		public Date randomize() {
			if (year < 0 && month < 0) {
				return RandomUtils.randomDate();
			} else if (year > 0 && month < 0) {
				return RandomUtils.randomDate(year);
			} else if (year > 0 && month > 0) {
				return RandomUtils.randomDate(year, month);
			}
			throw new IllegalArgumentException("Year: " + year + ", Month: " + month);
		}

	}

	public static class MockDateTime implements MockType<Date> {

		private final int year;
		private final int month;
		private final int dayOfMonth;

		public MockDateTime(int year, int month, int dayOfMonth) {
			this.year = year;
			this.month = month;
			this.dayOfMonth = dayOfMonth;
		}

		@Override
		public Type getType() {
			return Date.class;
		}

		@Override
		public Date randomize() {
			if (year < 0 && month < 0 && dayOfMonth < 0) {
				return RandomUtils.randomDateTime();
			} else if (year > 0 && month < 0 && dayOfMonth < 0) {
				return RandomUtils.randomDateTime(year);
			} else if (year > 0 && month > 0 && dayOfMonth < 0) {
				return RandomUtils.randomDateTime(year, month);
			} else if (year > 0 && month > 0 && dayOfMonth > 0) {
				return RandomUtils.randomDateTime(year, month, dayOfMonth);
			}
			throw new IllegalArgumentException("Year: " + year + ", Month: " + month + ", DayOfMonth: " + dayOfMonth);
		}

	}

	public static class MockBigDecimal implements MockType<BigDecimal> {

		private final int position;
		private final int scale;

		public MockBigDecimal(int position, int scale) {
			this.position = position;
			this.scale = scale;
		}

		@Override
		public Type getType() {
			return BigDecimal.class;
		}

		@Override
		public BigDecimal randomize() {
			return RandomUtils.randomBigDecimal(position, scale);
		}

	}

	public static class MockBigInteger implements MockType<BigInteger> {

		private final int position;

		public MockBigInteger(int position) {
			this.position = position;
		}

		@Override
		public Type getType() {
			return BigInteger.class;
		}

		@Override
		public BigInteger randomize() {
			return RandomUtils.randomBigInteger(position);
		}

	}

	public static class MockString implements MockType<String> {

		private final int length;
		private final String prefix;
		private final String suffix;
		private final boolean digits;
		private final boolean lowerCaseLetters;
		private final boolean upperCaseLetters;

		public MockString(int length) {
			this(length, true, true, true);
		}

		public MockString(int length, String prefix, String suffix) {
			this(length, prefix, suffix, true, true, true);
		}

		public MockString(int length, boolean digits, boolean lowerCaseLetters, boolean upperCaseLetters) {
			this(length, "", "", digits, lowerCaseLetters, upperCaseLetters);
		}

		public MockString(int length, String prefix, String suffix, boolean digits, boolean lowerCaseLetters, boolean upperCaseLetters) {
			this.length = length;
			this.prefix = prefix;
			this.suffix = suffix;
			this.digits = digits;
			this.lowerCaseLetters = lowerCaseLetters;
			this.upperCaseLetters = upperCaseLetters;
		}

		@Override
		public Type getType() {
			return String.class;
		}

		@Override
		public String randomize() {
			int remaining = this.length - (StringUtils.isNotBlank(prefix) ? prefix.length() : 0)
					- (StringUtils.isNotBlank(suffix) ? suffix.length() : 0);
			return new StringBuilder(this.length).append(prefix)
					.append(RandomStringUtils.randomString(remaining, digits, lowerCaseLetters, upperCaseLetters)).append(suffix)
					.toString();
		}
	}

	private static final Map<Type, MockType<?>> types = new HashMap<Type, MockType<?>>();

	static {
		setMockType(new MockBoolean());
		setMockType(new MockCharacter(true, true, true));
		setMockType(new MockByte((byte) 0, Byte.MAX_VALUE));
		setMockType(new MockShort((short) 0, Short.MAX_VALUE));
		setMockType(new MockInteger(0, Integer.MAX_VALUE));
		setMockType(new MockLong(0L, Long.MAX_VALUE));
		setMockType(new MockFloat(0L, Integer.MAX_VALUE));
		setMockType(new MockDouble(0L, Long.MAX_VALUE));
		setMockType(new MockBigInteger(11));
		setMockType(new MockBigDecimal(11, 2));
		setMockType(new MockDate(-1, -1));
		setMockType(new MockDateTime(-1, -1, -1));
		setMockType(new MockString(32));
	}

	public static <T> void setMockType(MockType<T> mockType) {
		if (mockType != null) {
			types.put(mockType.getType(), mockType);
		}
	}

	public static MockType<?> getMockType(Type propertyType) {
		return types.get(propertyType);
	}

}
