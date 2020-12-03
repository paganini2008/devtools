package com.github.paganini2008.devtools.reflection;

import java.lang.reflect.Method;

/**
 * 
 * MethodFilter
 * 
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public interface MethodFilter {

	boolean accept(String name, Method method);

	default MethodFilter and(MethodFilter filter) {
		return (name, method) -> {
			return accept(name, method) && filter.accept(name, method);
		};
	}

	default MethodFilter or(MethodFilter filter) {
		return (name, method) -> {
			return accept(name, method) || filter.accept(name, method);
		};
	}

	default MethodFilter not(MethodFilter filter) {
		return (name, method) -> {
			return !filter.accept(name, method);
		};
	}

}
