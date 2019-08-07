package com.github.paganini2008.springworld.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.github.paganini2008.springworld.config.examples.ConfigTestController;

/**
 * 
 * EnableApplicationPropertiesKeeper
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ ApplicationPropertiesConfig.class, ApplicationPropertiesKeeperConfig.class, ConfigController.class, ConfigTestController.class })
public @interface EnableApplicationPropertiesKeeper {
}
