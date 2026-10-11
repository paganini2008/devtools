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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.paganini2008.devtools.io.DirectoryWalker;
import com.github.paganini2008.devtools.io.StreamCopier;
import com.github.paganini2008.devtools.jdbc.DumpProgress;
import com.github.paganini2008.devtools.jdbc.JdbcDumpOptions;

/**
 *
 * Adapters plugging a {@link ProgressBar} into the progress callbacks of existing components ({@code JdbcDumpTemplate},
 * {@code DirectoryWalker}, {@code IOUtils.copy}) without changing them.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class ProgressAdapters {

	private static final Pattern TABLE_SQL = Pattern.compile("^select \\* from (\\S+)$");

	/**
	 * Adapts a bar to a {@link DumpProgress} for dumping <b>one</b> query or table: {@code onStart} starts the bar, each
	 * {@code progress} sets the total to the row count of the query and moves the bar to the cumulative count of rows
	 * written (out of order calls from several writer threads are safe), and {@code onEnd} completes it.
	 *
	 * <p>
	 * The row count is taken before the predicate of the dump options is applied, so a filtered dump may complete below
	 * 100%. If the dump throws, {@code onEnd} is not called: call {@link ProgressBar#fail(Throwable)} or close the bar
	 * yourself. To dump several tables use {@link #toDumpProgress(Function)}.
	 * </p>
	 *
	 * <pre>
	 * try (ProgressBar bar = ProgressBar.builder().taskName("Dump t_user").unit("rows").build()) {
	 * 	template.dump(null, "PUBLIC", "t_user", ProgressAdapters.toDumpProgress(bar), options, null);
	 * }
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the bar is null
	 */
	public static DumpProgress toDumpProgress(final ProgressBar progressBar) {
		requireNonNull(progressBar, "Progress bar is null");
		return new DumpProgress() {

			@Override
			public void onStart(String catalog, String schema, String sql, Object[] args, JdbcDumpOptions dumpOptions) {
				progressBar.start();
			}

			@Override
			public void progress(String catalog, String schema, String sql, Object[] args, long progressRecords, long totalRecords,
					JdbcDumpOptions dumpOptions) {
				update(progressBar, progressRecords, totalRecords);
			}

			@Override
			public void onEnd(String catalog, String schema, String sql, Object[] args, JdbcDumpOptions dumpOptions) {
				progressBar.complete();
			}
		};
	}

	/**
	 * Adapts a bar factory to a {@link DumpProgress} creating one bar per dumped table or query, for the multi-table
	 * {@code dump(catalog, schema, excludedTables, ...)} methods. The factory receives the table name when the statement
	 * is {@code select * from <table>} (as generated for table dumps), the SQL otherwise; it may return null to skip a
	 * table. Each bar is started on {@code onStart}, updated like {@link #toDumpProgress(ProgressBar)} and completed on
	 * {@code onEnd}. If a table fails, its bar is left running.
	 *
	 * <pre>
	 * template.dump(null, "PUBLIC", null, ProgressAdapters.toDumpProgress(table -&gt; ProgressBar.builder()
	 * 		.taskName("Dump " + table).unit("rows").build()), options, null);
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the factory is null
	 */
	public static DumpProgress toDumpProgress(final Function<String, ProgressBar> factory) {
		requireNonNull(factory, "Progress bar factory is null");
		final Map<String, ProgressBar> bars = new ConcurrentHashMap<String, ProgressBar>();
		return new DumpProgress() {

			@Override
			public void onStart(String catalog, String schema, String sql, Object[] args, JdbcDumpOptions dumpOptions) {
				ProgressBar bar = factory.apply(nameOf(sql));
				if (bar != null) {
					bars.put(String.valueOf(sql), bar);
					bar.start();
				}
			}

			@Override
			public void progress(String catalog, String schema, String sql, Object[] args, long progressRecords, long totalRecords,
					JdbcDumpOptions dumpOptions) {
				ProgressBar bar = bars.get(String.valueOf(sql));
				if (bar != null) {
					update(bar, progressRecords, totalRecords);
				}
			}

			@Override
			public void onEnd(String catalog, String schema, String sql, Object[] args, JdbcDumpOptions dumpOptions) {
				ProgressBar bar = bars.remove(String.valueOf(sql));
				if (bar != null) {
					bar.complete();
				}
			}
		};
	}

	static String nameOf(String sql) {
		if (sql == null) {
			return null;
		}
		Matcher matcher = TABLE_SQL.matcher(sql);
		return matcher.matches() ? matcher.group(1) : sql;
	}

	private static void update(ProgressBar bar, long progressRecords, long totalRecords) {
		if (totalRecords > 0 && bar.getTotal() != totalRecords) {
			bar.setTotal(totalRecords);
		}
		bar.stepTo(progressRecords);
	}

	/**
	 * Adapts a bar to a {@link DirectoryWalker.Progressable}. The walker only reports the completed ratio of the top
	 * level entries, so the bar counts in percent: its total is set to 100 (if unknown), it moves to
	 * {@code round(completedRatio * 100)} (at most 99 until the ratio reaches 1) and completes when the ratio reaches 1,
	 * which happens on the final report of the walk. The extra message shows the counts walked so far, e.g.
	 * {@code files=12 dirs=3 size=1.5 KB}. The walker reports every second. Consider hiding the count and rate of a {@link ConsoleProgressRenderer} since they are in percent.
	 *
	 * @throws IllegalArgumentException if the bar is null
	 */
	public static DirectoryWalker.Progressable toProgressable(final ProgressBar progressBar) {
		return toProgressable(progressBar, 1);
	}

	/**
	 * Like {@link #toProgressable(ProgressBar)} with the given report interval in seconds (at least 1).
	 *
	 * @throws IllegalArgumentException if the bar is null
	 */
	public static DirectoryWalker.Progressable toProgressable(final ProgressBar progressBar, final int refreshIntervalSeconds) {
		requireNonNull(progressBar, "Progress bar is null");
		final int interval = Math.max(1, refreshIntervalSeconds);
		return new DirectoryWalker.Progressable() {

			@Override
			public void progress(int fileCount, int folderCount, long length, float completedRatio, long elapsed) {
				if (progressBar.isFinished()) {
					return;
				}
				if (progressBar.getTotal() <= 0) {
					progressBar.setTotal(100);
				}
				progressBar.setExtraMessage("files=" + fileCount + " dirs=" + folderCount + " size=" + ProgressFormats.formatSize(length));
				long total = progressBar.getTotal();
				if (completedRatio >= 1f) {
					progressBar.stepTo(total);
					progressBar.complete();
				} else {
					progressBar.stepTo(Math.min(total - 1, Math.round(Math.max(0f, completedRatio) * total)));
				}
			}

			@Override
			public int refreshInterval() {
				return interval;
			}
		};
	}

	/**
	 * Wraps a {@link StreamCopier}: every call is forwarded to the delegate, and the bar is started on {@code onStart},
	 * moved to the cumulative byte count on {@code onProgress} and completed on {@code onEnd}. A failure of the delegate
	 * {@code copy} fails the bar; a failure while reading the input cannot be told apart by the callbacks and completes
	 * the bar. Use the unit "B" to display sizes.
	 *
	 * <pre>
	 * ProgressBar bar = ProgressBar.builder().taskName("Copy").total(file.length()).unit("B").build();
	 * IOUtils.copy(in, ProgressAdapters.toStreamCopier(bar, copier), 8192, -1);
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the bar or the delegate is null
	 */
	public static StreamCopier toStreamCopier(final ProgressBar progressBar, final StreamCopier delegate) {
		requireNonNull(progressBar, "Progress bar is null");
		requireNonNull(delegate, "Delegate stream copier is null");
		return new StreamCopier() {

			@Override
			public void onStart() {
				delegate.onStart();
				progressBar.start();
			}

			@Override
			public void copy(byte[] bytes, int offset, int length) throws IOException {
				try {
					delegate.copy(bytes, offset, length);
				} catch (IOException | RuntimeException e) {
					progressBar.fail(e);
					throw e;
				}
			}

			@Override
			public void onProgress(long length) {
				delegate.onProgress(length);
				progressBar.stepTo(length);
			}

			@Override
			public void onEnd(long length) {
				try {
					delegate.onEnd(length);
				} finally {
					progressBar.stepTo(length);
					progressBar.complete();
				}
			}
		};
	}

	private static void requireNonNull(Object value, String message) {
		if (value == null) {
			throw new IllegalArgumentException(message);
		}
	}

}
