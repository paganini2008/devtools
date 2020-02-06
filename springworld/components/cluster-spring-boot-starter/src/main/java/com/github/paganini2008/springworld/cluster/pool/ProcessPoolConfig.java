package com.github.paganini2008.springworld.cluster.pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastConfig;
import com.github.paganini2008.springworld.cluster.utils.RedisSharedLatch;

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

	private static final int DEFAULT_LATCH_LIFESPAN = 60;

	@Value("${spring.application.name}")
	private String applicationName;

	@Bean
	public ProcessPoolProperties poolConfig() {
		return new ProcessPoolProperties();
	}

	@ConditionalOnMissingBean(ClusterLatch.class)
	@Bean
	public ClusterLatch clusterLatch(ProcessPoolProperties poolConfig, RedisConnectionFactory redisConnectionFactory) {
		return new RedisSharedLatch(applicationName, poolConfig.getPoolSize(), DEFAULT_LATCH_LIFESPAN, redisConnectionFactory);
	}

	@ConditionalOnMissingBean(WorkQueue.class)
	@Bean
	public WorkQueue workQueue() {
		return new RedisWorkQueue();
	}

	@Bean
	public ProcessPool processPool() {
		return new ProcessPoolExecutor();
	}

	@Bean
	public ProcessPoolWorkThread processPoolWorkThread() {
		return new ProcessPoolWorkThread();
	}

	@Bean
	public ProcessPoolBackgroundProcessor processPoolBackgroundProcessor() {
		return new ProcessPoolBackgroundProcessor();
	}

	@Bean
	public InvocationResult invocationResult() {
		return new InvocationResult();
	}

}
