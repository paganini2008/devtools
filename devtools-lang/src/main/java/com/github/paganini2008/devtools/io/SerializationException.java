package com.github.paganini2008.devtools.io;

/**
 * 
 * SerializationException
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class SerializationException extends RuntimeException {

	private static final long serialVersionUID = 1364430466704746145L;

	public SerializationException(String msg) {
		super(msg);
	}

	public SerializationException(String msg, Throwable e) {
		super(msg, e);
	}

	public SerializationException(Throwable e) {
		super(e);
	}

}
