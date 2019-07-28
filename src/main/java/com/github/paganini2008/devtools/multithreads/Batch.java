package com.github.paganini2008.devtools.multithreads;

import java.util.Iterator;

import com.github.paganini2008.devtools.multithreads.Producer.Consumer;

/**
 * 
 * Batch
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 */
public interface Batch<T> extends Iterator<T> {

	default <R> long forEach(int nThreads, Consumer<T, R> consumer) {
		return Producer.executeBatch(this, nThreads, consumer);
	}

}
