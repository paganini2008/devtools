package com.github.paganini2008.springworld.socketbird;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.cluster.implementor.MulticastChannelListener;
import com.github.paganini2008.springworld.socketbird.transport.NioClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SocketIoMulticastChannelListener
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class SocketIoMulticastChannelListener implements MulticastChannelListener {

	@Autowired
	private NioClient nioClient;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Value("${spring.application.name}")
	private String applicationName;

	@Override
	public void onJoin(String instanceId) {
		log.info("{} join the spring application cluster: {}", instanceId, applicationName);
		final String key = String.format(Constants.APPLICATION_KEY, applicationName);
		while (!redisTemplate.opsForHash().hasKey(key, instanceId)) {
			ThreadUtils.sleep(1000L);
		}
		String location = (String) redisTemplate.opsForHash().get(key, instanceId);
		if (StringUtils.isNotBlank(location)) {
			String[] args = location.split(":", 2);
			String hostName = args[0];
			int port = Integer.parseInt(args[1]);
			nioClient.connect(new InetSocketAddress(hostName, port));
		}
	}

	@Override
	public void onLeave(String instanceId) {
		log.info("{} leave the spring application cluster: {}", instanceId, applicationName);
	}
	
	

}