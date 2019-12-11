package com.github.paganini2008.springworld.redisplus;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 
 * RedisMessageAutoConfiguration
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Configuration
@Import({ RedisMessageConfig.class, RedisMessageListenerBeanProcessor.class })
public class RedisMessageAutoConfiguration {

}
