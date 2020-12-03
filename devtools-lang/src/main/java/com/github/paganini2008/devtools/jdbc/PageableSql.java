package com.github.paganini2008.devtools.jdbc;

/**
 * 
 * PageableSql
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface PageableSql {

	String countableSql();

	String pageableSql(int maxResults, int firstResult);

}
