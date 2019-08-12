package com.github.paganini2008.springworld.cluster.implementor;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.cluster.ContextClusterHeartbeatEvent;
import com.github.paganini2008.springworld.cluster.ContextClusterStandbyEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextClusterHeartbeatListener
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class ContextClusterHeartbeatListener implements ApplicationListener<ContextClusterHeartbeatEvent> {

	static final String HEART_BEAT_KEY = "springboot:cluster:heartbeat:";
	static final String HEART_BEAT_VALUE = "ok";

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Value("${spring.application.name}")
	private String applicationName;

	private HeartbeatTask heartbeatTask;

	@Override
	public void onApplicationEvent(ContextClusterHeartbeatEvent event) {
		heartbeatTask = new HeartbeatTask();
		heartbeatTask.start(event.getTicket());
		final ApplicationContext context = event.getApplicationContext();
		context.publishEvent(new ContextClusterStandbyEvent(context));
		log.info(
				"The process of cluster leader election is completed. You can set ApplicationListener in your system to proceed subsequent step by watching event type: "
						+ ContextClusterStandbyEvent.class.getName());
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
	class HeartbeatTask implements Executable {

		Timer timer;

		HeartbeatTask() {
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
			redisTemplate.opsForValue().set(HEART_BEAT_KEY + applicationName, HEART_BEAT_VALUE, 5, TimeUnit.SECONDS);
			timer = ThreadUtils.scheduleAtFixedRate(this, 3, TimeUnit.SECONDS);
		}

		public void stop() {
			if (timer != null) {
				timer.cancel();
			}
		}

	}

}
