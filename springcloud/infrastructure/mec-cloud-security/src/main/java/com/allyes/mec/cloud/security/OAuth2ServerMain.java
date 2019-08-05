package com.allyes.mec.cloud.security;

import java.io.File;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allyes.components.authorization.EnableOAuth2Server;
import com.allyes.components.config.GitConfigurationSpringApplication;
import com.allyes.developer.utils.Env;
import com.allyes.developer.utils.io.FileUtils;

/**
 * 
 * OAuth2ServerMain
 * 
 * @author Fred Feng
 *
 */
@EnableOAuth2Server
@RestController
@SpringBootApplication
@ComponentScan(basePackages = { "com.allyes.mec.cloud.security" })
public class OAuth2ServerMain {
	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserHome(), "logs", "mec-cloud-security");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("MEC_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		GitConfigurationSpringApplication.run(OAuth2ServerMain.class, args);
		System.out.println(Env.getPid());
	}

	@Value("${spring.profiles.active}")
	private String active;

	@GetMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	@GetMapping("/ping")
	public String ping() {
		return "pong:" + active;
	}
}
