package com.github.paganini2008.devtools.reflection;

import java.lang.reflect.Field;

/**
 * 
 * FieldFilter
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public interface FieldFilter {

	boolean accept(String name, Field field);

	default FieldFilter and(FieldFilter filter) {
		return (name, field) -> {
			return accept(name, field) && filter.accept(name, field);
		};
	}

	default FieldFilter or(FieldFilter filter) {
		return (name, field) -> {
			return accept(name, field) || filter.accept(name, field);
		};
	}

	default FieldFilter not(FieldFilter filter) {
		return (name, field) -> {
			return !filter.accept(name, field);
		};
	}

}
