package com.github.paganini2008.springboot.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 
 * OAuth2ClientConfig
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@Configuration
public class OAuth2ClientConfig {

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	@Bean
	public TokenStore tokenStore() {
		return new BugFixedRedisTokenStore(redisConnectionFactory);
	}

	@Primary
	@Bean
	public OAuth2RestTemplate restTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
		return new OAuth2RestTemplate(resource, context);
	}

	@Bean
	public AccessTokenHolder accessTokenHolder(OAuth2RestTemplate restTemplate) {
		return new AccessTokenHolder(restTemplate);
	}
}
