/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import com.github.paganini2008.devtools.net.Urls;

/**
 * 
 * DefaultHttpConnectionFactory
 * 
 * @author Fred Feng
 * 
 * 
 * @since 2.0.1
 */
public class DefaultHttpConnectionFactory implements HttpConnectionFactory {

	private SSLSocketFactory sslSocketFactory;

	public HttpURLConnection openConnection(HttpRequest httpRequest) throws IOException {
		HttpURLConnection connection;
		if (httpRequest.proxy() != null) {
			connection = (HttpURLConnection) httpRequest.url().openConnection(new Proxy(Proxy.Type.HTTP, httpRequest.proxy()));
		} else {
			connection = (HttpURLConnection) httpRequest.url().openConnection();
		}
		connection.setRequestMethod(httpRequest.method());
		connection.setConnectTimeout(httpRequest.connectTimeout());
		connection.setReadTimeout(httpRequest.readTimeout());
		connection.setDoOutput(httpRequest.doOutput());
		connection.setInstanceFollowRedirects(httpRequest.followRedirects());

		if (httpRequest.cookies().size() > 0) {
			connection.addRequestProperty("Cookie", httpRequest.cookie());
		}
		for (Map.Entry<String, String> header : httpRequest.headers().entrySet()) {
			connection.addRequestProperty(header.getKey(), header.getValue());
		}
		if (connection instanceof HttpsURLConnection) {
			if (httpRequest.validateTLSCertificates()) {
				initUnsecuredTSL();
				((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
				((HttpsURLConnection) connection).setHostnameVerifier(getInsecureVerifier());
			}
		}
		return connection;
	}

	private synchronized void initUnsecuredTSL() throws IOException {
		if (sslSocketFactory == null) {
			sslSocketFactory = Urls.defaultSSLSocketFactory();
		}
	}

	private static HostnameVerifier getInsecureVerifier() {
		return new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		};
	}

}
