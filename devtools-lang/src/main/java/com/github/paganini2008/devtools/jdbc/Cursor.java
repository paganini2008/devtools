package com.github.paganini2008.devtools.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * Cursor
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Cursor<T> extends Iterator<T> {

	default List<T> list() {
		List<T> list = new ArrayList<T>();
		for (T t : CollectionUtils.forEach(this)) {
			list.add(t);
		}
		return list;
	}

	default T first() {
		return CollectionUtils.getFirst(list());
	}

	boolean isOpened();

	void close();

}
