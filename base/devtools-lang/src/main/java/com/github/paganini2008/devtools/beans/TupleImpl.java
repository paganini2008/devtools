package com.github.paganini2008.devtools.beans;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.Case;
import com.github.paganini2008.devtools.Cases;
import com.github.paganini2008.devtools.MissingKeyException;
import com.github.paganini2008.devtools.collection.KeyCaseInsensitiveMap;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * TupleImpl
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 */
public class TupleImpl extends KeyCaseInsensitiveMap<Object> implements Tuple {

	private static final long serialVersionUID = 3578053139527863010L;

	public TupleImpl() {
		this(Cases.LOWER);
	}

	public TupleImpl(Case format) {
		super(format);
	}

	public Object[] toArray() {
		return values().toArray();
	}

	public String[] keys() {
		return keySet().toArray(new String[0]);
	}

	public void set(String key, Object value) {
		super.put(key, value);
	}

	public Object get(String key) {
		return super.get(key);
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

	public <T> T getRequiredProperty(String key, Class<T> requiredType) {
		T result = getProperty(key, requiredType);
		if (result == null) {
			throw new MissingKeyException(key);
		}
		return result;
	}

	public <T> T toBean(Class<T> requiredType) {
		final T object = BeanUtils.instantiate(requiredType);
		fill(object);
		return object;
	}

	public void fill(Object object) {
		for (String key : keySet()) {
			BeanUtils.setProperty(object, key, get(key));
		}
	}

	public Map<String, Object> toMap() {
		return new LinkedHashMap<String, Object>(this);
	}

}
