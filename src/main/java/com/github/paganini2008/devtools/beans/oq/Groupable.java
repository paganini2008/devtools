package com.github.paganini2008.devtools.beans.oq;

import java.util.Map;

import com.github.paganini2008.devtools.beans.Getter;

/**
 * 
 * Groupable
 * 
 * @author Fred Feng
 *
 */
public interface Groupable<E> {

	<T> Groupable<E> by(Getter<E, T> getter, String alias);

	Viewable<E, Map<String, Object>> view();
}
