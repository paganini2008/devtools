package com.github.paganini2008.devtools.db4j;

import java.util.List;

import com.github.paganini2008.devtools.beans.PropertyUtils;

/**
 * BeanPropertySqlParameters
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class BeanPropertySqlParameters extends AbstractSqlParameter implements SqlParameters {

	public BeanPropertySqlParameters(List<?> objectList) {
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
