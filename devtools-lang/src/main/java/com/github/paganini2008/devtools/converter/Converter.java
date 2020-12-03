package com.github.paganini2008.devtools.converter;

/**
 * Converter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Converter<S, T> {

	T convertValue(S source, T defaultValue);

}
