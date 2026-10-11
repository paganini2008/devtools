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

import java.io.UnsupportedEncodingException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates {@link java.util.logging.Logger}s which write to the console with the configured level, charset, formatter and filter.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
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

	/**
	 * Returns the logger for the name with the configured level and a console handler. Calling it again for the same
	 * name replaces the console handler instead of adding another one.
	 */
	public Logger getLogger(String name) {
		Logger logger = Logger.getLogger(name);
		logger.setUseParentHandlers(false);
		logger.setLevel(level);
		for (Handler handler : logger.getHandlers()) {
			if (handler instanceof StdoutHandler) {
				logger.removeHandler(handler);
				handler.close();
			}
		}
		ConsoleHandler stdout = new StdoutHandler();
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

	/**
	 * Marker type of the console handler installed by this factory, so that it is replaced rather than added repeatedly.
	 */
	private static class StdoutHandler extends ConsoleHandler {
	}

}
