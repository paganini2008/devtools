package com.github.paganini2008.devtools.db4j.mapper;

/**
 * RowMapperException
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class RowMapperException extends RuntimeException {

	private static final long serialVersionUID = -6460344809438661419L;

	public RowMapperException(String msg) {
		super(msg);
	}

	public RowMapperException(Throwable e) {
		super(e);
	}

	public RowMapperException(String msg, Throwable e) {
		super(msg, e);
	}
}
