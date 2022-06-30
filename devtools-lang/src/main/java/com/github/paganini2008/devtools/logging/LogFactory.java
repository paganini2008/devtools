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

import com.github.paganini2008.devtools.ServiceLoader;
import com.github.paganini2008.devtools.ServiceNotFoundException;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * 
 * LogFactory
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public final class LogFactory {

	private final static LruMap<String, Log> cache = new LruMap<String, Log>(1024);

	static {
		ServiceLoader.lookupPath("META-INF/spi/com/github/paganini2008/devtools/logging");

		initialize(new LoggerFactoryBuilder() {
			public LoggerFactory getFactory() {
				return new ExtendedLoggerFactory();
			}
		});
	}

	public static void initialize(LoggerFactoryBuilder factoryBuilder) {
		JdkLog.setLoggerFactory(factoryBuilder.getFactory());
	}

	public static Log getLog(Class<?> className) {
		return getLog(className.getName());
	}

	public static Log getLog(String name) {
		Log logger = cache.get(name);
		if (logger == null) {
			try {
				cache.put(name, ServiceLoader.load(Log.class).getFirst(name));
			} catch (ServiceNotFoundException e) {
			}
			logger = cache.get(name);
			if (logger == null) {
				cache.put(name, new JdkLog(name));
				logger = cache.get(name);
			}
		}
		return logger;
	}

}
