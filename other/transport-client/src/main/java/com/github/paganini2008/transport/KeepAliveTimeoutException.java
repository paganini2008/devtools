package com.github.paganini2008.transport;

/**
 * 
 * KeepAliveTimeoutException
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public class KeepAliveTimeoutException extends RuntimeException {

	private static final long serialVersionUID = -3214862285809923018L;

	public KeepAliveTimeoutException() {
		super();
	}

	public KeepAliveTimeoutException(String msg) {
		super(msg);
	}

}
