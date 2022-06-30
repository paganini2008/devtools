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

/**
 * Log
 * 
 * @author Fred Feng
 * @since 2.0.1
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
