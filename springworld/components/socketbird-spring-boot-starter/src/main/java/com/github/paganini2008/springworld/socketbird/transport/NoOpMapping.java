package com.github.paganini2008.springworld.socketbird.transport;

/**
 * 
 * NoOpMapping
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class NoOpMapping implements Mapping<String> {

	@Override
	public String map(String data) {
		return data;
	}

}
