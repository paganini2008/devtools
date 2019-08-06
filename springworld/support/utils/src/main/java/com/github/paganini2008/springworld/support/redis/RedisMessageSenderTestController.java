package com.github.paganini2008.springworld.support.redis;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * RedisMessageSenderTestController
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
@RequestMapping("/test/redis")
@RestController
@ConditionalOnBean(EnhancedRedisConfig.class)
public class RedisMessageSenderTestController {

	private static final Logger logger = LoggerFactory.getLogger(RedisMessageSenderTestController.class);

	@Autowired
	private RedisMessageSender messageSender;

	@Autowired
	private RedisMessageListener messageListener;

	@Autowired
	private RedisKeyExpiredEventListener keyExpiredListener;

	@GetMapping("/sendMessage")
	public void sendMessage(@RequestParam("content") String content) {
		final String channel = "test";
		messageListener.registerMessageHandler(channel, (say, message) -> {
			logger.info(say + ": " + message);
		});
		messageSender.sendMessage(channel, content);
	}

	@GetMapping("/sendDelayMessage")
	public void sendDelayMessage(@RequestParam("content") String content, @RequestParam("delay") long delay) {
		final String channel = "test_" + System.currentTimeMillis();
		keyExpiredListener.registerCallback(content, (redisExpiredKey, redisExpiredValue) -> {
			logger.info(redisExpiredKey + ": " + redisExpiredValue);
		});
		messageSender.sendDelayMessage(channel, content, delay, TimeUnit.SECONDS);
	}

}
