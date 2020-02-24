package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;

/**
 * 
 * Customizable
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Customizable<T> {

	T customize(Connection connection, ParsedSqlRunner sqlRunner);

}
