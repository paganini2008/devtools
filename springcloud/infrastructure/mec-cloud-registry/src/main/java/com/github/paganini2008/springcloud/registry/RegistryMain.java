package com.github.paganini2008.springcloud.registry;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.io.FileUtils;

/**
 * 
 * RegistryMain
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2018-05
 * @version 1.0
 */
@RestController
@SpringBootApplication
@EnableEurekaServer
public class RegistryMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "springcloud", "registry");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("MEC_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RegistryMain.class, args);
		System.out.println(getPid());
	}

	@Value("${spring.profiles.active}")
	private String active;

	@GetMapping("/ping")
	public String ping() {
		return "pong:" + active;
	}

	private static int getPid() {
		try {
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			return Integer.parseInt(runtimeMXBean.getName().split("@")[0]);
		} catch (Exception e) {
			return 0;
		}
	}
}
