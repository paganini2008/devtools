package com.github.paganini2008.devtools.db4j;

/**
 * 
 * Transaction
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Transaction extends JdbcOperations {

	void rollback();
	
	void commit();
	
	void close();
	
	boolean isCompleted();
	
}
