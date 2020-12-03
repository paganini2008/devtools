package com.github.paganini2008.devtools.cron4j;

/**
 * 
 * SchedulingException
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class SchedulingException extends RuntimeException {

	private static final long serialVersionUID = -5503002820477023202L;
	
	public SchedulingException() {
		super();
	}

	public SchedulingException(String msg) {
		super(msg);
	}

	public SchedulingException(String msg, Throwable e) {
		super(msg, e);
	}

}
