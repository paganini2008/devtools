package com.github.paganini2008.springworld.webcrawler.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.springworld.cluster.ContextMasterStandbyEvent;
import com.github.paganini2008.springworld.webcrawler.utils.RedisIdentifier;

/**
 * 
 * CrawlerContextInitializer
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class CrawlerContextInitializer implements ApplicationListener<ContextMasterStandbyEvent> {

	@Autowired
	private RedisIdentifier redisIdentifier;

	@Override
	public void onApplicationEvent(ContextMasterStandbyEvent event) {
		configure(event.getApplicationContext());
	}

	protected void configure(ApplicationContext applicationContext) {
		initializeRedisIdentifier();
	}

	private void initializeRedisIdentifier() {
		long timeMillis = (System.currentTimeMillis() * 10000) + 0x01B21DD213814000L;
		long currentTimestamp = redisIdentifier.currentValue();
		if (timeMillis - currentTimestamp > 5000 * 10000) {
			redisIdentifier.setValue(timeMillis);
		}
	}

}
