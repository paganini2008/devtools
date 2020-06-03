package com.github.paganini2008.devtools.cron4j.parser;

/**
 * 
 * CronParserException
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class CronParserException extends IllegalArgumentException {

	private static final long serialVersionUID = 7451249976418045968L;

	public CronParserException(String msg) {
		super(msg);
	}

	public CronParserException(String msg, Throwable e) {
		super(msg, e);
	}

}
