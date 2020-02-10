package com.github.paganini2008.springworld.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.paganini2008.springworld.redis.pubsub.RedisPubSubConfig;

/**
 * 
 * RedisPlusAutoConfiguration
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Configuration
@Import({ RedisPubSubConfig.class })
public class RedisPlusAutoConfiguration {

}
