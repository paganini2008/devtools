package com.github.paganini2008.springworld.cluster.implementor;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * ContextClusterMulticastChannelGroup
 *
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
public class ContextClusterMulticastChannelGroup {

	private final List<String> channels = new CopyOnWriteArrayList<String>();

	@Autowired
	private RedisMessagePubSub redisMessager;

	@Autowired
	private LoadBalance loadBalance;

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

	public void unicast(Object message) {
		String channel = loadBalance.select(channels, message);
		if (StringUtils.isNotBlank(channel)) {
			redisMessager.sendMessage(channel, message);
		}
	}

	public void multicast(Object message) {
		for (String channel : new HashSet<String>(channels)) {
			redisMessager.sendMessage(channel, message);
		}
	}

}
