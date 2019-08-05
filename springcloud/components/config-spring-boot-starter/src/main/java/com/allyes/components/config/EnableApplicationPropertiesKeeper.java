package com.allyes.components.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 
 * EnableApplicationPropertiesKeeper
 * 
 * @author Fred Feng
 * @created 2019-03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ ApplicationPropertiesConfig.class, ApplicationPropertiesKeeperConfig.class, ConfigController.class })
public @interface EnableApplicationPropertiesKeeper {
}
