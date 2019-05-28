package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;

/**
 * 
 * PropertyFilter
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface PropertyFilter {

	boolean accept(String name, PropertyDescriptor descriptor);

	default PropertyFilter and(PropertyFilter filter) {
		return (name, descriptor) -> {
			return accept(name, descriptor) && filter.accept(name, descriptor);
		};
	}

	default PropertyFilter or(PropertyFilter filter) {
		return (name, descriptor) -> {
			return accept(name, descriptor) || filter.accept(name, descriptor);
		};
	}

	default PropertyFilter not(PropertyFilter filter) {
		return (name, descriptor) -> {
			return !filter.accept(name, descriptor);
		};
	}

}
