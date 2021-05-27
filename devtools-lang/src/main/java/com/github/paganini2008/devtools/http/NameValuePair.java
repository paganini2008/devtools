package com.github.paganini2008.devtools.http;

import java.io.InputStream;

/**
 * NameValuePair
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface NameValuePair {

	NameValuePair name(String name);

	String name();

	NameValuePair value(String value);

	String value();

	NameValuePair inputStream(InputStream inputStream);

	InputStream inputStream();

	boolean hasInputStream();

}
