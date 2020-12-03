package com.github.paganini2008.devtools.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 
 * JdkProxyFactory
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
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
	 * @author Jimmy Hoff
	 *
	 * @since 1.0
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
					if (e.getCause() instanceof InvocationTargetException) {
						aspect.catchException(target, method, args, ((InvocationTargetException) cause).getTargetException());
					} else {
						throw e;
					}
				}
			}
			if (aspect.afterCall(target, method, args)) {
				return result;
			}
			return null;
		}

	}

	public static ProxyFactory getInstance() {
		return instance;
	}

}
