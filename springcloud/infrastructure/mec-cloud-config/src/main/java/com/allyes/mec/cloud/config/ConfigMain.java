package com.allyes.mec.cloud.config;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allyes.developer.utils.Env;

/**
 * 
 * ConfigMain
 * 
 * @author Fred Feng
 *
 */
@RestController
@SpringBootApplication
@EnableConfigServer
public class ConfigMain {
	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File userHome = new File(System.getProperty("user.home"));
		File logDir = FileUtils.getFile(userHome, "logs", "mec-cloud-config");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("MEC_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		SpringApplication.run(ConfigMain.class, args);
		System.out.println(Env.getPid());
	}

	@Value("${spring.profiles.active}")
	private String active;

	@GetMapping("/ping")
	public String ping() {
		return "pong:" + active;
	}
}
