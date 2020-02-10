package com.github.paganini2008.springworld.transport;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 
 * TransportAutoConfiguration
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Import({ TransportServerConfiguration.class, BenchmarkController.class })
@Configuration
public class TransportAutoConfiguration {
}
