package com.github.paganini2008.springworld.webcrawler.core;

import org.springframework.stereotype.Component;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.Handler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * StatHandler
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
@Component
public class StatHandler implements Handler {

	public void onData(Tuple tuple) {
		if (log.isTraceEnabled()) {
			log.trace("It works ok.");
		}
	}

	@Override
	public int compareTo(Handler handler) {
		return 100;
	}

}
