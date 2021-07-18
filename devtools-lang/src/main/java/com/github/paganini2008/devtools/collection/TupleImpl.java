/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.collection;

import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.CaseFormat;
import com.github.paganini2008.devtools.CaseFormats;
import com.github.paganini2008.devtools.MissingKeyException;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.PropertyUtils;

/**
 * 
 * TupleImpl
 *
 * @author Fred Feng
 * @version 1.0
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

	public Object[] toValues() {
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
		Map<String, PropertyDescriptor> desc = PropertyUtils.getPropertyDescriptors(object.getClass());
		Object value;
		for (String key : desc.keySet()) {
			value = get(key);
			if (value != null) {
				PropertyUtils.setProperty(object, key, value);
			}
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
		System.out.println(CaseFormats.LOWER_CAMEL.toCase("last_modified"));
		System.out.println(CaseFormats.UPPER_CAMEL.toCase("last_modified"));
		Tuple tuple = Tuple.newTuple(CaseFormats.LOWER_CAMEL);
		tuple.set("last_modified", new Date());
		System.out.println(tuple);
		System.out.println(tuple.getProperty("LastModified"));
	}

}
