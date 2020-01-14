package com.github.paganini2008.springworld.webcrawler.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.github.paganini2008.springworld.socketbird.store.RedisStore;
import com.github.paganini2008.springworld.socketbird.store.Store;
import com.github.paganini2008.springworld.webcrawler.dao.JdbcResourceService;
import com.github.paganini2008.springworld.webcrawler.dao.ResourceService;

/**
 * 
 * CrawlerConfig
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Order(99)
@Configuration
public class CrawlerConfig {

	@Primary
	@Bean
	public Store store() {
		return new RedisStore();
	}

	@Bean("crawlerPathMatcher")
	public PathMatcher crawlerPathMatcher() {
		return new AntPathMatcher();
	}

	@ConditionalOnProperty(name = "webcrawler.store", havingValue = "jdbc", matchIfMissing = true)
	@Bean
	public ResourceService resourceService() {
		return new JdbcResourceService();
	}

}
