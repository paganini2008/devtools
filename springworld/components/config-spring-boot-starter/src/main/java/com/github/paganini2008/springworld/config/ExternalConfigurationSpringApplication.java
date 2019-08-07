package com.github.paganini2008.springworld.config;

import java.io.IOError;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.SystemPropertyUtils;
import com.github.paganini2008.devtools.io.ResourceUtils;

/**
 * 
 * ExternalConfigurationSpringApplication
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public abstract class ExternalConfigurationSpringApplication extends SpringApplication {

	private static final String CONFIG_NAME = "springboot-cfg.properties";
	private static final String CONFIG_NAME_PATTERN = "springboot-cfg-%s.properties";
	private static final String CURRENT_APP_ENV = "spring.profiles.active";
	private static final String CURRENT_APP_NAME = "spring.application.name";
	static final String CONFIGURATION_BOOT_NAME = "applicationBootstrapConfig";

	protected ExternalConfigurationSpringApplication(Class<?>... mainClasses) {
		super(mainClasses);
	}

	@Override
	protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
		super.configureEnvironment(environment, args);

		final Map<String, String> bootConfig;
		try {
			bootConfig = ResourceUtils.getResource(CONFIG_NAME);
		} catch (Exception e) {
			throw new IOError(e);
		}

		String[] activeProfiles = environment.getActiveProfiles();
		String env = activeProfiles != null && activeProfiles.length > 0 ? activeProfiles[0] : "";
		if (StringUtils.isBlank(env)) {
			env = SystemPropertyUtils.getString(CURRENT_APP_ENV);
		}
		if (StringUtils.isBlank(env)) {
			env = bootConfig.get(CURRENT_APP_ENV);
		}
		if (StringUtils.isBlank(env)) {
			env = "dev";
		}

		System.setProperty(CURRENT_APP_ENV, env);
		bootConfig.put(CURRENT_APP_ENV, env);
		try {
			Map<String, String> localConfig = ResourceUtils.getResource(String.format(CONFIG_NAME_PATTERN, env));
			bootConfig.putAll(localConfig);
		} catch (Exception ignored) {
		}

		String applicationName = environment.getProperty(CURRENT_APP_NAME);
		if (StringUtils.isBlank(applicationName)) {
			applicationName = bootConfig.get(CURRENT_APP_NAME);
		}
		if (StringUtils.isBlank(applicationName)) {
			throw new IllegalStateException("ApplicationName must be defined.");
		}

		environment.getPropertySources()
				.addLast(new OriginTrackedMapPropertySource(CONFIGURATION_BOOT_NAME, bootConfig));
		try {
			applySettings(applicationName, env, environment);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to apply settings from remote for this application", e);
		}

	}

	protected abstract void applySettings(String applicationName, String env, ConfigurableEnvironment environment)
			throws Exception;

}
