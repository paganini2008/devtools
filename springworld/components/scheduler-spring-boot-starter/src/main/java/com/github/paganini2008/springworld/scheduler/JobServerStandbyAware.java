package com.github.paganini2008.springworld.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.github.paganini2008.springworld.cluster.ContextMasterStandbyEvent;

/**
 * 
 * JobServerStandbyAware
 * 
 * @author Fred Feng
 * @created 2019-11
 * @revised 2019-11
 * @version 1.0
 */
@Component
public class JobServerStandbyAware implements ApplicationListener<ContextMasterStandbyEvent> {

	@Autowired
	private JobManager jobManager;

	@Override
	public void onApplicationEvent(ContextMasterStandbyEvent event) {
		jobManager.runNow();
	}

}
