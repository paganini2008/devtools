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
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.jdbc.DBUtils;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;
import com.github.paganini2008.devtools.scheduler.SchedulingException;
import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskDetail;
import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskFuture;

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
public class JdbcJobManager extends AbstractJobManager implements PersistentJobManager, PersistentJobsInitializer, ApplicationContextAware {

	public static String DEF_DDL_CRON_JOB_DETAIL_SQL = "create table cron_job_detail(job_name varchar(255) unique not null, job_description varchar(255), job_class varchar(255) not null, running tinyint(1) not null, paused tinyint(1) not null, completed_count int default 0, failed_count int default 0, last_executed timestamp null, next_executed timestamp null, create_date timestamp default current_timestamp)";

	private static final String DEF_INSERT_JOB_SQL = "insert into cron_job_detail(job_name,job_description,job_class,running,paused) values (?,?,?,?,?)";
	private static final String DEF_CHECK_JOB_EXISTS_SQL = "select count(*) from cron_job_detail where job_name=?";
	private static final String DEF_SELECT_JOBS_SQL = "select * from cron_job_detail";
	private static final String DEF_DELETE_JOB_SQL = "delete from cron_job_detail where job_name=?";
	private static final String DEF_UPDATE_JOB_SQL = "update cron_job_detail set running=?, paused=?, completed_count=?, failed_count=?, last_executed=?, next_executed=? where job_name=?";

	private DataSource dataSource;
	private ApplicationContext context;

	public JdbcJobManager() {
		this(8);
	}

	public JdbcJobManager(int nThreads) {
		super(nThreads);
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
		schedule(job);
		log.info("Reload job '" + jobName + "' from database ok.");
	}
	
	

	@Override
	public void schedule(Job job) {
		checkJobNameIfBlank(job);
		save(job);
		super.schedule(job);
	}

	public void save(Job job) {
		if (!hasJob(job)) {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils.executeUpdate(connection, DEF_INSERT_JOB_SQL, ps -> {
					ps.setString(1, job.getName());
					ps.setString(2, job.getDescription());
					ps.setString(3, job.getClass().getName());
					ps.setBoolean(4, false);
					ps.setBoolean(5, false);
				});
				log.info("Save job '" + job.getName() + "' ok.");
			} catch (SQLException e) {
				throw new SchedulingException("Failed to save job detail to database.", e);
			} finally {
				DBUtils.closeQuietly(connection);
			}
		}
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

	public void close() {
		if (taskExecutor != null) {
			taskExecutor.close();
		}
	}

	public void beforeJobExecution(TaskFuture future) {
		final Job job = (Job) future.getDetail().getTaskObject();
		final TaskDetail taskDetail = future.getDetail();
		if (hasJob(job)) {
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
		final ResultSetSlice<Tuple> delegate = DBUtils.pagingQuery(dataSource, DEF_SELECT_JOBS_SQL, (Object[]) null);
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
	}

}
