package com.github.paganini2008.springworld.scheduler.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.springworld.cluster.implementor.ContextClusterMulticastChannelGroup;

/**
 * 
 * LoadBalancedQuartzJobBeanProxy
 * 
 * @author Fred Feng
 * @created 2019-11
 * @revised 2019-11
 * @version 1.0
 */
public class LoadBalancedQuartzJobBeanProxy implements Job {

	@Autowired
	private ContextClusterMulticastChannelGroup multicastChannelGroup;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String jobBeanName = (String) dataMap.get("jobBeanName");
		String jobBeanClassName = (String) dataMap.get("jobBeanClassName");
		Tuple tuple = Tuple.newTuple();
		tuple.set("jobBeanClassName", jobBeanClassName);
		tuple.set("jobBeanName", jobBeanName);
		multicastChannelGroup.unicast(tuple);
	}

}
