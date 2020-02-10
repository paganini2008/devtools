package com.github.paganini2008.springworld.cluster.pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastConfig;
import com.github.paganini2008.springworld.redis.concurrents.Lifespan;
import com.github.paganini2008.springworld.redis.concurrents.RedisKeyLifespan;
import com.github.paganini2008.springworld.redis.concurrents.SharedLatch;

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

	@Bean
	public ProcessPoolProperties poolConfig() {
		return new ProcessPoolProperties();
	}

	@Bean
	public Lifespan lifespan(RedisTemplate<String, Object> redisOperations) {
		return new RedisKeyLifespan(threadPoolTaskScheduler(), redisOperations);
	}
	
	@ConditionalOnMissingBean(TaskScheduler.class)
	@Bean
	public TaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(8);
		scheduler.setThreadNamePrefix("taskScheduler-");
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		scheduler.setAwaitTerminationSeconds(300);
		return scheduler;
	}

	@ConditionalOnMissingBean(SharedLatch.class)
	@Bean
	public SharedLatch clusterLatch(ProcessPoolProperties poolConfig, RedisConnectionFactory redisConnectionFactory) {
		return new ContextClusterLatch(applicationName, poolConfig.getPoolSize(), redisConnectionFactory);
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
