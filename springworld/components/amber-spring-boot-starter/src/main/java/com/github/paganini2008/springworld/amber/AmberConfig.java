package com.github.paganini2008.springworld.amber;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 
 * AmberConfig
 * 
 * @author Fred Feng
 * @create 2018-03
 */
@Component
@PropertySource(value = "amber.properties", ignoreResourceNotFound = true)
public class AmberConfig {
}
