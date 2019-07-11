package com.github.paganini2008.devtools.beans.streaming;

import java.util.List;

/**
 * 
 * Calculation
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @version 1.0
 */
public interface Calculation<E, T> {

	T getResult(List<E> elements);

}
