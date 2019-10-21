package com.github.paganini2008.springworld.socketbird.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.springworld.socketbird.Tuple;

/**
 * 
 * RedisStore
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class RedisStore implements Store {

	@Qualifier("socketbird.store.redis")
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

}
