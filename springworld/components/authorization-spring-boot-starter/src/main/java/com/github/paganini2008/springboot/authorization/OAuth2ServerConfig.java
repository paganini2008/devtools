package com.github.paganini2008.springboot.authorization;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;

import javax.sql.DataSource;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.jdbc.DBUtils;

/**
 * 
 * OAuth2ServerConfig
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@Configuration
public class OAuth2ServerConfig {

	public static final String GLOBAL_REALM = "API-SECURITY/CLIENT";

	@Order(2)
	@Configuration
	@EnableWebSecurity
	public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		private static final String SQL_SELECT_USER_DETAILS = "select * from oauth_user_details";

		@Autowired
		DataSource dataSource;

		private Iterator<Tuple> getUserDetails() {
			try {
				return DBUtils.executeQuery(dataSource.getConnection(), SQL_SELECT_USER_DETAILS);
			} catch (SQLException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}

		@Bean
		@Override
		protected UserDetailsService userDetailsService() {
			InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
			manager.createUser(
					User.withUsername("admin").password("admin123").authorities("trusted-client").roles("admin", "user", "guest").build());
			Iterator<Tuple> iterator = getUserDetails();
			while (iterator.hasNext()) {
				Tuple userDetail = iterator.next();
				String username = (String) userDetail.get("username");
				String password = (String) userDetail.get("password");
				String authorities = (String) userDetail.get("authorities");
				String roles = (String) userDetail.get("roles");
				UserDetails userDetails = User.withUsername(username).password(password).authorities(authorities.split(","))
						.roles(roles.split(",")).build();
				manager.createUser(userDetails);
			}
			return manager;
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.httpBasic().and().authorizeRequests().antMatchers("/oauth/**", "/ping", "/user", "/done", "/login/**").permitAll()
					.anyRequest().authenticated().and().csrf().disable();
		}

		@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}
	}

	@Configuration
	@EnableAuthorizationServer
	public static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

		@Autowired
		AuthenticationManager authenticationManager;

		@Autowired
		RedisConnectionFactory redisConnectionFactory;

		@Autowired
		DataSource dataSource;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(clientDetails());
		}

		@Bean
		public ClientDetailsService clientDetails() {
			return new JdbcClientDetailsService(dataSource);
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.tokenStore(new BugFixedRedisTokenStore(redisConnectionFactory)).authenticationManager(authenticationManager)
					.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.realm(GLOBAL_REALM).allowFormAuthenticationForClients();
			oauthServer.passwordEncoder(passwordEncoder()).authenticationEntryPoint((req, resp, exception) -> {
				resp.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				resp.getWriter().write(new ObjectMapper().writeValueAsString(UnauthorizedReponseEntity.newInstance()));
			});
			oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()");
		}

		@Bean
		public PasswordEncoder passwordEncoder() {
			return new PasswordEncoder() {
				@Override
				public String encode(CharSequence charSequence) {
					return charSequence.toString();
				}

				@Override
				public boolean matches(CharSequence charSequence, String s) {
					return Objects.equals(charSequence.toString(), s);
				}
			};
		}

	}

}
