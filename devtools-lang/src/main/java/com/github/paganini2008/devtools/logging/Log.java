package com.github.paganini2008.devtools.logging;

/**
 * Log
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface Log {

	static final String MODULE = "Logging";

	String getName();

	void fatal(Object arg);

	void fatal(String pattern, Object... args);

	void fatal(Object arg, Throwable cause);

	void fatal(String pattern, Throwable cause, Object... args);

	void debug(Object arg);

	void debug(String pattern, Object... args);

	void debug(Object arg, Throwable cause);

	void debug(String pattern, Throwable cause, Object... args);

	void error(Object arg);

	void error(String pattern, Object... args);

	void error(Object arg, Throwable cause);

	void error(String pattern, Throwable cause, Object... args);

	void info(Object arg);

	void info(String pattern, Object... args);

	void info(Object arg, Throwable cause);

	void info(String pattern, Throwable cause, Object... args);

	void warn(Object arg);

	void warn(String pattern, Object... args);

	void warn(Object arg, Throwable cause);

	void warn(String pattern, Throwable cause, Object... args);

	boolean isFatalEnabled();

	boolean isDebugEnabled();

	boolean isErrorEnabled();

	boolean isInfoEnabled();

	boolean isWarnEnabled();

}
