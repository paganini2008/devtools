package com.allyes.mec.cloud.code.config;

import java.util.concurrent.ThreadPoolExecutor;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.allyes.mec.common.multithreads.ThreadPoolHolder;
import com.allyes.mec.common.utils.TipInfoService;

/**
 * 
 * SystemConfig
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
@Configuration
public class SystemConfig {

	private static final Logger logger = LoggerFactory.getLogger(SystemConfig.class);

	@Bean(destroyMethod = "close")
	public TipInfoService createTipInfoService(DataSource dataSource) throws Exception {
		TipInfoService tipInfoService = new TipInfoService();
		tipInfoService.setDataSource(dataSource);
		tipInfoService.start(60);
		return tipInfoService;
	}

	@Bean("threadPoolBean")
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(10);
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		return taskExecutor;
	}

	@Bean(destroyMethod = "shutdown")
	@DependsOn("threadPoolBean")
	public ThreadPoolHolder threadPoolHolder() {
		return new ThreadPoolHolder();
	}

}
