package com.github.paganini2008.devtools.logging;

import java.io.UnsupportedEncodingException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * DefaultLoggerFactory
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DefaultLoggerFactory implements LoggerFactory {

	private Level level = Levels.INFO;
	private String charset = "UTF-8";
	private Formatter formatter = new SimpleFormatter();
	private Filter filter;

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Level getLevel() {
		return level;
	}

	public String getCharset() {
		return charset;
	}

	public Formatter getFormatter() {
		return formatter;
	}

	public Filter getFilter() {
		return filter;
	}

	public Logger getLogger(String name) {
		Logger logger = Logger.getLogger(name);
		logger.setUseParentHandlers(false);
		logger.setLevel(level);
		ConsoleHandler stdout = new ConsoleHandler();
		try {
			stdout.setEncoding(charset);
		} catch (UnsupportedEncodingException e) {
		}
		stdout.setLevel(level);
		stdout.setFormatter(formatter);
		if (filter != null) {
			stdout.setFilter(filter);
		}
		logger.addHandler(stdout);
		return logger;
	}

}
