package com.github.paganini2008.springworld.cluster.pool;

import static com.github.paganini2008.springworld.cluster.pool.ProcessPool.TOPIC_IDENTITY;

import java.util.concurrent.RejectedExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * RedisWorkQueue
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public class RedisWorkQueue implements WorkQueue {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private ProcessPoolProperties poolConfig;

	@Value("${spring.application.name}")
	private String applicationName;

	public void push(Signature signature) {
		String key = key();
		long queueSize = redisTemplate.opsForList().size(key);
		if (queueSize > poolConfig.getQueueSize()) {
			throw new RejectedExecutionException("Pool queue has been full. Size: " + queueSize);
		} else {
			redisTemplate.opsForList().leftPush(key, signature);
		}
	}

	public Signature pop() {
		return (Signature) redisTemplate.opsForList().leftPop(key());
	}

	public void waitForTermination() {
		while (redisTemplate.opsForList().size(key()) != 0) {
			ThreadUtils.randomSleep(1000L);
		}
	}
	
	public int size() {
		return redisTemplate.opsForList().size(key()).intValue();
	}

	private String key() {
		return TOPIC_IDENTITY + ":" + applicationName;
	}

}
