package com.github.paganini2008.devtools.http;

import java.net.URL;
import java.util.Map;

/**
 * 
 * HttpBase
 * 
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public interface HttpBase<T extends HttpBase<T>> {

	static final String DEFAULT_CHARSET = "UTF-8";

	URL url();

	T url(URL url);

	String method();

	T method(String method);

	T charset(String charset);

	String charset();

	String header(String name);

	T header(String name, String value);

	boolean hasHeader(String name);

	boolean hasHeaderWithValue(String name, String value);

	T removeHeader(String name);

	Map<String, String> headers();

	T headers(Map<String, String> headers);

	String cookie(String name);

	T cookie(String name, String value);

	T cookies(Map<String, String> cookies);

	boolean hasCookie(String name);

	T removeCookie(String name);

	Map<String, String> cookies();

	String cookie();

}
