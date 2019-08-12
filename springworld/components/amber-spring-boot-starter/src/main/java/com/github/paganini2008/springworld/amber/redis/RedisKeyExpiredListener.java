 package com.github.paganini2008.springworld.amber.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.springworld.amber.utils.JsonUtils;

/**
 * 
 * RedisKeyExpiredListener
 * 
 * @author Fred Feng
 * @created 2018-03
 */
@SuppressWarnings("rawtypes")
public class RedisKeyExpiredListener implements ApplicationListener<RedisKeyExpiredEvent> {

	private static final Logger logger = LoggerFactory.getLogger(RedisKeyExpiredListener.class);

	@Value("${amber.redis.channel}")
	private String channel;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource());
		if (expiredKey.startsWith(RedisDispatcher.keyPrefix)) {
			JobServerEvent serverEvent = new JobServerEvent(expiredKey, EventType.ON_DISCONNECTION);
			String json = JsonUtils.parseObject(serverEvent);
			redisTemplate.convertAndSend(channel, json);
		}
	}

}
