package com.github.paganini2008.springworld.crontab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.github.paganini2008.springworld.cluster.ContextMasterStandbyEvent;

/**
 * 
 * JobServerStandbyAware
 *
 * @author Fred Feng
 * @created 2018-03
 * @revised 2019-08
 * @version 1.0
 */
@Component
public class JobServerStandbyAware implements ApplicationListener<ContextMasterStandbyEvent> {

	@Autowired
	private JobManager jobManager;

	@Override
	public void onApplicationEvent(ContextMasterStandbyEvent event) {
		if (jobManager instanceof PersistentJobsInitializer) {
			((PersistentJobsInitializer) jobManager).reloadPersistentJobs();
		}
		jobManager.runNow();
	}

}
