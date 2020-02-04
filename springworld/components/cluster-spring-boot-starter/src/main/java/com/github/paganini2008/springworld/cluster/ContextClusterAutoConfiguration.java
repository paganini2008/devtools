package com.github.paganini2008.springworld.cluster;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastConfig;
import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastController;
import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastEventHandlerBeanProcessor;
import com.github.paganini2008.springworld.cluster.pool.ProcessPoolConfig;
import com.github.paganini2008.springworld.cluster.utils.ApplicationContextUtils;

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
@Import({ ContextClusterConfig.class, ContextMulticastConfig.class, ContextMulticastEventHandlerBeanProcessor.class,
		ProcessPoolConfig.class, ContextClusterController.class, ContextMulticastController.class, ApplicationContextUtils.class })
public class ContextClusterAutoConfiguration {
}
