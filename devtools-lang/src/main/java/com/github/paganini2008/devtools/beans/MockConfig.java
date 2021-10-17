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
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.paganini2008.devtools.RandomStringUtils;
import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * MockConfig
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public class MockConfig {

	public interface MockType<T> {

		Type getType();

		T randomize();

		static final MockType<Byte> BYTE = new MockType<Byte>() {

			@Override
			public Type getType() {
				return Byte.class;
			}

			@Override
			public Byte randomize() {
				return RandomUtils.randomByte(Byte.MIN_VALUE, Byte.MAX_VALUE);
			}

		};

		static final MockType<Short> SHORT = new MockType<Short>() {

			@Override
			public Type getType() {
				return Short.class;
			}

			@Override
			public Short randomize() {
				return RandomUtils.randomShort(Short.MIN_VALUE, Short.MAX_VALUE);
			}

		};

		static final MockType<Integer> INTEGER = new MockType<Integer>() {

			@Override
			public Type getType() {
				return Integer.class;
			}

			@Override
			public Integer randomize() {
				return RandomUtils.randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
			}

		};

		static final MockType<Long> LONG = new MockType<Long>() {

			@Override
			public Type getType() {
				return Integer.class;
			}

			@Override
			public Long randomize() {
				return RandomUtils.randomLong(Long.MIN_VALUE, Long.MAX_VALUE);
			}

		};

		static final MockType<Float> FLOAT = new MockType<Float>() {

			@Override
			public Type getType() {
				return Float.class;
			}

			@Override
			public Float randomize() {
				return RandomUtils.randomFloat(Long.MIN_VALUE, Long.MAX_VALUE);
			}

		};

		static final MockType<Double> DOUBLE = new MockType<Double>() {

			@Override
			public Type getType() {
				return Double.class;
			}

			@Override
			public Double randomize() {
				return RandomUtils.randomDouble(Long.MIN_VALUE, Long.MAX_VALUE);
			}

		};

		static final MockType<Boolean> BOOLEAN = new MockType<Boolean>() {

			@Override
			public Type getType() {
				return Boolean.class;
			}

			@Override
			public Boolean randomize() {
				return RandomUtils.randomBoolean();
			}

		};

		static final MockType<Character> CHARACTER = new MockType<Character>() {

			@Override
			public Type getType() {
				return Character.class;
			}

			@Override
			public Character randomize() {
				return RandomStringUtils.randomString(1).charAt(0);
			}

		};

	}

	public static class MockLocalDate implements MockType<LocalDate> {

		@Override
		public Type getType() {
			return LocalDate.class;
		}

		@Override
		public LocalDate randomize() {
			return RandomUtils.randomDate();
		}

	}

	public static class MockLocalDateTime implements MockType<LocalDateTime> {

		@Override
		public Type getType() {
			return LocalDateTime.class;
		}

		@Override
		public LocalDateTime randomize() {
			return RandomUtils.randomDateTime();
		}

	}

	public static class MockLocalTime implements MockType<LocalTime> {

		@Override
		public Type getType() {
			return LocalTime.class;
		}

		@Override
		public LocalTime randomize() {
			return RandomUtils.randomTime();
		}

	}

	public static class MockDate implements MockType<Date> {

		@Override
		public Type getType() {
			return Date.class;
		}

		@Override
		public Date randomize() {
			return DateUtils.toDate(RandomUtils.randomDateTime(), ZoneId.systemDefault());
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
		private boolean digits;
		private boolean lowerCaseLetters;
		private boolean upperCaseLetters;

		public MockString(int length) {
			this(length, true, true, true);
		}

		public MockString(int length, String prefix, String suffix) {
			this(length, prefix, suffix, true, true, true);
		}

		public MockString(int length, boolean digits, boolean lowerCaseLetters, boolean upperCaseLetters) {
			this(length, null, null, digits, lowerCaseLetters, upperCaseLetters);
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

	private final Map<Type, MockType<?>> suppliers = new HashMap<>();

	public MockConfig() {
		addMockType(MockType.BYTE);
		addMockType(MockType.SHORT);
		addMockType(MockType.INTEGER);
		addMockType(MockType.LONG);
		addMockType(MockType.FLOAT);
		addMockType(MockType.DOUBLE);
		addMockType(MockType.BOOLEAN);
		addMockType(MockType.CHARACTER);
		addMockType(new MockLocalDate());
		addMockType(new MockLocalDateTime());
		addMockType(new MockLocalTime());
		addMockType(new MockDate());
		addMockType(new MockBigDecimal(11, 2));
		addMockType(new MockBigInteger(11));
		addMockType(new MockString(32));
	}

	public <T> void addMockType(MockType<T> mockType) {
		if (mockType != null) {
			suppliers.put(mockType.getType(), mockType);
		}
	}

	protected MockType<?> getMockType(Class<?> propertyType) {
		return suppliers.get(propertyType);
	}

	protected boolean recurs(Class<?> propertyType) {
		return false;
	}

	protected <T> void assignManually(T bean) {
	}

}
