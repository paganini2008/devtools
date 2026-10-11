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
package com.github.paganini2008.devtools.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 
 * JdkProxyFactory
 * 
 * A {@link ProxyFactory} based on JDK dynamic proxies. Exceptions thrown by the target method are
 * passed to {@link Aspect#catchException(Object, java.lang.reflect.Method, Object[], Throwable)}.
 * 
 * @author Fred Feng
 * @date 2020-08-01
 *
 * @since 2.0.1
 */
public class JdkProxyFactory implements ProxyFactory {

	private static final JdkProxyFactory instance = new JdkProxyFactory();

	@Override
	public Object getProxy(Object target, Aspect aspect, Class<?>... interfaces) {
		return Proxy.newProxyInstance(target != null ? target.getClass().getClassLoader() : JdkProxyFactory.class.getClassLoader(),
				interfaces, new JdkProxyInterceptor(target, aspect));
	}

	/**
	 * 
	 * JdkProxyInterceptor
	 * 
	 * @author Fred Feng
	 *
	 * @since 2.0.1
	 */
	private static class JdkProxyInterceptor implements InvocationHandler {

		private final Object target;
		private final Aspect aspect;

		JdkProxyInterceptor(Object target, Aspect aspect) {
			this.target = target;
			this.aspect = aspect;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object result = null;
			if (aspect.beforeCall(target, method, args)) {
				try {
					result = aspect.call(target, method, args);
				} catch (Throwable e) {
					final Throwable cause = e.getCause();
					if (e instanceof InvocationTargetException) {
						aspect.catchException(target, method, args, ((InvocationTargetException) e).getTargetException());
					} else if (cause instanceof InvocationTargetException) {
						aspect.catchException(target, method, args, ((InvocationTargetException) cause).getTargetException());
					} else {
						throw e;
					}
				}
			}
			if (!aspect.afterCall(target, method, args)) {
				result = null;
			}
			if (result == null && method.getReturnType().isPrimitive() && method.getReturnType() != void.class) {
				return defaultValue(method.getReturnType());
			}
			return result;
		}

		private static Object defaultValue(Class<?> type) {
			if (type == boolean.class) {
				return Boolean.FALSE;
			} else if (type == char.class) {
				return Character.valueOf((char) 0);
			} else if (type == byte.class) {
				return Byte.valueOf((byte) 0);
			} else if (type == short.class) {
				return Short.valueOf((short) 0);
			} else if (type == int.class) {
				return Integer.valueOf(0);
			} else if (type == long.class) {
				return Long.valueOf(0L);
			} else if (type == float.class) {
				return Float.valueOf(0F);
			}
			return Double.valueOf(0D);
		}

	}

	/**
	 * Returns the shared instance.
	 */
	public static ProxyFactory getInstance() {
		return instance;
	}

}
