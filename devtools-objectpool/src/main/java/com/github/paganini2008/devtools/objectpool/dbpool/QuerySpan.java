package com.github.paganini2008.devtools.objectpool.dbpool;

import java.util.List;

/**
 * 
 * QuerySpan 
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface QuerySpan {

	/**
	 * Record every execution
	 * 
	 * @param queryTrace
	 */
	void record(QueryTrace queryTrace);

	/**
	 * Get slow queries
	 * @return
	 */
	List<QueryTrace> getQueryTraces();

	/**
	 * Get executed total.
	 * 
	 * @return
	 */
	long getExecutionCount();

	/**
	 * Get avg elapsed time.
	 * 
	 * @return
	 */
	float getExecutionAvgTime();

	/**
	 * Get max elapsed time.
	 * 
	 * @return
	 */
	long getExecutionMaxTime();

	/**
	 * Get min elapsed time.
	 * 
	 * @return
	 */
	long getExecutionMinTime();

}