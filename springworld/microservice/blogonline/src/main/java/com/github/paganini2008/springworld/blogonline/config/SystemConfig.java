package com.github.paganini2008.springworld.blogonline.config;

import java.util.concurrent.ThreadPoolExecutor;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.github.paganini2008.springworld.support.tip.TipInfoService;

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

}
