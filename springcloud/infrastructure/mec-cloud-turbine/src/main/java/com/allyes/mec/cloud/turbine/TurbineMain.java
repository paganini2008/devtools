package com.allyes.mec.cloud.turbine;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.web.bind.annotation.GetMapping;

import com.allyes.developer.utils.Env;
import com.allyes.developer.utils.StringUtils;

/**
 * 
 * TurbineMain
 * 
 * @author Fred Feng
 *
 */
@SpringBootApplication
@EnableTurbine
@EnableHystrixDashboard
public class TurbineMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File userHome = new File(System.getProperty("user.home"));
		File logDir = FileUtils.getFile(userHome, "logs", "mec-cloud-turbine");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("MEC_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		Map<String, String> params = Env.getInitParameters(args);
		String logdir = params.get("appLogdir");
		if (StringUtils.isNotBlank(logdir)) {
			System.out.println("Ext LogDir: " + logdir);
			System.setProperty("MEC_LOG_BASE", logdir);
		}
		SpringApplication.run(TurbineMain.class, args);
		System.out.println(Env.getPid());
	}
	
	@Value("${spring.profiles.active}")
	private String active;

	@GetMapping("/ping")
	public String ping() {
		return "pong:" + active;
	}
}
