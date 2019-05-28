package com.github.paganini2008.devtools.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * PropertyEditorFactory
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class PropertyEditorFactory {

	private static final ObjectPropertyEditor OBJECT_EDITOR = new ObjectPropertyEditor();
	private final Map<Class<?>, PropertyEditor> editors = new HashMap<Class<?>, PropertyEditor>();

	public PropertyEditorFactory() {
		registerEditor(Map.class, new MapPropertyEditor());
	}

	public <E> void registerEditor(Class<?> requiredType, PropertyEditor<E> propertyEditor) {
		editors.put(requiredType, propertyEditor);
	}

	public <E> PropertyEditor<E> lookupEditor(Class<?> requiredType) {
		for (Class<?> type : editors.keySet()) {
			if (type.equals(requiredType) || type.isAssignableFrom(requiredType)) {
				return editors.get(type);
			}
		}
		return OBJECT_EDITOR;
	}

}
