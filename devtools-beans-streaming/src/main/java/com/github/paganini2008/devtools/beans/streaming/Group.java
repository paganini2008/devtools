package com.github.paganini2008.devtools.beans.streaming;

import java.util.List;
import java.util.Map;

/**
 * 
 * Group
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Group<E> extends Summary<E> {

	Map<String, Object> getAttributes();

	List<E> elements();

	Group<E> rollup();

	default int count() {
		return elements().size();
	}
}
