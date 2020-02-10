package com.github.paganini2008.springworld.transport.transport;

/**
 * 
 * TransformerException
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class TransformerException extends RuntimeException {

	private static final long serialVersionUID = 3217023130400544469L;

	public TransformerException(String msg, Throwable e) {
		super(msg, e);
	}

	public TransformerException(Throwable e) {
		super(e);
	}

}
