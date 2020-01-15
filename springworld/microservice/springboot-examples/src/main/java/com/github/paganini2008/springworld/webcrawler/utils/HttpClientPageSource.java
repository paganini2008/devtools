package com.github.paganini2008.springworld.webcrawler.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.github.paganini2008.devtools.RandomUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * HttpClientPageSource
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
public class HttpClientPageSource implements PageSource {

	@Qualifier("default")
	@Autowired
	private RestTemplate restTemplate;

	@Value("${webcrawler.crawler.requestRetries:3}")
	private int requestRetries;

	public String getHtml(String url) throws Exception {
		int retries = 1;
		boolean failed = false;
		do {
			HttpHeaders headers = new HttpHeaders();
			headers.add("User-Agent", RandomUtils.randomChoice(userAgents));
			headers.add("X-Forwarded-For", IpUtils.getRandomIp());
			ResponseEntity<String> responseEntity;
			try {
				responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
				return responseEntity.getStatusCode() == HttpStatus.OK ? responseEntity.getBody() : "";
			} catch (Exception e) {
				failed = true;
				log.error(e.getMessage(), e);
			}
		} while (failed && retries++ < requestRetries);
		return "";
	}

}
