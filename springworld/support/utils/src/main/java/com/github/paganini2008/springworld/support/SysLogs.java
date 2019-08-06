package com.github.paganini2008.springworld.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * 
 * SysLogs
 * 
 * @author Fred Feng
 * @created 2019-03
 */
public final class SysLogs {

	public static final String GLOBAL_MARKER_NAME = "mec";

	private static final String LOG_NAME = "ILILIL";
	private static final Logger logger = LoggerFactory.getLogger(LOG_NAME);
	private static final Marker defaultMarker = MarkerFactory.getMarker(GLOBAL_MARKER_NAME);

	public static void debug(String msg) {
		if (logger.isDebugEnabled()) {
			logger.debug(defaultMarker, msg);
		}
	}

	public static void debug(String msg, Throwable e) {
		if (logger.isDebugEnabled()) {
			logger.info(defaultMarker, msg, e);
		}
	}

	public static void debug(String msg, Object... args) {
		if (logger.isDebugEnabled()) {
			logger.debug(defaultMarker, msg, args);
		}
	}

	public static void info(String msg) {
		if (logger.isInfoEnabled()) {
			logger.info(defaultMarker, msg);
		}
	}

	public static void info(String msg, Throwable e) {
		if (logger.isInfoEnabled()) {
			logger.info(defaultMarker, msg, e);
		}
	}

	public static void info(String msg, Object... args) {
		if (logger.isInfoEnabled()) {
			logger.info(defaultMarker, msg, args);
		}
	}

	public static void warn(String msg) {
		if (logger.isWarnEnabled()) {
			logger.warn(defaultMarker, msg);
		}
	}

	public static void warn(String msg, Throwable e) {
		if (logger.isWarnEnabled()) {
			logger.warn(defaultMarker, msg, e);
		}
	}

	public static void warn(String msg, Object... args) {
		if (logger.isWarnEnabled()) {
			logger.warn(defaultMarker, msg, args);
		}
	}

	public static void error(String msg) {
		if (logger.isErrorEnabled()) {
			logger.error(defaultMarker, msg);
		}
	}

	public static void error(String msg, Throwable e) {
		if (logger.isErrorEnabled()) {
			logger.error(defaultMarker, msg, e);
		}
	}

	public static void error(String msg, Object... args) {
		if (logger.isErrorEnabled()) {
			logger.error(defaultMarker, msg, args);
		}
	}

	private SysLogs() {
	}

}
