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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.paganini2008.devtools.RandomDateUtils;
import com.github.paganini2008.devtools.RandomStringUtils;
import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.math.BigDecimalUtils;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;

/**
 * 
 * MockTypes
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class MockTypes {

	public static class MockChoiceByte implements MockType<Byte> {

		private final byte[] choice;

		public MockChoiceByte(byte[] choice) {
			this.choice = choice;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Byte.class, byte.class };
		}

		@Override
		public Byte randomize() {
			return RandomUtils.randomChoice(choice);
		}
	}

	public static class MockRangeByte implements MockType<Byte> {

		private final byte from;
		private final byte to;

		public MockRangeByte(byte from, byte to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Byte.class, byte.class };
		}

		@Override
		public Byte randomize() {
			return RandomUtils.randomByte(from, to);
		}

	};

	public static class MockChoiceShort implements MockType<Short> {

		private final short[] choice;

		public MockChoiceShort(short[] choice) {
			this.choice = choice;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Short.class, short.class };
		}

		@Override
		public Short randomize() {
			return RandomUtils.randomChoice(choice);
		}

	};

	public static class MockRangeShort implements MockType<Short> {

		private final short from;
		private final short to;

		public MockRangeShort(short from, short to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Short.class, short.class };
		}

		@Override
		public Short randomize() {
			return RandomUtils.randomShort(from, to);
		}

	};

	public static class MockChoiceInteger implements MockType<Integer> {

		private final int[] choice;

		public MockChoiceInteger(int[] choice) {
			this.choice = choice;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Integer.class, int.class };
		}

		@Override
		public Integer randomize() {
			return RandomUtils.randomChoice(choice);
		}

	};

	public static class MockRangeInteger implements MockType<Integer> {

		private final int from;
		private final int to;

		public MockRangeInteger(int from, int to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Integer.class, int.class };
		}

		@Override
		public Integer randomize() {
			return RandomUtils.randomInt(from, to);
		}

	};

	public static class MockInteger implements MockType<Integer> {

		private final int precision;

		public MockInteger(int precision) {
			this.precision = precision;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Integer.class, int.class };
		}

		@Override
		public Integer randomize() {
			return RandomUtils.defineInt(precision);
		}
	}

	public static class MockLong implements MockType<Long> {

		private final int precision;

		public MockLong(int precision) {
			this.precision = precision;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Long.class, long.class };
		}

		@Override
		public Long randomize() {
			return RandomUtils.defineLong(precision);
		}

	};

	public static class MockChoiceLong implements MockType<Long> {

		private final long[] choice;

		public MockChoiceLong(long[] choice) {
			this.choice = choice;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Long.class, long.class };
		}

		@Override
		public Long randomize() {
			return RandomUtils.randomChoice(choice);
		}

	};

	public static class MockRangeLong implements MockType<Long> {

		private final long from;
		private final long to;

		public MockRangeLong(long from, long to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Long.class, long.class };
		}

		@Override
		public Long randomize() {
			return RandomUtils.randomLong(from, to);
		}

	};

	public static class MockFloat implements MockType<Float> {

		private final int precision;
		private final int scale;

		public MockFloat(int precision, int scale) {
			this.precision = precision;
			this.scale = scale;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Float.class, float.class };
		}

		@Override
		public Float randomize() {
			return RandomUtils.defineFloat(precision, scale);
		}

	};

	public static class MockChoiceFloat implements MockType<Float> {

		private final float[] choice;

		public MockChoiceFloat(float[] choice) {
			this.choice = choice;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Float.class, float.class };
		}

		@Override
		public Float randomize() {
			return RandomUtils.randomChoice(choice);
		}

	};

	public static class MockRangeFloat implements MockType<Float> {

		private final long from;
		private final long to;
		private int scale;

		public MockRangeFloat(long from, long to, int scale) {
			this.from = from;
			this.to = to;
			this.scale = scale;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Float.class, float.class };
		}

		@Override
		public Float randomize() {
			float f = RandomUtils.randomFloat(from, to);
			return scale >= 0 ? Floats.toFixed(f, scale) : f;
		}

	};

	public static class MockDouble implements MockType<Double> {

		private final int precision;
		private final int scale;

		public MockDouble(int precision, int scale) {
			this.precision = precision;
			this.scale = scale;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Double.class, double.class };
		}

		@Override
		public Double randomize() {
			return RandomUtils.defineDouble(precision, scale);
		}

	};

	public static class MockChoiceDouble implements MockType<Double> {

		private final double[] choice;

		public MockChoiceDouble(double[] choice) {
			this.choice = choice;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Double.class, double.class };
		}

		@Override
		public Double randomize() {
			return RandomUtils.randomChoice(choice);
		}

	};

	public static class MockRangeDouble implements MockType<Double> {

		private final long from;
		private final long to;
		private final int scale;

		public MockRangeDouble(long from, long to, int scale) {
			this.from = from;
			this.to = to;
			this.scale = scale;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Double.class, double.class };
		}

		@Override
		public Double randomize() {
			double d = RandomUtils.randomDouble(from, to);
			return scale >= 0 ? Doubles.toFixed(d, scale) : d;
		}

	};

	public static class MockBoolean implements MockType<Boolean> {

		@Override
		public Type[] getTypes() {
			return new Type[] { Boolean.class, boolean.class };
		}

		@Override
		public Boolean randomize() {
			return RandomUtils.randomBoolean();
		}

	};

	public static class MockChoiceCharacter implements MockType<Character> {

		private final char[] choice;

		public MockChoiceCharacter(char[] choice) {
			this.choice = choice;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { Character.class, char.class };
		}

		@Override
		public Character randomize() {
			return RandomUtils.randomChoice(choice);
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
		public Type[] getTypes() {
			return new Type[] { Character.class, char.class };
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
		public Type[] getTypes() {
			return new Type[] { LocalDate.class };
		}

		@Override
		public LocalDate randomize() {
			return RandomDateUtils.randomLocalDate(year, month);
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
		public Type[] getTypes() {
			return new Type[] { LocalDateTime.class };
		}

		@Override
		public LocalDateTime randomize() {
			return RandomDateUtils.randomLocalDateTime(year, month, dayOfMonth);
		}

	}

	public static class MockLocalTime implements MockType<LocalTime> {

		@Override
		public Type[] getTypes() {
			return new Type[] { LocalTime.class };
		}

		@Override
		public LocalTime randomize() {
			return RandomDateUtils.randomLocalTime();
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
		public Type[] getTypes() {
			return new Type[] { Date.class };
		}

		@Override
		public Date randomize() {
			if (year < 0 && month < 0) {
				return RandomDateUtils.randomDate();
			} else if (year > 0 && month < 0) {
				return RandomDateUtils.randomDate(year);
			} else if (year > 0 && month > 0) {
				return RandomDateUtils.randomDate(year, month);
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
		public Type[] getTypes() {
			return new Type[] { Date.class };
		}

		@Override
		public Date randomize() {
			if (year < 0 && month < 0 && dayOfMonth < 0) {
				return RandomDateUtils.randomDateTime();
			} else if (year > 0 && month < 0 && dayOfMonth < 0) {
				return RandomDateUtils.randomDateTime(year);
			} else if (year > 0 && month > 0 && dayOfMonth < 0) {
				return RandomDateUtils.randomDateTime(year, month);
			} else if (year > 0 && month > 0 && dayOfMonth > 0) {
				return RandomDateUtils.randomDateTime(year, month, dayOfMonth);
			}
			throw new IllegalArgumentException("Year: " + year + ", Month: " + month + ", DayOfMonth: " + dayOfMonth);
		}

	}

	public static class MockChoiceBigDecimal implements MockType<BigDecimal> {

		private final BigDecimal[] choice;

		public MockChoiceBigDecimal(BigDecimal[] choice) {
			this.choice = choice;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { BigDecimal.class };
		}

		@Override
		public BigDecimal randomize() {
			return RandomUtils.randomChoice(choice);
		}

	};

	public static class MockRangeBigDecimal implements MockType<BigDecimal> {

		private final BigDecimal from;
		private final BigDecimal to;
		private final int scale;

		public MockRangeBigDecimal(double from, double to, int scale) {
			this(BigDecimal.valueOf(from), BigDecimal.valueOf(to), scale);
		}

		public MockRangeBigDecimal(BigDecimal from, BigDecimal to, int scale) {
			this.from = from;
			this.to = to;
			this.scale = scale;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { BigDecimal.class };
		}

		@Override
		public BigDecimal randomize() {
			BigDecimal value = RandomUtils.randomBigDecimal(from, to);
			return BigDecimalUtils.setScale(value, scale, RoundingMode.HALF_UP);
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
		public Type[] getTypes() {
			return new Type[] { BigDecimal.class };
		}

		@Override
		public BigDecimal randomize() {
			return RandomUtils.defineBigDecimal(position, scale);
		}

	}

	public static class MockChoiceBigInteger implements MockType<BigInteger> {

		private final BigInteger[] choice;

		public MockChoiceBigInteger(BigInteger[] choice) {
			this.choice = choice;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { BigInteger.class };
		}

		@Override
		public BigInteger randomize() {
			return RandomUtils.randomChoice(choice);
		}

	};

	public static class MockRangeBigInteger implements MockType<BigInteger> {

		private final BigInteger from;
		private final BigInteger to;

		public MockRangeBigInteger(long from, long to) {
			this(BigInteger.valueOf(from), BigInteger.valueOf(to));
		}

		public MockRangeBigInteger(BigInteger from, BigInteger to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { BigInteger.class };
		}

		@Override
		public BigInteger randomize() {
			return RandomUtils.randomBigInteger(from, to);
		}

	}

	public static class MockBigInteger implements MockType<BigInteger> {

		private final int position;

		public MockBigInteger(int position) {
			this.position = position;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { BigInteger.class };
		}

		@Override
		public BigInteger randomize() {
			return RandomUtils.defineBigInteger(position);
		}

	}

	public static class MockChoiceString implements MockType<String> {

		private final String[] choice;

		public MockChoiceString(String[] choice) {
			this.choice = choice;
		}

		@Override
		public Type[] getTypes() {
			return new Type[] { String.class };
		}

		@Override
		public String randomize() {
			return RandomUtils.randomChoice(choice);
		}

	};

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
		public Type[] getTypes() {
			return new Type[] { String.class };
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
		setMockType(new MockRangeByte((byte) 0, Byte.MAX_VALUE));
		setMockType(new MockRangeShort((short) 0, Short.MAX_VALUE));
		setMockType(new MockRangeInteger(0, Integer.MAX_VALUE));
		setMockType(new MockRangeLong(0L, Long.MAX_VALUE));
		setMockType(new MockFloat(10, 2));
		setMockType(new MockDouble(10, 2));
		setMockType(new MockBigInteger(11));
		setMockType(new MockBigDecimal(11, 2));
		setMockType(new MockDate(-1, -1));
		setMockType(new MockDateTime(-1, -1, -1));
		setMockType(new MockString(32));
	}

	public static <T> void setMockType(MockType<T> mockType) {
		if (mockType != null) {
			for (Type type : mockType.getTypes()) {
				types.put(type, mockType);
			}
		}
	}

	public static MockType<?> getMockType(Type propertyType) {
		return types.get(propertyType);
	}

}
