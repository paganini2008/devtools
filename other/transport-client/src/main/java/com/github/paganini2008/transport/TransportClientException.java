package com.github.paganini2008.transport;

/**
 * 
 * TransportClientException
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class TransportClientException extends RuntimeException {

	private static final long serialVersionUID = 232293217347009731L;

	public TransportClientException(String message) {
		super(message);
	}

	public TransportClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
