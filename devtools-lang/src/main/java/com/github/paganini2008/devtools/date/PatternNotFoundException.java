package com.github.paganini2008.devtools.date;

/**
 * 
 * PatternNotFoundException
 *
 * @author Fred Feng
 * @version 1.0
 */
public class PatternNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2775088227811223383L;

	public PatternNotFoundException(String msg) {
		super(msg);
	}

	public PatternNotFoundException(String msg, Throwable e) {
		super(msg, e);
	}

}
