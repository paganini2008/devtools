package com.github.paganini2008.devtools.beans;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.CaseFormat;
import com.github.paganini2008.devtools.MissingKeyException;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.KeyConversionMap;
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
public class TupleImpl extends KeyConversionMap<String, String, Object> implements Tuple {

	private static final long serialVersionUID = 507463156717310533L;

	public TupleImpl(CaseFormat format) {
		super(new LinkedHashMap<String, Object>());
		this.caseFormat = format;
	}

	private final CaseFormat caseFormat;

	protected String convertKey(Object key) {
		String str = key != null ? key.toString() : null;
		if (StringUtils.isNotBlank(str)) {
			str = caseFormat.toCase(str);
		}
		return str;
	}

	public Object[] valueArray() {
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

	public void append(Map<String, Object> m) {
		if (m != null) {
			putAll(m);
		}
	}

	public Map<String, Object> toMap() {
		return new LinkedHashMap<String, Object>(this);
	}

	public static void main(String[] args) {
		Tuple tuple = Tuple.newTuple();
		tuple.set("last_modified", new Date());
		System.out.println(tuple);
		System.out.println(tuple.getProperty("lastModified"));
	}

}
