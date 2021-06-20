/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import java.io.IOException;
import java.util.UUID;

import com.github.paganini2008.devtools.io.SizeUnit;

public class TestMain {

	static {
		try {
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initialize() throws IOException {
		final ExtendedLoggerFactory loggerFactory = new ExtendedLoggerFactory();
		loggerFactory.getConfiguration().addHandler("file",
				Handlers.fileHandler("d:/sql/test.log", SizeUnit.MB.toBytes(10).intValue(), "info"));
		loggerFactory.getConfiguration().bindHandler("com.github.paganini2008.http", new String[] { "file" });
		LogFactory.initialize(new LoggerFactoryBuilder() {
			public LoggerFactory getFactory() {
				return loggerFactory;
			}
		});
	}

	public static void main(String[] args) {
		Log logger = LogFactory.getLog(TestMain.class);
		for (int i = 0; i < 100000; i++) {
			logger.info(UUID.randomUUID().toString());
		}

	}

}
