package com.github.paganini2008.springworld.examples.controller;

import org.springframework.stereotype.Component;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.Handler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SocketbirdHandler
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
@Component
public class SocketbirdHandler implements Handler {

	@Override
	public void onData(Tuple tuple) {
		log.info(tuple.toString());
	}

}
