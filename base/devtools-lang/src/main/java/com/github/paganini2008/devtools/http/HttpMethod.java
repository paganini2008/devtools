package com.github.paganini2008.devtools.http;

/**
 * 
 * HttpMethod
 * 
 * @author Fred Feng
 * @created 2016-11
 * @revised 2019-12
 * @version 1.0
 */
public enum HttpMethod {

	GET(false, true), 
	POST(true, false), 
	HEAD(false, true), 
	PUT(true, false), 
	DELETE(false, false), 
	OPTIONS(true, false), 
	TRACE(true, false), 
	PATCH(true, false);

	private final boolean doOutput;
	private final boolean followRedirects;

	private HttpMethod(boolean doOutput, boolean followRedirects) {
		this.doOutput = doOutput;
		this.followRedirects = followRedirects;
	}

	public boolean doOutput() {
		return doOutput;
	}

	public boolean followRedirects() {
		return followRedirects;
	}
}
