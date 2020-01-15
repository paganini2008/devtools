package com.github.paganini2008.springworld.webcrawler.core;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.github.paganini2008.springworld.webcrawler.utils.FreeProxyPool;

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

	@Value("${webcrawler.httpclient.requestRetries:3}")
	private int requestRetries;

	@Value("${webcrawler.httpclient.connectTimeout:60000}")
	private int connectTimeout;

	@Bean("default")
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
		List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
		for (int i = 0; i < messageConverters.size(); i++) {
			HttpMessageConverter<?> httpMessageConverter = messageConverters.get(i);
			if (httpMessageConverter instanceof StringHttpMessageConverter) {
				messageConverters.set(i, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			}
		}
		return restTemplate;
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
		HttpClientBuilder builder = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler(requestRetries, true))
				.setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfigBuilder.build());
		return builder.build();
	}

	@Configuration
	public static class RequestConfigBuilder {

		@Value("${webcrawler.httpclient.connectTimeout:60000}")
		private int connectTimeout;

		@Autowired
		private FreeProxyPool freeProxyPool;

		public RequestConfig build() {
			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT)
					.setCircularRedirectsAllowed(false).setRedirectsEnabled(false).setSocketTimeout(connectTimeout)
					.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout);
			InetSocketAddress socketAddress = (InetSocketAddress) freeProxyPool.getRandomProxy();
			if (socketAddress != null) {
				requestConfigBuilder.setProxy(new HttpHost(socketAddress.getAddress()));
			}
			return requestConfigBuilder.build();
		}
	}

}
