package com.github.paganini2008.springworld.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * ApplicationPropertiesKeeperConfig
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@Configuration
@ConditionalOnBean(ApplicationProperties.class)
public class ApplicationPropertiesKeeperConfig {

	@Bean
	public ApplicationPropertiesKeeper getApplicationPropertiesKeeper(ApplicationProperties applicationProperties) {
		ApplicationPropertiesKeeper keeper = new ApplicationPropertiesKeeper();
		keeper.setApplicationProperties(applicationProperties);
		return keeper;
	}

}
