package com.github.paganini2008.devtools.beans.streaming;

/**
 * 
 * Order
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @version 1.0
 */
public interface Order<E> {

	Sort<E> toSort();

}
