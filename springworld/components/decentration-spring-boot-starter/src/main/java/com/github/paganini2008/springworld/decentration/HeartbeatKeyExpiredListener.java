package com.github.paganini2008.springworld.decentration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.springworld.amber.utils.JsonUtils;

/**
 * 
 * HeartbeatKeyExpiredListener
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class HeartbeatKeyExpiredListener implements ApplicationListener<RedisKeyExpiredEvent> {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private RedisAtomicLong serial;

	@Value("spring.application.name")
	private String applicationName;

	@Override
	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource());
		final String heartbeatKey = HeartbeatEventListener.HEART_BEAT_KEY + applicationName;
		if (heartbeatKey.equals(expiredKey)) {
			final String ticketKey = HeartbeatEventListener.TICKET_KEY + applicationName;
			final long ticket = Long.valueOf(redisTemplate.opsForValue().get(ticketKey));
		}
	}

}
