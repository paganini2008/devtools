package com.github.paganini2008.springworld.transport;

import com.github.paganini2008.transport.Tuple;

/**
 * 
 * Handler
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface Handler {

	void onData(Tuple tuple);

}
