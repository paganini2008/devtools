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
package com.github.paganini2008.devtools.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * Utilities for locating, reading and writing fields via reflection: fields declared by the class itself of any
 * visibility (including private and static ones), public fields inherited from super classes and interface constants.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class FieldUtils {

	/**
	 * Reads the field of the target, making it accessible first.
	 * 
	 * @throws IllegalArgumentException if the target or field is null
	 * @throws ReflectionException if the field cannot be read
	 */
	public static Object readField(Object target, Field field) {
		Assert.isNull(target, "Target object must not be null.");
		Assert.isNull(field, "The field must not be null.");
		try {
			field.setAccessible(true);
			return field.get(target);
		} catch (Exception e) {
			throw new ReflectionException("Cannot read field by name: " + field.getName(), e);
		}
	}

	/**
	 * Reads the static field, making it accessible first.
	 * 
	 * @throws IllegalArgumentException if the field is null or not static
	 */
	public static Object readStaticField(Field field) {
		Assert.isNull(field, "The field must not be null.");
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("The field '" + field.getName() + "' is not static");
		}
		try {
			field.setAccessible(true);
			return field.get(null);
		} catch (Exception e) {
			throw new ReflectionException("Cannot read static field by name: " + field.getName(), e);
		}
	}

	/**
	 * Reads the named field located by {@link #getField(Class, String)}.
	 */
	public static Object readField(Object target, String fieldName) {
		Assert.isNull(target, "Target object must not be null.");
		Class<?> cls = target.getClass();
		Field field = getField(cls, fieldName);
		return readField(target, field);
	}

	/**
	 * Reads the named field declared by the target's class itself.
	 */
	public static Object readDeclaredField(Object target, String fieldName) {
		Assert.isNull(target, "Target object must not be null.");
		Field field = getDeclaredField(target.getClass(), fieldName);
		return readField(target, field);
	}

	/**
	 * Reads the named static field located by {@link #getField(Class, String)}.
	 */
	public static Object readStaticField(Class<?> type, String fieldName) {
		Field field = getField(type, fieldName);
		return readStaticField(field);
	}

	/**
	 * Reads the named static field declared by the class itself.
	 */
	public static Object readDeclaredStaticField(Class<?> type, String fieldName) {
		Field field = getDeclaredField(type, fieldName);
		return readStaticField(field);
	}

	/**
	 * Writes the field of the target, making it accessible first.
	 * 
	 * @throws IllegalArgumentException if the target or field is null
	 * @throws ReflectionException if the value cannot be written (e.g. null for a primitive field)
	 */
	public static void writeField(Object target, Field field, Object value) {
		Assert.isNull(target, "Target object must not be null.");
		Assert.isNull(field, "The field must not be null.");
		try {
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			throw new ReflectionException("Cannot write field by name: " + field.getName(), e);
		}
	}

	/**
	 * Writes the static field, making it accessible first.
	 * 
	 * @throws IllegalArgumentException if the field is null or not static
	 * @throws ReflectionException if the value cannot be written (e.g. the field is final)
	 */
	public static void writeStaticField(Field field, Object value) {
		Assert.isNull(field, "The field must not be null.");
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("The field '" + field.getName() + "' is not static.");
		}
		try {
			field.setAccessible(true);
			field.set(null, value);
		} catch (Exception e) {
			throw new ReflectionException("Cannot write static field by name: " + field.getName(), e);
		}
	}

	/**
	 * Writes the named field declared by the target's class itself.
	 */
	public static void writeDeclaredField(Object target, String fieldName, Object value) {
		Assert.isNull(target, "Target object must not be null.");
		final Field field = getDeclaredField(target.getClass(), fieldName);
		writeField(target, field, value);
	}

	/**
	 * Writes the named field located by {@link #getField(Class, String)}.
	 */
	public static void writeField(Object target, String fieldName, Object value) {
		Assert.isNull(target, "Target object must not be null.");
		final Field field = getField(target.getClass(), fieldName);
		writeField(target, field, value);
	}

	/**
	 * Writes the named static field located by {@link #getField(Class, String)}.
	 */
	public static void writeStaticField(Class<?> type, String fieldName, Object value) {
		final Field field = getField(type, fieldName);
		writeStaticField(field, value);
	}

	/**
	 * Writes the named static field declared by the class itself.
	 */
	public static void writeDeclaredStaticField(Class<?> type, String fieldName, Object value) {
		final Field field = getDeclaredField(type, fieldName);
		writeStaticField(field, value);
	}

	/**
	 * Same as {@link #getField(Class, String)} but returns null instead of throwing.
	 */
	public static Field getFieldIfAbsent(Class<?> cls, String fieldName) {
		try {
			return getField(cls, fieldName);
		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * Finds a field declared by the class (any visibility), or else a public field of its super classes, or else a
	 * constant of its interfaces.
	 * 
	 * @throws IllegalArgumentException if the type is null or the name is blank
	 * @throws ReflectionException if no field is found
	 */
	public static Field getField(Class<?> type, String fieldName) {
		Assert.isNull(type, "The class must not be null.");
		Assert.hasNoText(fieldName, "The field name must not be null.");
		try {
			return type.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
		}
		return searchField(type, fieldName);
	}

	private static Field searchField(Class<?> type, String fieldName) {
		try {
			return type.getField(fieldName);
		} catch (NoSuchFieldException e) {
		}
		for (Iterator<Class<?>> it = ClassUtils.getAllInterfaces(type).iterator(); it.hasNext();) {
			try {
				Field match = it.next().getField(fieldName);
				if (match != null) {
					return match;
				}
			} catch (NoSuchFieldException e) {
			}
		}
		throw new ReflectionException("Cannot find field " + type.getName() + "." + fieldName);
	}

	/**
	 * Finds a field declared by the class itself (any visibility).
	 * 
	 * @throws IllegalArgumentException if the type is null or the name is blank
	 * @throws ReflectionException if no field is found
	 */
	public static Field getDeclaredField(Class<?> type, String fieldName) {
		Assert.isNull(type, "The class must not be null.");
		Assert.hasNoText(fieldName, "The field name must not be null");
		try {
			return type.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			throw new ReflectionException("Cannot find declared field " + type.getName() + "." + fieldName);
		}
	}

	/**
	 * Returns the fields declared by the class and its directly implemented interfaces accepted by the filter (all if
	 * null).
	 */
	public static List<Field> getDeclaredFields(Class<?> cls, FieldFilter fieldFilter) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : CollectionUtils.forEach(new DeclaredFieldIterator(cls))) {
			if (fieldFilter == null || fieldFilter.accept(field.getName(), field)) {
				fields.add(field);
			}
		}
		return fields;
	}

	/**
	 * Returns the fields declared by the class, all its super classes (except {@link Object}) and all its interfaces,
	 * accepted by the filter (all if null). Each field appears once.
	 */
	public static List<Field> getFields(Class<?> cls, FieldFilter fieldFilter) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : CollectionUtils.forEach(new FieldIterator(cls))) {
			if (fieldFilter == null || fieldFilter.accept(field.getName(), field)) {
				fields.add(field);
			}
		}
		return fields;
	}

	/**
	 * Iterates the fields declared by a class followed by the fields of its directly implemented interfaces.
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	public static class DeclaredFieldIterator implements Iterator<Field> {

		DeclaredFieldIterator(Class<?> type) {
			this.fields = CollectionUtils.iterator(type.getDeclaredFields());
			this.interfaces = CollectionUtils.iterator(type.getInterfaces());
		}

		private Iterator<Class<?>> interfaces;
		private Iterator<Field> fields;

		public boolean hasNext() {
			boolean next;
			while (!(next = canContinue()) && interfaces.hasNext()) {
				fields = CollectionUtils.iterator(interfaces.next().getDeclaredFields());
			}
			return next;
		}

		private boolean canContinue() {
			return fields != null && fields.hasNext();
		}

		public Field next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return fields.next();
		}
	}

	/**
	 * Iterates the fields declared by a class, its super classes and all its interfaces.
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	public static class FieldIterator implements Iterator<Field> {

		private final Iterator<Class<?>> superClassesAndInterfaces;
		private Iterator<Field> fields;

		FieldIterator(Class<?> type) {
			this.fields = CollectionUtils.iterator(type.getDeclaredFields());
			this.superClassesAndInterfaces = ClassUtils.getAllSuperClassesAndInterfaces(type).iterator();
		}

		public boolean hasNext() {
			boolean next;
			while (!(next = canContinue()) && superClassesAndInterfaces.hasNext()) {
				fields = CollectionUtils.iterator(superClassesAndInterfaces.next().getDeclaredFields());
			}
			return next;
		}

		private boolean canContinue() {
			return fields != null && fields.hasNext();
		}

		public Field next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return fields.next();
		}

	}
}
