package com.github.paganini2008.devtools.beans.streaming;

/**
 * 
 * Order
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public interface Order<E> {

	Sorter<E> toSort();

}
