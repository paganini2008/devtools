package com.github.paganini2008.devtools;

public class ServiceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 5338432559862797765L;

	public ServiceNotFoundException(String msg) {
		super(msg);
	}

	public ServiceNotFoundException(String msg, Throwable e) {
		super(msg, e);
	}

	public ServiceNotFoundException(Throwable e) {
		super(e);
	}

}
