package com.github.paganini2008.springworld.decentration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.paganini2008.springworld.decentration.implementation.redis.RedisDecentrationImplementor;

/**
 * 
 * DecentrationAutoConfiguration
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Configuration
@Import(RedisDecentrationImplementor.class)
public class DecentrationAutoConfiguration {
}
