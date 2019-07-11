package com.github.paganini2008.devtools.beans.streaming;

/**
 * 
 * Transformer
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2014-05
 * @version 1.0
 */
public interface Transformer<E, T> {

	T transfer(E element);

}
