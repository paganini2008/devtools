package com.github.paganini2008.devtools.beans.streaming;

/**
 * 
 * Order
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public interface Order<E> {

	Sorter<E> toSort();

}
