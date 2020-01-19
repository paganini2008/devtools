package com.github.paganini2008.springworld.webcrawler.core;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.github.paganini2008.springworld.webcrawler.dao.JdbcResourceService;
import com.github.paganini2008.springworld.webcrawler.dao.ResourceService;
import com.github.paganini2008.springworld.webcrawler.utils.RedisBloomFilter;
import com.github.paganini2008.springworld.webcrawler.utils.RedisUUID;

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
		return new HttpClientPageSource();
	}

	@Bean
	public RedisBloomFilter redisBloomFilter(StringRedisTemplate redisTemplate) {
		return new RedisBloomFilter("bloomFiter:" + applicationName, 100000000, 0.03d, redisTemplate);
	}

	@Bean
	public RedisUUID redisUuid(@Qualifier("bigint") RedisTemplate<String, Long> redisTemplate) {
		return new RedisUUID("uuid:" + applicationName + ":timestamp", redisTemplate);
	}

	@Bean
	public ResourceCounter resourceCounter() {
		return new ResourceCounter();
	}

	@ConditionalOnMissingBean(FinishCondition.class)
	@Bean
	public FinishCondition countLimited(ResourceCounter resourceCounter) {
		return new CountLimitedCondition(resourceCounter);
	}

}
