package com.github.paganini2008.springworld.crontab;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.io.SerializationUtils;
import com.github.paganini2008.devtools.jdbc.DBUtils;
import com.github.paganini2008.devtools.scheduler.GenericTaskExecutor;
import com.github.paganini2008.devtools.scheduler.ScheduleException;
import com.github.paganini2008.devtools.scheduler.TaskExecutor;
import com.github.paganini2008.devtools.scheduler.TaskInterceptorHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JdbcJobManager
 *
 * @author Fred Feng
 * @created 2018-03
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class JdbcJobManager implements JobManager, ApplicationContextAware, TaskInterceptorHandler {

	private static final String SQL_SELECT_ALL = "select * from cron_job_detail where del=0";
	private static final String SQL_INSERT = "insert into cron_job_detail(name,descrption,job_class,cron) values (?,?,?,?)";
	private static final String SQL_CHECK_EXISTS = "select count(*) from cron_job_detail where name=?";
	private static final String SQL_SELECT_NAMES = "select name from cron_job_detail";
	private static final String SQL_DELETE = "delete from cron_job_detail where name=?";
	private final Observable observable = Observable.unrepeatable();
	private final Map<Job, TaskExecutor.TaskFuture> store = new ConcurrentHashMap<Job, TaskExecutor.TaskFuture>();
	private TaskExecutor taskExecutor;
	private DataSource dataSource;
	private ApplicationContext context;

	public JdbcJobManager() {
		taskExecutor = new GenericTaskExecutor(8, "crontab");
		taskExecutor.setTaskInterceptorHandler(this);
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		if (taskExecutor != null) {
			taskExecutor.setTaskInterceptorHandler(this);
			this.taskExecutor = taskExecutor;
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void reload() {
		Iterator<Tuple> iterator;
		try {
			iterator = DBUtils.executeQuery(dataSource.getConnection(), SQL_SELECT_ALL);
		} catch (SQLException e) {
			throw new ScheduleException(e.getMessage(), e);
		}
		while (iterator.hasNext()) {
			Tuple tuple = iterator.next();
			reloadJob(tuple);
		}
		log.info("Reload and schedule all jobs ok.");
	}

	private void reloadJob(Tuple tuple) {
		String jobClassName = (String) tuple.get("job_class");
		Class<?> jobClass = null;
		try {
			jobClass = Class.forName(jobClassName);
		} catch (ClassNotFoundException e) {
			log.warn("JobClass '" + jobClassName + "'seems be missing.");
			return;
		}
		if (!Job.class.isAssignableFrom(jobClass)) {
			throw new ScheduleException("Class '" + jobClass.getName() + "' is not a jobClass.");
		}
		if (context.getBean(jobClass) != null) {
			return;
		}
		Job job = (Job) BeanUtils.instantiate(jobClass);
		observable.addObserver((ob, arg) -> {
			store.put(job, taskExecutor.schedule(job, job.cron()));
		});
		log.info("Reload and schedule job: " + job.getClass().getName() + " from db.");
	}

	public void schedule(final Job... jobs) throws ScheduleException {
		for (Job job : jobs) {
			if (hasJob(job)) {
				throw new ScheduleException("Job '" + job.name() + "' exists.");
			}
			observable.addObserver((ob, arg) -> {
				store.put(job, taskExecutor.schedule(job, job.cron()));
			});
			try {
				DBUtils.executeUpdate(dataSource.getConnection(), SQL_INSERT, ps -> {
					ps.setString(1, job.name());
					ps.setString(2, job.description());
					ps.setString(3, job.getClass().getName());
					ps.setBytes(4, SerializationUtils.toByteArray(job.cron()));
				});
				log.info("Schedule job: " + job.getClass().getName() + ", current job size: " + countOfJobs());
			} catch (SQLException e) {
				throw new ScheduleException("Failed to save job detail to db.", e);
			}
		}
	}

	public void runNow() {
		observable.notifyObservers();
		log.info("Run all jobs now.");
	}

	public int countOfJobs() {
		return jobNames().length;
	}

	public String[] jobNames() {
		List<String> names = new ArrayList<String>();
		Iterator<Tuple> iterator;
		try {
			iterator = DBUtils.executeQuery(dataSource.getConnection(), SQL_SELECT_NAMES);
		} catch (SQLException e) {
			throw new ScheduleException(e.getMessage(), e);
		}
		while (iterator.hasNext()) {
			names.add((String) iterator.next().get("name"));
		}
		return names.toArray(new String[0]);
	}

	public boolean hasJob(Job job) {
		try {
			Object result = DBUtils.executeOneResultQuery(dataSource.getConnection(), SQL_CHECK_EXISTS, new Object[] { job.name() });
			return result instanceof Number ? ((Number) result).intValue() > 0 : false;
		} catch (SQLException e) {
			throw new ScheduleException(e.getMessage(), e);
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	public void pauseJob(Job job) {
		if (hasJob(job)) {
			store.get(job).pause();
		}
	}

	public void resumeJob(Job job) {
		if (hasJob(job)) {
			store.get(job).resume();
		}
	}

	public void deleteJob(Job job) {
		if (hasJob(job)) {
			taskExecutor.removeSchedule(job);
			store.remove(job);
			try {
				DBUtils.executeUpdate(dataSource.getConnection(), SQL_DELETE, new Object[] { job.name() });
				log.info("Delete job: " + job.name());
			} catch (SQLException e) {
				throw new ScheduleException(e.getMessage(), e);
			}
		}
	}

	public void close() {
		taskExecutor.close();
	}

}
