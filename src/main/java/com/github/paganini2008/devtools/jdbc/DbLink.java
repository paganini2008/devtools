package com.github.paganini2008.devtools.jdbc;

import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.utils.Entry;

/**
 * DbLink
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface DbLink extends Iterable<Entry> {

	void open() throws SQLException;

	void close();

}
