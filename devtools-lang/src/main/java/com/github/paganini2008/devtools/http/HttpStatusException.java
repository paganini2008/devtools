package com.github.paganini2008.devtools.http;

import java.io.IOException;

/**
 * HttpStatusException
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class HttpStatusException extends IOException {

	private static final long serialVersionUID = 2734229897177246457L;
	private int statusCode;
	private String url;

	public HttpStatusException(String message, int statusCode, String url) {
		super(message);
		this.statusCode = statusCode;
		this.url = url;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getUrl() {
		return url;
	}

	public String toString() {
		return super.toString() + ". Status=" + statusCode + ", URL=" + url;
	}

}
