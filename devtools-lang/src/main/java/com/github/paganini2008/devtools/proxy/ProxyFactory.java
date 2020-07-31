package com.github.paganini2008.devtools.proxy;

/**
 * 
 * ProxyFactory
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface ProxyFactory {

	default Object getProxy(Object target, Aspect aspect) {
		return getProxy(target, aspect, target.getClass().getInterfaces());
	}

	Object getProxy(Object target, Aspect aspect, Class<?>[] interfaces);

}
