package com.github.paganini2008.springworld.cluster.pool;

import static com.github.paganini2008.springworld.cluster.pool.ProcessPool.TOPIC_IDENTITY;

import java.util.concurrent.RejectedExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * WorkQueue
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public class WorkQueue {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private ProcessPoolProperties poolConfig;

	@Value("${spring.application.name}")
	private String applicationName;

	public void push(Signature signature) {
		String key = TOPIC_IDENTITY + ":" + applicationName;
		long queueSize = redisTemplate.opsForList().size(key);
		if (queueSize > poolConfig.getQueueSize()) {
			throw new RejectedExecutionException("Pool queue has been full. Size: " + queueSize);
		} else {
			redisTemplate.opsForList().leftPush(key, signature);
		}
	}

	public Signature pop() {
		String key = TOPIC_IDENTITY + ":" + applicationName;
		return (Signature) redisTemplate.opsForList().leftPop(key);
	}

	public void cleaningForTermination() {
		String key = TOPIC_IDENTITY + ":" + applicationName;
		while (redisTemplate.opsForList().size(key) != 0) {
			ThreadUtils.randomSleep(1000L);
		}
	}

}
