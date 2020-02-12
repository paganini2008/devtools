package com.github.paganini2008.springworld.crontab;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.jdbc.DBUtils;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;
import com.github.paganini2008.devtools.scheduler.SchedulingException;
import com.github.paganini2008.devtools.scheduler.TaskExecutor;
import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskDetail;
import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskFuture;
import com.github.paganini2008.devtools.scheduler.TaskInterceptorHandler;
import com.github.paganini2008.devtools.scheduler.ThreadPoolTaskExecutor;

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
public class JdbcJobManager implements PersistentJobManager, PersistentJobsInitializer, TaskInterceptorHandler, ApplicationContextAware {

	private static final String DEF_DDL_CRON_JOB_DETAIL_SQL = "create table cron_job_detail(job_name varchar(255) unique not null, job_descrption varchar(255), job_class varchar(255) not null, running bit not null, paused bit not null, completed_count int default 0, failed_count int default 0, last_executed timestamp, next_executed timestamp)";

	private static final String DEF_INSERT_JOB_SQL = "insert into cron_job_detail(job_name,descrption,job_class,running,paused) values (?,?,?,?,?)";
	private static final String DEF_CHECK_JOB_EXISTS_SQL = "select count(*) from cron_job_detail where job_name=?";
	private static final String DEF_SELECT_JOB_NAMES_SQL = "select job_name from cron_job_detail";
	private static final String DEF_SELECT_JOBS_SQL = "select * from cron_job_detail";
	private static final String DEF_DELETE_JOB_SQL = "delete from cron_job_detail where job_name=?";
	private static final String DEF_UPDATE_JOB_SQL = "update cron_job_detail set running=?, paused=?, completed_count=?, failed_count=?, last_executed=?, next_executed=? where job_name=?";

	private final Observable observable = Observable.unrepeatable();
	private final TaskExecutor taskExecutor;
	private DataSource dataSource;
	private ApplicationContext context;
	private final Date startDate;

	public JdbcJobManager() {
		this(8);
	}

	public JdbcJobManager(int nThreads) {
		taskExecutor = new ThreadPoolTaskExecutor(nThreads, "crontab");
		taskExecutor.setTaskInterceptorHandler(this);
		this.startDate = new Date();
	}

	public void setDataSource(DataSource dataSource, boolean autoDDL) throws SQLException {
		if (dataSource != null) {
			this.dataSource = dataSource;
			if (autoDDL) {
				Connection connection = null;
				try {
					connection = dataSource.getConnection();
					if (!DBUtils.existsTable(connection, null, "cron_job_detail")) {
						DBUtils.executeUpdate(connection, DEF_DDL_CRON_JOB_DETAIL_SQL);
					}
				} finally {
					DBUtils.closeQuietly(connection);
				}
			}
		}
	}

	public void reloadPersistentJobs() {
		Connection connection = null;
		List<Tuple> dataList = null;
		try {
			connection = dataSource.getConnection();
			Iterator<Tuple> iterator = DBUtils.executeQuery(connection, DEF_SELECT_JOBS_SQL);
			dataList = CollectionUtils.toList(iterator);
		} catch (SQLException e) {
			throw new SchedulingException(e.getMessage(), e);
		} finally {
			DBUtils.closeQuietly(connection);
		}
		if (CollectionUtils.isNotCollection(dataList)) {
			for (Tuple tuple : dataList) {
				reloadPersistentJob(tuple);
			}
		}
		log.info("Reload and schedule all customized jobs ok.");
	}

	private void reloadPersistentJob(Tuple tuple) {
		final String jobName = (String) tuple.get("jobName");
		final String jobClassName = (String) tuple.get("jobClass");

		Class<?> jobClass = ClassUtils.forName(jobClassName);
		if (!Job.class.isAssignableFrom(jobClass)) {
			throw new SchedulingException("Class '" + jobClass.getName() + "' is not a implementor of Job interface.");
		}

		if (context.getBean(jobClass) != null) {
			return;
		}

		Job job = (Job) BeanUtils.instantiate(jobClass);
		observable.addObserver((ob, arg) -> {
			if (!hasScheduled(job)) {
				taskExecutor.schedule(job, job.getCronExpression());
				log.info("Schedule job '" + job.getName() + "' ok. Currently scheduling's size is " + countOfScheduling());
			}
		});
		log.info("Reload job '" + jobName + "' from database ok.");
	}

