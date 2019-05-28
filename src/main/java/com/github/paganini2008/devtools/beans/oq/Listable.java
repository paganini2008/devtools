package com.github.paganini2008.devtools.beans.oq;

import java.util.List;

/**
 * 
 * Listable
 * 
 * @author Fred Feng
 *
 */
public interface Listable<E> {

	List<E> list();

	List<E> distinctList();
}
