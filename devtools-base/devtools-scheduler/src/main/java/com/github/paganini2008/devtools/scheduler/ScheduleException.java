package com.github.paganini2008.devtools.scheduler;

/**
 * 
 * ScheduleException
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class ScheduleException extends RuntimeException {

	private static final long serialVersionUID = -5503002820477023202L;

	public ScheduleException(String msg) {
		super(msg);
	}

	public ScheduleException(String msg, Throwable e) {
		super(msg, e);
	}

}
