package com.github.paganini2008.springworld.socketbird.buffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

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

	@Override
	public void set(String name, Tuple tuple) {
		template.opsForList().leftPush(name, tuple);
	}

	@Override
	public Tuple get(String name) {
		return (Tuple) template.opsForList().leftPop(name);
	}

	@Override
	public int size(String name) {
		return template.opsForList().size(name).intValue();
	}

}
