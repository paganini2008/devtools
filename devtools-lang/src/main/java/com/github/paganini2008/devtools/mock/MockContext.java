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
package com.github.paganini2008.devtools.mock;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.github.paganini2008.devtools.mock.MockTypeHandlers.BigDecimalTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.BigIntegerTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.BooleanTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.ByteTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.CharacterTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.DateTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.DoubleTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.EnumTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.FloatTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.IntegerTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.LocalDateTimeTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.LocalDateTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.LocalTimeTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.LongTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.ShortTypeHandler;
import com.github.paganini2008.devtools.mock.MockTypeHandlers.StringTypeHandler;
import com.github.paganini2008.devtools.time.DateUtils;

/**
 * 
 * MockContext
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public final class MockContext {

	private final List<MockTypeHandler> typeHandlers = new ArrayList<MockTypeHandler>();
	private final Map<String, Supplier<String>> stringSupplier = new HashMap<String, Supplier<String>>();
	private final Map<String, Supplier<Integer>> integerSupplier = new HashMap<String, Supplier<Integer>>();
	private final Map<String, Supplier<Long>> longSupplier = new HashMap<String, Supplier<Long>>();
	private final Map<String, Supplier<Float>> floatSupplier = new HashMap<String, Supplier<Float>>();
	private final Map<String, Supplier<Double>> doubleSupplier = new HashMap<String, Supplier<Double>>();
	private final Map<String, Supplier<BigInteger>> bigIntegerSupplier = new HashMap<String, Supplier<BigInteger>>();
	private final Map<String, Supplier<BigDecimal>> bigDecimalSupplier = new HashMap<String, Supplier<BigDecimal>>();
	private final Map<String, Supplier<Date>> dateSupplier = new HashMap<String, Supplier<Date>>();
	private final Map<String, Supplier<LocalDate>> localDateSupplier = new HashMap<String, Supplier<LocalDate>>();
	private final Map<String, Supplier<LocalTime>> localTimeSupplier = new HashMap<String, Supplier<LocalTime>>();
	private final Map<String, Supplier<LocalDateTime>> localDateTimeSupplier = new HashMap<String, Supplier<LocalDateTime>>();

	public MockContext() {
		initialize();
		reset();
	}

	public static class RandomConfig {

		private byte maxByteValue = Byte.MAX_VALUE;
		private byte minByteValue = Byte.MIN_VALUE;
		private short maxShortValue = Short.MAX_VALUE;
		private short minShortValue = Short.MIN_VALUE;
		private int maxIntValue = Integer.MAX_VALUE;
		private int minIntValue = Integer.MIN_VALUE;
		private long maxLongValue = Long.MAX_VALUE;
		private long minLongValue = Long.MIN_VALUE;
		private float maxFloatValue = Float.MAX_VALUE;
		private float minFloatValue = Float.MIN_VALUE;
		private double maxDoubleValue = Double.MAX_VALUE;
		private double minDoubleValue = Double.MIN_VALUE;
		private char maxCharValue = Character.MAX_VALUE;
		private char minCharValue = Character.MIN_VALUE;

		public byte getMaxByteValue() {
			return maxByteValue;
		}

		public void setMaxByteValue(byte maxByteValue) {
			this.maxByteValue = maxByteValue;
		}

		public byte getMinByteValue() {
			return minByteValue;
		}

		public void setMinByteValue(byte minByteValue) {
			this.minByteValue = minByteValue;
		}

		public short getMaxShortValue() {
			return maxShortValue;
		}

		public void setMaxShortValue(short maxShortValue) {
			this.maxShortValue = maxShortValue;
		}

		public short getMinShortValue() {
			return minShortValue;
		}

		public void setMinShortValue(short minShortValue) {
			this.minShortValue = minShortValue;
		}

		public int getMaxIntValue() {
			return maxIntValue;
		}

		public void setMaxIntValue(int maxIntValue) {
			this.maxIntValue = maxIntValue;
		}

		public int getMinIntValue() {
			return minIntValue;
		}

		public void setMinIntValue(int minIntValue) {
			this.minIntValue = minIntValue;
		}

		public long getMaxLongValue() {
			return maxLongValue;
		}

		public void setMaxLongValue(long maxLongValue) {
			this.maxLongValue = maxLongValue;
		}

		public long getMinLongValue() {
			return minLongValue;
		}

		public void setMinLongValue(long minLongValue) {
			this.minLongValue = minLongValue;
		}

		public float getMaxFloatValue() {
			return maxFloatValue;
		}

		public void setMaxFloatValue(float maxFloatValue) {
			this.maxFloatValue = maxFloatValue;
		}

		public float getMinFloatValue() {
			return minFloatValue;
		}

		public void setMinFloatValue(float minFloatValue) {
			this.minFloatValue = minFloatValue;
		}

		public double getMaxDoubleValue() {
			return maxDoubleValue;
		}

		public void setMaxDoubleValue(double maxDoubleValue) {
			this.maxDoubleValue = maxDoubleValue;
		}

		public double getMinDoubleValue() {
			return minDoubleValue;
		}

		public void setMinDoubleValue(double minDoubleValue) {
			this.minDoubleValue = minDoubleValue;
		}

		public char getMaxCharValue() {
			return maxCharValue;
		}

		public void setMaxCharValue(char maxCharValue) {
			this.maxCharValue = maxCharValue;
		}

		public char getMinCharValue() {
			return minCharValue;
		}

		public void setMinCharValue(char minCharValue) {
			this.minCharValue = minCharValue;
		}

	}

	public static class RandomDateConfig {
		private int fromYear = DateUtils.MIN_YEAR;
		private int toYear = DateUtils.getYear();
		private int fromMonth = 1;
		private int toMonth = 12;
		private int fromDayOfMonth = 1;
		private int toDayOfMonth = 31;
		private int fromHourOfDay = 0;
		private int toHourOfDay = 23;
		private int fromMinute = 0;
		private int toMinute = 59;
		private int fromSecond = 0;
		private int toSecond = 59;

		public int getFromYear() {
			return fromYear;
		}

		public void setFromYear(int fromYear) {
			this.fromYear = fromYear;
		}

		public int getToYear() {
			return toYear;
		}

		public void setToYear(int toYear) {
			this.toYear = toYear;
		}

		public int getFromMonth() {
			return fromMonth;
		}

		public void setFromMonth(int fromMonth) {
			this.fromMonth = fromMonth;
		}

		public int getToMonth() {
			return toMonth;
		}

		public void setToMonth(int toMonth) {
			this.toMonth = toMonth;
		}

		public int getFromDayOfMonth() {
			return fromDayOfMonth;
		}

		public void setFromDayOfMonth(int fromDayOfMonth) {
			this.fromDayOfMonth = fromDayOfMonth;
		}

		public int getToDayOfMonth() {
			return toDayOfMonth;
		}

		public void setToDayOfMonth(int toDayOfMonth) {
			this.toDayOfMonth = toDayOfMonth;
		}

		public int getFromHourOfDay() {
			return fromHourOfDay;
		}

		public void setFromHourOfDay(int fromHourOfDay) {
			this.fromHourOfDay = fromHourOfDay;
		}

		public int getToHourOfDay() {
			return toHourOfDay;
		}

		public void setToHourOfDay(int toHourOfDay) {
			this.toHourOfDay = toHourOfDay;
		}

		public int getFromMinute() {
			return fromMinute;
		}

		public void setFromMinute(int fromMinute) {
			this.fromMinute = fromMinute;
		}

		public int getToMinute() {
			return toMinute;
		}

		public void setToMinute(int toMinute) {
			this.toMinute = toMinute;
		}

		public int getFromSecond() {
			return fromSecond;
		}

		public void setFromSecond(int fromSecond) {
			this.fromSecond = fromSecond;
		}

		public int getToSecond() {
			return toSecond;
		}

		public void setToSecond(int toSecond) {
			this.toSecond = toSecond;
		}

	}

	public static class RandomStringConfig {

		private int length = 32;
		private String prefix = "";
		private String suffix = "";
		private boolean digit = true;
		private boolean lowerCaseLetter = true;
		private boolean upperCaseLetter = true;

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getSuffix() {
			return suffix;
		}

		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}

		public boolean isDigit() {
			return digit;
		}

		public void setDigit(boolean digit) {
			this.digit = digit;
		}

		public boolean isLowerCaseLetter() {
			return lowerCaseLetter;
		}

		public void setLowerCaseLetter(boolean lowerCaseLetter) {
			this.lowerCaseLetter = lowerCaseLetter;
		}

		public boolean isUpperCaseLetter() {
			return upperCaseLetter;
		}

		public void setUpperCaseLetter(boolean upperCaseLetter) {
			this.upperCaseLetter = upperCaseLetter;
		}
	}

	protected void initialize() {
		addLast(new StringTypeHandler());
		addLast(new BigDecimalTypeHandler());
		addLast(new BigIntegerTypeHandler());
		addLast(new DateTypeHandler());
		addLast(new LocalDateTypeHandler());
		addLast(new LocalDateTimeTypeHandler());
		addLast(new LocalTimeTypeHandler());
		addLast(new LongTypeHandler());
		addLast(new IntegerTypeHandler());
		addLast(new ShortTypeHandler());
		addLast(new ByteTypeHandler());
		addLast(new FloatTypeHandler());
		addLast(new DoubleTypeHandler());
		addLast(new BooleanTypeHandler());
		addLast(new CharacterTypeHandler());
		addLast(new EnumTypeHandler());
	}

	private RandomConfig randomConfig = new RandomConfig();
	private RandomDateConfig randomDateConfig = new RandomDateConfig();
	private RandomStringConfig randomStringConfig = new RandomStringConfig();

	public RandomConfig getRandomConfig() {
		return randomConfig;
	}

	public void setRandomConfig(RandomConfig randomConfig) {
		this.randomConfig = randomConfig;
	}

	public RandomDateConfig getRandomDateConfig() {
		return randomDateConfig;
	}

	public void setRandomDateConfig(RandomDateConfig randomDateConfig) {
		this.randomDateConfig = randomDateConfig;
	}

	public RandomStringConfig getRandomStringConfig() {
		return randomStringConfig;
	}

	public void setRandomStringConfig(RandomStringConfig randomStringConfig) {
		this.randomStringConfig = randomStringConfig;
	}

	public void addFirst(MockTypeHandler typeHandler) {
		typeHandlers.add(0, typeHandler);
		reset();
	}

	public void addLast(MockTypeHandler typeHandler) {
		typeHandlers.add(typeHandler);
	}

	private int index;

	public void reset() {
		this.index = 0;
	}

	public void setStringSupplier(String example, Supplier<String> supplier) {
		stringSupplier.put(example, supplier);
	}

	public Supplier<String> getStringSupplier(String example) {
		return stringSupplier.get(example);
	}

	public void setIntegerSupplier(String example, Supplier<Integer> supplier) {
		integerSupplier.put(example, supplier);
	}

	public Supplier<Integer> getIntegerSupplier(String example) {
		return integerSupplier.get(example);
	}

	public void setLongSupplier(String example, Supplier<Long> supplier) {
		longSupplier.put(example, supplier);
	}

	public Supplier<Long> getLongSupplier(String example) {
		return longSupplier.get(example);
	}

	public void setFloatSupplier(String example, Supplier<Float> supplier) {
		floatSupplier.put(example, supplier);
	}

	public Supplier<Float> getFloatSupplier(String example) {
		return floatSupplier.get(example);
	}

	public void setDoubleSupplier(String example, Supplier<Double> supplier) {
		doubleSupplier.put(example, supplier);
	}

	public Supplier<Double> getDoubleSupplier(String example) {
		return doubleSupplier.get(example);
	}

	public Supplier<BigDecimal> getBigDecimalSupplier(String example) {
		return bigDecimalSupplier.get(example);
	}

	public Supplier<BigInteger> getBigIntegerSupplier(String example) {
		return bigIntegerSupplier.get(example);
	}

	public Supplier<Date> getDateSupplier(String example) {
		return dateSupplier.get(example);
	}

	public Supplier<LocalDate> getLocalDateSupplier(String example) {
		return localDateSupplier.get(example);
	}

	public Supplier<LocalDateTime> getLocalDateTimeSupplier(String example) {
		return localDateTimeSupplier.get(example);
	}

	public Supplier<LocalTime> getLocalTimeSupplier(String example) {
		return localTimeSupplier.get(example);
	}

	protected Object mock(Field field, RandomOperations operations) {
		if (index < typeHandlers.size()) {
			MockTypeHandler typeHandler = typeHandlers.get(index++);
			return typeHandler.apply(field, operations, this);
		}
		return null;
	}

}
