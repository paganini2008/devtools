package com.github.paganini2008.springworld.redis.concurrents;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.scheduling.TaskScheduler;

import com.github.paganini2008.devtools.collection.MapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RedisKeyLifespan
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Slf4j
public class RedisKeyLifespan implements Lifespan {

	private final ConcurrentMap<Long, ScheduledFuture<?>> taskFutures = new ConcurrentHashMap<Long, ScheduledFuture<?>>();
	private final ConcurrentMap<Long, List<String>> keyNames = new ConcurrentHashMap<Long, List<String>>();
	private final Set<String> deletedKeyNames = new CopyOnWriteArraySet<String>();
	private final TaskScheduler taskScheduler;
	private final RedisOperations<String, Object> redisOperations;

	public RedisKeyLifespan(TaskScheduler taskScheduler, RedisOperations<String, Object> redisOperations) {
		this.taskScheduler = taskScheduler;
		this.redisOperations = redisOperations;
	}

	@Override
	public void watch(String key, long expiration, long checkInterval, TimeUnit timeUnit) {
		final long expirationInSecond = TimeUnit.SECONDS.convert(expiration, timeUnit);
		if (expirationInSecond <= 1) {
			throw new IllegalArgumentException("Key expiration(seconds) must be great than 1.");
		}
		final long checkIntervalInSecond = TimeUnit.SECONDS.convert(checkInterval, timeUnit);
		if (expirationInSecond <= checkIntervalInSecond) {
			throw new IllegalArgumentException("Key expiration(seconds) must be great than check interval.");
		}

		if (taskFutures.containsKey(expirationInSecond)) {
			keyNames.get(expirationInSecond).add(key);
		} else {
			MapUtils.get(keyNames, expirationInSecond, () -> {
				return new CopyOnWriteArrayList<String>();
			}).add(key);

			taskFutures.put(expirationInSecond, taskScheduler.scheduleAtFixedRate(() -> {
				final List<String> keys = keyNames.get(expirationInSecond);
				keys.forEach(keyName -> {
					if (deletedKeyNames.contains(keyName)) {
						deletedKeyNames.remove(keyName);

						keys.remove(keyName);
						if (keys.isEmpty()) {
							keyNames.remove(expirationInSecond);
							ScheduledFuture<?> taskFuture = taskFutures.remove(expirationInSecond);
							if (taskFuture != null) {
								taskFuture.cancel(false);
							}
						}
					} else {
						redisOperations.expire(keyName, expirationInSecond, TimeUnit.SECONDS);
					}
				});
			}, Duration.ofSeconds(checkIntervalInSecond)));
		}
		log.info("Start to watch redis key: " + key);
	}

	@Override
	public void expire(String key) {
		deletedKeyNames.add(key);
		log.info("No longer to watch redis key: " + key);
	}

}
