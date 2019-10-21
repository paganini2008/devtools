package com.github.paganini2008.springworld.socketbird.transport;

/**
 * 
 * Mapping
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface Mapping<T> {

	T map(String data);

}
