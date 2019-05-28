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
		loggerFactory.getConfiguration().bindHandler("org.lazycat.lang", new String[] { "file" });
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
