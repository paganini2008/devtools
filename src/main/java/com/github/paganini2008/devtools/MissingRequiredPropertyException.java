package com.github.paganini2008.devtools;

/**
 * 
 * MissingRequiredPropertyException
 * 
 * @author Fred Feng
 * @created 2019-03
 */
public class MissingRequiredPropertyException extends IllegalStateException {

	private static final long serialVersionUID = -8600579917890312063L;

	public MissingRequiredPropertyException(String property) {
		super(property);
	}

	public MissingRequiredPropertyException(String property, Throwable e) {
		super(property, e);
	}

}
