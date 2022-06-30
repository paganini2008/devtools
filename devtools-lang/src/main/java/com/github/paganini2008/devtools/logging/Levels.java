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

import java.util.logging.Level;

/**
 * Levels
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class Levels {

	private static final String LOGGING_LEVEL_BUNDLE = "sun.util.logging.resources.logging";

	static abstract class LoggingLevel extends Level implements Comparable<LoggingLevel> {

		private static final long serialVersionUID = 1L;

		LoggingLevel(String name, int value) {
			super(name, value, LOGGING_LEVEL_BUNDLE);
			this.value = value;
		}

		private final int value;

		public int compareTo(LoggingLevel other) {
			return this.value - other.value;
		}

	}

	static class Off extends LoggingLevel {
		private static final long serialVersionUID = 1L;

		Off() {
			super("OFF", Integer.MIN_VALUE);
		}

	}

	static class Debug extends LoggingLevel {
		private static final long serialVersionUID = 1L;

		Debug() {
			super("DEBUG", 10000);
		}

	}

	static class Info extends LoggingLevel {
		private static final long serialVersionUID = 1L;

		Info() {
			super("INFO", 20000);
		}

	}

	static class Warn extends LoggingLevel {
		private static final long serialVersionUID = 1L;

		Warn() {
			super("WARN", 30000);
		}

	}

	static class Error extends LoggingLevel {
		private static final long serialVersionUID = 1L;

		Error() {
			super("ERROR", 40000);
		}

	}

	static class Fatal extends LoggingLevel {
		private static final long serialVersionUID = 1L;

		Fatal() {
			super("FATAL", 50000);
		}

	}

	static class All extends LoggingLevel {
		private static final long serialVersionUID = 1L;

		All() {
			super("ALL", Integer.MAX_VALUE);
		}

	}

	public final static Level OFF = new Off();

	public final static Level DEBUG = new Debug();

	public final static Level INFO = new Info();

	public final static Level WARN = new Warn();

	public final static Level ERROR = new Error();

	public final static Level FATAL = new Fatal();

	public final static Level ALL = new All();

	public static Level getByName(String name) {
		if ("OFF".equalsIgnoreCase(name)) {
			return OFF;
		} else if ("DEBUG".equalsIgnoreCase(name)) {
			return DEBUG;
		} else if ("INFO".equalsIgnoreCase(name)) {
			return INFO;
		} else if ("WARN".equalsIgnoreCase(name)) {
			return WARN;
		} else if ("ERROR".equalsIgnoreCase(name)) {
			return ERROR;
		} else if ("FATAL".equalsIgnoreCase(name)) {
			return FATAL;
		} else if ("ALL".equalsIgnoreCase(name)) {
			return ALL;
		}
		throw new IllegalArgumentException("Unknown level name: " + name);
	}

	private Levels() {
	}

}
