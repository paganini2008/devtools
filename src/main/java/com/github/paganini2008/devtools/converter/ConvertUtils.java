package com.github.paganini2008.devtools.converter;

/**
 * 
 * ConvertUtils
 * 
 * @author Fred Feng
 * @revised 2019-05
 */
public class ConvertUtils {

	private static final TypeConverter INSTANCE = new StandardTypeConverter();

	public static <T> void registerConverter(Class<T> requiredType, BaseConverter<T> converter) {
		INSTANCE.register(requiredType, converter);
	}

	public static void removeConverter(Class<?> requiredType) {
		INSTANCE.remove(requiredType);
	}

	public static void containsConverter(Class<?> requiredType) {
		INSTANCE.contains(requiredType);
	}

	public static <T> BaseConverter<T> lookupConverter(Class<T> requiredType) {
		return INSTANCE.lookup(requiredType);
	}

	public static <T> T convertValue(Object value, Class<T> requiredType) {
		return convertValue(value, requiredType, null);
	}

	public static <T> T convertValue(Object value, Class<T> requiredType, T defaultValue) {
		return INSTANCE.convert(value, requiredType, defaultValue);
	}

	private ConvertUtils() {
	}

}
