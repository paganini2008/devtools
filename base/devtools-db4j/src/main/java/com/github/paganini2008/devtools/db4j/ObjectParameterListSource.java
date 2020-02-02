package com.github.paganini2008.devtools.db4j;

import java.util.List;

import com.github.paganini2008.devtools.beans.PropertyUtils;

/**
 * ObjectParameterListSource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ObjectParameterListSource extends ParameterSourceSupport implements ParameterListSource {

	public ObjectParameterListSource(List<?> objectList) {
		this.objectList = objectList;
	}

	private final List<?> objectList;

	public int getSize() {
		return objectList.size();
	}

	public boolean hasValue(int index, String paramName) {
		Object o = objectList.get(index);
		return PropertyUtils.hasProperty(o.getClass(), paramName);
	}

	public Object getValue(int index, String paramName) {
		Object o = objectList.get(index);
		return PropertyUtils.getProperty(o, paramName);
	}

}
