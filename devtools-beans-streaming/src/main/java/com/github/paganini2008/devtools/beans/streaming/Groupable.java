package com.github.paganini2008.devtools.beans.streaming;

import java.util.function.Function;
import java.util.function.Predicate;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * Groupable
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public interface Groupable<E> {

	default <T> Groupable<E> groupBy(String attributeName, Class<T> requiredType) {
		return groupBy(Property.forName(attributeName, requiredType), attributeName);
	}

	<T> Groupable<E> groupBy(Function<E, T> function, String attributeName);

	Groupable<E> having(Predicate<Group<E>> predicate);

	Groupable<E> orderBy(Sort<Group<E>> sort);

	<T> ResultSetSlice<T> setTransformer(Transformer<Group<E>, T> transformer);

}
