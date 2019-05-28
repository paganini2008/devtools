package com.github.paganini2008.devtools.http;

import java.io.IOException;
import java.util.List;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * TooManyRedirectsException
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class TooManyRedirectsException extends IOException {

	private static final long serialVersionUID = -3107366399442953435L;

	public TooManyRedirectsException(String msg, List<String> redirectUrls) {
		super(msg);
		this.redirectUrls = redirectUrls;
	}

	private final List<String> redirectUrls;

	public String toString() {
		return super.toString() + ", RedirectUrls: " + CollectionUtils.join(redirectUrls, " ==> ");
	}
}
