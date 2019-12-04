package com.github.paganini2008.springcloud.security;

import java.io.File;
import java.security.Principal;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.springboot.authorization.EnableOAuth2Server;
import com.github.paganini2008.springcloud.security.utils.Env;
import com.github.paganini2008.springworld.config.GitConfigurationSpringApplication;

/**
 * 
 * OAuth2ServerMain
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2018-05
 * @version 1.0
 */
@EnableOAuth2Server
@RestController
@SpringBootApplication
@ComponentScan(basePackages = { "com.github.paganini2008.springcloud.security" })
public class OAuth2ServerMain {
	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "springcloud", "security");
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
