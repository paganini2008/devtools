package com.github.paganini2008.devtools.db4j;

import java.util.Map;

import com.github.paganini2008.devtools.beans.PropertyUtils;

/**
 * ObjectParameterSource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ObjectParameterSource extends ParameterSourceSupport implements ParameterSource {

	public ObjectParameterSource(Object object) {
		this(object, null);
	}

	public ObjectParameterSource(Object object, Map<String, JdbcType> jdbcTypeMap) {
		this.object = object;
		if (jdbcTypeMap != null) {
			for (Map.Entry<String, JdbcType> e : jdbcTypeMap.entrySet()) {
				addJdbcType(e.getKey(), e.getValue());
			}
		}
	}

	private final Object object;

	public boolean hasValue(String paramName) {
		return PropertyUtils.hasProperty(object.getClass(), paramName);
	}

	public Object getValue(String paramName) {
		return PropertyUtils.getProperty(object, paramName);
	}

}
