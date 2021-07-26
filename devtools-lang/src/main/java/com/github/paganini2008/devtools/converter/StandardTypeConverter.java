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
package com.github.paganini2008.devtools.converter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TypeConverter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
@SuppressWarnings("unchecked")
public class StandardTypeConverter implements TypeConverter {

	private static final Map<Type, BasicConverter<?>> preparedSettings = new HashMap<Type, BasicConverter<?>>() {

		private static final long serialVersionUID = 1L;

		{
			BooleanConverter booleanHandler = new BooleanConverter();
			put(boolean.class, booleanHandler);
			put(Boolean.class, booleanHandler);

			CharacterConverter characterHandler = new CharacterConverter();
			put(char.class, characterHandler);
			put(Character.class, characterHandler);
			put(Character[].class, new CharacterObjectArrayConverter());
			put(char[].class, new CharArrayConverter());

			ByteConverter byteHandler = new ByteConverter();
			put(byte.class, byteHandler);
			put(Byte.class, byteHandler);
			put(Byte[].class, new ByteObjectArrayConverter());
			put(byte[].class, new ByteArrayConverter());

			ShortConverter shortHandler = new ShortConverter();
			put(short.class, shortHandler);
			put(Short.class, shortHandler);
			put(Short[].class, new ShortObjectArrayConverter());
			put(short[].class, new ShortArrayConverter());

			IntegerConverter integerHandler = new IntegerConverter();
			put(int.class, integerHandler);
			put(Integer.class, integerHandler);
			put(Integer[].class, new IntegerObjectArrayConverter());
			put(int[].class, new IntArrayConverter());

			FloatConverter floatHandler = new FloatConverter();
			put(float.class, floatHandler);
			put(Float.class, floatHandler);
			put(Float[].class, new FloatObjectArrayConverter());
			put(float[].class, new FloatArrayConverter());

			DoubleConverter doubleHandler = new DoubleConverter();
			put(double.class, doubleHandler);
			put(Double.class, doubleHandler);
			put(Double[].class, new DoubleObjectArrayConverter());
			put(double[].class, new DoubleArrayConverter());

			LongConverter longHandler = new LongConverter();
			put(Long.class, longHandler);
			put(long.class, longHandler);
			put(Long[].class, new LongObjectArrayConverter());
			put(long[].class, new LongArrayConverter());

			put(BigDecimal.class, new BigDecimaConverter());
			put(BigDecimal[].class, new BigDecimalArrayConverter());

			put(BigInteger.class, new BigIntegerConverter());
			put(BigInteger[].class, new BigIntegerArrayConverter());

			put(Date.class, new DateConverter());
			put(Calendar.class, new CalendarConverter());

			put(String.class, new StringConverter());
			put(String[].class, new StringArrayConverter());

			put(Charset.class, new CharsetConverter());
			put(UUID.class, new UUIDConverter());
			put(Locale.class, new LocaleConverter());

			put(LocalDate.class, new LocalDateConverter());
			put(LocalTime.class, new LocalTimeConverter());
			put(LocalDateTime.class, new LocalDateTimeConverter());
		}
	};

	private final Map<Type, BasicConverter<?>> converters;

	public StandardTypeConverter() {
		this.converters = new ConcurrentHashMap<Type, BasicConverter<?>>(preparedSettings);
	}

	public <T> void registerType(Class<T> javaType, BasicConverter<T> converter) {
		converters.put(javaType, converter);
	}

	public void removeType(Class<?> javaType) {
		converters.remove(javaType);
	}

	public boolean hasType(Class<?> javaType) {
		return converters.containsKey(javaType);
	}

	public <T> BasicConverter<T> lookupType(Class<T> javaType) {
		return (BasicConverter<T>) converters.get(javaType);
	}

	public <T> T convertValue(Object value, Class<T> requiredType, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		try {
			return requiredType.cast(value);
		} catch (ClassCastException ignored) {
		}
		BasicConverter<T> converter = (BasicConverter<T>) lookupType(requiredType);
		if (converter != null) {
			return converter.convertValue(value, defaultValue);
		}
		return defaultValue;
	}
}
