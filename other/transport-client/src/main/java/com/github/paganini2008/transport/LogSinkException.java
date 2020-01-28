package com.github.paganini2008.transport;

/**
 * 
 * LogSinkException
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class LogSinkException extends RuntimeException {

	private static final long serialVersionUID = 232293217347009731L;

	public LogSinkException(String message) {
		super(message);
	}

	public LogSinkException(String message, Throwable cause) {
		super(message, cause);
	}

}
