package com.github.paganini2008.springworld.cluster;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * ContextClusterConfigProperties
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Getter
public class ContextClusterConfigProperties {

	@Value("${spring.application.name}")
	private String applicationName;
	
	@Value("${spring.application.cluster.namespace:spring:application:cluster}")
	private String namespace;
	
	@Setter
	@Value("${spring.application.cluster.id:}")
	private String id;

	@Value("${spring.application.cluster.weight:1}")
	private int weight;

	public String getApplicationClusterName() {
		return namespace + ":" + applicationName;
	}

}
