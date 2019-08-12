package com.github.paganini2008.springworld.cluster.implementor;

import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextMulticastAware
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class ContextMulticastAware implements ApplicationListener<ContextRefreshedEvent> {

	private final String id;

	public ContextMulticastAware(String id) {
		this.id = UUID.randomUUID().toString();
	}

	@Autowired
	private RedisMessagePubSub redisMessager;

	@Autowired
	private ContextMulticastChannelGroup channelGroup;

	@Value("${spring.redis.pubsub.keyexpired.namespace:springboot:cluster:multicast:member:}")
	private String namespace;

	private HeartbeatTask heartbeatTask;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		channelGroup.registerChannel(id, 1);
		redisMessager.subcribeChannel("standby", new StandbyMessageHandler());
		redisMessager.subcribeChannel(id, new MulticastMessageHandler(event.getApplicationContext()));
		redisMessager.subcribeEphemeralChannel(new DeactiveMessageHandler());

		redisMessager.sendMessage("standby", id);

		heartbeatTask = new HeartbeatTask();
		heartbeatTask.start(namespace + id);
	}

	public String getId() {
		return id;
	}

	class HeartbeatTask implements Executable {

		Timer timer = null;
		String channel;

		HeartbeatTask() {
		}

		@Override
		public boolean execute() {
			try {
				redisMessager.sendEphemeralMessage(channel, id, 5, TimeUnit.SECONDS);
				return true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return false;
			}
		}

		public void start(String channel) {
			redisMessager.sendEphemeralMessage(channel, id, 5, TimeUnit.SECONDS);
			timer = ThreadUtils.scheduleWithFixedDelay(this, 3, 3, TimeUnit.SECONDS);
			this.channel = channel;
		}

		public void stop() {
			if (timer != null) {
				timer.cancel();
			}
		}

	}

}
