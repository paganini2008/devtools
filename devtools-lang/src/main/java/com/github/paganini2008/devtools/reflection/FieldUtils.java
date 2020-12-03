package com.github.paganini2008.devtools.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * FieldUtils
 * 
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public abstract class FieldUtils {

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

	public static Object readStaticField(Class<?> type, String fieldName) {
		Field field = getField(type, fieldName);
		return readStaticField(field);
	}

	public static Object readDeclaredStaticField(Class<?> type, String fieldName) {
		Field field = getDeclaredField(type, fieldName);
		return readStaticField(field);
	}

	public static void writeField(Object target, Field field, Object value) {
		Assert.isNull(target, "Target object must not be null.");
		Assert.isNull(field, "The field must not be null.");
		try {
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			throw new ReflectionException("Cannot read field by name: " + field.getName(), e);
		}
	}

	public static void writeStaticField(Field field, Object value) {
		Assert.isNull(field, "The field must not be null.");
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("The field '" + field.getName() + "' is not static.");
		}
		try {
			field.setAccessible(true);
			field.set(null, value);
		} catch (Exception e) {
			throw new ReflectionException("Cannot read field by name: " + field.getName(), e);
		}
	}

	public static void writeDeclaredField(Object target, String fieldName, Object value) {
		Assert.isNull(target, "Target object must not be null.");
		final Field field = getDeclaredField(target.getClass(), fieldName);
		writeField(target, field, value);
	}

	public static void writeField(Object target, String fieldName, Object value) {
		Assert.isNull(target, "Target object must not be null.");
		final Field field = getField(target.getClass(), fieldName);
		writeField(target, field, value);
	}

	public static void writeStaticField(Class<?> type, String fieldName, Object value) {
		final Field field = getField(type, fieldName);
		writeStaticField(field, value);
	}

	public static void writeDeclaredStaticField(Class<?> type, String fieldName, Object value) {
		final Field field = getDeclaredField(type, fieldName);
		writeStaticField(field, value);
	}

	public static Field getFieldIfAbsent(Class<?> cls, String fieldName) {
		try {
			return getField(cls, fieldName);
		} catch (RuntimeException e) {
			return null;
		}
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

	public static List<Field> getDeclaredFields(Class<?> cls, FieldFilter fieldFilter) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : CollectionUtils.forEach(new DeclaredFieldIterator(cls))) {
			if (fieldFilter == null || fieldFilter.accept(field.getName(), field)) {
				fields.add(field);
			}
		}
		return fields;
	}

	public static List<Field> getFields(Class<?> cls, FieldFilter fieldFilter) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : CollectionUtils.forEach(new FieldIterator(cls))) {
			if (fieldFilter == null || fieldFilter.accept(field.getName(), field)) {
				fields.add(field);
			}
		}
		return fields;
	}

	public static void main(String[] args) {
		Iterator<Field> iterator = new FieldIterator(AbstractAction.class);
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}

	/**
	 * 
	 * DeclaredFieldIterator
	 * 
	 * @author Jimmy Hoff
	 * 
	 * 
	 * @version 1.0
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
			if (!(next = canContinue())) {
				fields = interfaces.hasNext() ? CollectionUtils.iterator(interfaces.next().getDeclaredFields()) : null;
				next = canContinue();
			}
			return next;
		}

		private boolean canContinue() {
			return fields != null && fields.hasNext();
		}

		public Field next() {
			return fields.next();
		}
	}

	/**
	 * 
	 * FieldIterator
	 * 
	 * @author Jimmy Hoff
	 * 
	 * 
	 * @version 1.0
	 */
	public static class FieldIterator implements Iterator<Field> {

		private final Iterator<Class<?>> superClassesAndInterfaces;
		private Iterator<Field> fields;

		FieldIterator(Class<?> type) {
			this.fields = new DeclaredFieldIterator(type);
			this.superClassesAndInterfaces = ClassUtils.getAllSuperClassesAndInterfaces(type).iterator();
		}

		public boolean hasNext() {
			boolean next;
			if (!(next = canContinue())) {
				fields = superClassesAndInterfaces.hasNext()
						? new DeclaredFieldIterator(superClassesAndInterfaces.next())
						: null;
				next = canContinue();
			}
			return next;
		}

		private boolean canContinue() {
			return fields != null && fields.hasNext();
		}

		public Field next() {
			return fields.next();
		}

	}
}
