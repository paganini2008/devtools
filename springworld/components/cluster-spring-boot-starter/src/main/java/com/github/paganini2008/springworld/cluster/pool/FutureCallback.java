package com.github.paganini2008.springworld.cluster.pool;

/**
 * 
 * FutureCallback
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public interface FutureCallback {

	void onSuccess(Object result);

	default void onFailure(Exception e) {
	}

}
