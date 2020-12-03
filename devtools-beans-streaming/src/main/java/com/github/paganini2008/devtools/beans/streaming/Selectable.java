package com.github.paganini2008.devtools.beans.streaming;

import java.util.function.Function;
import java.util.function.Predicate;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * Selectable
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public interface Selectable<E> extends ResultSetSlice<E> {

	Selectable<E> filter(Predicate<E> predicate);

	Selectable<E> orderBy(Sort<E> sort);

	Selectable<E> distinct();

	<T> Groupable<E> groupBy(Function<E, T> function, String alias);

	default <T> Groupable<E> groupBy(String attributeName, Class<T> requiredType) {
		return groupBy(Property.forName(attributeName, requiredType), attributeName);
	}

	<T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer);

}
