package com.github.paganini2008.springworld.cluster;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.paganini2008.springworld.cluster.implementor.ContextClusterMasterSlavesConfiguration;
import com.github.paganini2008.springworld.cluster.implementor.ContextClusterMulticastConfiguration;

/**
 * 
 * ContextClusterAutoConfiguration
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Configuration
@Import({ ContextClusterMasterSlavesConfiguration.class, ContextClusterMulticastConfiguration.class, MulticastMessageDelivery.class,
		ApplicationContextUtils.class })
public class ContextClusterAutoConfiguration {
}
