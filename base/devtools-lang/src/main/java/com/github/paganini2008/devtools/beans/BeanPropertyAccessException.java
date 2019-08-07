package com.github.paganini2008.devtools.beans;

/**
 * 
 * BeanPropertyAccessException
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @revised 2012-08
 * @version 1.0
 */
public class BeanPropertyAccessException extends RuntimeException {

	private static final long serialVersionUID = -4156649541392668704L;

	public BeanPropertyAccessException(String msg) {
		super(msg);
	}

	public BeanPropertyAccessException(String msg, Throwable e) {
		super(msg, e);
	}

	public BeanPropertyAccessException(Throwable e) {
		super(e);
	}

}