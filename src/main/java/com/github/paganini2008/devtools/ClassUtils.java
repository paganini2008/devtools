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
package com.github.paganini2008.devtools;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * Utility methods for classes: primitive and wrapper conversion, type checks,
 * class hierarchy traversal, assignability and class loading.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
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

	private static final Map<Class<?>, Class<?>> wrapperPrimitiveMapper = MapUtils.exchange(primitiveWrapperMapper);

	private static final Map<String, Object> primitiveDefaultValueMap = new LinkedHashMap<String, Object>();

	static {
		primitiveDefaultValueMap.put("byte", (byte) 0);
		primitiveDefaultValueMap.put("short", (short) 0);
		primitiveDefaultValueMap.put("int", 0);
		primitiveDefaultValueMap.put("long", 0L);
		primitiveDefaultValueMap.put("float", 0F);
		primitiveDefaultValueMap.put("double", 0D);
		primitiveDefaultValueMap.put("boolean", false);
		primitiveDefaultValueMap.put("char", '\0');
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

	private static final Map<String, String> reverseAbbreviationMap = MapUtils.exchange(abbreviationMap);

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

	/**
	 * Returns the primitive array class by name such as "int[]", or null.
	 */
	public static Class<?> getPrimitiveArrayClass(String name) {
		return primitiveArrayClassMap.get(name);
	}

	/**
	 * Returns the wrapper array class by name such as "Integer[]", or null.
	 */
	public static Class<?> getWrapperArrayClass(String name) {
		return wrapperArrayClassMap.get(name);
	}

	/**
	 * Checks whether the class is a primitive type or its wrapper. Null returns false.
	 */
	public static boolean isPrimitiveOrWrapper(Class<?> type) {
		return type != null && (type.isPrimitive() || wrapperPrimitiveMapper.containsKey(type));
	}

	/**
	 * Returns the JVM abbreviation of a primitive type name (e.g. "int" to "I"),
	 * "" for null, or null for an unknown name.
	 */
	public static String getAbbreviation(String name) {
		if (name == null) {
			return "";
		}
		return abbreviationMap.get(name.toLowerCase());
	}

	/**
	 * Returns the primitive type name of a JVM abbreviation (e.g. "J" to "long"),
	 * "" for null, or null for an unknown abbreviation.
	 */
	public static String getName(String name) {
		if (name == null) {
			return "";
		}
		return reverseAbbreviationMap.get(name.toUpperCase());
	}

	/**
	 * Returns the wrapper class of a primitive type, or the type itself otherwise.
	 */
	public static Class<?> toWrapper(Class<?> type) {
		if (type != null && type.isPrimitive()) {
			return primitiveWrapperMapper.get(type);
		}
		return type;
	}

	/**
	 * Returns the primitive type of a wrapper class, or the type itself otherwise.
	 */
	public static Class<?> toPrimitive(Class<?> type) {
		if (type != null && wrapperPrimitiveMapper.containsKey(type)) {
			return wrapperPrimitiveMapper.get(type);
		}
		return type;
	}

	/**
	 * Checks whether the class is an array class. Null returns false.
	 */
	public static boolean isArray(Class<?> c) {
		return c != null ? c.isArray() : false;
	}

	/**
	 * Negation of {@link #isArray(Class)}.
	 */
	public static boolean isNotArray(Class<?> c) {
		return !isArray(c);
	}

	/**
	 * Negation of {@link #isBoolean(Class)}.
	 */
	public static boolean isNotBoolean(Class<?> clazz) {
		return !isBoolean(clazz);
	}

	/**
	 * Checks whether the class is {@code boolean} or {@code Boolean}. Null returns false.
	 */
	public static boolean isBoolean(Class<?> clazz) {
		return clazz != null ? ((Boolean.class == clazz) || (boolean.class == clazz)) : false;
	}

	/**
	 * Negation of {@link #isCharacter(Class)}.
	 */
	public static boolean isNotCharacter(Class<?> clazz) {
		return !isCharacter(clazz);
	}

	/**
	 * Checks whether the class is {@code char} or {@code Character}. Null returns false.
	 */
	public static boolean isCharacter(Class<?> clazz) {
		return clazz != null ? ((Character.class == clazz) || (char.class == clazz)) : false;
	}

	/**
	 * Negation of {@link #isByte(Class)}.
	 */
	public static boolean isNotByte(Class<?> clazz) {
		return !isByte(clazz);
	}

	/**
	 * Checks whether the class is {@code byte} or {@code Byte}. Null returns false.
	 */
	public static boolean isByte(Class<?> clazz) {
		return clazz != null ? ((Byte.class == clazz) || (byte.class == clazz)) : false;
	}

	/**
	 * Negation of {@link #isShort(Class)}.
	 */
	public static boolean isNotShort(Class<?> clazz) {
		return !isShort(clazz);
	}

	/**
	 * Checks whether the class is {@code short} or {@code Short}. Null returns false.
	 */
	public static boolean isShort(Class<?> clazz) {
		return clazz != null ? ((Short.class == clazz) || (short.class == clazz)) : false;
	}

	/**
	 * Negation of {@link #isInteger(Class)}.
	 */
	public static boolean isNotInteger(Class<?> clazz) {
		return !isInteger(clazz);
	}

	/**
	 * Checks whether the class is {@code int} or {@code Integer}. Null returns false.
	 */
	public static boolean isInteger(Class<?> clazz) {
		return clazz != null ? ((Integer.class == clazz) || (int.class == clazz)) : false;
	}

	/**
	 * Negation of {@link #isFloat(Class)}.
	 */
	public static boolean isNotFloat(Class<?> clazz) {
		return !isFloat(clazz);
	}

	/**
	 * Checks whether the class is {@code float} or {@code Float}. Null returns false.
	 */
	public static boolean isFloat(Class<?> clazz) {
		return clazz != null ? ((Float.class == clazz) || (float.class == clazz)) : false;
	}

	/**
	 * Negation of {@link #isDouble(Class)}.
	 */
	public static boolean isNotDouble(Class<?> clazz) {
		return !isDouble(clazz);
	}

	/**
	 * Checks whether the class is {@code double} or {@code Double}. Null returns false.
	 */
	public static boolean isDouble(Class<?> clazz) {
		return clazz != null ? ((Double.class == clazz) || (double.class == clazz)) : false;
	}

	/**
	 * Negation of {@link #isLong(Class)}.
	 */
	public static boolean isNotLong(Class<?> clazz) {
		return !isLong(clazz);
	}

	/**
	 * Checks whether the class is {@code long} or {@code Long}. Null returns false.
	 */
	public static boolean isLong(Class<?> clazz) {
		return clazz != null ? ((Long.class == clazz) || (long.class == clazz)) : false;
	}

	/**
	 * Negation of {@link #isAssignableFrom(Class, Class)}.
	 */
	public static boolean isNotAssignableFrom(Class<?> superClass, Class<?> clazz) {
		return !isAssignableFrom(superClass, clazz);
	}

	/**
	 * Null-safe {@link Class#isAssignableFrom(Class)}; returns false if either is
	 * null.
	 */
	public static boolean isAssignableFrom(Class<?> superClass, Class<?> clazz) {
		return (superClass != null) && (clazz != null) && superClass.isAssignableFrom(clazz);
	}

	/**
	 * Returns the component type of an array class, or the class itself otherwise.
	 */
	public static Class<?> getType(Class<?> type) {
		return type != null ? type.isArray() ? type.getComponentType() : type : null;
	}

	/**
	 * Collects all parameterized interfaces implemented by the class, its super
	 * classes and super interfaces.
	 *
	 * @throws IllegalArgumentException if the class is null
	 */
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

	/**
	 * Collects all interfaces implemented by the class and its super classes,
	 * including super interfaces, without duplicates.
	 *
	 * @throws IllegalArgumentException if the class is null
	 */
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

	/**
	 * Collects all super classes of the class, nearest first, excluding Object.
	 *
	 * @throws IllegalArgumentException if the class is null
	 */
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

	/**
	 * Collects all super classes (excluding Object) and interfaces of the class.
	 *
	 * @throws IllegalArgumentException if the class is null
	 */
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

	/**
	 * Checks whether {@code actual} is assignable to any of the types.
	 */
	public static boolean isAssignable(Class<?>[] types, Class<?> actual) {
		int length = types != null ? types.length : 0;
		for (int i = 0; i < length; i++) {
			if (isAssignable(types[i], actual)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Negation of {@link #isAssignable(Class[], Class[])}.
	 */
	public static boolean isNotAssignable(Class<?>[] types, Class<?>[] toTypes) {
		return !isAssignable(types, toTypes);
	}

	/**
	 * Checks whether each type in {@code toTypes} is assignable to the type at the
	 * same position in {@code types}, considering auto-boxing. Null is treated as an
	 * empty array.
	 */
	public static boolean isAssignable(Class<?>[] types, Class<?>[] toTypes) {
		if (ArrayUtils.isNotSameLength(types, toTypes)) {
			return false;
		}
		for (int i = 0, l = ArrayUtils.getLength(types); i < l; i++) {
			if (isNotAssignable(types[i], toTypes[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether two class arrays are equal element by element. Null is treated
	 * as an empty array.
	 */
	public static boolean equals(Class<?>[] types, Class<?>[] toTypes) {
		if (ArrayUtils.isNotSameLength(types, toTypes)) {
			return false;
		}
		for (int i = 0, l = ArrayUtils.getLength(types); i < l; i++) {
			if (ObjectUtils.notEquals(types[i], toTypes[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Negation of {@link #isAssignable(Class, Class)}.
	 */
	public static boolean isNotAssignable(Class<?> original, Class<?> actual) {
		return !isAssignable(original, actual);
	}

	/**
	 * Checks whether a value of type {@code actual} can be assigned to
	 * {@code original}, considering auto-boxing and unboxing. Returns false if
	 * either is null.
	 *
	 * <pre>
	 * ClassUtils.isAssignable(Number.class, int.class)  = true
	 * ClassUtils.isAssignable(int.class, Integer.class) = true
	 * ClassUtils.isAssignable(String.class, Object.class) = false
	 * </pre>
	 */
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

	/**
	 * Checks whether the class can be loaded by the context class loader (without
	 * initializing it).
	 */
	public static boolean isPresent(String className) {
		return isPresent(className, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Checks whether the class can be loaded by the class loader (without
	 * initializing it).
	 *
	 * @throws IllegalArgumentException if the class name is blank
	 */
	public static boolean isPresent(String className, ClassLoader loader) {
		Assert.hasNoText(className, "Nullable className");
		try {
			Class.forName(className, false, loader);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Loads the class with the context class loader.
	 *
	 * @throws IllegalArgumentException if the class name is blank or not found
	 */
	public static Class<?> forName(String className) {
		return forName(className, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Loads the class with the class loader.
	 *
	 * @throws IllegalArgumentException if the class name is blank or not found
	 */
	public static Class<?> forName(String className, ClassLoader loader) {
		Assert.hasNoText(className, "Nullable className");
		try {
			return Class.forName(className, true, loader);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	/**
	 * Loads the class and checks it is a subtype of the required type.
	 *
	 * @throws IllegalArgumentException if the class is not found or not a subtype
	 */
	public static <T> Class<? extends T> forName(String className, Class<T> requiredType) {
		Assert.hasNoText(className, "Nullable className");
		Class<?> clazz = forName(className);
		if (!requiredType.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException("Class " + className + " is not assignable from class " + requiredType.getName());
		}
		return (Class<T>) clazz;
	}

	/**
	 * Returns the default value of a primitive type (e.g. 0 for int, false for
	 * boolean), or null for other types.
	 */
	public static Object getNullableValue(Class<?> cls) {
		if (cls != null && cls.isPrimitive()) {
			return primitiveDefaultValueMap.get(cls.getName());
		}
		return null;
	}

	/**
	 * Negation of {@link #contains(Class[], Class)}.
	 */
	public static boolean notContains(Class<?>[] types, Class<?> type) {
		return !contains(types, type);
	}

	/**
	 * Negation of {@link #contains(Class[], Class, boolean)}.
	 */
	public static boolean notContains(Class<?>[] types, Class<?> type, boolean matched) {
		return !contains(types, type, matched);
	}

	/**
	 * Checks whether any of the types is a supertype of (or equal to) the type.
	 */
	public static boolean contains(Class<?>[] types, Class<?> type) {
		return contains(types, type, false);
	}

	/**
	 * Checks whether the types contain the type: exactly if {@code matched}, or as a
	 * supertype otherwise. A null array returns false.
	 */
	public static boolean contains(Class<?>[] types, Class<?> type, boolean matched) {
		if (types == null) {
			return false;
		}
		for (int i = 0, l = types.length; i < l; i++) {
			if (matched) {
				if (types[i].equals(type)) {
					return true;
				}
			} else {
				if (isAssignableFrom(types[i], type)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Joins the class names with the delimiter.
	 */
	public static String join(Class<?>[] types, String delimiter) {
		return ArrayUtils.join(types, type -> type.getName(), delimiter, true);
	}

}
