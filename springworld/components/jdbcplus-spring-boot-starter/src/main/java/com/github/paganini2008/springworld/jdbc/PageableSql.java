package com.github.paganini2008.springworld.jdbc;

/**
 * 
 * PageableSql
 *
 * @author Fred Feng
 * @created 2019-10
 * @revised 2020-01
 * @version 1.0
 */
public interface PageableSql {

	String countableSql();

	String pageableSql(int maxResults, int firstResult);

}
