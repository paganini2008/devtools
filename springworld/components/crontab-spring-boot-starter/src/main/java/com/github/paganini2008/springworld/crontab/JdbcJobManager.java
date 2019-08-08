package com.github.paganini2008.springworld.crontab;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.io.SerializationUtils;
import com.github.paganini2008.devtools.jdbc.DBUtils;
import com.github.paganini2008.devtools.scheduler.GenericTaskExecutor;
import com.github.paganini2008.devtools.scheduler.ScheduleException;
import com.github.paganini2008.devtools.scheduler.TaskExecutor;
import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskFuture;

/**
 * 
 * JdbcJobManager
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class JdbcJobManager implements JobManager {

	private static final String SQL_INSERT = "insert into cron_job_detail(name,descrption,job_class,cron) values (?,?,?,?)";
	private final Observable observable = Observable.unrepeatable();
	private TaskExecutor taskExecutor = new GenericTaskExecutor(8, "crontab");
	private DataSource dataSource;

	@Override
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void schedule(Job job) throws ScheduleException {
		observable.addObserver((ob, arg) -> {
			TaskFuture taskFuture = taskExecutor.schedule(job, job.cron());
			taskFuture.setTaskInterceptorHandler(null);
		});
		Object[] sqlParams = new Object[] { job.name(), job.description(), job.getClass().getName(),
				SerializationUtils.toByteArray(job.cron()) };
		try {
			DBUtils.executeUpdate(dataSource.getConnection(), SQL_INSERT, sqlParams);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void runNow() {
		// TODO Auto-generated method stub

	}

	@Override
	public int countOfJobs() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] jobNames() {
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasJob(Job job) {
		// TODO Auto-generated method stub
		return false;
	}

}
