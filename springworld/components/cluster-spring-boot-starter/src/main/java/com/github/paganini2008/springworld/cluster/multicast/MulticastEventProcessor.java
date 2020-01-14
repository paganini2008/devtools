package com.github.paganini2008.springworld.cluster.multicast;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.springworld.cluster.InstanceId;
import com.github.paganini2008.springworld.redisplus.RedisMessageHandler;

/**
 * 
 * MulticastEventProcessor
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class MulticastEventProcessor implements RedisMessageHandler {

	@Autowired
	private InstanceId instanceId;

	@Autowired
	private ContextMulticastEventListener multicastEventListener;

	@Override
	public void onMessage(Object message) {
		String line = (String) message;
		if (StringUtils.isBlank(line)) {
			return;
		}
		String[] args = line.split("#", 2);
		String topic = args[0];
		String content = args[1];
		multicastEventListener.fireOnMessage(instanceId.get(), topic, content);
	}

	@Override
	public String getChannel() {
		return instanceId.get();
	}

}
