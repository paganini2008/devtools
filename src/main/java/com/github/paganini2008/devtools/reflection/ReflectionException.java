package com.github.paganini2008.devtools.reflection;

/**
 * 
 * ReflectionException
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class ReflectionException extends RuntimeException {

	private static final long serialVersionUID = 7883319961125053816L;

	public ReflectionException(String msg) {
		super(msg);
	}

	public ReflectionException(String msg, Throwable e) {
		super(msg, e);
	}

	public ReflectionException(Throwable e) {
		super(e);
	}

}
