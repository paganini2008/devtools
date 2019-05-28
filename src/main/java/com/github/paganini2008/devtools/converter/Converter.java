package com.github.paganini2008.devtools.converter;

/**
 * Converter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface Converter<S, T> {

	T getValue(S source, T defaultValue);

}
