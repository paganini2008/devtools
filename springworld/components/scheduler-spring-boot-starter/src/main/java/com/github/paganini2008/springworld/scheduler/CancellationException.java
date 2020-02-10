package com.github.paganini2008.springworld.scheduler;

/**
 * 
 * CancellationException
 * 
 * @author Fred Feng
 * @created 2019-11
 * @revised 2019-11
 * @version 1.0
 */
public class CancellationException extends RuntimeException {

	private static final long serialVersionUID = -6445957287165627969L;

	public CancellationException() {
		super();
	}

	public CancellationException(String msg) {
		super(msg);
	}

}
