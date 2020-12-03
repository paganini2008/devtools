package com.github.paganini2008.devtools.proxy;

import java.lang.reflect.Method;

import com.github.paganini2008.devtools.reflection.MethodUtils;

/**
 * 
 * Aspect
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface Aspect {

	default boolean beforeCall(Object target, Method method, Object[] args) {
		return true;
	}

	default Object call(Object target, Method method, Object[] args) throws Throwable {
		return MethodUtils.invokeMethod(target, method, args);
	}

	default boolean afterCall(Object target, Method method, Object[] args) {
		return true;
	}

	default void catchException(Object target, Method method, Object[] args, Throwable e) {
	}

}
