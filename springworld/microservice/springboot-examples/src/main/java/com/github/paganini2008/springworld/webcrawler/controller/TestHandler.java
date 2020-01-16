package com.github.paganini2008.springworld.webcrawler.controller;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.transport.Handler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TestHandler implements Handler {

	private final AtomicInteger counter = new AtomicInteger();

	public void onData(Tuple tuple) {
		log.info("Tuple[" + counter.getAndIncrement() + "] ::: " + tuple.toString());
	}

}
