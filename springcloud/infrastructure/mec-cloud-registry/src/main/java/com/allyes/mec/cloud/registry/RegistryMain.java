package com.allyes.mec.cloud.registry;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allyes.developer.utils.Env;
import com.allyes.developer.utils.io.FileUtils;

/**
 * 
 * RegistryMain
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@RestController
@SpringBootApplication
@EnableEurekaServer
public class RegistryMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserHome(), "logs", "mec-cloud-registry");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("MEC_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RegistryMain.class, args);
		System.out.println(Env.getPid());
	}

	@Value("${spring.profiles.active}")
	private String active;

	@GetMapping("/ping")
	public String ping() {
		return "pong:" + active;
	}
}
