package com.github.paganini2008.devtools.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;

/**
 * 
 * FieldUtils
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class FieldUtils {

	private FieldUtils() {
	}

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

	public static Object readField(Object target, String fieldName) {
		Assert.isNull(target, "Target object must not be null.");
		Class<?> cls = target.getClass();
		Field field = getField(cls, fieldName);
		return readField(target, field);
	}

	public static Object readDeclaredField(Object target, String fieldName) {
		Assert.isNull(target, "Target object must not be null.");
		Field field = getDeclaredField(target.getClass(), fieldName);
		return readField(target, field);
	}

	public static Object readStaticField(Field field) {
		Assert.isNull(field, "The field must not be null.");
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("The field '" + field.getName() + "' is not static");
		}
		return readField((Object) null, field);
	}

	public static Object readStaticField(Class<?> type, String fieldName) {
		Field field = getField(type, fieldName);
		return readStaticField(field);
	}

	public static Object readDeclaredStaticField(Class<?> type, String fieldName) {
		Field field = getDeclaredField(type, fieldName);
		return readStaticField(field);
	}

	public static void writeField(Object target, Field field, Object value) {
		Assert.isNull(field, "The field must not be null.");
		try {
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			throw new ReflectionException("Cannot read field by name: " + field.getName(), e);
		}
	}

	public static void writeDeclaredField(Object target, String fieldName, Object value) {
		Assert.isNull(target, "Target object must not be null.");
		final Field field = getDeclaredField(target.getClass(), fieldName);
		writeField(target, field, value);
	}

	public static void writeStaticField(Field field, Object value) {
		Assert.isNull(field, "The field must not be null.");
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("The field '" + field.getName() + "' is not static.");
		}
		writeField((Object) null, field, value);
	}

	public static void writeStaticField(Class<?> type, String fieldName, Object value) {
		final Field field = getField(type, fieldName);
		writeStaticField(field, value);
	}

	public static void writeDeclaredStaticField(Class<?> type, String fieldName, Object value) {
		final Field field = getDeclaredField(type, fieldName);
		writeField((Object) null, field, value);
	}

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

	public static Field getDeclaredField(Class<?> type, String fieldName) {
		Assert.isNull(type, "The class must not be null.");
		Assert.hasNoText(fieldName, "The field name must not be null");
		try {
			return type.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			throw new ReflectionException("Cannot find declared field " + type.getName() + "." + fieldName);
		}
	}

	public static Iterator<Field> fieldIterator(Class<?> type) {
		return new FieldIterator(type);
	}

	/**
	 * 
	 * FieldIterator
	 * @author Fred Feng
	 * @revised 2019-05
	 * @version 1.0
	 */
	public static class FieldIterator implements Iterator<Field> {

		private final Class<?> rootType;
		private Class<?> type;
		private Iterator<Class<?>> interfaces;
		private Field[] fields;
		private int offset;

		FieldIterator(Class<?> type) {
			this.rootType = type;
			reset(type);
		}

		public void reset() {
			reset(rootType);
		}

		private void reset(Class<?> type) {
			this.type = type;
			this.fields = type.getDeclaredFields();
			this.offset = 0;
		}

		public boolean hasNext() {
			if (offset == fields.length) {
				if (interfaces != null) {
					if (interfaces.hasNext()) {
						reset(interfaces.next());
					}
				} else {
					Class<?> parentType = type.getSuperclass();
					if (parentType != null && parentType != Object.class) {
						reset(parentType);
					} else if (interfaces == null) {
						interfaces = ClassUtils.getAllInterfaces(type).iterator();
						if (interfaces.hasNext()) {
							reset(interfaces.next());
						}
					}
				}
			}
			return (offset++) < fields.length;
		}

		public Field next() {
			return fields[offset - 1];
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
