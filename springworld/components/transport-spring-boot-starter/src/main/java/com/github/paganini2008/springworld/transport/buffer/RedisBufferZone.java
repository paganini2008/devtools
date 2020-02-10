package com.github.paganini2008.springworld.transport.buffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.springworld.cluster.ClusterId;
import com.github.paganini2008.transport.Tuple;

/**
 * 
 * RedisBufferZone
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class RedisBufferZone implements BufferZone {

	@Autowired
	private RedisTemplate<String, Object> template;

	@Autowired
	private ClusterId clusterId;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.transport.bufferzone.reused:true}")
	private boolean reused;

	@Override
	public void set(String name, Tuple tuple) {
		template.opsForList().leftPush(getKey(name), tuple);
	}

	@Override
	public Tuple get(String name) {
		return (Tuple) template.opsForList().leftPop(getKey(name));
	}

	@Override
	public int size(String name) {
		return template.opsForList().size(getKey(name)).intValue();
	}

	protected String getKey(String name) {
		return "transport:bufferzone:" + name + ":" + applicationName + (reused ? ":" + clusterId.get() : "");
	}

}
