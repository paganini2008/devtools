package com.github.paganini2008.springworld.socketbird.transport;

import com.github.paganini2008.springworld.socketbird.Tuple;

/**
 * 
 * Handler
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface Handler {

	void onData(Tuple tuple);

}