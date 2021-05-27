package com.github.paganini2008.devtools;

/**
 * 
 * UnreachableCodeException
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class UnreachableCodeException extends RuntimeException {

	private static final long serialVersionUID = -138816290898082979L;

	public UnreachableCodeException() {
	}

	public UnreachableCodeException(String msg) {
		super(msg);
	}

}
