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

import com.github.paganini2008.devtools.ServiceLoader;
import com.github.paganini2008.devtools.ServiceNotFoundException;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * Entry point to obtain {@link Log} instances. A Log implementation can be supplied by SPI, otherwise {@link JdkLog} is used.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public final class LogFactory {

	private final static LruMap<String, Log> cache = new LruMap<String, Log>(1024);

	private static final String SPI_LOOKUP_PATH = "META-INF/spi/com/github/paganini2008/devtools/logging";

	static {
		initialize(new LoggerFactoryBuilder() {
			public LoggerFactory getFactory() {
				return new ExtendedLoggerFactory();
			}
		});
	}

	/**
	 * Replaces the {@link LoggerFactory} used by {@link JdkLog}s created afterwards.
	 */
	public static void initialize(LoggerFactoryBuilder factoryBuilder) {
		JdkLog.setLoggerFactory(factoryBuilder.getFactory());
	}

	/**
	 * Returns the (cached) log named after the class.
	 */
	public static Log getLog(Class<?> className) {
		return getLog(className.getName());
	}

	/**
	 * Returns the (cached) log for the name, created by the SPI implementation of {@link Log} if any, otherwise a
	 * {@link JdkLog}.
	 */
	public static Log getLog(String name) {
		synchronized (cache) {
			Log logger = cache.get(name);
			if (logger == null) {
				try {
					logger = ServiceLoader.load(Log.class, null, SPI_LOOKUP_PATH).getFirst(name);
				} catch (ServiceNotFoundException ignored) {
					// no SPI implementation, use the default one
				}
				if (logger == null) {
					logger = new JdkLog(name);
				}
				cache.put(name, logger);
			}
			return logger;
		}
	}

}
