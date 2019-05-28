package com.github.paganini2008.devtools.beans.oq;

/**
 * 
 * Expression
 * 
 * @author Fred Feng
 *
 */
public interface Expression<E> {

	boolean accept(E e);

}
