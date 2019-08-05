package com.github.paganini2008.springcloud.article.config;

/**
 * 
 * IdGenerator
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface IdGenerator {
	
	long currentValue();

	long nextValue();

	default void destroy() {
	}

}
