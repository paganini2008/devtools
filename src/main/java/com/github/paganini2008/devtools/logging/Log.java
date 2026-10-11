/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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

/**
 * A simple logging facade with <code>{}</code> placeholder support.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface Log {

	static final String MODULE = "Logging";

	/**
	 * Returns the name of this log.
	 */
	String getName();

	/**
	 * Logs a message at FATAL level; <code>null</code> is logged as an empty string.
	 */
	void fatal(Object arg);

	/**
	 * Logs a message at FATAL level, replacing each <code>{}</code> in the pattern with the next argument.
	 */
	void fatal(String pattern, Object... args);

	/**
	 * Logs a message with an exception at FATAL level.
	 */
	void fatal(Object arg, Throwable cause);

	/**
	 * Logs a message with an exception at FATAL level, replacing each <code>{}</code> with the next argument.
	 */
	void fatal(String pattern, Throwable cause, Object... args);

	/**
	 * Logs a message at DEBUG level; <code>null</code> is logged as an empty string.
	 */
	void debug(Object arg);

	/**
	 * Logs a message at DEBUG level, replacing each <code>{}</code> in the pattern with the next argument.
	 */
	void debug(String pattern, Object... args);

	/**
	 * Logs a message with an exception at DEBUG level.
	 */
	void debug(Object arg, Throwable cause);

	/**
	 * Logs a message with an exception at DEBUG level, replacing each <code>{}</code> with the next argument.
	 */
	void debug(String pattern, Throwable cause, Object... args);

	/**
	 * Logs a message at ERROR level; <code>null</code> is logged as an empty string.
	 */
	void error(Object arg);

	/**
	 * Logs a message at ERROR level, replacing each <code>{}</code> in the pattern with the next argument.
	 */
	void error(String pattern, Object... args);

	/**
	 * Logs a message with an exception at ERROR level.
	 */
	void error(Object arg, Throwable cause);

	/**
	 * Logs a message with an exception at ERROR level, replacing each <code>{}</code> with the next argument.
	 */
	void error(String pattern, Throwable cause, Object... args);

	/**
	 * Logs a message at INFO level; <code>null</code> is logged as an empty string.
	 */
	void info(Object arg);

	/**
	 * Logs a message at INFO level, replacing each <code>{}</code> in the pattern with the next argument.
	 */
	void info(String pattern, Object... args);

	/**
	 * Logs a message with an exception at INFO level.
	 */
	void info(Object arg, Throwable cause);

	/**
	 * Logs a message with an exception at INFO level, replacing each <code>{}</code> with the next argument.
	 */
	void info(String pattern, Throwable cause, Object... args);

	/**
	 * Logs a message at WARN level; <code>null</code> is logged as an empty string.
	 */
	void warn(Object arg);

	/**
	 * Logs a message at WARN level, replacing each <code>{}</code> in the pattern with the next argument.
	 */
	void warn(String pattern, Object... args);

	/**
	 * Logs a message with an exception at WARN level.
	 */
	void warn(Object arg, Throwable cause);

	/**
	 * Logs a message with an exception at WARN level, replacing each <code>{}</code> with the next argument.
	 */
	void warn(String pattern, Throwable cause, Object... args);

	/**
	 * Checks whether FATAL messages are logged.
	 */
	boolean isFatalEnabled();

	/**
	 * Checks whether DEBUG messages are logged.
	 */
	boolean isDebugEnabled();

	/**
	 * Checks whether ERROR messages are logged.
	 */
	boolean isErrorEnabled();

	/**
	 * Checks whether INFO messages are logged.
	 */
	boolean isInfoEnabled();

	/**
	 * Checks whether WARN messages are logged.
	 */
	boolean isWarnEnabled();

}
