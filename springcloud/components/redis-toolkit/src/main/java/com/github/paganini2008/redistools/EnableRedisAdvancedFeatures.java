package com.github.paganini2008.redistools;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 
 * EnableRedisAdvancedFeatures
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ EnhancedRedisConfig.class, RedisMessageSenderTestController.class })
public @interface EnableRedisAdvancedFeatures {

}
