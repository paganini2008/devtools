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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.io.DirectoryWalker;
import com.github.paganini2008.devtools.io.IOUtils;
import com.github.paganini2008.devtools.io.RecursiveDirectoryWalker;
import com.github.paganini2008.devtools.io.StreamCopier;
import com.github.paganini2008.devtools.jdbc.DumpProgress;
import com.github.paganini2008.devtools.jdbc.JdbcDumpOptions;
import com.github.paganini2008.devtools.jdbc.JdbcDumpTemplate;
import com.github.paganini2008.devtools.jdbc.JdbcTracker;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;

/**
 * ProgressAdaptersTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(30)
public class ProgressAdaptersTest {

	private JdbcTracker source;
	private JdbcTracker destination;
	private ExecutorService executor;

	@TempDir
	Path tempDir;

	@BeforeEach
	public void setUp() throws SQLException {
		source = new JdbcTracker("progress_dump_source");
		destination = new JdbcTracker("progress_dump_destination");
		String[] ddl = { "create table t_user(id int primary key, user_name varchar(20))", "create table t_log(id int primary key, msg varchar(50))",
				"create table t_empty(id int primary key)" };
		source.execute(ddl);
		destination.execute(ddl);
		List<Object[]> users = new ArrayList<Object[]>();
		for (int i = 1; i <= 12; i++) {
			users.add(new Object[] { i, "user" + i });
		}
		List<Object[]> logs = new ArrayList<Object[]>();
		for (int i = 1; i <= 5; i++) {
			logs.add(new Object[] { i, "log" + i });
		}
		try (Connection c = source.rawConnection()) {
			JdbcUtils.batchUpdate(c, "insert into t_user values (?, ?)", users);
			JdbcUtils.batchUpdate(c, "insert into t_log values (?, ?)", logs);
		}
		executor = Executors.newFixedThreadPool(4);
	}

	@AfterEach
	public void tearDown() throws SQLException {
		executor.shutdownNow();
		source.drop();
		destination.drop();
	}

	private JdbcDumpTemplate template() {
		return new JdbcDumpTemplate(source.connectionFactory(), destination.connectionFactory());
	}

	static class Options implements JdbcDumpOptions {

		java.util.concurrent.Executor executor;

		@Override
		public String getInsertionSql(Tuple t) {
			return null;
		}

		@Override
		public String getSchema() {
			return "PUBLIC";
		}

		@Override
		public java.util.concurrent.Executor getExecutor() {
			return executor;
		}
	}

	private static ProgressBar quietBar(String name, RecordingRenderer renderer) {
		return ProgressBar.builder().taskName(name).unit("rows").renderer(renderer).build();
	}

	@Test
	public void dumpProgress_singleTableRowByRow() throws SQLException {
		RecordingRenderer renderer = new RecordingRenderer();
		ProgressBar bar = quietBar("Dump t_user", renderer);
		long rows = template().dump(null, null, "t_user", ProgressAdapters.toDumpProgress(bar), new Options(), null);
		assertEquals(12L, rows);
		assertEquals(12L, bar.getTotal());
		assertEquals(12L, bar.getCompleted());
		assertEquals(ProgressState.COMPLETED, bar.getState());
		assertEquals(100d, renderer.last().getPercent());
		assertEquals(1, renderer.finished.size());
	}

	@Test
	public void dumpProgress_singleTableInBatchesWithExecutor() throws SQLException {
		RecordingRenderer renderer = new RecordingRenderer();
		ProgressBar bar = quietBar("Dump t_user", renderer);
		Options options = new Options();
		options.executor = executor;
		long rows = template().dump(null, null, "t_user", 5, ProgressAdapters.toDumpProgress(bar), options, null);
		assertEquals(12L, rows);
		assertEquals(12L, bar.getCompleted());
		assertEquals(12L, bar.getTotal());
		assertEquals(ProgressState.COMPLETED, bar.getState());
	}

	@Test
	public void dumpProgress_onePerTableForAllTables() throws SQLException {
		final Map<String, ProgressBar> bars = new ConcurrentHashMap<String, ProgressBar>();
		final RecordingRenderer renderer = new RecordingRenderer();
		Function<String, ProgressBar> factory = table -> {
			ProgressBar bar = quietBar("Dump " + table, renderer);
			bars.put(table.toUpperCase(), bar);
			return bar;
		};
		long[] rows = template().dump(null, "PUBLIC", (String[]) null, ProgressAdapters.toDumpProgress(factory), new Options(), null);
		assertArrayEquals(new long[] { 0L, 5L, 12L }, rows);
		assertEquals(3, bars.size(), bars.keySet().toString());
		assertEquals(12L, bars.get("T_USER").getCompleted());
		assertEquals(12L, bars.get("T_USER").getTotal());
		assertEquals(5L, bars.get("T_LOG").getCompleted());
		assertEquals(0L, bars.get("T_EMPTY").getCompleted());
		for (ProgressBar bar : bars.values()) {
			assertEquals(ProgressState.COMPLETED, bar.getState());
		}
		assertEquals(3, renderer.finished.size());
	}

	@Test
	public void dumpProgress_factoryMayReturnNullAndUnknownSqlIsIgnored() {
		DumpProgress progress = ProgressAdapters.toDumpProgress((Function<String, ProgressBar>) table -> null);
		progress.onStart(null, null, "select * from t", null, null);
		progress.progress(null, null, "select * from t", null, 1, 2, null);
		progress.onEnd(null, null, "select * from t", null, null);
		RecordingRenderer renderer = new RecordingRenderer();
		ProgressBar bar = quietBar("q", renderer);
		DumpProgress single = ProgressAdapters.toDumpProgress((Function<String, ProgressBar>) sql -> {
			assertEquals("select id from t where id > ?", sql);
			return bar;
		});
		single.progress(null, null, "other", null, 1, 2, null);
		single.onStart(null, null, "select id from t where id > ?", new Object[] { 1 }, null);
		single.progress(null, null, "select id from t where id > ?", null, 3, 0, null);
		assertEquals(3L, bar.getCompleted());
		assertEquals(-1L, bar.getTotal(), "an unknown row count keeps the total");
		single.onEnd(null, null, "select id from t where id > ?", null, null);
		assertTrue(bar.isFinished());
		assertNull(ProgressAdapters.nameOf(null));
		assertEquals("T_USER", ProgressAdapters.nameOf("select * from T_USER"));
	}

	@Test
	public void dumpProgress_nullArguments() {
		assertThrows(IllegalArgumentException.class, () -> ProgressAdapters.toDumpProgress((ProgressBar) null));
		assertThrows(IllegalArgumentException.class, () -> ProgressAdapters.toDumpProgress((Function<String, ProgressBar>) null));
		assertThrows(IllegalArgumentException.class, () -> ProgressAdapters.toProgressable(null));
		assertThrows(IllegalArgumentException.class, () -> ProgressAdapters.toStreamCopier(null, (b, o, l) -> {
		}));
		assertThrows(IllegalArgumentException.class, () -> ProgressAdapters.toStreamCopier(ProgressBar.create("x", 1), null));
	}

	private static void write(File file, int size) throws IOException {
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), new byte[size]);
	}

	@Test
	public void progressable_withRecursiveDirectoryWalker() throws IOException {
		File root = tempDir.toFile();
		write(new File(root, "a/1.txt"), 10);
		write(new File(root, "a/b/2.txt"), 20);
		write(new File(root, "c.txt"), 30);
		RecordingRenderer renderer = new RecordingRenderer();
		ProgressBar bar = ProgressBar.builder().taskName("Scan").renderer(renderer).build();
		RecursiveDirectoryWalker walker = new RecursiveDirectoryWalker(root, (file, depth) -> {
		});
		walker.setProgressable(ProgressAdapters.toProgressable(bar));
		walker.walk();
		assertEquals(ProgressState.COMPLETED, bar.getState());
		assertEquals(100L, bar.getTotal());
		assertEquals(100L, bar.getCompleted());
		assertEquals("files=3 dirs=2 size=60 B", bar.snapshot().getExtraMessage());
		assertEquals("files=3 dirs=2 size=60 B", renderer.last().getExtraMessage());
	}

	@Test
	public void progressable_mapsRatioToPercent() {
		RecordingRenderer renderer = new RecordingRenderer();
		ProgressBar bar = ProgressBar.builder().renderer(renderer).build();
		DirectoryWalker.Progressable p = ProgressAdapters.toProgressable(bar, 0);
		assertEquals(1, p.refreshInterval());
		assertEquals(3, ProgressAdapters.toProgressable(bar, 3).refreshInterval());
		p.progress(0, 0, 0, -0.5f, 0);
		assertEquals(0L, bar.getCompleted());
		assertEquals(100L, bar.getTotal());
		p.progress(1, 1, 2048, 0.5f, 0);
		assertEquals(50L, bar.getCompleted());
		assertEquals("files=1 dirs=1 size=2.0 KB", bar.snapshot().getExtraMessage());
		p.progress(1, 1, 2048, 0.999f, 0);
		assertEquals(99L, bar.getCompleted(), "never 100% before the ratio reaches 1");
		assertEquals(ProgressState.RUNNING, bar.getState());
		p.progress(2, 1, 4096, 1f, 0);
		assertEquals(100L, bar.getCompleted());
		assertEquals(ProgressState.COMPLETED, bar.getState());
		p.progress(9, 9, 9, 1f, 0);
		assertEquals("files=2 dirs=1 size=4.0 KB", bar.snapshot().getExtraMessage(), "reports after finish are ignored");

		ProgressBar permille = ProgressBar.builder().total(1000).renderer(renderer).build();
		ProgressAdapters.toProgressable(permille).progress(0, 0, 0, 0.25f, 0);
		assertEquals(250L, permille.getCompleted());
	}

	static class RecordingCopier implements StreamCopier {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final List<String> events = new ArrayList<String>();
		boolean failCopy;
		boolean failEnd;

		@Override
		public void onStart() {
			events.add("start");
		}

		@Override
		public void copy(byte[] bytes, int offset, int length) throws IOException {
			if (failCopy) {
				throw new IOException("disk full");
			}
			out.write(bytes, offset, length);
		}

		@Override
		public void onProgress(long length) {
			events.add("progress:" + length);
		}

		@Override
		public void onEnd(long length) {
			events.add("end:" + length);
			if (failEnd) {
				throw new IllegalStateException("end failed");
			}
		}
	}

	@Test
	public void streamCopier_withIOUtilsCopy() throws IOException {
		byte[] data = new byte[2500];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) i;
		}
		RecordingRenderer renderer = new RecordingRenderer();
		ProgressBar bar = ProgressBar.builder().taskName("Copy").unit("B").total(data.length).renderer(renderer).build();
		RecordingCopier copier = new RecordingCopier();
		long copied = IOUtils.copy(new ByteArrayInputStream(data), ProgressAdapters.toStreamCopier(bar, copier), 1024, -1);
		assertEquals(2500L, copied);
		assertArrayEquals(data, copier.out.toByteArray());
		assertEquals("[start, progress:1024, progress:2048, progress:2500, end:2500]", copier.events.toString());
		assertEquals(2500L, bar.getCompleted());
		assertEquals(ProgressState.COMPLETED, bar.getState());
		assertTrue(renderer.last().toString().startsWith("Copy [========================================] 100% 2.4 KB/2.4 KB"));
	}

	@Test
	public void streamCopier_failures() {
		ProgressBar bar = ProgressBar.builder().renderer(ProgressRenderers.noop()).build();
		RecordingCopier copier = new RecordingCopier();
		copier.failCopy = true;
		IOException e = assertThrows(IOException.class,
				() -> IOUtils.copy(new ByteArrayInputStream(new byte[10]), ProgressAdapters.toStreamCopier(bar, copier), 4, -1));
		assertEquals(ProgressState.FAILED, bar.getState());
		assertSame(e, bar.snapshot().getFailure());
		assertEquals("[start, end:0]", copier.events.toString());

		ProgressBar runtime = ProgressBar.builder().renderer(ProgressRenderers.noop()).build();
		StreamCopier failing = ProgressAdapters.toStreamCopier(runtime, (b, o, l) -> {
			throw new IllegalArgumentException("bad");
		});
		assertThrows(IllegalArgumentException.class, () -> failing.copy(new byte[1], 0, 1));
		assertEquals(ProgressState.FAILED, runtime.getState());

		ProgressBar ending = ProgressBar.builder().renderer(ProgressRenderers.noop()).build();
		RecordingCopier endCopier = new RecordingCopier();
		endCopier.failEnd = true;
		assertThrows(IllegalStateException.class,
				() -> IOUtils.copy(new ByteArrayInputStream(new byte[10]), ProgressAdapters.toStreamCopier(ending, endCopier), 4, -1));
		assertEquals(ProgressState.COMPLETED, ending.getState(), "the bar completes even if the delegate onEnd throws");
		assertEquals(10L, ending.getCompleted());
	}

}
