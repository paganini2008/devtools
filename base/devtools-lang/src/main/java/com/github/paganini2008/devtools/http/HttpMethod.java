package com.github.paganini2008.devtools.http;

/**
 * HttpMethod
 * 
 * @author Fred Feng
 * @version 1.0
 */
public enum HttpMethod {

	GET(false), POST(true), PUT(true), DELETE(false), HEAD(false), OPTIONS(true), PATCH(true);

	private final boolean hasBody;

	private HttpMethod(boolean hasBody) {
		this.hasBody = hasBody;
	}

	public boolean hasBody() {
		return hasBody;
	}
}
