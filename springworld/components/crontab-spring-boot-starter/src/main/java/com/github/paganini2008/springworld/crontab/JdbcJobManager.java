package com.github.paganini2008.springworld.crontab;

import java.sql.Connection;
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

import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.io.SerializationUtils;
import com.github.paganini2008.devtools.jdbc.DBUtils;
import com.github.paganini2008.devtools.scheduler.GenericTaskExecutor;
import com.github.paganini2008.devtools.scheduler.SchedulingException;
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
public class JdbcJobManager implements JobManager, PersistentJobsInitializer, TaskInterceptorHandler, ApplicationContextAware {
	public static final String DEFAULT_TABLE_NAME = "cron_job_detail";

	private static final String DEF_CREATE_TABLE_SQL = "create table cron_job_detail(job_name varchar(255) unique not null, job_descrption varchar(255), job_class varchar(255) not null, job_cron blob not null)";
	private static final String DEF_SELECT_JOBS_SQL = "select * from cron_job_detail";
	private static final String DEF_INSERT_JOB_SQL = "insert into cron_job_detail(name,descrption,job_class,cron) values (?,?,?,?)";
	private static final String DEF_CHECK_JOB_EXISTS_SQL = "select count(*) from cron_job_detail where name=?";
	private static final String DEF_SELECT_JOB_NAMES_SQL = "select name from cron_job_detail";
	private static final String DEF_DELETE_JOB_SQL = "delete from cron_job_detail where name=?";

	private final Observable observable = Observable.unrepeatable();
	private final Map<Job, TaskExecutor.TaskFuture> store = new ConcurrentHashMap<Job, TaskExecutor.TaskFuture>();
	private final TaskExecutor taskExecutor;
	private DataSource dataSource;
	private ApplicationContext context;

	public JdbcJobManager() {
		this(8);
	}

	public JdbcJobManager(int nThreads) {
		taskExecutor = new GenericTaskExecutor(nThreads, "crontab");
		taskExecutor.setTaskInterceptorHandler(this);
	}

	public void setDataSource(DataSource dataSource) throws SQLException {
		if (dataSource != null) {
			this.dataSource = dataSource;

			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				if (!DBUtils.existsTable(connection, null, DEFAULT_TABLE_NAME)) {
					DBUtils.executeUpdate(connection, DEF_CREATE_TABLE_SQL);
				}
			} finally {
				DBUtils.closeQuietly(connection);
			}
		}
	}

	public void reloadPersistentJobs() {
		Iterator<Tuple> iterator;
		try {
			iterator = DBUtils.executeQuery(dataSource.getConnection(), DEF_SELECT_JOBS_SQL);
		} catch (SQLException e) {
			throw new SchedulingException(e.getMessage(), e);
		}
		while (iterator.hasNext()) {
			Tuple tuple = iterator.next();
			scheduleCustomizedJob(tuple);
		}
		log.info("Reload and schedule all customized jobs ok.");
	}

	private void scheduleCustomizedJob(Tuple tuple) {
		String jobName = (String) tuple.get("jobName");
		String jobClassName = (String) tuple.get("jobClass");
		Class<?> jobClass = ClassUtils.forName(jobClassName);
		if (!Job.class.isAssignableFrom(jobClass)) {
			throw new SchedulingException("Class '" + jobClass.getName() + "' is not a implementor of Job interface.");
		}

		if (context.getBean(jobName, jobClass) != null) {
			return;
		}

		Job job = (Job) BeanUtils.instantiate(jobClass);
		observable.addObserver((ob, arg) -> {
			if (!store.containsKey(job)) {
				store.put(job, taskExecutor.schedule(job, job.getCronExpression()));
			}
		});
		log.info("Reload and schedule job: " + job.getClass().getName() + " from db.");
	}

	public void schedule(final Job... jobs) throws SchedulingException {
		for (Job job : jobs) {
			checkJobNameIfBlank(job);
			observable.addObserver((ob, arg) -> {
				if (!store.containsKey(job)) {
					store.put(job, taskExecutor.schedule(job, job.getCronExpression()));
				}
			});
			if (!hasJob(job)) {
				try {
					DBUtils.executeUpdate(dataSource.getConnection(), DEF_INSERT_JOB_SQL, ps -> {
						ps.setString(1, job.getName());
						ps.setString(2, job.getDescription());
						ps.setString(3, job.getClass().getName());
						ps.setBytes(4, SerializationUtils.serialize(job.getCronExpression(), false));
					});
					log.info("Save job: " + job.getClass().getName() + ", current job size: " + countOfJobs());
				} catch (SQLException e) {
					throw new SchedulingException("Failed to save job detail to database.", e);
				}
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
			iterator = DBUtils.executeQuery(dataSource.getConnection(), DEF_SELECT_JOB_NAMES_SQL);
		} catch (SQLException e) {
			throw new SchedulingException(e.getMessage(), e);
		}
		while (iterator.hasNext()) {
			names.add((String) iterator.next().get("name"));
		}
		return names.toArray(new String[0]);
	}

	public boolean hasJob(Job job) {
		try {
			Object result = DBUtils.executeOneResultQuery(dataSource.getConnection(), DEF_CHECK_JOB_EXISTS_SQL,
					new Object[] { job.getName() });
			return result instanceof Number ? ((Number) result).intValue() > 0 : false;
		} catch (SQLException e) {
			throw new SchedulingException(e.getMessage(), e);
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
				DBUtils.executeUpdate(dataSource.getConnection(), DEF_DELETE_JOB_SQL, new Object[] { job.getName() });
				log.info("Delete job: " + job.getName());
			} catch (SQLException e) {
				throw new SchedulingException(e.getMessage(), e);
			}
		}
	}
	
	private void checkJobNameIfBlank(Job job) {
		if (StringUtils.isBlank(job.getName())) {
			throw new SchedulingException("Job name is blank for class: " + job.getClass().getName());
		}
	}

	public void close() {
		taskExecutor.close();
	}

}
