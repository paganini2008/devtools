package com.github.paganini2008.devtools.db4j;

/**
 * 
 * TransactionException
 *
 * @author Fred Feng
 * @version 1.0
 */
public class TransactionException extends IllegalStateException {

	private static final long serialVersionUID = 6201907525682054380L;
	
	public TransactionException() {
		super();
	}

	public TransactionException(String msg) {
		super(msg);
	}

	public TransactionException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public TransactionException(Throwable cause) {
		super(cause);
	}

}
