/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.io.ResourceUtils;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;

/**
 * ServiceLoader
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
@SuppressWarnings("all")
public class ServiceLoader<T> {

	private static final String DEFAULT_LOOKUP_PATH = "META-INF/spi/com/github/paganini2008/devtools/";

	private static String lookupPath = DEFAULT_LOOKUP_PATH;

	private final Class<T> interfaceClass;

	private final ClassLoader classLoader;

	private final Map<String, Class<T>> serviceClasses = new LinkedHashMap<String, Class<T>>();

	private final LruMap<String, T> serviceBeanCache = new LruMap<String, T>();

	public static void lookupPath(String lookupPath) {
		ServiceLoader.lookupPath = lookupPath;
	}

	ServiceLoader(Class<T> interfaceClass, ClassLoader classLoader) {
		if (interfaceClass == null || !interfaceClass.isInterface()) {
			throw new ServiceNotFoundException("This class '" + interfaceClass + "' is not a interface class.");
		}
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		Map<String, String> mapper;
		try {
			String name = lookupPath + "/" + interfaceClass.getName();
			mapper = ResourceUtils.getResource(name, classLoader);
		} catch (Exception e) {
			throw new ServiceNotFoundException("Cannot find service '" + interfaceClass.getName() + "' on '" + lookupPath + "'.");
		}
		for (Map.Entry<String, String> en : mapper.entrySet()) {
			Class<?> cl;
			try {
				cl = Class.forName(en.getValue(), true, classLoader);
			} catch (ClassNotFoundException e) {
				throw new ServiceNotFoundException("Service class '" + en.getValue() + "' is not found.", e);
			}
			if (!interfaceClass.isAssignableFrom(cl)) {
				throw new ServiceNotFoundException(
						"Service class '" + en.getValue() + "' doesn't implement for '" + interfaceClass.getName() + "'.");
			}
			serviceClasses.put(en.getKey(), (Class<T>) cl);
		}
		this.interfaceClass = interfaceClass;
		this.classLoader = classLoader;
	}

	private T createObject(Class<T> serviceClass, Object[] parameters) {
		try {
			return ConstructorUtils.invokeConstructor(serviceClass, parameters);
		} catch (Exception e) {
			throw new ServiceNotFoundException("Service class '" + serviceClass.getName() + "' cannot access.", e);
		}
	}

	public static <T> ServiceLoader<T> load(Class<T> interfaceClass) {
		return new ServiceLoader<T>(interfaceClass, null);
	}

	public static <T> ServiceLoader<T> load(Class<T> interfaceClass, ClassLoader classLoader) {
		return new ServiceLoader<T>(interfaceClass, classLoader);
	}

	public T getFirst(Object... parameters) {
		return get("default", parameters);
	}

	public T get(String alias, Object... parameters) {
		T instance = serviceBeanCache.get(alias);
		if (instance == null) {
			if (!serviceClasses.containsKey(alias)) {
				throw new ServiceNotFoundException("For alias: " + alias);
			}
			serviceBeanCache.put(alias, createObject(serviceClasses.get(alias), parameters));
			instance = serviceBeanCache.get(alias);
		}
		return instance;
	}

}
