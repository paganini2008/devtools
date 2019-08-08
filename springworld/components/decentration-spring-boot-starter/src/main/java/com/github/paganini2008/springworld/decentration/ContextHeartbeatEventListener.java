package com.github.paganini2008.springworld.decentration;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextHeartbeatEventListener
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class ContextHeartbeatEventListener implements ApplicationListener<ContextHeartbeatEvent> {

	static final String HEART_BEAT_KEY = "heartbeat:";
	static final String HEART_BEAT_VALUE = "ok";

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Value("spring.application.name")
	private String applicationName;

	private HeartbeatTask heartbeatTask;

	@Override
	public void onApplicationEvent(ContextHeartbeatEvent event) {
		heartbeatTask = new HeartbeatTask(applicationName, redisTemplate);
		heartbeatTask.start(event.getTicket());
		final ApplicationContext context = event.getApplicationContext();
		context.publishEvent(new ContextActivatedEvent(context));
		log.info("Spring context is activated now.");
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
			redisTemplate.opsForValue().set(HEART_BEAT_KEY + applicationName, HEART_BEAT_VALUE);
			timer = ThreadUtils.scheduleAtFixedRate(this, 3, TimeUnit.SECONDS);
		}

		public void cancel() {
			if (timer != null) {
				timer.cancel();
			}
		}

	}

}
