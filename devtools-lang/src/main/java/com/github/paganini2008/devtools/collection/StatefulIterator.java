package com.github.paganini2008.devtools.collection;

import java.util.Iterator;

/**
 * StatefulIterator
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class StatefulIterator<T> implements Iterator<T> {

	public void prepare() throws Exception {
	}

	public void close() throws Exception {
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static <T> StatefulIterator<T> convertFrom(final Iterator<T> iterator) {
		if (iterator instanceof StatefulIterator) {
			return (StatefulIterator<T>) iterator;
		}
		return new StatefulIterator<T>() {

			public boolean hasNext() {
				return iterator.hasNext();
			}

			public T next() {
				return iterator.next();
			}
		};
	}

}
