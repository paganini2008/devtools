package com.github.paganini2008.devtools.jdbc;

/**
 * 
 * DetachedSqlException
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class DetachedSqlException extends RuntimeException {

	private static final long serialVersionUID = 8682622100406086038L;

	public DetachedSqlException(String msg) {
		super(msg);
	}

	public DetachedSqlException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public DetachedSqlException(Throwable e) {
		super(e);
	}

}
