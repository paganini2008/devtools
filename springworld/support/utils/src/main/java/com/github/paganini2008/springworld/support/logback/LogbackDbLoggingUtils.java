package com.github.paganini2008.springworld.support.logback;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.jdbc.DBUtils;

/**
 * 
 * LogbackDbLoggingUtils
 * 
 * @author Fred Feng
 * @create 2019-03
 */
public class LogbackDbLoggingUtils {

	private LogbackDbLoggingUtils() {
	}

	public static List<Map<String, Object>> searchLoggingEvent(Connection connection, String keyword, String level, Date startDate,
			Date endDate, int limit) throws SQLException {
		StringBuilder sql = new StringBuilder("select e.* from logging_event e where 1=1");
		List<Object> arguments = new ArrayList<Object>();
		if (StringUtils.isNotBlank(keyword)) {
			arguments.add("%" + keyword + "%");
			arguments.add("%" + keyword + "%");
			sql.append(
					" and (e.formatted_message like ? or exists (select 1 from logging_event_exception where event_id=e.event_id and trace_line like ?))");
		}
		if (StringUtils.isNotBlank(level)) {
			arguments.add(level.toUpperCase());
			sql.append(" and e.level_string=?");
		}
		if (startDate != null) {
			arguments.add(startDate.getTime());
			sql.append(" and e.timestmp>=?");
		}
		if (endDate != null) {
			arguments.add(endDate.getTime());
			sql.append(" and e.timestmp<?");
		}
		sql.append(" order by e.timestmp desc limit ?");
		arguments.add(limit);
		Iterator<Tuple> iterator = DBUtils.executeQuery(connection, sql.toString(), arguments.toArray());
		List<Map<String, Object>> eventList = new ArrayList<Map<String, Object>>();
		while (iterator.hasNext()) {
			Tuple data = iterator.next();
			processEventException(connection, data);
			long timestmp = (Long) data.get("timestmp");
			data.set("create_date", DateUtils.format(timestmp));
			eventList.add(data.toMap());
		}
		return eventList;
	}

	private static void processEventException(Connection connection, Tuple data) throws SQLException {
		Number eventId = (Number) data.get("event_id");
		String sql = "select trace_line from logging_event_exception where event_id=?";
		Iterator<Tuple> iterator = DBUtils.executeQuery(connection, sql, new Object[] { eventId.intValue() });
		List<String> traceLineList = new ArrayList<String>();
		while (iterator.hasNext()) {
			Tuple trace = iterator.next();
			String traceLine = (String) trace.get("trace_line");
			traceLine = traceLine.replace("\t", "    ");
			traceLineList.add(traceLine);
		}
		data.set("event_exception", traceLineList);
	}

}
