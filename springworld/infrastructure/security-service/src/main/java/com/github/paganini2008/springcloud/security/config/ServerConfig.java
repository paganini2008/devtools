package com.github.paganini2008.springcloud.security.config;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import io.undertow.UndertowOptions;

/**
 * 
 * ServerConfig
 * 
 * @author Fred Feng
 * @create 2019-03
 */
@Configuration
public class ServerConfig {

	@Primary
	@Bean
	public UndertowServletWebServerFactory undertowServletWebServerFactory(Environment environment, ServerProperties serverProperties) {
		UndertowServletWebServerFactory serverFactory = new UndertowServletWebServerFactory();
		final int port = environment.getRequiredProperty("server.port", Integer.class);
		serverFactory.setPort(port);
		serverFactory.setIoThreads(4);
		serverFactory.setWorkerThreads(50);
		serverFactory.setUseDirectBuffers(true);
		serverFactory.setBufferSize(1024);
		serverFactory.addBuilderCustomizers((builder) -> {
			builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE, 100L * 1024 * 1024);
		});
		return serverFactory;
	}

}
