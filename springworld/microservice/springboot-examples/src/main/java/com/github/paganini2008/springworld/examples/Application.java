package com.github.paganini2008.springworld.examples;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.net.NetUtils;
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
@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = { "com.github.paganini2008.springworld.examples" })
public class Application {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "springworld", "examples");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("DEFAULT_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		final int port = NetUtils.getRandomPort(Constants.MICROSERVICE_RANDOM_PORT_START, Constants.MICROSERVICE_BIZ_RANDOM_PORT_END);
		System.out.println("Server Port: " + port);
		System.setProperty("server.port", String.valueOf(port));
		SpringApplication.run(Application.class, args);
		System.out.println(Env.getPid());
	}
}
