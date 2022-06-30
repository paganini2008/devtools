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

import java.io.UnsupportedEncodingException;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * 
 * ExtendedLoggerFactory
 * 
 * @author Fred Feng
 * @since 2.0.1
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
