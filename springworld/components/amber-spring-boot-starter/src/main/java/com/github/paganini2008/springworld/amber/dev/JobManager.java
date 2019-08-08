package com.github.paganini2008.springworld.amber.dev;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.scheduler.TaskExecutor;

public class JobManager {

	public JobManager() {
	}

	private final Observable observable = Observable.unrepeatable();
	private TaskExecutor taskExecutor;

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void schedule(final Job job) {
		observable.addObserver((ob, arg) -> {
			taskExecutor.schedule(job, job.cron());
		});
	}

	public void runNow() {
		observable.notifyObservers();
	}

}
