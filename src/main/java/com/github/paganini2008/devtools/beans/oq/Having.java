package com.github.paganini2008.devtools.beans.oq;

/**
 * 
 * Having
 * 
 * @author Fred Feng
 *
 */
public interface Having<E> {

	boolean accept(Unitable<E> unitable);

}
