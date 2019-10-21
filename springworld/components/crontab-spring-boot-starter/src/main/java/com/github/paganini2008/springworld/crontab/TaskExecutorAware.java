package com.github.paganini2008.springworld.crontab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.github.paganini2008.springworld.cluster.ContextClusterMasterStandbyEvent;

/**
 * 
 * TaskExecutorAware
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Component
public class TaskExecutorAware implements ApplicationListener<ContextClusterMasterStandbyEvent> {

	@Autowired
	private JobManager jobManager;

	@Override
	public void onApplicationEvent(ContextClusterMasterStandbyEvent event) {
		jobManager.runNow();
	}

}
