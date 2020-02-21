package com.github.paganini2008.devtools.converter;

/**
 * 
 * ConvertUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class ConvertUtils {

	private static final TypeConverter INSTANCE = new StandardTypeConverter();

	public static <T> void registerType(Class<T> javaType, BasicConverter<T> converter) {
		INSTANCE.registerType(javaType, converter);
	}

	public static void removeType(Class<?> javaType) {
		INSTANCE.removeType(javaType);
	}

	public static void hasType(Class<?> javaType) {
		INSTANCE.hasType(javaType);
	}

	public static <T> BasicConverter<T> lookupType(Class<T> javaType) {
		return INSTANCE.lookupType(javaType);
	}

	public static <T> T convertValue(Object value, Class<T> javaType) {
		return convertValue(value, javaType, null);
	}

	public static <T> T convertValue(Object value, Class<T> javaType, T defaultValue) {
		return INSTANCE.convertValue(value, javaType, defaultValue);
	}
	
	public static void main(String[] args) {
		System.out.println(ConvertUtils.convertValue(38901.34554678f, String.class)) ;
	}

}
