package com.github.paganini2008.devtools.beans;

import java.util.Map;

import com.github.paganini2008.devtools.Case;
import com.github.paganini2008.devtools.Cases;
import com.github.paganini2008.devtools.MissingKeyException;
import com.github.paganini2008.devtools.collection.KeyCaseInsensitiveMap;
import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * Tuple
 * 
 * @author Fred Feng
 * @created 2019-03
 */
public class Tuple extends KeyCaseInsensitiveMap<Object> {

	private static final long serialVersionUID = 3578053139527863010L;

	public Tuple() {
		this(Cases.PLAIN);
	}

	public Tuple(Case format) {
		super(format);
	}

	public Object[] toArray() {
		return values().toArray();
	}
	public <T> T getRequiredProperty(String key, Class<T> requiredType) {
		T result = getProperty(key, requiredType);
		if (result == null) {
			throw new MissingKeyException(key);
		}
		return result;
	}

	public String getProperty(String key) {
		return getProperty(key, (String) null);
	}

	public String getProperty(String key, String defaultValue) {
		return getProperty(key, String.class, defaultValue);
	}

	public <T> T getProperty(String key, Class<T> requiredType) {
		return getProperty(key, requiredType, null);
	}

	public <T> T getProperty(String key, Class<T> requiredType, T defaultValue) {
		return MapUtils.get(this, key, requiredType, defaultValue);
	}

	public static Tuple createBy(Case caseFormat, Map<String, ?> kwargs) {
		Tuple tuple = new Tuple(caseFormat);
		tuple.putAll(kwargs);
		return tuple;
	}

	public static Tuple createBy(Map<String, ?> kwargs) {
		return createBy(Cases.PLAIN, kwargs);
	}

	public void fill(Object object) {
		for (String key : keySet()) {
			BeanUtils.setProperty(object, key, get(key));
		}
	}

	public <T> T toBean(Class<T> requiredType) {
		final T object = BeanUtils.instantiate(requiredType);
		fill(object);
		return object;
	}

}
