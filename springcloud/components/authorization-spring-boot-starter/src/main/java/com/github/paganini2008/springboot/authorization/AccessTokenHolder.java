package com.github.paganini2008.springboot.authorization;

import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * AccessTokenHolder
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@Slf4j
public class AccessTokenHolder {

	private static final int MAX_RETRIES = 3;

	private OAuth2RestTemplate restTemplate;

	private OAuth2AccessToken accessToken;

	public AccessTokenHolder(OAuth2RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public OAuth2AccessToken forceGetAccessToken() {
		int i = 0;
		Throwable cause = null;
		while (i++ < MAX_RETRIES) {
			try {
				return restTemplate.getAccessToken();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				cause = e;
			}
		}
		throw new OAuth2Exception("Unable to connect to auth server and get token.", cause);
	}

	public synchronized String getAccessToken() {
		if (accessToken == null || accessToken.isExpired()) {
			accessToken = forceGetAccessToken();
		}
		return accessToken.getValue();
	}
}
