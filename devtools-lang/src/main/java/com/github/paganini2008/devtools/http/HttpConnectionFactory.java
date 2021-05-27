package com.github.paganini2008.devtools.http;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * 
 * HttpConnectionFactory
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface HttpConnectionFactory {

	HttpURLConnection openConnection(HttpRequest httpRequest) throws IOException;

}
