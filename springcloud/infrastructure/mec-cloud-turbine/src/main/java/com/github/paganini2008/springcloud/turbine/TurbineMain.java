package com.github.paganini2008.springcloud.turbine;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 
 * TurbineMain
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2018-05
 * @version 1.0
 */
@SpringBootApplication
@EnableTurbine
@EnableHystrixDashboard
public class TurbineMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File userdir = new File(System.getProperty("user.dir"));
		File logDir = FileUtils.getFile(userdir, "logs", "springcloud", "turbine");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("MEC_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		SpringApplication.run(TurbineMain.class, args);
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
