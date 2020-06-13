package com.github.paganini2008.devtools.cron4j.parser;

/**
 * 
 * MalformedCronException
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class MalformedCronException extends RuntimeException {

	private static final long serialVersionUID = 7451249976418045968L;

	public MalformedCronException(String msg) {
		super(msg);
	}

	public MalformedCronException(String msg, Throwable e) {
		super(msg, e);
	}

}
