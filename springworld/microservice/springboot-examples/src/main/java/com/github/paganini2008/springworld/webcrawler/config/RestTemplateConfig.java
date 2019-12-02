package com.github.paganini2008.springworld.webcrawler.config;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * RestTemplateConfig
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Configuration
public class RestTemplateConfig {

	@Value("${webcrawler.httpclient.pool.maxTotal:200}")
	private int maxTotal;

	@Value("${webcrawler.httpclient.connectTimeout:60000}")
	private int connectTimeout;

	@Value("${webcrawler.httpclient.requestRetries:3}")
	private int requestRetries;

	@Value("${webcrawler.httpclient.proxy.host:}")
	private String proxyHost;

	@Value("${webcrawler.httpclient.proxy.port:}")
	private int proxyPort;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(httpRequestFactory());
	}

	@Bean
	public ClientHttpRequestFactory httpRequestFactory() {
		return new HttpComponentsClientHttpRequestFactory(httpClient());
	}

	@Bean
	public HttpClient httpClient() {
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
		connectionManager.setMaxTotal(maxTotal);
		connectionManager.setDefaultMaxPerRoute(20);
		connectionManager.setValidateAfterInactivity(2000);
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT)
				.setCircularRedirectsAllowed(false).setRedirectsEnabled(false).setSocketTimeout(connectTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout);
		if (StringUtils.isNotBlank(proxyHost)) {
			requestConfigBuilder.setProxy(new HttpHost(proxyHost, proxyPort));
		}
		RequestConfig requestConfig = requestConfigBuilder.build();

		HttpClientBuilder builder = HttpClients.custom().setDefaultRequestConfig(requestConfig)
				.setRetryHandler(new DefaultHttpRequestRetryHandler(requestRetries, true)).setConnectionManager(connectionManager);
		return builder.build();
	}

}
