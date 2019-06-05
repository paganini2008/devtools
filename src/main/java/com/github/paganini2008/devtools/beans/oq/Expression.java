package com.github.paganini2008.devtools.beans.oq;

/**
 * 
 * Expression
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface Expression<E> {

	boolean accept(E e);

}
