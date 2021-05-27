package com.github.paganini2008.devtools.logging;

import com.github.paganini2008.devtools.ServiceLoader;
import com.github.paganini2008.devtools.ServiceNotFoundException;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * 
 * LogFactory
 * 
 * @author Fred Feng
 * @version 1.0
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
