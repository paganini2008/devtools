package com.allyes.components.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * ApplicationPropertiesKeeperConfig
 * 
 * @author Fred Feng
 * @created 2019-03
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
