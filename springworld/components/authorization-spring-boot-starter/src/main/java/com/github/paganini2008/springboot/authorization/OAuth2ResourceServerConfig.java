package com.github.paganini2008.springboot.authorization;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.StringUtils;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * OAuth2ResourceServerConfig
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@Slf4j
@Configuration
public class OAuth2ResourceServerConfig {

	@Order(10)
	@Configuration
	@EnableOAuth2Sso
	@EnableResourceServer
	@ConfigurationProperties(prefix = "security.oauth2.configuration")
	public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

		@Autowired
		private RedisConnectionFactory redisConnectionFactory;

		@Autowired
		@Qualifier("auth")
		private RequestMatcher authRequestMatcher;

		@Autowired
		private ResourceServerProperties resourceServerProperties;

		@Setter
		private String resourceId;

		@Setter
		private String clientId;

		@Setter
		private String clientSecret;

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
			resources.resourceId(resourceId).stateless(true).tokenExtractor(new BearerTokenExtractor()).tokenServices(tokenServices())
					.tokenStore(tokenStore()).authenticationEntryPoint(new ErrorPageAuthenticationEntryPoint())
					.accessDeniedHandler(new ErrorPageAccessDeniedHandler());
		}

		@Bean
		public TokenStore tokenStore() {
			return new BugFixedRedisTokenStore(redisConnectionFactory);
		}

		@Primary
		@Bean
		public ResourceServerTokenServices tokenServices() throws IOException {
			final String checkTokenUrl = resourceServerProperties.getTokenInfoUri();
			log.info("ClientId: {}, clientSecret: {}, checkTokenUrl: {}", clientId, clientSecret, checkTokenUrl);
			RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
			remoteTokenServices.setCheckTokenEndpointUrl(checkTokenUrl);
			remoteTokenServices.setClientId(clientId);
			remoteTokenServices.setClientSecret(clientSecret);
			remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
			return remoteTokenServices;
		}

		@Bean
		public AccessTokenConverter accessTokenConverter() {
			return new DefaultAccessTokenConverter();
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/error/**").permitAll().requestMatchers(acceptedRequestFilter()).permitAll()
					.requestMatchers(authRequestMatcher).authenticated();
			http.csrf().disable();
			http.formLogin().loginPage("/login").permitAll().and().logout().logoutUrl("/logout").permitAll();

		}

		@Bean("accepted")
		public AcceptedRequestFilter acceptedRequestFilter() {
			return new AcceptedRequestFilter();
		}

	}

	public static class ErrorPageAuthenticationEntryPoint implements AuthenticationEntryPoint {

		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
				throws IOException, ServletException {
			log.warn("Unauthored action: " + request.getServletPath(), authException);
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			request.setAttribute("unauthorized_action", request.getServletPath());
			request.getRequestDispatcher("/error").forward(request, response);
		}

	}

	public static class ErrorPageAccessDeniedHandler implements AccessDeniedHandler {

		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
				throws IOException, ServletException {
			log.warn("Unauthored action: " + request.getServletPath(), accessDeniedException);
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			request.setAttribute("unauthorized_action", request.getServletPath());
			request.getRequestDispatcher("/error").forward(request, response);
		}
	}

	private static final String AUTHORIZATION_HEADER = "Authorization";

	@Component("auth")
	@Profile({ "local", "prod", "dev", "test" })
	public static class StrictAuthorizationRequestMatcher implements RequestMatcher {

		@Override
		public boolean matches(HttpServletRequest request) {
			String auth = request.getHeader(AUTHORIZATION_HEADER);
			if (StringUtils.isBlank(auth)) {
				auth = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
			}
			if (StringUtils.isBlank(auth)) {
				if (log.isTraceEnabled()) {
					log.trace("Invalid action: " + request.getServletPath());
				}
			}
			return true;
		}

	}

	@Component("auth")
	@Profile({ "uat" })
	public static class LooseAuthorizationRequestMatcher implements RequestMatcher {

		@Override
		public boolean matches(HttpServletRequest request) {
			String auth = request.getHeader(AUTHORIZATION_HEADER);
			boolean hasOauth2Token = StringUtils.isNotBlank(auth) && auth.startsWith(OAuth2AccessToken.BEARER_TYPE);
			boolean hasAccessToken = StringUtils.isNotBlank(request.getParameter(OAuth2AccessToken.ACCESS_TOKEN));
			return hasOauth2Token || hasAccessToken;
		}

	}

}
