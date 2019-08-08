package com.github.paganini2008.springworld.decentration;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * HeartbeatEventListener
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class HeartbeatEventListener implements ApplicationListener<HeartbeatEvent>, ApplicationContextAware {

	public static final String TICKET_KEY = "ticket:";
	public static final String HEART_BEAT_KEY = "heartbeat:";
	public static final String HEART_BEAT_VALUE = "ok";

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Value("spring.application.name")
	private String applicationName;

	private ApplicationContext context;

	private HeartbeatTask heartbeatTask;

	@Override
	public void onApplicationEvent(HeartbeatEvent event) {
		heartbeatTask = new HeartbeatTask(applicationName, redisTemplate);
		heartbeatTask.start(event.getTicket());
		context.publishEvent(new CentralizingEvent(this));
		log.info("...");
	}

	/**
	 * 
	 * HeartbeatTask
	 * 
	 * @author Fred Feng
	 * @created 2019-08
	 * @revised 2019-08
	 * @version 1.0
	 */
	static class HeartbeatTask implements Executable {

		String applicationName;
		StringRedisTemplate redisTemplate;
		Timer timer;

		HeartbeatTask(String applicationName, StringRedisTemplate redisTemplate) {
			this.applicationName = applicationName;
			this.redisTemplate = redisTemplate;
		}

		@Override
		public boolean execute() {
			try {
				redisTemplate.opsForValue().set(HEART_BEAT_KEY + applicationName, HEART_BEAT_VALUE, 5, TimeUnit.SECONDS);
				if (log.isTraceEnabled()) {
					log.trace("Heartbeat: " + HEART_BEAT_VALUE);
				}
				return true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return false;
			}
		}

		public void start(long ticket) {
			redisTemplate.opsForValue().set(TICKET_KEY + applicationName, String.valueOf(ticket));
			timer = ThreadUtils.scheduleAtFixedRate(this, 3, TimeUnit.SECONDS);
		}

		public void cancel() {
			if (timer != null) {
				timer.cancel();
			}
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}
