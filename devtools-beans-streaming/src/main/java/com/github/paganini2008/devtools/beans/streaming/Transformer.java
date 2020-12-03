package com.github.paganini2008.devtools.beans.streaming;

/**
 * 
 * Transformer
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
@FunctionalInterface
public interface Transformer<E, T> {

	T transfer(E element);

}
