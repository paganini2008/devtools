package com.github.paganini2008.devtools.converter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.github.paganini2008.devtools.ClassUtils;

/**
 * TypeConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class StandardTypeConverter implements TypeConverter {

	private static final Map<Type, BaseConverter<?>> preparedConverters = new HashMap<Type, BaseConverter<?>>() {

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

			StringConverter stringHandler = new StringConverter();
			put(String.class, stringHandler);
			put(String[].class, new StringArrayConverter(stringHandler));

			put(Charset.class, new CharsetConverter());
			put(UUID.class, new UUIDConverter());
			put(Locale.class, new LocaleConverter());
		}
	};

	public static final TypeConverter DEFALT = new StandardTypeConverter();

	private final Map<Type, BaseConverter<?>> converters;

	public StandardTypeConverter() {
		this.converters = new HashMap<Type, BaseConverter<?>>(preparedConverters);
	}

	public <T> void register(Class<T> javaType, BaseConverter<T> converter) {
		converters.put(javaType, converter);
	}

	public void remove(Class<?> javaType) {
		converters.remove(javaType);
	}

	public boolean contains(Class<?> javaType) {
		return converters.containsKey(javaType);
	}

	public <T> BaseConverter<T> lookup(Class<T> javaType) {
		return (BaseConverter<T>) converters.get(javaType);
	}

	public <T> T convert(Object value, Class<T> requiredType, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (requiredType.isPrimitive()) {
			requiredType = (Class<T>) ClassUtils.toWrapper(requiredType);
		}
		try {
			return requiredType.cast(value);
		} catch (RuntimeException e) {
		}
		BaseConverter<T> converter = (BaseConverter<T>) lookup(requiredType);
		if (converter != null) {
			return converter.getValue(value, defaultValue);
		}
		return defaultValue;
	}
}
