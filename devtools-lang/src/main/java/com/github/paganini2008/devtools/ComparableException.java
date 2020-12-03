package com.github.paganini2008.devtools;

/**
 * ComparableException
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ComparableException extends RuntimeException {

	private static final long serialVersionUID = -6935584403543745980L;

	public ComparableException(String msg) {
		super(msg);
	}

	public ComparableException(String msg, Throwable e) {
		super(msg, e);
	}

	public ComparableException(Throwable e) {
		super(e);
	}

}