	public void save(Job job) {
		if (!hasJob(job)) {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
				DBUtils.executeUpdate(connection, DEF_INSERT_JOB_SQL, ps -> {
					ps.setString(1, job.getName());
					ps.setString(2, job.getDescription());
					ps.setString(3, job.getClass().getName());
					ps.setBoolean(4, false);
					ps.setBoolean(5, false);
				});
				connection.commit();
				log.info("Save job '" + job.getName() + "' ok.");
			} catch (SQLException e) {
				DBUtils.rollbackQuietly(connection);
				throw new SchedulingException("Failed to save job detail to database.", e);
			} finally {
				DBUtils.closeQuietly(connection);
			}
		}
	}

	public void schedule(final Job job) throws SchedulingException {
		checkJobNameIfBlank(job);
		observable.addObserver((ob, arg) -> {
			save(job);

			if (!hasScheduled(job)) {
				taskExecutor.schedule(job, job.getCronExpression());
				log.info("Schedule job '" + job.getName() + "' ok. Currently scheduling's size is " + countOfScheduling());
			}
		});
	}

	public void runNow() {
		observable.notifyObservers();
		log.info("Run all jobs now.");
	}

	public int countOfScheduling() {
		return taskExecutor.taskCount();
	}

	public boolean hasScheduled(Job job) {
		return taskExecutor.hasScheduled(job);
	}

	public String[] jobNames() {
		List<String> names = new ArrayList<String>();
		Iterator<Tuple> iterator;
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			iterator = DBUtils.executeQuery(connection, DEF_SELECT_JOB_NAMES_SQL);
			while (iterator.hasNext()) {
				names.add((String) iterator.next().get("jobName"));
			}
		} catch (SQLException e) {
			throw new SchedulingException(e.getMessage(), e);
		} finally {
			DBUtils.closeQuietly(connection);
		}
		return names.toArray(new String[0]);
	}

	public boolean hasJob(Job job) {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Object result = DBUtils.executeOneResultQuery(connection, DEF_CHECK_JOB_EXISTS_SQL, new Object[] { job.getName() });
			return result instanceof Number ? ((Number) result).intValue() > 0 : false;
		} catch (SQLException e) {
			throw new SchedulingException(e.getMessage(), e);
		} finally {
			DBUtils.closeQuietly(connection);
		}
	}

	public void pauseJob(Job job) {
		if (hasScheduled(job)) {
			taskExecutor.getTaskFuture(job).pause();
		}
	}

	public void resumeJob(Job job) {
		if (hasScheduled(job)) {
			taskExecutor.getTaskFuture(job).resume();
		}
	}

	public void unscheduleJob(Job job) {
		if (hasScheduled(job)) {
			taskExecutor.removeSchedule(job);
			log.info("Unschedule job '" + job.getName() + "' ok.");
		}
	}

	public void deleteJob(Job job) {
		if (hasJob(job)) {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils.executeUpdate(connection, DEF_DELETE_JOB_SQL, new Object[] { job.getName() });
				log.info("Delete job '" + job.getName() + "' ok.");
			} catch (SQLException e) {
				throw new SchedulingException(e.getMessage(), e);
			} finally {
				DBUtils.closeQuietly(connection);
			}
		}

		unscheduleJob(job);
	}

	public Date getStartDate() {
		return startDate;
	}

	private static void checkJobNameIfBlank(Job job) {
		if (StringUtils.isBlank(job.getName())) {
			throw new SchedulingException("Job name is not blank for class: " + job.getClass().getName());
		}
	}

	public void close() {
		if (taskExecutor != null) {
			taskExecutor.close();
		}
	}

	public void beforeJobExecution(TaskFuture future) {
		final Job job = (Job) future.getDetail().getTaskObject();
		final TaskDetail taskDetail = future.getDetail();
		if (!hasJob(job)) {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils.executeUpdate(connection, DEF_UPDATE_JOB_SQL, ps -> {
					ps.setBoolean(1, taskDetail.isRunning());
					ps.setBoolean(2, future.isPaused());
					ps.setInt(3, taskDetail.completedCount());
					ps.setInt(4, taskDetail.failedCount());
					ps.setTimestamp(5, new Timestamp(taskDetail.lastExecuted()));
					ps.setTimestamp(6, new Timestamp(taskDetail.nextExecuted()));
					ps.setString(7, job.getName());
				});
			} catch (SQLException e) {
				throw new SchedulingException("Failed to save job detail to database.", e);
			} finally {
				DBUtils.closeQuietly(connection);
			}
		}
	}

	public void afterJobExecution(TaskFuture future) {
		beforeJobExecution(future);
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	public ResultSetSlice<JobInfo> getJobInfos() {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			final ResultSetSlice<Tuple> delegate = DBUtils.pagingQuery(connection, DEF_SELECT_JOBS_SQL, (Object[]) null);
			return new ResultSetSlice<JobInfo>() {

				@Override
				public int totalCount() {
					return delegate.totalCount();
				}

				@Override
				public List<JobInfo> list(int maxResults, int firstResult) {
					List<JobInfo> dataList = new ArrayList<JobInfo>(maxResults);
					for (Tuple tuple : delegate.list(maxResults, firstResult)) {
						JobInfo jobInfo = tuple.toBean(JobInfo.class);
						jobInfo.setStartDate(startDate);
						dataList.add(jobInfo);
					}
					return dataList;
				}

			};
		} catch (SQLException e) {
			throw new SchedulingException(e.getMessage(), e);
		} finally {
			DBUtils.closeQuietly(connection);
		}
	}

}
