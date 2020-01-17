package com.github.paganini2008.springworld.webcrawler.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.github.paganini2008.springworld.webcrawler.config.RedisBloomFilter;
import com.github.paganini2008.springworld.webcrawler.dao.JdbcResourceService;
import com.github.paganini2008.springworld.webcrawler.dao.ResourceService;

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

	@Value("${webcrawler.redis-key.bloomFilter}")
	private String bloomFilterRedisKey;

	@Bean("crawlerPathMatcher")
	public PathMatcher crawlerPathMatcher() {
		return new AntPathMatcher();
	}

	@ConditionalOnProperty(name = "webcrawler.resources.service", havingValue = "jdbc", matchIfMissing = true)
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
		return new RedisBloomFilter(bloomFilterRedisKey, 100000000, 0.03d, redisTemplate);
	}

	@Bean
	public ResourceCounter resourceCounter() {
		return new ResourceCounter();
	}

	@ConditionalOnMissingBean(BatchCondition.class)
	@Bean
	public BatchCondition countLimited(ResourceCounter resourceCounter) {
		return new CountLimitedCondition(resourceCounter);
	}

}
