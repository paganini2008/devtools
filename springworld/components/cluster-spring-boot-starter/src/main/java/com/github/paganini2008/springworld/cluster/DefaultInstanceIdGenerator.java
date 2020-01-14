package com.github.paganini2008.springworld.cluster;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * DefaultInstanceIdGenerator
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class DefaultInstanceIdGenerator implements InstanceIdGenerator {

	@Value("${spring.application.name}")
	private String applicationName;

	@Override
	public String generateInstanceId() {
		return applicationName + "@" + UUID.randomUUID().toString().replace("-", "");
	}

}
