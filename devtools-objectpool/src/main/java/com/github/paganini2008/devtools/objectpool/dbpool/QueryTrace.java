package com.github.paganini2008.devtools.objectpool.dbpool;

/**
 * Record each query info(Sql,SqlParameters,StartTime and EndTime)
 * 
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public interface QueryTrace extends Comparable<QueryTrace> {

	Object[] getParameters();

	String getSql();

	long getStartTime();

	long getEndTime();

	default int compareTo(QueryTrace qt) {
		long result = qt.getEndTime() - qt.getStartTime();
		if (result > 0) {
			return -1;
		}
		if (result < 0) {
			return 1;
		}
		return 0;
	}

}
