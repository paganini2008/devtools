package com.github.paganini2008.devtools.db4j.orm;

/**
 * 
 * TypeMapperException
 *
 * @author Fred Feng
 * @since 2.0.5
 */
public class TypeMapperException extends RuntimeException{

	private static final long serialVersionUID = -6363993377272681174L;
	
	public TypeMapperException() {
		super();
	}
	
	public TypeMapperException(String msg) {
		super(msg);
	}

	public TypeMapperException(String msg, Throwable e) {
		super(msg, e);
	}

}
