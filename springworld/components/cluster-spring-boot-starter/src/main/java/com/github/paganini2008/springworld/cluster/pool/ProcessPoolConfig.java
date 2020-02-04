package com.github.paganini2008.springworld.cluster.pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastConfig;

/**
 * 
 * ProcessPoolConfig
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Configuration
@ConditionalOnBean(ContextMulticastConfig.class)
@ConditionalOnProperty(value = "spring.application.cluster.pool.enabled", havingValue = "true")
public class ProcessPoolConfig {

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.application.cluster.pool.maxPermits:8}")
	private int maxPermits;

	@ConditionalOnMissingBean(ClusterLatch.class)
	@Bean
	public ClusterLatch clusterLatch(RedisConnectionFactory redisConnectionFactory) {
		return new RedisSharedLatch(applicationName, maxPermits, 60, redisConnectionFactory);
	}

	@Bean
	public ProcessPool processPool() {
		return new ProcessPoolExecutor();
	}
	
	@Bean
	public ProcessPoolWorkThread processPoolWorkThread() {
		return new ProcessPoolWorkThread();
	}

}
