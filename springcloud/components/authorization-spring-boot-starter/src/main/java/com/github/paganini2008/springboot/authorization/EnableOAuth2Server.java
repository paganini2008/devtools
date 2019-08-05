package com.github.paganini2008.springboot.authorization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 
 * EnableOAuth2Server
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OAuth2ServerConfig.class)
public @interface EnableOAuth2Server {
}
