package com.github.paganini2008.devtools.objectpool;

/**
 * 
 * PooledObjectException
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class PooledObjectException extends IllegalStateException {

	private static final long serialVersionUID = 5191791208200801701L;
	
	public PooledObjectException(String msg) {
		super(msg);
	}
	
	public PooledObjectException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
