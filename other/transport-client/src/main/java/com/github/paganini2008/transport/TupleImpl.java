package com.github.paganini2008.transport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * 
 * TupleImpl
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class TupleImpl extends HashMap<String, Object> implements Tuple {

	private static final long serialVersionUID = -3458553128530299260L;

	public TupleImpl() {
		setField("timestamp", System.currentTimeMillis());
	}

	TupleImpl(Map<String, ?> kwargs) {
		super(kwargs);
		setField("timestamp", System.currentTimeMillis());
	}

	@Override
	public boolean hasField(String fieldName) {
		return containsKey(fieldName);
	}

	@Override
	public void setField(String fieldName, Object value) {
		put(fieldName, value);
	}

	@Override
	public Object getField(String fieldName) {
		return get(fieldName);
	}

	@Override
	public <T> T getField(String fieldName, Class<T> requiredType) {
		return ConvertUtils.convertValue(getField(fieldName), requiredType);
	}

	@Override
	public void fill(Object object) {
		for (String key : keySet()) {
			BeanUtils.setProperty(object, key, get(key));
		}
	}

	@Override
	public Map<String, Object> toMap() {
		return Collections.unmodifiableMap(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Tuple clone() {
		return Tuple.wrap((Map<String, Object>) super.clone());
	}

}
