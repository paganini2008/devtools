/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * StdoutLog
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class StdoutLog implements Log {

	private static final int LEVEL_FATAL = 5;
	private static final int LEVEL_ERROR = 4;
	private static final int LEVEL_WARN = 3;
	private static final int LEVEL_INFO = 2;
	private static final int LEVEL_DEBUG = 1;
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	StdoutLog(String name) {
		this.name = name;
	}

	public static Log getLog(String name) {
		return new StdoutLog(name);
	}

	private final String name;
	private int level = LEVEL_INFO;

	public String getName() {
		return name;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void fatal(Object arg) {
		if (isFatalEnabled()) {
			printOut("FATAL", arg, null);
		}
	}

	public void fatal(String pattern, Object... args) {
		if (isFatalEnabled()) {
			printOut("FATAL", pattern, args, null);
		}
	}

	public void fatal(Object arg, Throwable cause) {
		if (isFatalEnabled()) {
			printOut("FATAL", arg, cause);
		}
	}

	public void fatal(String pattern, Throwable cause, Object... args) {
		if (isFatalEnabled()) {
			printOut("FATAL", pattern, args, cause);
		}
	}

	public void debug(Object arg) {
		if (isDebugEnabled()) {
			printOut("DEBUG", arg, null);
		}
	}

	public void debug(String pattern, Object... args) {
		if (isDebugEnabled()) {
			printOut("DEBUG", pattern, args, null);
		}
	}

	public void debug(Object arg, Throwable cause) {
		if (isDebugEnabled()) {
			printOut("DEBUG", arg, cause);
		}
	}

	public void debug(String pattern, Throwable cause, Object... args) {
		if (isDebugEnabled()) {
			printOut("DEBUG", pattern, args, cause);
		}
	}

	public void error(Object arg) {
		if (isErrorEnabled()) {
			errorOut("ERROR", arg, null);
		}
	}

	public void error(String pattern, Object... args) {
		if (isErrorEnabled()) {
			errorOut("ERROR", pattern, args, null);
		}
	}

	public void error(Object arg, Throwable cause) {
		if (isErrorEnabled()) {
			errorOut("ERROR", arg, cause);
		}
	}

	public void error(String pattern, Throwable cause, Object... args) {
		if (isErrorEnabled()) {
			errorOut("ERROR", pattern, args, cause);
		}
	}

	public void info(Object arg) {
		if (isInfoEnabled()) {
			printOut("INFO", arg, null);
		}
	}

	public void info(String pattern, Object... args) {
		if (isInfoEnabled()) {
			printOut("INFO", pattern, args, null);
		}
	}

	public void info(Object arg, Throwable cause) {
		if (isInfoEnabled()) {
			printOut("INFO", arg, cause);
		}
	}

	public void info(String pattern, Throwable cause, Object... args) {
		if (isInfoEnabled()) {
			printOut("INFO", pattern, args, cause);
		}
	}

	public void warn(Object arg) {
		if (isWarnEnabled()) {
			errorOut("WARN", arg, null);
		}
	}

	public void warn(String pattern, Object... args) {
		if (isWarnEnabled()) {
			errorOut("WARN", pattern, args, null);
		}
	}

	public void warn(Object arg, Throwable cause) {
		if (isWarnEnabled()) {
			errorOut("WARN", arg, cause);
		}
	}

	public void warn(String pattern, Throwable cause, Object... args) {
		if (isWarnEnabled()) {
			errorOut("WARN", pattern, args, cause);
		}
	}

	private String parseText(String pattern, Object... args) {
		String text = String.format(pattern, args);
		text = tokenParser.parse(text, args);
		return text;
	}

	private TokenParser tokenParser = new VarsTokenParser("{}");

	public void setTokenParser(TokenParser tokenParser) {
		this.tokenParser = tokenParser;
	}

	public boolean isFatalEnabled() {
		return level <= LEVEL_FATAL;
	}

	public boolean isDebugEnabled() {
		return level <= LEVEL_DEBUG;
	}

	public boolean isErrorEnabled() {
		return level <= LEVEL_ERROR;
	}

	public boolean isInfoEnabled() {
		return level <= LEVEL_INFO;
	}

	public boolean isWarnEnabled() {
		return level <= LEVEL_WARN;
	}

	private void printOut(String level, Object o, Throwable t) {
		String msg = o != null ? o.toString() : "";
		System.out.printf("%s %s [%s] %s\n", dateFormat.format(new Date()), level, Thread.currentThread().getName(), msg);
		if (t != null) {
			t.printStackTrace(System.out);
		}
	}

	private void printOut(String level, String pattern, Object[] args, Throwable t) {
		String msg = parseText(pattern, args);
		System.out.printf("%s %s [%s] %s\n", dateFormat.format(new Date()), level, Thread.currentThread().getName(), msg);
		if (t != null) {
			t.printStackTrace(System.out);
		}
	}

	private void errorOut(String level, Object o, Throwable t) {
		String msg = o != null ? o.toString() : "";
		System.err.printf("%s %s [%s] %s\n", dateFormat.format(new Date()), level, Thread.currentThread().getName(), msg);
		if (t != null) {
			t.printStackTrace(System.err);
		}
	}

	private void errorOut(String level, String pattern, Object[] args, Throwable t) {
		String msg = parseText(pattern, args);
		System.err.printf("%s %s [%s] %s\n", dateFormat.format(new Date()), level, Thread.currentThread().getName(), msg);
		if (t != null) {
			t.printStackTrace(System.err);
		}
	}

}
