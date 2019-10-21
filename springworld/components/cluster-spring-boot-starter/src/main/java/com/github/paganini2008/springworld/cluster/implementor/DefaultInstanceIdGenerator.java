package com.github.paganini2008.springworld.cluster.implementor;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.StringUtils;

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

	@Value("${spring.application.instanceId:}")
	private String instanceId;

	@Override
	public String generateInstanceId() {
		return StringUtils.isNotBlank(instanceId) ? instanceId : UUID.randomUUID().toString();
	}

}
