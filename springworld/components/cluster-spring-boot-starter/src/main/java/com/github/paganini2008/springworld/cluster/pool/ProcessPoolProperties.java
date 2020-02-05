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
@ConfigurationProperties(prefix = "spring.application.cluster.pool", ignoreUnknownFields = true)
public class ProcessPoolProperties {

	public static final int DEFUALT_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
	public static final int DEFAULT_QUEUE_SIZE = Integer.MAX_VALUE;

	private int poolSize = DEFUALT_POOL_SIZE;
	private int timeout = -1;
	private int queueSize = DEFAULT_QUEUE_SIZE;

}
