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
package com.github.paganini2008.devtools.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.logging.Log;

/**
 * ProgressRenderersTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ProgressRenderersTest {

	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	private final PrintStream out = new PrintStream(buffer, true);

	private String output() throws UnsupportedEncodingException {
		return new String(buffer.toByteArray(), "UTF-8").replace("\r\n", "\n");
	}

	private static ProgressSnapshot snapshot(String name, long completed, long total, String unit, int width, long elapsed,
			String extra, ProgressState state, Throwable failure) {
		return new ProgressSnapshot(name, completed, total, unit, width, elapsed, extra, state, failure);
	}

	@Test
	public void format_knownTotalLine() {
		ProgressSnapshot s = snapshot("Dump users", 560, 1000, "rows", 20, 4000, null, ProgressState.RUNNING, null);
		assertEquals("Dump users [===========>        ]  56% 560/1000 rows  140.0 rows/s  ETA 00:00:03", s.toString());
	}

	@Test
	public void format_unknownTotalLine() {
		ProgressSnapshot s = snapshot("Scan", 42, -1, "files", 20, 2000, "dir=/tmp", ProgressState.RUNNING, null);
		assertEquals("Scan 42 files  21.0 files/s  Elapsed 00:00:02  dir=/tmp", s.toString());
	}

	@Test
	public void format_byteUnitUsesSizes() {
		ProgressSnapshot s = snapshot("Copy", 3 * 512 * 1024, 3 * 1024 * 1024, "B", 0, 1000, " ", ProgressState.RUNNING, null);
		assertEquals("Copy  50% 1.5 MB/3.0 MB  1.5 MB/s  ETA 00:00:01", s.toString());
		ProgressSnapshot u = snapshot("Copy", 512, -1, "bytes", 0, 1000, null, ProgressState.COMPLETED, null);
		assertEquals("Copy 512 B  512 B/s  Done in 00:00:01", u.toString());
	}

	@Test
	public void format_failedLine() {
		ProgressSnapshot s = snapshot("Job", 30, 100, "", 0, 3000, null, ProgressState.FAILED, new IllegalStateException("boom"));
		assertEquals("Job  30% 30/100  10.0/s  Failed after 00:00:03 (IllegalStateException: boom)", s.toString());
		ProgressSnapshot n = snapshot("Job", 30, 100, "", 0, 3000, null, ProgressState.FAILED, new IllegalStateException());
		assertTrue(n.toString().endsWith("Failed after 00:00:03 (IllegalStateException)"));
		ProgressSnapshot w = snapshot("Job", 30, 100, "", 0, 3000, null, ProgressState.FAILED, null);
		assertTrue(w.toString().endsWith("Failed after 00:00:03"));
	}

	@Test
	public void formats_helpers() {
		assertEquals("--:--:--", ProgressFormats.formatDuration(-1));
		assertEquals("00:00:00", ProgressFormats.formatDuration(999));
		assertEquals("01:01:01", ProgressFormats.formatDuration(3661000));
		assertEquals("100:00:00", ProgressFormats.formatDuration(360000000L));
		assertEquals("0 B", ProgressFormats.formatSize(0));
		assertEquals("1023 B", ProgressFormats.formatSize(1023));
		assertEquals("1.0 KB", ProgressFormats.formatSize(1024));
		assertEquals("1.5 KB", ProgressFormats.formatSize(1536));
		assertEquals("1.0 GB", ProgressFormats.formatSize(1024L * 1024 * 1024));
		assertEquals("8.0 EB", ProgressFormats.formatSize(Long.MAX_VALUE));
		assertEquals("1024.0 EB", ProgressFormats.formatSize(Math.pow(1024, 7)));
		assertTrue(ProgressFormats.isByteUnit("b"));
		assertTrue(ProgressFormats.isByteUnit("Byte"));
		assertTrue(ProgressFormats.isByteUnit("BYTES"));
		assertFalse(ProgressFormats.isByteUnit("rows"));
		assertFalse(ProgressFormats.isByteUnit(null));
		assertEquals("[>   ]", "[" + ProgressFormats.formatBar(0, 4, '=', '>', ' ') + "]");
		assertEquals("####", ProgressFormats.formatBar(100, 4, '#', '>', '-'));
		assertEquals("##>-", ProgressFormats.formatBar(50, 4, '#', '>', '-'));
		assertEquals("12.0/s", ProgressFormats.formatRate(12, ""));
	}

	@Test
	public void console_interactiveRefreshesInPlace() throws Exception {
		ConsoleProgressRenderer renderer = new ConsoleProgressRenderer(out, true).chars('#', '>', '.');
		assertTrue(renderer.isInteractive());
		renderer.render(snapshot("Job", 0, 4, "", 4, 0, "long extra message", ProgressState.RUNNING, null));
		renderer.render(snapshot("Job", 2, 4, "", 4, 1000, null, ProgressState.RUNNING, null));
		ProgressSnapshot done = snapshot("Job", 4, 4, "", 4, 2000, null, ProgressState.COMPLETED, null);
		renderer.render(done);
		renderer.onFinish(done);
		String first = "Job [>...]   0% 0/4  0.0/s  ETA --:--:--  long extra message";
		String second = "Job [##>.]  50% 2/4  2.0/s  ETA 00:00:01";
		String third = "Job [####] 100% 4/4  2.0/s  Done in 00:00:02";
		StringBuilder expected = new StringBuilder();
		expected.append('\r').append(first);
		expected.append('\r').append(second);
		for (int i = second.length(); i < first.length(); i++) {
			expected.append(' ');
		}
		expected.append('\r').append(third);
		for (int i = third.length(); i < second.length(); i++) {
			expected.append(' ');
		}
		expected.append('\n');
		assertEquals(expected.toString(), output());
	}

	@Test
	public void console_nonInteractivePrintsEveryTenPercentThroughProgressBar() throws Exception {
		AtomicLong now = new AtomicLong();
		ConsoleProgressRenderer renderer = new ConsoleProgressRenderer(out, false);
		assertFalse(renderer.isInteractive());
		ProgressBar bar = ProgressBar.builder().taskName("Job").total(100).width(10).renderer(renderer)
				.refreshInterval(0, TimeUnit.MILLISECONDS).clock(now::get).build();
		bar.start();
		for (int i = 1; i <= 100; i++) {
			now.set(i * 100L);
			bar.step();
		}
		bar.complete();
		List<String> lines = Arrays.asList(output().split("\n"));
		assertEquals(11, lines.size(), output());
		assertEquals("Job [>         ]   0% 0/100  0.0/s  ETA --:--:--", lines.get(0));
		assertEquals("Job [=>        ]  10% 10/100  10.0/s  ETA 00:00:09", lines.get(1));
		assertEquals("Job [=========>]  90% 90/100  10.0/s  ETA 00:00:01", lines.get(9));
		assertEquals("Job [==========] 100% 100/100  10.0/s  Done in 00:00:10", lines.get(10));
		assertFalse(output().contains("\r"));
	}

	@Test
	public void console_nonInteractiveUnknownTotalPrintsByInterval() throws Exception {
		ConsoleProgressRenderer renderer = new ConsoleProgressRenderer(out, false).lineStep(25, 1000).showCount(false)
				.showRate(false);
		renderer.render(snapshot("Scan", 0, -1, "files", 10, 0, null, ProgressState.RUNNING, null));
		renderer.render(snapshot("Scan", 5, -1, "files", 10, 999, null, ProgressState.RUNNING, null));
		renderer.render(snapshot("Scan", 9, -1, "files", 10, 1000, null, ProgressState.RUNNING, null));
		renderer.render(snapshot("Scan", 9, -1, "files", 10, 1500, null, ProgressState.COMPLETED, null));
		assertEquals("Scan  Elapsed 00:00:00\nScan  Elapsed 00:00:01\nScan  Done in 00:00:01\n", output());
	}

	@Test
	public void console_onFinishResetsStateForReuse() throws Exception {
		ConsoleProgressRenderer renderer = new ConsoleProgressRenderer(out, false).lineStep(50, 0);
		ProgressSnapshot done = snapshot("A", 2, 2, "", 0, 0, null, ProgressState.COMPLETED, null);
		renderer.render(snapshot("A", 0, 2, "", 0, 0, null, ProgressState.RUNNING, null));
		renderer.render(snapshot("A", 1, 2, "", 0, 0, null, ProgressState.RUNNING, null));
		renderer.render(snapshot("A", 1, 2, "", 0, 0, null, ProgressState.RUNNING, null));
		renderer.render(done);
		renderer.onFinish(done);
		renderer.render(snapshot("B", 0, 2, "", 0, 0, null, ProgressState.RUNNING, null));
		String[] lines = output().split("\n");
		assertEquals(4, lines.length);
		assertTrue(lines[1].startsWith("A  50%"));
		assertTrue(lines[3].startsWith("B   0%"));
	}

	@Test
	public void console_constructorsAndValidation() {
		assertTrue(new ConsoleProgressRenderer(null, false) != null);
		new ConsoleProgressRenderer();
		new ConsoleProgressRenderer(out);
		ConsoleProgressRenderer r = new ConsoleProgressRenderer(out, false);
		assertThrows(IllegalArgumentException.class, () -> r.lineStep(0, 10));
		assertThrows(IllegalArgumentException.class, () -> r.lineStep(101, 10));
		assertThrows(IllegalArgumentException.class, () -> r.lineStep(10, -1));
	}

	static class LogRecorder {
		final List<String> calls = new ArrayList<String>();

		Log log() {
			return (Log) Proxy.newProxyInstance(Log.class.getClassLoader(), new Class<?>[] { Log.class }, (proxy, method, args) -> {
				if ("getName".equals(method.getName())) {
					return "test";
				}
				if (method.getReturnType() == boolean.class) {
					return true;
				}
				String call = method.getName() + ":" + args[0];
				if (args.length > 1 && args[1] instanceof Throwable) {
					call += ":" + ((Throwable) args[1]).getMessage();
				}
				calls.add(call);
				return null;
			});
		}
	}

	@Test
	public void logging_logsEveryStepAndFailure() {
		LogRecorder recorder = new LogRecorder();
		AtomicLong now = new AtomicLong();
		ProgressBar bar = ProgressBar.builder().taskName("Job").total(10).renderer(new LoggingProgressRenderer(recorder.log(), 50, 1000))
				.refreshInterval(0, TimeUnit.MILLISECONDS).clock(now::get).build();
		bar.start();
		for (int i = 0; i < 6; i++) {
			now.addAndGet(1000);
			bar.step();
		}
		bar.fail(new IllegalStateException("boom"));
		assertEquals(Arrays.asList("info:Job   0% 0/10  0.0/s  ETA --:--:--", "info:Job  50% 5/10  1.0/s  ETA 00:00:05",
				"error:Job  60% 6/10  1.0/s  Failed after 00:00:06 (IllegalStateException: boom):boom"), recorder.calls);
	}

	@Test
	public void logging_defaultsAndCompletion() {
		LogRecorder recorder = new LogRecorder();
		LoggingProgressRenderer renderer = new LoggingProgressRenderer(recorder.log());
		ProgressSnapshot done = snapshot("Job", 3, -1, "", 0, 1000, null, ProgressState.COMPLETED, null);
		renderer.render(snapshot("Job", 0, -1, "", 0, 0, null, ProgressState.RUNNING, null));
		renderer.render(snapshot("Job", 1, -1, "", 0, 9999, null, ProgressState.RUNNING, null));
		renderer.render(snapshot("Job", 2, -1, "", 0, 10000, null, ProgressState.RUNNING, null));
		renderer.render(done);
		renderer.onFinish(done);
		assertEquals(3, recorder.calls.size());
		assertEquals("info:Job 3  3.0/s  Done in 00:00:01", recorder.calls.get(2));
		new LoggingProgressRenderer();
		assertThrows(IllegalArgumentException.class, () -> new LoggingProgressRenderer(null, 0, 1));
	}

	@Test
	public void composite_forwardsToAllAndSkipsNulls() {
		RecordingRenderer a = new RecordingRenderer();
		RecordingRenderer b = new RecordingRenderer();
		ProgressRenderer composite = ProgressRenderers.composite(a, null, b);
		ProgressSnapshot s = snapshot("Job", 1, 2, "", 0, 0, null, ProgressState.COMPLETED, null);
		composite.render(s);
		composite.onFinish(s);
		assertEquals(1, a.rendered.size());
		assertEquals(1, b.rendered.size());
		assertEquals(1, a.finished.size());
		assertEquals(1, b.finished.size());
		assertSame(ProgressRenderers.noop(), ProgressRenderers.composite());
		assertSame(ProgressRenderers.noop(), ProgressRenderers.composite((ProgressRenderer[]) null));
		assertSame(ProgressRenderers.noop(), ProgressRenderers.composite((ProgressRenderer) null));
		ProgressRenderers.noop().render(s);
		ProgressRenderers.noop().onFinish(s);
	}

}
