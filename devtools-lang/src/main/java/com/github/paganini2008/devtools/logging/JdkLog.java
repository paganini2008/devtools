/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.logging;

import static com.github.paganini2008.devtools.logging.Levels.DEBUG;
import static com.github.paganini2008.devtools.logging.Levels.ERROR;
import static com.github.paganini2008.devtools.logging.Levels.FATAL;
import static com.github.paganini2008.devtools.logging.Levels.INFO;
import static com.github.paganini2008.devtools.logging.Levels.WARN;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * JdkLog
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class JdkLog implements Log {

	private static LoggerFactory loggerFactory = new DefaultLoggerFactory();

	static void setLoggerFactory(LoggerFactory loggerFactory) {
		JdkLog.loggerFactory = loggerFactory;
	}

	private static TokenParser tokenParser = new VarsTokenParser("{}");

	public void setTokenParser(TokenParser tokenParser) {
		JdkLog.tokenParser = tokenParser;
	}

	private final Logger logger;

	public JdkLog(String name) {
		this.logger = loggerFactory.getLogger(name);
	}

	public String getName() {
		return logger.getName();
	}

	public void all() {
		this.logger.setLevel(Levels.ALL);
	}

	public void off() {
		this.logger.setLevel(Levels.OFF);
	}

	public void fatal(Object arg) {
		log(FATAL, arg, null);
	}

	public void fatal(String pattern, Object... args) {
		log(FATAL, pattern, args, null);
	}

	public void fatal(Object arg, Throwable cause) {
		log(FATAL, arg, cause);
	}

	public void fatal(String pattern, Throwable cause, Object... args) {
		log(FATAL, pattern, args, cause);
	}

	public void debug(Object arg) {
		log(DEBUG, arg, null);
	}

	public void debug(String pattern, Object... args) {
		log(DEBUG, pattern, args, null);
	}

	public void debug(Object arg, Throwable cause) {
		log(DEBUG, arg, cause);
	}

	public void debug(String pattern, Throwable cause, Object... args) {
		log(DEBUG, pattern, args, cause);
	}

	public void error(Object arg) {
		log(ERROR, arg, null);
	}

	public void error(String pattern, Object... args) {
		log(ERROR, pattern, args, null);
	}

	public void error(Object arg, Throwable cause) {
		log(ERROR, arg, cause);
	}

	public void error(String pattern, Throwable cause, Object... args) {
		log(ERROR, pattern, args, cause);
	}

	public void info(Object arg) {
		log(INFO, arg, null);
	}

	public void info(String pattern, Object... args) {
		log(INFO, pattern, args, null);
	}

	public void info(Object arg, Throwable cause) {
		log(INFO, arg, cause);
	}

	public void info(String pattern, Throwable cause, Object... args) {
		log(INFO, pattern, args, cause);
	}

	public void warn(Object arg) {
		log(WARN, arg, null);
	}

	public void warn(String pattern, Object... args) {
		log(WARN, pattern, args, null);
	}

	public void warn(Object arg, Throwable cause) {
		log(WARN, arg, cause);
	}

	public void warn(String pattern, Throwable cause, Object... args) {
		log(WARN, pattern, args, cause);
	}

	public boolean isFatalEnabled() {
		return logger.isLoggable(FATAL);
	}

	public boolean isDebugEnabled() {
		return logger.isLoggable(DEBUG);
	}

	public boolean isErrorEnabled() {
		return logger.isLoggable(ERROR);
	}

	public boolean isInfoEnabled() {
		return logger.isLoggable(INFO);
	}

	public boolean isWarnEnabled() {
		return logger.isLoggable(WARN);
	}

	private void log(Level level, Object o, Throwable e) {
		String msg = o != null ? o.toString() : "";
		logger.log(level, msg, e);
	}

	private void log(Level level, String pattern, Object[] args, Throwable e) {
		String msg = parseText(pattern, args);
		logger.log(level, msg, e);
	}

	public String toString() {
		return "[JdkLog] Name: " + getName() + ", logger: " + logger;
	}

	private String parseText(String pattern, Object... args) {
		return tokenParser.parse(pattern, args);
	}

}
