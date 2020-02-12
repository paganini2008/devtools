package com.github.paganini2008.springworld.redis;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * RedisListSlice
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public class RedisListSlice<T> implements ResultSetSlice<T> {

	private final String key;
	private final RedisTemplate<String, Object> redisTemplate;

	public RedisListSlice(String key, RedisTemplate<String, Object> redisTemplate) {
		this.key = key;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public int totalCount() {
		Number number = redisTemplate.opsForList().size(key);
		return number != null ? number.intValue() : 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(int maxResults, int firstResult) {
		return (List<T>) redisTemplate.opsForList().range(key, firstResult, firstResult + maxResults - 1);
	}

}
