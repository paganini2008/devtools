package com.github.paganini2008.springworld.cluster.implementor;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.springworld.redisplus.RedisMessageSender;

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

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private RedisMessageSender messageSender;

	@Autowired
	private LoadBalance loadBalance;

	@Autowired
	private InstanceId instanceId;

	public void registerChannel(String channel, int weight) {
		Assert.hasNoText("Channel is required.");
		for (int i = 0; i < weight; i++) {
			channels.add(channel);
		}
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

	public void unicast(String message) {
		unicast(ContextMulticastEventListener.GLOBAL_TOPIC, message);
	}

	public void unicast(String topic, String message) {
		Assert.hasNoText("Topic is required");
		String channel = loadBalance.select(message, channels);
		if (StringUtils.isNotBlank(channel)) {
			messageSender.sendMessage(channel, topic + "#" + message);
		}
	}

	public void multicast(String message) {
		multicast(ContextMulticastEventListener.GLOBAL_TOPIC, message);
	}

	public void multicast(String topic, String message) {
		Assert.hasNoText("Topic is required");
		for (String channel : new HashSet<String>(channels)) {
			messageSender.sendMessage(channel, topic + "#" + message);
		}
	}

	public String getClusterInfo() {
		final String newLine = System.getProperty("line.separator");
		StringBuilder str = new StringBuilder();
		for (String channel : new HashSet<String>(channels)) {
			str.append(applicationName).append("/").append(channel).append("/").append(instanceId.isMaster());
			str.append(newLine);
		}
		return str.toString();
	}

}
