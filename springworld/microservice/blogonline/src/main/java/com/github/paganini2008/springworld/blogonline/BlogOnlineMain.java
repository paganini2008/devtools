package com.github.paganini2008.springworld.blogonline;

import java.io.File;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.net.NetUtils;
import com.github.paganini2008.springboot.authorization.EnableOAuth2ResourceServer;
import com.github.paganini2008.springworld.config.EnableApplicationPropertiesKeeper;
import com.github.paganini2008.springworld.config.GitConfigurationSpringApplication;
import com.github.paganini2008.springworld.support.Env;

/**
 * 
 * BlogOnlineMain
 *
 * @author Fred Feng
 * @created 2019-07
 * @revised 2019-08
 * @version 1.0
 */
@EnableOAuth2ResourceServer
@EnableApplicationPropertiesKeeper
@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = { "com.github.paganini2008.springworld" })
public class BlogOnlineMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "springworld", "blogonline");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("DEFAULT_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		final int port = NetUtils.getRandomPort(Constants.MICROSERVICE_RANDOM_PORT_START, Constants.MICROSERVICE_BIZ_RANDOM_PORT_END);
		System.out.println("Server Port: " + port);
		System.setProperty("server.port", String.valueOf(port));
		GitConfigurationSpringApplication.run(BlogOnlineMain.class, args);
		System.out.println(Env.getPid());
	}
}
