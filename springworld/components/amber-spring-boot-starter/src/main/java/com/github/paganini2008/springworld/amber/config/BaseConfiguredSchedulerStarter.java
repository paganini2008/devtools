package com.github.paganini2008.springworld.amber.config;

import java.util.concurrent.TimeUnit;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.Observer;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.springworld.amber.annotation.CronJob;
import com.github.paganini2008.springworld.amber.annotation.DelayedJob;
import com.github.paganini2008.springworld.amber.annotation.Job;

/**
 * 
 * BaseConfiguredSchedulerStarter
 * 
 * @author Fred Feng
 * @created 2018-03
 */
public class BaseConfiguredSchedulerStarter extends LoggingSchedulerListener implements BeanPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(BaseConfiguredSchedulerStarter.class);
	public static final String THEME_SCHEDULER_STARTED = "scheduler:started";
	public static final String THEME_SCHEDULER_SHUTINGDOWN = "scheduler:shutdowning";

	private static final String THEME_ADD_CRON_JOB = "scheduler:job:cron";
	private static final String THEME_ADD_DELAYED_JOB = "scheduler:job:delayed";
	private static final String THEME_ADD_SIMPLE_JOB = "scheduler:job:simple";

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private JobManager jobManager;

	private final Observable observable = Observable.unrepeatable();
	
	private boolean standby;
	private boolean cleanAllOnExit = false;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> beanClass = bean.getClass();
		if (beanClass.isAnnotationPresent(Job.class)) {
			logger.info("Find @Job: " + bean.getClass().getName());
			final Job jobDetail = bean.getClass().getAnnotation(Job.class);
			observable.addObserver(THEME_ADD_SIMPLE_JOB, new Observer() {
				public void update(Observable ob, Object arg) {
					final String jobName = StringUtils.isNotBlank(jobDetail.name()) ? jobDetail.name() : bean.getClass().getName();
					try {
						if (!jobManager.checkExists(jobName)) {
							long delayInMs = TimeUnit.MILLISECONDS.convert(jobDetail.delay(), jobDetail.delayTimeUnit());
							long intervalInMs = TimeUnit.MILLISECONDS.convert(jobDetail.interval(), jobDetail.intervalTimeUnit());
							jobManager.addJob(jobName, bean.getClass(), jobDetail.description(), delayInMs, intervalInMs,
									jobDetail.repeatCount(), null);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		} else if (beanClass.isAnnotationPresent(CronJob.class)) {
			logger.info("Find @CronJob: " + bean.getClass().getName());
			final CronJob jobDetail = bean.getClass().getAnnotation(CronJob.class);
			observable.addObserver(THEME_ADD_CRON_JOB, new Observer() {
				public void update(Observable ob, Object arg) {
					final String jobName = StringUtils.isNotBlank(jobDetail.name()) ? jobDetail.name() : bean.getClass().getName();
					try {
						if (!jobManager.checkExists(jobName)) {
							jobManager.addCronJob(jobName, bean.getClass(), jobDetail.description(), jobDetail.cron(), null);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		} else if (beanClass.isAnnotationPresent(DelayedJob.class)) {
			logger.info("Find @DelayedJob: " + bean.getClass().getName());
			final DelayedJob jobDetail = bean.getClass().getAnnotation(DelayedJob.class);
			observable.addObserver(THEME_ADD_DELAYED_JOB, new Observer() {
				public void update(Observable ob, Object arg) {
					final String jobName = StringUtils.isNotBlank(jobDetail.name()) ? jobDetail.name() : bean.getClass().getName();
					try {
						if (!jobManager.checkExists(jobName)) {
							long delay = TimeUnit.MILLISECONDS.convert(jobDetail.delay(), jobDetail.timeUnit());
							jobManager.addDelayedJob(jobName, bean.getClass(), jobDetail.description(), delay, null);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		}
		return bean;
	}

	public void start() throws SchedulerException {
		schedulerFactoryBean.getScheduler().getListenerManager().addSchedulerListener(this);
	}

	public void setCleanAllOnExit(boolean cleanAllOnExit) {
		this.cleanAllOnExit = cleanAllOnExit;
	}

	public void addStateListener(SchedulerStateListener schedulerStateListener) {
		observable.addObserver(THEME_SCHEDULER_STARTED, new Observer() {

			public void update(Observable ob, Object arg) {
				schedulerStateListener.onStart();
			}
		});

		observable.addObserver(THEME_SCHEDULER_SHUTINGDOWN, new Observer() {

			public void update(Observable ob, Object arg) {
				schedulerStateListener.onShutingdown();
			}
		});
	}

	public void schedulerStarted() {
		standby = true;
		observable.notifyObservers(THEME_SCHEDULER_STARTED);

		observable.notifyObservers(THEME_ADD_SIMPLE_JOB);
		observable.notifyObservers(THEME_ADD_CRON_JOB);
		observable.notifyObservers(THEME_ADD_DELAYED_JOB);

		logger.info("Scheduler is started now.");
	}

	public void schedulerShuttingdown() {
		super.schedulerShuttingdown();

		observable.notifyObservers(THEME_SCHEDULER_SHUTINGDOWN);

		if (cleanAllOnExit) {
			try {
				jobManager.pauseAll();
				jobManager.deleteAll();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public boolean isStandby() {
		return standby;
	}

}
