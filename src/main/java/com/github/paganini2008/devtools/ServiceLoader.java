/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
 * A simple service provider lookup. The implementations of an interface are declared in the
 * properties file {@code META-INF/spi/com/github/paganini2008/devtools/<interface name>} as
 * {@code alias=implementation class}; the alias {@code default} is used by {@link #getFirst(Object...)}.
 * Service instances are created on demand and cached by alias.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
@SuppressWarnings("all")
public class ServiceLoader<T> {

	private static final String DEFAULT_LOOKUP_PATH = "META-INF/spi/com/github/paganini2008/devtools/";

	private static volatile String lookupPath = DEFAULT_LOOKUP_PATH;

	private final Class<T> interfaceClass;

	private final ClassLoader classLoader;

	private final Map<String, Class<T>> serviceClasses = new LinkedHashMap<String, Class<T>>();

	private final LruMap<String, T> serviceBeanCache = new LruMap<String, T>();

	/**
	 * Changes the global class path directory where service declarations are looked up. When the
	 * declarations of an interface are not found there, the default directory is still searched.
	 * Prefer {@link #load(Class, ClassLoader, String)} to use a specific directory.
	 *
	 * @param lookupPath the new directory; null or blank restores the default
	 */
	public static void lookupPath(String lookupPath) {
		ServiceLoader.lookupPath = StringUtils.isNotBlank(lookupPath) ? lookupPath : DEFAULT_LOOKUP_PATH;
	}

	ServiceLoader(Class<T> interfaceClass, ClassLoader classLoader) {
		this(interfaceClass, classLoader, null);
	}

	ServiceLoader(Class<T> interfaceClass, ClassLoader classLoader, String path) {
		if (interfaceClass == null || !interfaceClass.isInterface()) {
			throw new ServiceNotFoundException("This class '" + interfaceClass + "' is not a interface class.");
		}
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		final boolean explicit = StringUtils.isNotBlank(path);
		if (!explicit) {
			path = lookupPath;
		}
		Map<String, String> mapper = loadDeclarations(interfaceClass, classLoader, path);
		if (mapper.isEmpty() && !explicit && !normalize(path).equals(normalize(DEFAULT_LOOKUP_PATH))) {
			// The global lookup path may have been changed by another component, fall back to the default one
			mapper = loadDeclarations(interfaceClass, classLoader, DEFAULT_LOOKUP_PATH);
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

	private static String normalize(String path) {
		while (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	private static Map<String, String> loadDeclarations(Class<?> interfaceClass, ClassLoader classLoader, String path) {
		try {
			return ResourceUtils.getResource(normalize(path) + "/" + interfaceClass.getName(), classLoader);
		} catch (Exception e) {
			throw new ServiceNotFoundException("Cannot find service '" + interfaceClass.getName() + "' on '" + path + "'.");
		}
	}

	private T createObject(Class<T> serviceClass, Object[] parameters) {
		try {
			return ConstructorUtils.invokeConstructor(serviceClass, parameters);
		} catch (Exception e) {
			throw new ServiceNotFoundException("Service class '" + serviceClass.getName() + "' cannot access.", e);
		}
	}

	/**
	 * Loads the service declarations of the interface with the context class loader.
	 *
	 * @throws ServiceNotFoundException if the class is not an interface or a declared class is invalid
	 */
	public static <T> ServiceLoader<T> load(Class<T> interfaceClass) {
		return new ServiceLoader<T>(interfaceClass, null);
	}

	/**
	 * Loads the service declarations of the interface with the given class loader.
	 *
	 * @throws ServiceNotFoundException if the class is not an interface or a declared class is invalid
	 */
	public static <T> ServiceLoader<T> load(Class<T> interfaceClass, ClassLoader classLoader) {
		return new ServiceLoader<T>(interfaceClass, classLoader);
	}

	/**
	 * Loads the service declarations of the interface from the given class path directory, without
	 * reading or changing the global {@link #lookupPath(String) lookup path}.
	 *
	 * @param interfaceClass the service interface
	 * @param classLoader the class loader, null for the context class loader
	 * @param lookupPath the directory of the declarations, null or blank for the global lookup path
	 * @throws ServiceNotFoundException if the class is not an interface or a declared class is invalid
	 * @since 3.0.0
	 */
	public static <T> ServiceLoader<T> load(Class<T> interfaceClass, ClassLoader classLoader, String lookupPath) {
		return new ServiceLoader<T>(interfaceClass, classLoader, lookupPath);
	}

	/**
	 * Returns the service registered under the alias {@code default}.
	 *
	 * @see #get(String, Object...)
	 */
	public T getFirst(Object... parameters) {
		return get("default", parameters);
	}

	/**
	 * Returns the service registered under the alias, creating and caching it on first access.
	 *
	 * @param alias the alias
	 * @param parameters the constructor arguments used on first access
	 * @throws ServiceNotFoundException if the alias is unknown or the instance cannot be created
	 */
	public synchronized T get(String alias, Object... parameters) {
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
