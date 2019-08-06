package com.github.paganini2008.springworld.blogonline;

import java.io.File;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.net.NetUtils;
import com.github.paganini2008.springboot.authorization.EnableOAuth2ResourceServer;
import com.github.paganini2008.springboot.config.EnableApplicationPropertiesKeeper;
import com.github.paganini2008.springboot.config.GitConfigurationSpringApplication;
import com.github.paganini2008.springworld.support.Env;
import com.github.paganini2008.springworld.support.redis.EnableRedisAdvancedFeatures;

/**
 * 
 * CommunityApiMain
 * 
 * @author Fred Feng
 * @created 2019-06
 * @version 2.0.0
 */
@EnableOAuth2ResourceServer
@EnableRedisAdvancedFeatures
@EnableApplicationPropertiesKeeper
@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = { "com.github.paganini2008.springcloud" })
public class BlogApiMain {
	
	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserHome(), "logs", "mec-cloud-biz");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("MEC_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		final int port = NetUtils.getRandomPort(Constants.MICROSERVICE_RANDOM_PORT_START, Constants.MICROSERVICE_BIZ_RANDOM_PORT_END);
		System.out.println("Server Port: " + port);
		System.setProperty("server.port", String.valueOf(port));
		GitConfigurationSpringApplication.run(BlogApiMain.class, args);
		System.out.println(Env.getPid());
	}
}
