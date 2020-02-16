package com.github.paganini2008.devtools;

/**
 * 
 * MissingKeyException
 *
 * @author Fred Feng
 * @version 1.0
 */
public class MissingKeyException extends IllegalStateException {

	private static final long serialVersionUID = -8600579917890312063L;

	public MissingKeyException(String property) {
		super(property);
	}

	public MissingKeyException(String property, Throwable e) {
		super(property, e);
	}

}
