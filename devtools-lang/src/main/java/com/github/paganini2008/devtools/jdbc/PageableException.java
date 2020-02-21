package com.github.paganini2008.devtools.jdbc;

/**
 * 
 * PageableException
 *
 * @author Fred Feng
 * @version 1.0
 */
public class PageableException extends DetachedSqlException {

	private static final long serialVersionUID = -2514330548661823215L;

	public PageableException(String msg) {
		super(msg);
	}

	public PageableException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
