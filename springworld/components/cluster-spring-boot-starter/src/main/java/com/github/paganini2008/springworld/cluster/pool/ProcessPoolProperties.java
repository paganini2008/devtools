package com.github.paganini2008.springworld.cluster.pool;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 
 * ProcessPoolProperties
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "spring.application.cluster.pool")
public class ProcessPoolProperties {

	private int maxPoolSize;
	private int timeout;
	private int queueSize;
	
}
