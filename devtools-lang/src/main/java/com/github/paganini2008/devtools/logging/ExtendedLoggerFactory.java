package com.github.paganini2008.devtools.logging;

import java.io.UnsupportedEncodingException;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * 
 * ExtendedLoggerFactory
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ExtendedLoggerFactory extends DefaultLoggerFactory {

	private Configuration configuration = new Configuration();

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public Logger getLogger(String name) {
		final Logger logger = super.getLogger(name);
		for (Handler handler : configuration.getHandlers(name)) {
			try {
				handler.setEncoding(getCharset());
			} catch (UnsupportedEncodingException e) {
			}
			handler.setFormatter(getFormatter());
			handler.setFilter(getFilter());
			logger.addHandler(handler);
		}
		logger.setLevel(configuration.getLevel(name));
		return logger;
	}

}
