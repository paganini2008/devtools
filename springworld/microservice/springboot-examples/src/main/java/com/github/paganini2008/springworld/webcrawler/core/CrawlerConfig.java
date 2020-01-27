package com.github.paganini2008.springworld.webcrawler.core;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.github.paganini2008.springworld.socketbird.utils.HashPartitioner;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;
import com.github.paganini2008.springworld.webcrawler.jdbc.JdbcResourceService;
import com.github.paganini2008.springworld.webcrawler.jdbc.ResourceService;
import com.github.paganini2008.springworld.webcrawler.utils.RedisIdentifier;

/**
 * 
 * CrawlerConfig
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 */
@Configuration
public class CrawlerConfig {

	@Value("${spring.application.name}")
	private String applicationName;

	@Primary
	@Bean
	public Partitioner hashPartitioner() {
		return new HashPartitioner("sourceId,refer,path,version".split(","));
	}

	@Bean("crawlerPathMatcher")
	public PathMatcher crawlerPathMatcher() {
		return new AntPathMatcher();
	}

	@ConditionalOnMissingBean(ResourceService.class)
	@Bean
	public ResourceService resourceService() {
		return new JdbcResourceService();
	}

	@ConditionalOnMissingBean(PageSource.class)
	@Bean(initMethod = "configure", destroyMethod = "destroy")
	public PageSource pageSource() {
		return new HtmlUnitPageSource();
	}

	@Bean
	public RedisIdentifier redisIdentifier(@Qualifier("bigint") RedisTemplate<String, Long> redisTemplate) {
		return new RedisIdentifier("serial:" + applicationName, redisTemplate);
	}

	@Bean
	public ResourceCounter resourceCounter() {
		return new ResourceCounter();
	}

	@ConditionalOnMissingBean(FinishCondition.class)
	@Bean
	public FinishCondition countLimited(ResourceCounter resourceCounter) {
		return new CountLimitedCondition(100000, resourceCounter);
	}

	@Bean
	public CrawlerContextInitializer crawlerContextInitializer() {
		return new CrawlerContextInitializer();
	}

	@ConditionalOnMissingBean(PathAcceptor.class)
	@Bean
	public PathAcceptor pathAcceptor() {
		return new DefaultPathAcceptor();
	}

}
