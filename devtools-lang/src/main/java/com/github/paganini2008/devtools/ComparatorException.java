package com.github.paganini2008.devtools;

/**
 * ComparatorException
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ComparatorException extends RuntimeException {

	private static final long serialVersionUID = -6935584403543745980L;

	public ComparatorException(String msg) {
		super(msg);
	}

	public ComparatorException(String msg, Throwable e) {
		super(msg, e);
	}

	public ComparatorException(Throwable e) {
		super(e);
	}

}
