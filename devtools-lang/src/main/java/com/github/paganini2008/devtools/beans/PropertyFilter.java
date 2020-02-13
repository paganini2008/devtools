package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;

/**
 * 
 * PropertyFilter
 * 
 * @author Fred Feng
 * @created 2013-03
 * @revised 2019-05
 * @version 1.0
 */
public interface PropertyFilter {

	boolean accept(Class<?> type, String name, PropertyDescriptor descriptor);

	static PropertyFilter disjunction() {
		return (type, name, descriptor) -> {
			return false;
		};
	}

	static PropertyFilter conjunction() {
		return (type, name, descriptor) -> {
			return true;
		};
	}

	default PropertyFilter and(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return accept(type, name, descriptor) && filter.accept(type, name, descriptor);
		};
	}

	default PropertyFilter or(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return accept(type, name, descriptor) || filter.accept(type, name, descriptor);
		};
	}

	default PropertyFilter not(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return !filter.accept(type, name, descriptor);
		};
	}

}
