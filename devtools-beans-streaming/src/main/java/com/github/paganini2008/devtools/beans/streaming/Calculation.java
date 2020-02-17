package com.github.paganini2008.devtools.beans.streaming;

import java.util.List;

/**
 * 
 * Calculation
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public interface Calculation<E, T> {

	T getResult(List<E> elements);

}
