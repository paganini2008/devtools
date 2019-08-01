package com.github.paganini2008.devtools.jdbc;

/**
 * 
 * JdbcException
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 */
public class JdbcException extends RuntimeException {

	private static final long serialVersionUID = -2514330548661823215L;

	public JdbcException(String msg) {
		super(msg);
	}

	public JdbcException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
