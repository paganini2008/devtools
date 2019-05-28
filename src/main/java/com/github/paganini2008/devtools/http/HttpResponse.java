package com.github.paganini2008.devtools.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * HttpResponse
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface HttpResponse extends HttpBase<HttpResponse> {

	int statusCode();

	String statusMessage();

	String contentType();

	String body();

	byte[] bytes();
	
	long length();

	HttpResponse previous();

	int numRedirects();

	void output(OutputStream os, int maxSize) throws IOException;
}
