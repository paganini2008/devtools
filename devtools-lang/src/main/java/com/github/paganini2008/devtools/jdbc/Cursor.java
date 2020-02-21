package com.github.paganini2008.devtools.jdbc;

import java.util.Iterator;

/**
 * 
 * Cursor
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Cursor<T> extends Iterator<T> {

	boolean isOpened();

	void close();

}
