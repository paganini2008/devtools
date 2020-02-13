package com.github.paganini2008.devtools.http;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * 
 * HttpConnectionFactory
 * 
 * @author Fred Feng
 * @created 2016-11
 * @revised 2019-12
 * @version 1.0
 */
public interface HttpConnectionFactory {

	HttpURLConnection openConnection(HttpRequest httpRequest) throws IOException;

}
