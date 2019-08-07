package com.github.paganini2008.springcloud.security.examples;

import java.io.Serializable;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.github.paganini2008.springboot.authorization.BugFixedRedisTokenStore;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * ResourceServerConfig
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@Configuration
public class ResourceServerConfig {

	public static final String RESOURCE_ID = "web-api";

	@Getter
	@Setter
	public static class UnauthorizedReponseEntity implements Serializable {

		private static final long serialVersionUID = -5079368708333321873L;
		private Object data;
		private boolean success;
		private String code = "";
		private String msg;
		private int responseStatus;

		UnauthorizedReponseEntity() {
		}

	}

	public static UnauthorizedReponseEntity unauthorized() {
		UnauthorizedReponseEntity responseEntity = new UnauthorizedReponseEntity();
		responseEntity.setSuccess(false);
		responseEntity.setResponseStatus(HttpStatus.UNAUTHORIZED.value());
		responseEntity.setMsg("未经授权的操作");
		responseEntity.setCode("UNAUTHORED_OPERATION_FAULT");
		return responseEntity;
	}

	@Order(10)
	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Autowired
		private RedisConnectionFactory redisConnectionFactory;

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(RESOURCE_ID).stateless(true).tokenStore(tokenStore());
		}

		@Bean
		public TokenStore tokenStore() {
			return new BugFixedRedisTokenStore(redisConnectionFactory);
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().exceptionHandling().authenticationEntryPoint((req, resp, exception) -> {
				resp.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				resp.getWriter().write(new ObjectMapper().writeValueAsString(unauthorized()));
			});
			http.authorizeRequests().antMatchers("/protect/**").access("#oauth2.hasScope('read') and hasRole('user')")
					.antMatchers("/echo/**").authenticated();
		}

	}

}
