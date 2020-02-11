package com.github.paganini2008.devtools.scheduler;

/**
 * 
 * SchedulingException
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class SchedulingException extends RuntimeException {

	private static final long serialVersionUID = -5503002820477023202L;

	public SchedulingException(String msg) {
		super(msg);
	}

	public SchedulingException(String msg, Throwable e) {
		super(msg, e);
	}

}