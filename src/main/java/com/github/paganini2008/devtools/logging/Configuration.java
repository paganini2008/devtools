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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Logging configuration of handlers and levels by logger name prefix. Levels can also be overridden with the system property <code>devtools.logging.&lt;loggerName&gt;</code>.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class Configuration {

	private static final String SYS_PROP_PREFIX = "devtools.logging.";

	private static final Comparator<String> nameComparator = new Comparator<String>() {
		public int compare(String left, String right) {
			int result = right.length() - left.length();
			if (result == 0) {
				return left.compareTo(right);
			}
			return result;
		}
	};

	private final Map<String, Handler> handlers = new HashMap<String, Handler>();
	private final Map<String, List<String>> loggings = new TreeMap<String, List<String>>(nameComparator);
	private final Map<String, Level> levels = new TreeMap<String, Level>(nameComparator);

	/**
	 * Registers a handler under a name; <code>null</code> handlers are ignored.
	 */
	public synchronized void addHandler(String name, Handler handler) {
		if (handler != null) {
			handlers.put(name, handler);
		}
	}

	/**
	 * Binds a registered handler to the loggers whose name starts with <code>name</code>.
	 */
	public void bindHandler(String name, String handler) {
		bindHandler(name, new String[] { handler });
	}

	/**
	 * Binds registered handlers to the loggers whose name starts with <code>name</code>.
	 */
	public synchronized void bindHandler(String name, String[] handlers) {
		loggings.put(name, Arrays.asList(handlers));
	}

	/**
	 * Returns the handlers bound to all name prefixes of the logger, without duplicates.
	 */
	public synchronized Handler[] getHandlers(String loggerName) {
		List<Handler> list = new ArrayList<Handler>();
		for (Map.Entry<String, List<String>> entry : loggings.entrySet()) {
			if (matches(loggerName, entry.getKey())) {
				for (String handlerName : entry.getValue()) {
					Handler handler = handlers.get(handlerName);
					if (handler != null && !list.contains(handler)) {
						list.add(handler);
					}
				}
			}
		}
		return list.toArray(new Handler[0]);
	}

	/**
	 * Sets the level of the loggers whose name starts with <code>loggerName</code>; the longest prefix wins.
	 *
	 * @throws IllegalArgumentException if the level name is unknown
	 */
	public synchronized void addLevel(String loggerName, String level) {
		levels.put(loggerName, Levels.getByName(level));
	}

	/**
	 * Returns the level of a logger: the system property <code>devtools.logging.&lt;loggerName&gt;</code> first, then the level
	 * of the longest matching prefix, and {@link Levels#ALL} (no restriction) if nothing is configured.
	 */
	public synchronized Level getLevel(String loggerName) {
		String syskey = SYS_PROP_PREFIX + loggerName;
		if (System.getProperties().containsKey(syskey)) {
			String levelName = System.getProperties().getProperty(syskey);
			return Levels.getByName(levelName);
		}
		for (Map.Entry<String, Level> entry : levels.entrySet()) {
			if (matches(loggerName, entry.getKey())) {
				return entry.getValue();
			}
		}
		return Levels.ALL;
	}

	private static boolean matches(String loggerName, String name) {
		return loggerName.equals(name) || loggerName.startsWith(name);
	}
}
