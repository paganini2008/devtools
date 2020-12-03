package com.github.paganini2008.devtools.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * Cursor
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Cursor<T> extends Iterator<T> {

	default List<T> list() {
		List<T> results = new ArrayList<T>();
		for (T t : CollectionUtils.forEach(this)) {
			results.add(t);
		}
		return results;
	}
	
	int getRownum();
	
	void mark(int rownum);

	boolean isOpened();

	void close();

}
