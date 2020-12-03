package com.github.paganini2008.devtools.db4j;

/**
 * SqlParameters
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface SqlParameters extends SqlType {

	int getSize();

	boolean hasValue(int index, String name);

	Object getValue(int index, String name);

}
