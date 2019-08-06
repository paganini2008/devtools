package com.github.paganini2008.devtools.beans;

/**
 * 
 * BeanInstantiationException
 * 
 * @author Fred Feng
 * @created 2019-07
 * @revised 2017-07
 * @version 1.0
 */
public class BeanInstantiationException extends RuntimeException {

	private static final long serialVersionUID = -6165101337845158099L;

	public BeanInstantiationException(String msg) {
		super(msg);
	}

	public BeanInstantiationException(String msg, Throwable e) {
		super(msg, e);
	}

	public BeanInstantiationException(Throwable e) {
		super(e);
	}

}
