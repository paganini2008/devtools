package com.github.paganini2008.springworld.amber.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 
 * NoDispatchingConfig
 * 
 * @author Fred Feng
 * @created 2019-03
 */
@Configuration
@ConditionalOnProperty(prefix = "amber.dispatcher", name = "type", havingValue = "none", matchIfMissing = true)
public class NoDispatchingConfig {

	@Primary
	@Bean("defaultJobDispatcher")
	public JobDispatcher jobDispatcher() {
		return new NoRouteDispatcher();
	}

}
