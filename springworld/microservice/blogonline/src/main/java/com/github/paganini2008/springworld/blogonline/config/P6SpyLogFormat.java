package com.github.paganini2008.springworld.blogonline.config;

import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;

import com.github.paganini2008.devtools.StringUtils;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * 
 * P6SpyLogFormat
 * 
 * @author Fred Feng
 *
 */
public class P6SpyLogFormat implements MessageFormattingStrategy {

	private final static BasicFormatterImpl sqlformat = new BasicFormatterImpl();

	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql) {
		if (StringUtils.isBlank(sql)) {
			return "";
		}
		try {
			StringBuilder content = new StringBuilder();
			content.append("[SlowSql] ");
			content.append("connectionId: " + connectionId);
			content.append(", now: " + now);
			content.append(", elapsed: " + elapsed);
			content.append(", category: " + category);
			content.append(", sql: " + sqlformat.format(sql));
			return content.toString();
		} catch (Exception e) {
			return "";
		}
	}

}
