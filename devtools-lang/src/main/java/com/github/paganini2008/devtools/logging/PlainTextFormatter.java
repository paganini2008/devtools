package com.github.paganini2008.devtools.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * PlainTextFormatter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class PlainTextFormatter extends Formatter {
	
	private static final String lineSeparator = System.getProperty("line.separator");

	public String format(LogRecord record) {
		return formatMessage(record) + lineSeparator;
	}

}
