package com.github.paganini2008.springworld.amber.redis;

import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.amber.config.BaseConfiguredSchedulerStarter;
import com.github.paganini2008.springworld.amber.config.JobDispatcher;
import com.github.paganini2008.springworld.amber.config.JobParameter;
import com.github.paganini2008.springworld.amber.config.SchedulerStateListener;
import com.github.paganini2008.springworld.amber.utils.JsonUtils;

/**
 * 
 * RedisDispatcher
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public class RedisDispatcher implements JobDispatcher {

	private static final Logger logger = LoggerFactory.getLogger(JobDispatcher.class);
	public static final String keyPrefix = "job:dispatcher:";
	public static final String NULL = "NULL";
	public static final String ID = keyPrefix + UUID.randomUUID().toString();
	private static final int HEART_BEAT_INTERVAL = 3;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private RedisClientQueue clientQueue;

	@Value("${amber.redis.channel}")
	private String channel;

	@Autowired
	public void configureStateListener(final BaseConfiguredSchedulerStarter schedulerStarter) {
		schedulerStarter.addStateListener(getSchedulerStateListener());
	}

	public void dispatch(JobParameter parameter) {
		String eventId = clientQueue.selectId();
		try {
			String json = JsonUtils.parseObject(parameter);
			redisTemplate.opsForList().leftPush(channel, json);
			JobServerEvent serverEvent = new JobServerEvent(eventId, EventType.ON_MESSAGE);
			json = JsonUtils.parseObject(serverEvent);
			redisTemplate.convertAndSend(channel, json);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Bean
	public SchedulerStateListener getSchedulerStateListener() {
		return new RedisSchedulerStateListener();
	}

	public class RedisSchedulerStateListener implements SchedulerStateListener {

		@Override
		public void onStart() {
			try {
				JobServerEvent serverEvent = new JobServerEvent(ID, EventType.ON_CONNECTION);
				String json = JsonUtils.parseObject(serverEvent);
				redisTemplate.convertAndSend(channel, json);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		@Override
		public void onShutingdown() {
		}

	}

	@Configuration
	public static class HeartbeatSender implements Executable {

		public HeartbeatSender(StringRedisTemplate redisTemplate) {
			this.redisTemplate = redisTemplate;
		}

		private final StringRedisTemplate redisTemplate;
		private Timer timer;

		@PostConstruct
		public void start() {
			timer = ThreadUtils.scheduleAtFixedRate(this, HEART_BEAT_INTERVAL, HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
		}

		@PreDestroy
		public void stop() {
			if (timer != null) {
				timer.cancel();
			}
		}

		public boolean execute() {
			redisTemplate.opsForValue().set(ID, NULL);
			redisTemplate.expire(ID, 5, TimeUnit.SECONDS);
			return true;
		}

	}

}
