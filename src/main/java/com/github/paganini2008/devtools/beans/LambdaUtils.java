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
package com.github.paganini2008.devtools.beans;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utilities to inspect serializable lambdas, mainly to resolve the property
 * name of a getter method reference such as {@code User::getName}.
 * <p>
 * The information is read from the {@link SerializedLambda} the compiler
 * generates for every serializable lambda (through its private
 * {@code writeReplace} method). Resolved property names are cached per lambda
 * class with a {@link ClassValue}, so the cache never keeps a class (and its
 * class loader) alive.
 * 
 * <pre>
 * LambdaUtils.getPropertyName(User::getName)    = "name"
 * LambdaUtils.getPropertyName(User::isActive)   = "active"
 * LambdaUtils.getPropertyName(Page::getURL)     = "URL"
 * LambdaUtils.getPropertyName(u -&gt; u.getName()) throws IllegalArgumentException
 * LambdaUtils.getPropertyName(String::length)   throws IllegalArgumentException
 * </pre>
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class LambdaUtils {

	private static final String GETTER_REQUIRED = "A getter method reference is required, such as User::getName";

	private static final ClassValue<AtomicReference<String>> propertyNames = new ClassValue<AtomicReference<String>>() {

		@Override
		protected AtomicReference<String> computeValue(Class<?> type) {
			return new AtomicReference<String>();
		}
	};

	/**
	 * Returns the {@link SerializedLambda} of a serializable lambda or method
	 * reference.
	 * 
	 * @throws IllegalArgumentException if the argument is null or is not a
	 *             serializable lambda (e.g. an ordinary class implementing a
	 *             serializable functional interface)
	 */
	public static SerializedLambda getSerializedLambda(Serializable lambda) {
		if (lambda == null) {
			throw new IllegalArgumentException(GETTER_REQUIRED + ", but got null.");
		}
		Object replacement;
		try {
			Method method = lambda.getClass().getDeclaredMethod("writeReplace");
			method.setAccessible(true);
			replacement = method.invoke(lambda);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Not a serializable lambda: " + lambda.getClass().getName(), e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Cannot access the serialized form of the lambda: " + lambda.getClass().getName(),
					e.getTargetException());
		} catch (IllegalAccessException | RuntimeException e) {
			// RuntimeException: e.g. InaccessibleObjectException on a newer JDK
			throw new IllegalArgumentException("Cannot access the serialized form of the lambda: " + lambda.getClass().getName(), e);
		}
		if (!(replacement instanceof SerializedLambda)) {
			throw new IllegalArgumentException("Not a serializable lambda: " + lambda.getClass().getName());
		}
		return (SerializedLambda) replacement;
	}

	/**
	 * Returns the class declaring the method the lambda refers to (for
	 * {@code User::getName} it is {@code User} or the super class declaring
	 * {@code getName}).
	 * 
	 * @throws IllegalArgumentException if the argument is null, not a
	 *             serializable lambda or the class cannot be loaded
	 */
	public static Class<?> getImplClass(SerializableFunction<?, ?> lambda) {
		SerializedLambda serializedLambda = getSerializedLambda(lambda);
		String className = serializedLambda.getImplClass().replace('/', '.');
		ClassLoader classLoader = lambda.getClass().getClassLoader();
		try {
			return Class.forName(className, false, classLoader);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Cannot load class: " + className, e);
		}
	}

	/**
	 * Resolves the property name of a getter method reference following the
	 * JavaBeans naming rules: the 'get' or 'is' prefix is removed and the rest is
	 * decapitalized like {@link Introspector#decapitalize(String)} does.
	 * 
	 * <pre>
	 * LambdaUtils.getPropertyName(User::getName)  = "name"
	 * LambdaUtils.getPropertyName(User::isActive) = "active"
	 * LambdaUtils.getPropertyName(Page::getURL)   = "URL"
	 * LambdaUtils.getPropertyName(Page::getUrl)   = "url"
	 * </pre>
	 * 
	 * Note: only the method name is checked, so an 'isXxx' method returning
	 * {@link Boolean} is accepted as well.
	 * 
	 * @throws IllegalArgumentException if the argument is null, an ordinary
	 *             lambda (like {@code u -> u.getName()}) or a method reference
	 *             to a method that is not a getter (like {@code String::length})
	 */
	public static String getPropertyName(SerializableFunction<?, ?> getter) {
		if (getter == null) {
			throw new IllegalArgumentException(GETTER_REQUIRED + ", but got null.");
		}
		AtomicReference<String> ref = propertyNames.get(getter.getClass());
		String propertyName = ref.get();
		if (propertyName == null) {
			propertyName = resolvePropertyName(getSerializedLambda(getter).getImplMethodName());
			ref.set(propertyName);
		}
		return propertyName;
	}

	private static String resolvePropertyName(String methodName) {
		if (methodName.startsWith("lambda$")) {
			throw new IllegalArgumentException(GETTER_REQUIRED + ", but got an ordinary lambda expression (" + methodName + ").");
		}
		String name = null;
		if (methodName.startsWith("get") && methodName.length() > 3) {
			name = methodName.substring(3);
		} else if (methodName.startsWith("is") && methodName.length() > 2) {
			name = methodName.substring(2);
		}
		if (name == null) {
			throw new IllegalArgumentException(GETTER_REQUIRED + ", but got a reference to method '" + methodName + "'.");
		}
		return Introspector.decapitalize(name);
	}

}
