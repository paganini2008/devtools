package com.github.paganini2008.springworld.socketbird;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 
 * ImportConfiguration
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Import({ ImportServerConfiguration.class})
@Configuration
public class ImportConfiguration {

}
