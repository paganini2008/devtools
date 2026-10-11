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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * LoggingTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LoggingTest {

	@TempDir
	File tempDir;

	/**
	 * Collects the published log records
	 */
	static class CollectingHandler extends Handler {

		final List<LogRecord> records = new ArrayList<LogRecord>();

		public void publish(LogRecord record) {
			if (isLoggable(record)) {
				records.add(record);
			}
		}

		public void flush() {
		}

		public void close() {
		}

		List<String> messages() {
			List<String> list = new ArrayList<String>();
			for (LogRecord record : records) {
				list.add(record.getLevel().getName() + ":" + record.getMessage());
			}
			return list;
		}
	}

	private static int stdoutHandlers(Logger logger) {
		int n = 0;
		for (Handler handler : logger.getHandlers()) {
			if (handler.getClass().getName().endsWith("StdoutHandler")) {
				n++;
			}
		}
		return n;
	}

	@Test
	public void testLevels() {
		assertSame(Levels.OFF, Levels.getByName("off"));
		assertSame(Levels.DEBUG, Levels.getByName("Debug"));
		assertSame(Levels.INFO, Levels.getByName("INFO"));
		assertSame(Levels.WARN, Levels.getByName("warn"));
		assertSame(Levels.ERROR, Levels.getByName("error"));
		assertSame(Levels.FATAL, Levels.getByName("fatal"));
		assertSame(Levels.ALL, Levels.getByName("all"));
		assertThrows(IllegalArgumentException.class, () -> Levels.getByName("TRACE"));
		assertThrows(IllegalArgumentException.class, () -> Levels.getByName(null));

		// OFF is the highest level and ALL the lowest one, as in java.util.logging
		assertEquals(Level.OFF.intValue(), Levels.OFF.intValue());
		assertEquals(Level.ALL.intValue(), Levels.ALL.intValue());
		assertTrue(((Levels.LoggingLevel) Levels.ALL).compareTo((Levels.LoggingLevel) Levels.OFF) < 0);
		assertTrue(((Levels.LoggingLevel) Levels.DEBUG).compareTo((Levels.LoggingLevel) Levels.INFO) < 0);
		assertEquals(0, ((Levels.LoggingLevel) Levels.WARN).compareTo((Levels.LoggingLevel) Levels.WARN));
		assertTrue(Levels.FATAL.intValue() > Levels.ERROR.intValue());
	}

	@Test
	public void testJdkLogLevels() {
		DefaultLoggerFactory factory = new DefaultLoggerFactory();
		JdkLog.setLoggerFactory(factory);
		try {
			JdkLog log = new JdkLog("test.logging.levels");
			assertEquals("test.logging.levels", log.getName());
			assertTrue(log.toString().contains("test.logging.levels"));
			assertFalse(log.isDebugEnabled());
			assertTrue(log.isInfoEnabled());
			assertTrue(log.isWarnEnabled());
			assertTrue(log.isErrorEnabled());
			assertTrue(log.isFatalEnabled());
			log.off();
			assertFalse(log.isFatalEnabled());
			assertFalse(log.isInfoEnabled());
			log.all();
			assertTrue(log.isDebugEnabled());
		} finally {
			JdkLog.setLoggerFactory(new ExtendedLoggerFactory());
		}
	}

	@Test
	public void testJdkLogOutput() {
		DefaultLoggerFactory factory = new DefaultLoggerFactory();
		factory.setLevel(Levels.ALL);
		JdkLog.setLoggerFactory(factory);
		try {
			JdkLog log = new JdkLog("test.logging.output");
			Logger logger = Logger.getLogger("test.logging.output");
			for (Handler handler : logger.getHandlers()) {
				handler.setLevel(Levels.OFF);
			}
			CollectingHandler collector = new CollectingHandler();
			logger.addHandler(collector);
			Throwable e = new IllegalStateException("boom");

			log.debug("d");
			log.debug("d {}", 1);
			log.debug("d", e);
			log.debug("d {}", e, 2);
			log.info("i");
			log.info("i {} {}", "a", "b");
			log.info("i", e);
			log.info("i {}", e, 3);
			log.warn("w");
			log.warn("w {}", 4);
			log.warn("w", e);
			log.warn("w {}", e, 5);
			log.error("e");
			log.error("e {}", 6);
			log.error("e", e);
			log.error("e {}", e, 7);
			log.fatal("f");
			log.fatal("f {}", 8);
			log.fatal("f", e);
			log.fatal("f {}", e, 9);
			log.info((Object) null);
			log.info("", "ignored");

			List<String> messages = collector.messages();
			assertEquals(22, messages.size());
			assertEquals("DEBUG:d", messages.get(0));
			assertEquals("DEBUG:d 1", messages.get(1));
			assertEquals("DEBUG:d 2", messages.get(3));
			assertEquals("INFO:i a b", messages.get(5));
			assertEquals("WARN:w 4", messages.get(9));
			assertEquals("ERROR:e 7", messages.get(15));
			assertEquals("FATAL:f 8", messages.get(17));
			assertEquals("INFO:", messages.get(20));
			assertEquals("INFO:", messages.get(21));
			assertSame(e, collector.records.get(2).getThrown());
			assertNull(collector.records.get(0).getThrown());
			logger.removeHandler(collector);
		} finally {
			JdkLog.setLoggerFactory(new ExtendedLoggerFactory());
		}
	}

	@Test
	public void testDefaultLoggerFactory() {
		DefaultLoggerFactory factory = new DefaultLoggerFactory();
		assertSame(Levels.INFO, factory.getLevel());
		assertEquals("UTF-8", factory.getCharset());
		assertTrue(factory.getFormatter() instanceof SimpleFormatter);
		assertNull(factory.getFilter());
		factory.setLevel(Levels.WARN);
		factory.setCharset("no-such-charset");
		PlainTextFormatter formatter = new PlainTextFormatter();
		factory.setFormatter(formatter);
		factory.setFilter(record -> true);
		Logger logger = factory.getLogger("test.logging.factory");
		assertSame(Levels.WARN, logger.getLevel());
		assertFalse(logger.getUseParentHandlers());
		assertEquals(1, stdoutHandlers(logger));
		// creating the same logger again must not add duplicated console handlers
		factory.getLogger("test.logging.factory");
		factory.getLogger("test.logging.factory");
		assertEquals(1, stdoutHandlers(logger));
		for (Handler handler : logger.getHandlers()) {
			assertSame(formatter, handler.getFormatter());
			assertNotNull(handler.getFilter());
		}
	}

	@Test
	public void testConfiguration() {
		Configuration configuration = new Configuration();
		CollectingHandler a = new CollectingHandler();
		CollectingHandler b = new CollectingHandler();
		configuration.addHandler("a", a);
		configuration.addHandler("b", b);
		configuration.addHandler("nothing", null);
		configuration.bindHandler("com.example", "a");
		configuration.bindHandler("com.example.sub", new String[] { "a", "b", "unknown" });
		assertArrayEquals(new Handler[] { a }, configuration.getHandlers("com.example.Foo"));
		// handlers are not duplicated
		assertArrayEquals(new Handler[] { a, b }, configuration.getHandlers("com.example.sub.Bar"));
		assertEquals(0, configuration.getHandlers("org.other").length);

		configuration.addLevel("com.example", "warn");
		configuration.addLevel("com.example.sub", "debug");
		assertSame(Levels.WARN, configuration.getLevel("com.example.Foo"));
		assertSame(Levels.DEBUG, configuration.getLevel("com.example.sub.Bar"));
		// not configured: everything is passed to the handlers
		assertSame(Levels.ALL, configuration.getLevel("org.other"));

		String key = "devtools.logging.test.logging.sysprop";
		System.setProperty(key, "error");
		try {
			assertSame(Levels.ERROR, configuration.getLevel("test.logging.sysprop"));
		} finally {
			System.clearProperty(key);
		}
	}

	@Test
	public void testExtendedLoggerFactory() {
		ExtendedLoggerFactory factory = new ExtendedLoggerFactory();
		assertNotNull(factory.getConfiguration());
		Configuration configuration = new Configuration();
		CollectingHandler handler = new CollectingHandler();
		configuration.addHandler("collector", handler);
		configuration.bindHandler("test.logging.extended", "collector");
		configuration.addLevel("test.logging.extended", "warn");
		factory.setConfiguration(configuration);
		assertSame(configuration, factory.getConfiguration());

		Logger logger = factory.getLogger("test.logging.extended.Foo");
		factory.getLogger("test.logging.extended.Foo");
		int count = 0;
		for (Handler h : logger.getHandlers()) {
			if (h == handler) {
				count++;
			}
		}
		assertEquals(1, count);
		assertSame(Levels.WARN, logger.getLevel());
		assertSame(factory.getFormatter(), handler.getFormatter());
		logger.log(Levels.INFO, "ignored");
		logger.log(Levels.ERROR, "kept");
		assertEquals(1, handler.records.size());
		assertEquals("kept", handler.records.get(0).getMessage());

		// a logger without configured level still logs INFO through the console handler
		Logger other = factory.getLogger("test.logging.unconfigured");
		assertTrue(other.isLoggable(Levels.INFO));
	}

	@Test
	public void testLogFactory() {
		Log log = LogFactory.getLog(LoggingTest.class);
		assertSame(log, LogFactory.getLog(LoggingTest.class.getName()));
		assertTrue(log instanceof JdkLog);
		assertTrue(log.isInfoEnabled());
		LogFactory.initialize(() -> new DefaultLoggerFactory());
		try {
			Log other = LogFactory.getLog("test.logging.factory.other");
			assertFalse(other.isDebugEnabled());
		} finally {
			LogFactory.initialize(() -> new ExtendedLoggerFactory());
		}
	}

	@Test
	public void testFormatters() {
		LogRecord record = new LogRecord(Levels.WARN, "hello {0}");
		record.setParameters(new Object[] { "world" });
		assertEquals("hello world" + System.getProperty("line.separator"), new PlainTextFormatter().format(record));

		String text = new SimpleFormatter().format(record);
		assertTrue(text.contains(": [WARN] hello world"), text);
		assertFalse(text.contains("Exception"));
		record.setThrown(new IllegalStateException("boom"));
		text = new SimpleFormatter().format(record);
		assertTrue(text.contains("java.lang.IllegalStateException: boom"), text);
	}

	@Test
	public void testTokenParser() {
		VarsTokenParser parser = new VarsTokenParser("{}");
		assertEquals("a 1 b 2", parser.parse("a {} b {}", 1, 2));
		assertEquals("a 1 b {}", parser.parse("a {} b {}", 1));
		assertEquals("a null", parser.parse("a {}", (Object) null));
		assertEquals("took 5ms", parser.parse("took {}ms", 5));
		// legacy indexed placeholders
		assertEquals("b a", parser.parse("{}1 {}0", "a", "b"));
		assertEquals("{}9", parser.parse("{}9", "a"));
		// escaped placeholder
		assertEquals("{} 1", parser.parse("\\{} {}", 1));
		assertEquals("x", new VarsTokenParser("").parse("x", 1));
		assertEquals("a {}", parser.parse("a {}"));
		assertEquals("a {}", parser.parse("a {}", (Object[]) null));
		assertEquals("", parser.parse("", 1));
		assertNull(parser.parse(null, 1));
	}

	@Test
	public void testStdoutLog() {
		StdoutLog log = (StdoutLog) StdoutLog.getLog("stdout");
		assertEquals("stdout", log.getName());
		assertFalse(log.isDebugEnabled());
		assertTrue(log.isInfoEnabled());
		assertTrue(log.isWarnEnabled());
		assertTrue(log.isErrorEnabled());
		assertTrue(log.isFatalEnabled());
		// level above FATAL disables all output
		log.setLevel(6);
		assertFalse(log.isFatalEnabled());
		assertFalse(log.isErrorEnabled());
		assertFalse(log.isWarnEnabled());
		assertFalse(log.isInfoEnabled());
		Throwable e = new IllegalStateException("boom");
		callAll(log, e);

		final List<String> parsed = new ArrayList<String>();
		log.setTokenParser((text, args) -> {
			parsed.add(text);
			return text;
		});
		log.setLevel(1);
		assertTrue(log.isDebugEnabled());
		java.io.PrintStream out = System.out;
		java.io.PrintStream err = System.err;
		java.io.ByteArrayOutputStream outBuffer = new java.io.ByteArrayOutputStream();
		java.io.ByteArrayOutputStream errBuffer = new java.io.ByteArrayOutputStream();
		System.setOut(new java.io.PrintStream(outBuffer, true));
		System.setErr(new java.io.PrintStream(errBuffer, true));
		try {
			callAll(log, e);
			log.info("50%z done {}", 1);
			log.info("%s items", 3);
		} finally {
			System.setOut(out);
			System.setErr(err);
		}
		String stdout = new String(outBuffer.toByteArray(), StandardCharsets.UTF_8);
		String stderr = new String(errBuffer.toByteArray(), StandardCharsets.UTF_8);
		assertTrue(stdout.contains("DEBUG [" + Thread.currentThread().getName() + "] debug"), stdout);
		assertTrue(stdout.contains("INFO"));
		assertTrue(stdout.contains("FATAL"));
		assertTrue(stdout.contains("java.lang.IllegalStateException: boom"));
		assertTrue(stderr.contains("WARN"));
		assertTrue(stderr.contains("ERROR"));
		// an invalid format pattern does not break logging
		assertTrue(parsed.contains("50%z done {}"), parsed.toString());
		assertTrue(parsed.contains("3 items"), parsed.toString());
	}

	private static void callAll(Log log, Throwable e) {
		log.debug("debug");
		log.debug("debug {}", 1);
		log.debug("debug", e);
		log.debug("debug {}", e, 1);
		log.info("info");
		log.info("info {}", 1);
		log.info("info", e);
		log.info("info {}", e, 1);
		log.warn("warn");
		log.warn("warn {}", 1);
		log.warn("warn", e);
		log.warn("warn {}", e, 1);
		log.error("error");
		log.error("error {}", 1);
		log.error("error", e);
		log.error("error {}", e, 1);
		log.fatal("fatal");
		log.fatal("fatal {}", 1);
		log.fatal("fatal", e);
		log.fatal("fatal {}", e, 1);
		log.info((Object) null);
	}

	@Test
	public void testHandlers() throws IOException {
		File pattern = new File(tempDir, "test%g.log");
		Handler file = Handlers.fileHandler(pattern.getAbsolutePath(), 1024 * 1024, "info");
		try {
			assertSame(Levels.INFO, file.getLevel());
			file.setFormatter(new PlainTextFormatter());
			file.publish(new LogRecord(Levels.WARN, "to file"));
			file.flush();
		} finally {
			file.close();
		}
		String content = new String(Files.readAllBytes(new File(tempDir, "test0.log").toPath()), StandardCharsets.UTF_8);
		assertTrue(content.contains("to file"));

		try (ServerSocket server = new ServerSocket(0)) {
			Handler socket = Handlers.socketHandler("127.0.0.1", server.getLocalPort(), "error");
			try {
				assertSame(Levels.ERROR, socket.getLevel());
			} finally {
				socket.close();
			}
		}
	}

}
