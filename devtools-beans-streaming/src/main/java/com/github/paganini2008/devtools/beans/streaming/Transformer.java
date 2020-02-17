package com.github.paganini2008.devtools.beans.streaming;

/**
 * 
 * Transformer
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface Transformer<E, T> {

	T transfer(E element);

}
