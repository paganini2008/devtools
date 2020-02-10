package com.github.paganini2008.springworld.cluster.multicast;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.springworld.redis.pubsub.RedisMessageSender;

/**
 * 
 * ContextMulticastGroup
 *
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
public class ContextMulticastGroup {

	private final List<String> channels = new CopyOnWriteArrayList<String>();

	@Autowired
	private RedisMessageSender messageSender;

	@Autowired
	private LoadBalance loadBalance;

	public void registerChannel(String channel, int weight) {
		Assert.hasNoText("Channel is required.");
		for (int i = 0; i < weight; i++) {
			channels.add(channel);
		}
		Collections.sort(channels);
	}

	public boolean hasRegistered(String channel) {
		return channels.contains(channel);
	}

	public void removeChannel(String channel) {
		Assert.hasNoText("Channel is required.");
		while (channels.contains(channel)) {
			channels.remove(channel);
		}
	}

	public void unicast(Object message) {
		unicast(ContextMulticastEventListener.GLOBAL_TOPIC, message);
	}

	public void unicast(String topic, Object message) {
		Assert.hasNoText("Topic is required");
		String channel = loadBalance.select(message, channels);
		if (StringUtils.isNotBlank(channel)) {
			messageSender.sendMessage(channel, createMessage(topic, message));
		}
	}

	public void multicast(Object message) {
		multicast(ContextMulticastEventListener.GLOBAL_TOPIC, message);
	}

	public void multicast(String topic, Object message) {
		Assert.hasNoText("Topic is required");
		for (String channel : new HashSet<String>(channels)) {
			messageSender.sendMessage(channel, createMessage(topic, message));
		}
	}

	private static Map<String, Object> createMessage(String topic, Object message) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("topic", topic);
		data.put("message", message);
		return data;
	}

}
