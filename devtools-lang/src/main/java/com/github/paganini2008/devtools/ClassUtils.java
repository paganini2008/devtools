package com.github.paganini2008.devtools;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ClassUtils
 *
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public abstract class ClassUtils {

	public static final Class[] EMPTY_ARRAY = new Class[0];

	private static final Map<Class<?>, Class<?>> primitiveWrapperMapper = new LinkedHashMap<Class<?>, Class<?>>();

	static {
		primitiveWrapperMapper.put(Byte.TYPE, Byte.class);
		primitiveWrapperMapper.put(Short.TYPE, Short.class);
		primitiveWrapperMapper.put(Integer.TYPE, Integer.class);
		primitiveWrapperMapper.put(Long.TYPE, Long.class);
		primitiveWrapperMapper.put(Float.TYPE, Float.class);
		primitiveWrapperMapper.put(Double.TYPE, Double.class);
		primitiveWrapperMapper.put(Boolean.TYPE, Boolean.class);
		primitiveWrapperMapper.put(Character.TYPE, Character.class);
	}

	private static final Map<Class<?>, Class<?>> wrapperPrimitiveMapper = MapUtils.reverse(primitiveWrapperMapper);

	private static final Map<String, Object> primitiveDefaultValueMap = new LinkedHashMap<String, Object>();

	static {
		primitiveDefaultValueMap.put("byte", 0);
		primitiveDefaultValueMap.put("short", 0);
		primitiveDefaultValueMap.put("int", 0);
		primitiveDefaultValueMap.put("long", 0L);
		primitiveDefaultValueMap.put("float", 0F);
		primitiveDefaultValueMap.put("double", 0D);
		primitiveDefaultValueMap.put("boolean", false);
		primitiveDefaultValueMap.put("char", '\0');
	}

	private static final Map<String, Object> wrapperDefaultValueMap = new LinkedHashMap<String, Object>();

	static {
		wrapperDefaultValueMap.put("Byte", 0);
		wrapperDefaultValueMap.put("Short", 0);
		wrapperDefaultValueMap.put("Integer", 0);
		wrapperDefaultValueMap.put("Long", 0L);
		wrapperDefaultValueMap.put("Float", 0F);
		wrapperDefaultValueMap.put("Double", 0D);
		wrapperDefaultValueMap.put("Boolean", Boolean.FALSE);
		wrapperDefaultValueMap.put("Character", Character.MIN_VALUE);
	}

	private static final Map<String, String> abbreviationMap = new LinkedHashMap<String, String>();

	static {
		abbreviationMap.put("int", "I");
		abbreviationMap.put("boolean", "Z");
		abbreviationMap.put("float", "F");
		abbreviationMap.put("long", "J");
		abbreviationMap.put("short", "S");
		abbreviationMap.put("byte", "B");
		abbreviationMap.put("double", "D");
		abbreviationMap.put("char", "C");
	}

	private static final Map<String, String> reverseAbbreviationMap = MapUtils.reverse(abbreviationMap);

	private static final Map<String, Class<?>> primitiveArrayClassMap = new LinkedHashMap<String, Class<?>>();

	static {
		primitiveArrayClassMap.put("byte[]", byte[].class);
		primitiveArrayClassMap.put("short[]", short[].class);
		primitiveArrayClassMap.put("int[]", int[].class);
		primitiveArrayClassMap.put("long[]", long[].class);
		primitiveArrayClassMap.put("float[]", float[].class);
		primitiveArrayClassMap.put("double[]", double[].class);
		primitiveArrayClassMap.put("boolean[]", boolean[].class);
		primitiveArrayClassMap.put("char[]", char[].class);
	}

	private static final Map<String, Class<?>> wrapperArrayClassMap = new LinkedHashMap<String, Class<?>>();

	static {
		wrapperArrayClassMap.put("Byte[]", Byte[].class);
		wrapperArrayClassMap.put("Short[]", Short[].class);
		wrapperArrayClassMap.put("Integer[]", Integer[].class);
		wrapperArrayClassMap.put("Long[]", Long[].class);
		wrapperArrayClassMap.put("Float[]", Float[].class);
		wrapperArrayClassMap.put("Double[]", Double[].class);
		wrapperArrayClassMap.put("Boolean[]", Boolean[].class);
		wrapperArrayClassMap.put("Character[]", Character[].class);
	}

	public static Class<?> getPrimitiveArrayClass(String name) {
		return primitiveArrayClassMap.get(name);
	}

	public static Class<?> getWrapperArrayClass(String name) {
		return wrapperArrayClassMap.get(name);
	}

	public static boolean isPrimitiveOrWrapper(Class<?> type) {
		return type != null && (type.isPrimitive() || wrapperPrimitiveMapper.containsKey(type));
	}

	public static String getAbbreviation(String name) {
		if (name == null) {
			return "";
		}
		return abbreviationMap.get(name.toLowerCase());
	}

	public static String getName(String name) {
		if (name == null) {
			return "";
		}
		return reverseAbbreviationMap.get(name.toUpperCase());
	}

	public static Class<?> toWrapper(Class<?> type) {
		if (type != null && type.isPrimitive()) {
			return primitiveWrapperMapper.get(type);
		}
		return type;
	}

	public static Class<?> toPrimitive(Class<?> type) {
		if (type != null && wrapperPrimitiveMapper.containsKey(type)) {
			return wrapperPrimitiveMapper.get(type);
		}
		return type;
	}

	public static boolean isArray(Class<?> c) {
		return c != null ? c.isArray() : false;
	}

	public static boolean isNotArray(Class<?> c) {
		return !isArray(c);
	}

	public static boolean isNotBoolean(Class<?> clazz) {
		return !isBoolean(clazz);
	}

	public static boolean isBoolean(Class<?> clazz) {
		return clazz != null ? ((Boolean.class == clazz) || (boolean.class == clazz)) : false;
	}

	public static boolean isNotCharacter(Class<?> clazz) {
		return !isCharacter(clazz);
	}

	public static boolean isCharacter(Class<?> clazz) {
		return clazz != null ? ((Character.class == clazz) || (char.class == clazz)) : false;
	}

	public static boolean isNotByte(Class<?> clazz) {
		return !isByte(clazz);
	}

	public static boolean isByte(Class<?> clazz) {
		return clazz != null ? ((Byte.class == clazz) || (byte.class == clazz)) : false;
	}

	public static boolean isNotShort(Class<?> clazz) {
		return !isShort(clazz);
	}

	public static boolean isShort(Class<?> clazz) {
		return clazz != null ? ((Short.class == clazz) || (short.class == clazz)) : false;
	}

	public static boolean isNotInteger(Class<?> clazz) {
		return !isInteger(clazz);
	}

	public static boolean isInteger(Class<?> clazz) {
		return clazz != null ? ((Integer.class == clazz) || (int.class == clazz)) : false;
	}

	public static boolean isNotFloat(Class<?> clazz) {
		return !isFloat(clazz);
	}

	public static boolean isFloat(Class<?> clazz) {
		return clazz != null ? ((Float.class == clazz) || (float.class == clazz)) : false;
	}

	public static boolean isNotDouble(Class<?> clazz) {
		return !isDouble(clazz);
	}

	public static boolean isDouble(Class<?> clazz) {
		return clazz != null ? ((Double.class == clazz) || (double.class == clazz)) : false;
	}

	public static boolean isNotLong(Class<?> clazz) {
		return !isLong(clazz);
	}

	public static boolean isLong(Class<?> clazz) {
		return clazz != null ? ((Long.class == clazz) || (long.class == clazz)) : false;
	}

	public static boolean isNotAssignableFrom(Class<?> superClass, Class<?> clazz) {
		return !isAssignableFrom(superClass, clazz);
	}

	public static boolean isAssignableFrom(Class<?> superClass, Class<?> clazz) {
		return (superClass != null) && (clazz != null) && superClass.isAssignableFrom(clazz);
	}

	public static Class<?> getType(Class<?> type) {
		return type != null ? type.isArray() ? type.getComponentType() : type : null;
	}

	public static List<ParameterizedType> getAllParameterizedTypes(Class<?> objectClass) {
		Assert.isNull(objectClass, " Class object can not be null.");
		List<ParameterizedType> parameterizedTypesFound = new ArrayList<ParameterizedType>();
		getAllParameterizedTypes(objectClass, parameterizedTypesFound);
		return parameterizedTypesFound;
	}

	private static void getAllParameterizedTypes(Class<?> objectClass, List<ParameterizedType> parameterizedTypesFound) {
		while (objectClass != null) {
			Type[] types = objectClass.getGenericInterfaces();
			for (int i = 0; i < types.length; i++) {
				if (types[i] instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) types[i];
					if (!parameterizedTypesFound.contains(parameterizedType)) {
						parameterizedTypesFound.add(parameterizedType);
						getAllParameterizedTypes((Class<?>) parameterizedType.getRawType(), parameterizedTypesFound);
					}
				} else {
					getAllParameterizedTypes((Class<?>) types[i], parameterizedTypesFound);
				}
			}
			objectClass = objectClass.getSuperclass();
		}
	}

	public static List<Class<?>> getAllInterfaces(Class<?> cls) {
		Assert.isNull(cls, " Class object can not be null.");
		List<Class<?>> interfacesFound = new ArrayList<Class<?>>();
		getAllInterfaces(cls, interfacesFound);
		return interfacesFound;
	}

	private static void getAllInterfaces(Class<?> cls, List<Class<?>> interfacesFound) {
		while (cls != null) {
			Class<?>[] interfaces = cls.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				if (!interfacesFound.contains(interfaces[i])) {
					interfacesFound.add(interfaces[i]);
					getAllInterfaces(interfaces[i], interfacesFound);
				}
			}
			cls = cls.getSuperclass();
		}
	}

	public static List<Class<?>> getAllSuperClasses(Class<?> cls) {
		Assert.isNull(cls, " Class object can not be null.");
		List<Class<?>> superClassesFound = new ArrayList<Class<?>>();
		getAllSuperClasses(cls.getSuperclass(), superClassesFound);
		return superClassesFound;
	}

	private static void getAllSuperClasses(Class<?> cls, List<Class<?>> superClassesFound) {
		while (cls != null && cls != Object.class) {
			if (!superClassesFound.contains(cls)) {
				superClassesFound.add(cls);
			}
			cls = cls.getSuperclass();
		}
	}

	public static List<Class<?>> getAllSuperClassesAndInterfaces(Class<?> cls) {
		Assert.isNull(cls, " Class object can not be null.");
		List<Class<?>> found = new ArrayList<Class<?>>();
		getAllSuperClassesAndInterfaces(cls, found);
		return found;
	}

	private static void getAllSuperClassesAndInterfaces(Class<?> cls, List<Class<?>> found) {
		if (cls != null && cls != Object.class) {
			Class<?> superClass = cls.getSuperclass();
			if (superClass != null && superClass != Object.class && !found.contains(superClass)) {
				found.add(superClass);
			}
			Class<?>[] interfaces = cls.getInterfaces();
			for (Class<?> interfaceClass : interfaces) {
				if (!found.contains(interfaceClass)) {
					found.add(interfaceClass);
					getAllInterfaces(interfaceClass, found);
				}
			}
			getAllSuperClassesAndInterfaces(superClass, found);
		}
	}

	public static boolean isAssignable(Class<?>[] types, Class<?> actual) {
		int length = types != null ? types.length : 0;
		for (int i = 0; i < length; i++) {
			if (isAssignable(types[i], actual)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNotAssignable(Class<?>[] types, Class<?>[] toTypes) {
		return !isAssignable(types, toTypes);
	}

	public static boolean isAssignable(Class<?>[] types, Class<?>[] toTypes) {
		if (ArrayUtils.isNotSameLength(types, toTypes)) {
			return false;
		}
		for (int i = 0; i < types.length; i++) {
			if (isNotAssignable(types[i], toTypes[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean equals(Class<?>[] types, Class<?>[] toTypes) {
		if (ArrayUtils.isNotSameLength(types, toTypes)) {
			return false;
		}
		for (int i = 0; i < types.length; i++) {
			if (ObjectUtils.notEquals(types[i], toTypes[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotAssignable(Class<?> original, Class<?> actual) {
		return !isAssignable(original, actual);
	}

	public static boolean isAssignable(Class<?> original, Class<?> actual) {
		if (original == null || actual == null) {
			return false;
		}
		if (actual.isPrimitive() && !original.isPrimitive()) {
			actual = toWrapper(actual);
		} else if (!actual.isPrimitive() && original.isPrimitive()) {
			actual = toPrimitive(actual);
		}
		if (original.equals(actual) || original.isAssignableFrom(actual)) {
			return true;
		}
		return false;
	}

	public static Class<?> forName(String className) {
		Assert.hasNoText(className);
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	public static <T> Class<? extends T> forName(String className, Class<T> requiredType) {
		Class<?> clazz = forName(className);
		if (!requiredType.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException("Class " + className + " is not assignable from class " + requiredType.getName());
		}
		return (Class<T>) clazz;
	}

	public static Object getNullableValue(Class<?> cls) {
		if (cls != null && cls.isPrimitive()) {
			return primitiveDefaultValueMap.get(cls.getName());
		}
		return null;
	}

}
