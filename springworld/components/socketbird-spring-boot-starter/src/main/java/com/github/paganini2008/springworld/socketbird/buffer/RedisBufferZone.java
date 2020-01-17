package com.github.paganini2008.springworld.socketbird.buffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.springworld.cluster.InstanceId;
import com.github.paganini2008.springworld.socketbird.Tuple;

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
	private InstanceId instanceId;

	@Override
	public void set(String name, Tuple tuple) {
		template.opsForList().leftPush(keyForName(name), tuple);
	}

	@Override
	public Tuple get(String name) {
		return (Tuple) template.opsForList().leftPop(keyForName(name));
	}

	@Override
	public int size(String name) {
		return template.opsForList().size(keyForName(name)).intValue();
	}

	String keyForName(String name) {
		return name + ":" + instanceId.get();
	}

}
