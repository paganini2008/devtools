package com.github.paganini2008.devtools.beans;

import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * Property
 * 
 * @author Fred Feng
 * @version 1.0
 */
public final class Property<E, T> implements Getter<E, T> {

	private static final PropertyEditorFactory defaultPropertyEditorFactory = new PropertyEditorFactory();

	private final String propertyName;
	private final Class<?> requiredType;
	private final PropertyEditorFactory propertyEditorFactory;

	public static <E> void registerEditor(Class<?> requiredType, PropertyEditor<E> propertyEditor) {
		defaultPropertyEditorFactory.registerEditor(requiredType, propertyEditor);
	}

	public Property(String propertyName, Class<?> requiredType, PropertyEditorFactory propertyEditorFactory) {
		this.propertyName = propertyName;
		this.requiredType = requiredType;
		this.propertyEditorFactory = propertyEditorFactory;
	}

	public static <E, T> Property<E, T> forName(String propertyName) {
		return forName(propertyName, null);
	}

	public static <E, T> Property<E, T> forName(String propertyName, Class<?> requiredType) {
		return new Property<E, T>(propertyName, requiredType, defaultPropertyEditorFactory);
	}

	@SuppressWarnings("unchecked")
	public T apply(E entity) {
		PropertyEditor<E> editor = propertyEditorFactory.lookupEditor(entity.getClass());
		T result = editor.getValue(entity, propertyName);
		return requiredType != null ? (T) ConvertUtils.convertValue(result, requiredType) : result;
	}

	public String toString() {
		return "PropertyName: " + propertyName + ", RequiredType: " + requiredType;
	}

}
