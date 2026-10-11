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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.stream.Stream;

import com.github.paganini2008.devtools.io.Directory;
import com.github.paganini2008.devtools.io.DirectoryWalker;

/**
 *
 * A thread-safe progress bar which can be attached to any long running code: plain loops, iterables, arrays, streams,
 * I/O streams, or the callbacks of existing components (see {@link ProgressAdapters}).
 *
 * <p>
 * Steps are counted with atomic operations; rendering is throttled to at most once per refresh interval (start, completion
 * and failure are always rendered) and never blocks a step for long: if another thread is rendering, the step simply
 * skips its rendering. Exceptions thrown by the renderer are ignored so that a broken display never breaks the work.
 * </p>
 *
 * <p>
 * Manual mode in a plain for loop:
 * </p>
 *
 * <pre>
 * try (ProgressBar bar = ProgressBar.create("Import", n)) {
 * 	for (int i = 0; i &lt; n; i++) {
 * 		importRow(i);
 * 		bar.step();
 * 	}
 * }
 * </pre>
 *
 * <p>
 * Index loop and collection loop, wrapped in one line:
 * </p>
 *
 * <pre>
 * for (int i : ProgressBar.range("Process", 0, n)) {
 * 	process(i);
 * }
 * for (User user : ProgressBar.track("Dump users", users)) {
 * 	dump(user);
 * }
 * </pre>
 * 
 * <p>
 * Iterators, arrays, streams and directory walks:
 * </p>
 * 
 * <pre>
 * for (Row row : ProgressBar.track("Read", cursor, rowCount)) { ... }
 * for (int id : ProgressBar.track("Ids", ids)) { ... }
 * try (Stream&lt;User&gt; s = ProgressBar.track("Users", users.stream(), users.size())) { s.forEach(this::save); }
 * Directory dir = ProgressBar.walk("Scan", new RecursiveDirectoryWalker(root, handler));
 * </pre>
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ProgressBar implements AutoCloseable {

	/**
	 * The default width of the bar in characters.
	 */
	public static final int DEFAULT_WIDTH = 40;

	/**
	 * The default refresh interval in milliseconds.
	 */
	public static final long DEFAULT_REFRESH_INTERVAL_MILLIS = 200L;

	private final String taskName;
	private final String unit;
	private final int width;
	private final long refreshIntervalMillis;
	private final ProgressRenderer renderer;
	private final LongSupplier clock;

	private final AtomicLong completed = new AtomicLong();
	private final AtomicReference<ProgressState> state = new AtomicReference<ProgressState>(ProgressState.NEW);
	private final ReentrantLock renderLock = new ReentrantLock();
	private volatile long total;
	private volatile String extraMessage;
	private volatile long startTime;
	private volatile long endTime;
	private volatile Throwable failure;
	private volatile long lastRenderTime;

	protected ProgressBar(Builder builder) {
		this.taskName = builder.taskName != null ? builder.taskName : "";
		this.unit = builder.unit != null ? builder.unit : "";
		this.width = builder.width;
		this.refreshIntervalMillis = builder.refreshIntervalMillis;
		this.renderer = builder.renderer != null ? builder.renderer : new ConsoleProgressRenderer();
		this.clock = builder.clock;
		this.total = builder.total > 0 ? builder.total : -1L;
	}

	/**
	 * Creates a builder.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Creates a progress bar rendered on the console; a total &lt;= 0 means unknown.
	 */
	public static ProgressBar create(String taskName, long total) {
		return builder().taskName(taskName).total(total).build();
	}

	/**
	 * Returns an index range {@code [from, to)} rendered on the console with a new bar whose total is {@code to - from};
	 * the bar completes when the iteration ends.
	 *
	 * <pre>
	 * for (int i : ProgressBar.range("Process", 0, n)) { ... }
	 * </pre>
	 */
	public static Iterable<Integer> range(String taskName, int from, int to) {
		return create(taskName, -1).range(from, to);
	}

	/**
	 * Long version of {@link #range(String, int, int)}.
	 */
	public static Iterable<Long> range(String taskName, long from, long to) {
		return create(taskName, -1).range(from, to);
	}

	/**
	 * Wraps an iterable with a new console bar; the total is the size of a collection, unknown otherwise. The
	 * bar completes when the iteration ends; an exception thrown by the underlying iterator fails the bar and is rethrown
	 * (an exception thrown by the loop body cannot be seen and leaves the bar running). A null iterable is empty.
	 *
	 * <pre>
	 * for (User user : ProgressBar.track("Import", users)) { ... }
	 * </pre>
	 */
	public static <T> Iterable<T> track(String taskName, Iterable<T> items) {
		return create(taskName, -1).wrap(items);
	}

	/**
	 * Wraps an iterator with a new console bar of unknown total; see {@link #track(String, Iterable)}. The returned
	 * iterable can be iterated only once.
	 */
	public static <T> Iterable<T> track(String taskName, Iterator<T> iterator) {
		return track(taskName, iterator, -1);
	}

	/**
	 * Wraps an iterator with a new console bar of the given total (&lt;= 0 for unknown); see
	 * {@link #track(String, Iterable)}. The returned iterable can be iterated only once.
	 */
	public static <T> Iterable<T> track(String taskName, Iterator<T> iterator, long total) {
		final Iterator<T> wrapped = create(taskName, total).wrap(iterator);
		return () -> wrapped;
	}

	/**
	 * Iterates over an array with a new console bar whose total is the array length. A null array is empty.
	 */
	public static <T> Iterable<T> track(String taskName, T[] array) {
		return create(taskName, -1).wrap(array);
	}

	/**
	 * Iterates over an int array with a new console bar whose total is the array length. A null array is empty.
	 */
	public static Iterable<Integer> track(String taskName, int[] array) {
		return create(taskName, -1).wrap(array);
	}

	/**
	 * Iterates over a long array with a new console bar whose total is the array length. A null array is empty.
	 */
	public static Iterable<Long> track(String taskName, long[] array) {
		return create(taskName, -1).wrap(array);
	}

	/**
	 * Wraps a stream with a new console bar of the given total; see {@link #wrap(Stream)}: the bar completes when the
	 * stream is closed, so use it in try-with-resources.
	 */
	public static <T> Stream<T> track(String taskName, Stream<T> stream, long total) {
		return create(taskName, total).wrap(stream, total);
	}

	/**
	 * Walks a directory tree with a console bar in percent (see {@link ProgressAdapters#toProgressable(ProgressBar)})
	 * and returns the result of {@link DirectoryWalker#walk()}. The bar completes when the walk returns; if the walk
	 * throws, the bar fails and the exception is rethrown. The progressable of the walker is replaced.
	 *
	 * <pre>
	 * Directory dir = ProgressBar.walk("Scan", new RecursiveDirectoryWalker(root, handler));
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the walker is null
	 */
	public static Directory walk(String taskName, DirectoryWalker walker) throws IOException {
		if (walker == null) {
			throw new IllegalArgumentException("Directory walker is null");
		}
		ProgressBar bar = builder().taskName(taskName)
				.renderer(new ConsoleProgressRenderer().showCount(false).showRate(false)).build();
		return walk(bar, walker);
	}

	static Directory walk(ProgressBar bar, DirectoryWalker walker) throws IOException {
		walker.setProgressable(ProgressAdapters.toProgressable(bar));
		Directory directory;
		try {
			directory = walker.walk();
		} catch (IOException | RuntimeException | Error e) {
			bar.fail(e);
			throw e;
		}
		bar.stepTo(Math.max(bar.getTotal(), 0L));
		bar.complete();
		return directory;
	}

	/**
	 * Runs the action for each item with a new console bar whose total is the size of the collection. The bar completes
	 * after the last item; if the action throws, the bar fails and the exception is rethrown. A null collection does
	 * nothing.
	 *
	 * <pre>
	 * ProgressBar.forEach("Send mails", users, user -&gt; send(user));
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the action is null
	 */
	public static <T> void forEach(String taskName, Collection<T> items, Consumer<? super T> action) {
		if (items == null) {
			return;
		}
		create(taskName, items.size()).forEach(items, action);
	}

	/**
	 * Runs the action for each item of the iterable, stepping after each item and completing at the end; if the action
	 * throws, this bar fails and the exception is rethrown. A null iterable completes the bar immediately.
	 *
	 * @throws IllegalArgumentException if the action is null
	 */
	public <T> void forEach(Iterable<T> items, Consumer<? super T> action) {
		if (action == null) {
			throw new IllegalArgumentException("Action is null");
		}
		if (items instanceof Collection && total <= 0) {
			setTotal(((Collection<?>) items).size());
		}
		start();
		try {
			if (items != null) {
				for (T item : items) {
					action.accept(item);
					step();
				}
			}
		} catch (RuntimeException | Error e) {
			fail(e);
			throw e;
		}
		complete();
	}

	/**
	 * Starts this bar (rendered immediately). Does nothing if already started or finished.
	 */
	public void start() {
		if (state.get() != ProgressState.NEW) {
			return;
		}
		synchronized (this) {
			if (state.get() != ProgressState.NEW) {
				return;
			}
			startTime = currentTimeMillis();
			state.set(ProgressState.RUNNING);
		}
		render(true);
	}

	/**
	 * Adds one unit of work; starts the bar if needed. Ignored once finished.
	 */
	public void step() {
		stepBy(1L);
	}

	/**
	 * Adds {@code n} units of work; starts the bar if needed. Ignored once finished.
	 *
	 * @throws IllegalArgumentException if {@code n} is negative
	 */
	public void stepBy(long n) {
		if (n < 0) {
			throw new IllegalArgumentException("Negative step: " + n);
		}
		if (!prepareStep()) {
			return;
		}
		completed.addAndGet(n);
		render(false);
	}

	/**
	 * Moves the completed count to {@code n}; the count never decreases, so callbacks reporting cumulative values from
	 * several threads out of order are safe. Starts the bar if needed. Ignored once finished.
	 */
	public void stepTo(long n) {
		if (!prepareStep()) {
			return;
		}
		completed.accumulateAndGet(n, Math::max);
		render(false);
	}

	private boolean prepareStep() {
		ProgressState current = state.get();
		if (current == ProgressState.NEW) {
			start();
			current = state.get();
		}
		return !current.isFinished();
	}

	/**
	 * Changes the total amount of work; a value &lt;= 0 means unknown.
	 */
	public void setTotal(long total) {
		this.total = total > 0 ? total : -1L;
	}

	/**
	 * Sets a message displayed after the progress, may be null.
	 */
	public void setExtraMessage(String extraMessage) {
		this.extraMessage = extraMessage;
	}

	/**
	 * Marks this bar as completed and renders it a last time. Does nothing if already finished.
	 */
	public void complete() {
		finish(ProgressState.COMPLETED, null);
	}

	/**
	 * Marks this bar as failed and renders it a last time. Does nothing if already finished.
	 */
	public void fail(Throwable cause) {
		finish(ProgressState.FAILED, cause);
	}

	private void finish(ProgressState finalState, Throwable cause) {
		synchronized (this) {
			ProgressState current = state.get();
			if (current.isFinished()) {
				return;
			}
			long now = currentTimeMillis();
			if (current == ProgressState.NEW) {
				startTime = now;
			}
			endTime = now;
			failure = cause;
			state.set(finalState);
		}
		renderLock.lock();
		try {
			ProgressSnapshot snapshot = snapshot();
			try {
				renderer.render(snapshot);
				renderer.onFinish(snapshot);
			} catch (RuntimeException ignored) {
				// a broken renderer must not break the work
			}
		} finally {
			renderLock.unlock();
		}
	}

	/**
	 * Completes this bar if it is not finished yet; idempotent.
	 */
	@Override
	public void close() {
		complete();
	}

	private void render(boolean force) {
		if (!force && currentTimeMillis() - lastRenderTime < refreshIntervalMillis) {
			return;
		}
		if (force) {
			renderLock.lock();
		} else if (!renderLock.tryLock()) {
			return;
		}
		try {
			long now = currentTimeMillis();
			if (!force && now - lastRenderTime < refreshIntervalMillis) {
				return;
			}
			ProgressSnapshot snapshot = snapshot();
			if (snapshot.getState().isFinished()) {
				return;
			}
			lastRenderTime = now;
			try {
				renderer.render(snapshot);
			} catch (RuntimeException ignored) {
				// a broken renderer must not break the work
			}
		} finally {
			renderLock.unlock();
		}
	}

	/**
	 * Returns the current time in milliseconds; tests may override it or inject a clock via the builder.
	 */
	protected long currentTimeMillis() {
		return clock != null ? clock.getAsLong() : System.currentTimeMillis();
	}

	public String getTaskName() {
		return taskName;
	}

	public String getUnit() {
		return unit;
	}

	public long getCompleted() {
		return completed.get();
	}

	/**
	 * Returns the total amount of work, -1 if unknown.
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * Returns the completed percentage between 0 and 100, -1 if the total is unknown.
	 */
	public double getPercent() {
		return ProgressSnapshot.percent(completed.get(), total);
	}

	/**
	 * Returns the milliseconds elapsed since start (until finish once finished), 0 if not started.
	 */
	public long getElapsedMillis() {
		ProgressState current = state.get();
		if (current == ProgressState.NEW) {
			return 0L;
		}
		long end = current.isFinished() ? endTime : currentTimeMillis();
		return Math.max(0L, end - startTime);
	}

	/**
	 * Returns the average count of work units completed per second since start.
	 */
	public double getRatePerSecond() {
		return ProgressSnapshot.rate(completed.get(), getElapsedMillis());
	}

	/**
	 * Returns the estimated remaining milliseconds, -1 if unknown (unknown total or nothing completed yet).
	 */
	public long getEtaMillis() {
		return ProgressSnapshot.eta(completed.get(), total, getElapsedMillis());
	}

	public ProgressState getState() {
		return state.get();
	}

	/**
	 * Returns true if this bar is completed or failed.
	 */
	public boolean isFinished() {
		return state.get().isFinished();
	}

	/**
	 * Returns an immutable snapshot of the current progress.
	 */
	public ProgressSnapshot snapshot() {
		ProgressState current = state.get();
		long elapsed = getElapsedMillis();
		return new ProgressSnapshot(taskName, completed.get(), total, unit, width, elapsed, extraMessage, current, failure);
	}

	/**
	 * Returns an iterable whose iterators step this bar on each element and complete it when exhausted. If the iterable
	 * is a collection and the total is unknown, the total becomes its size. A null iterable is treated as empty.
	 */
	public <T> Iterable<T> wrap(final Iterable<T> iterable) {
		if (iterable instanceof Collection) {
			return wrap((Collection<T>) iterable);
		}
		return () -> wrap(iterable != null ? iterable.iterator() : null);
	}

	/**
	 * Like {@link #wrap(Iterable)}; the total becomes the size of the collection if it is unknown.
	 */
	public <T> Iterable<T> wrap(final Collection<T> collection) {
		if (collection != null && total <= 0) {
			setTotal(collection.size());
		}
		return () -> wrap(collection != null ? collection.iterator() : null);
	}

	/**
	 * Returns an iterator stepping this bar on each element and completing it when exhausted. A null iterator is
	 * treated as empty.
	 */
	public <T> Iterator<T> wrap(Iterator<T> iterator) {
		return new ProgressIterator<T>(iterator, this);
	}

	/**
	 * Iterates over an array; the total becomes its length if unknown. A null array is treated as empty.
	 */
	public <T> Iterable<T> wrap(final T[] array) {
		final int length = prepareArray(array != null ? array.length : 0);
		return () -> new IndexIterator<T>(this, length, i -> array[i]);
	}

	/**
	 * Iterates over an int array; the total becomes its length if unknown. A null array is treated as empty.
	 */
	public Iterable<Integer> wrap(final int[] array) {
		final int length = prepareArray(array != null ? array.length : 0);
		return () -> new IndexIterator<Integer>(this, length, i -> array[i]);
	}

	/**
	 * Iterates over a long array; the total becomes its length if unknown. A null array is treated as empty.
	 */
	public Iterable<Long> wrap(final long[] array) {
		final int length = prepareArray(array != null ? array.length : 0);
		return () -> new IndexIterator<Long>(this, length, i -> array[i]);
	}

	/**
	 * Iterates over a double array; the total becomes its length if unknown. A null array is treated as empty.
	 */
	public Iterable<Double> wrap(final double[] array) {
		final int length = prepareArray(array != null ? array.length : 0);
		return () -> new IndexIterator<Double>(this, length, i -> array[i]);
	}

	private int prepareArray(int length) {
		if (total <= 0) {
			setTotal(length);
		}
		return length;
	}

	/**
	 * Returns the index range {@code [from, to)} stepping this bar on each index and completing it at the end; the total
	 * becomes {@code to - from} if unknown. An empty range ({@code to <= from}) completes immediately when iterated.
	 */
	public Iterable<Integer> range(final int from, final int to) {
		final int length = to > from ? to - from : 0;
		prepareArray(length);
		return () -> new IndexIterator<Integer>(this, length, i -> from + i);
	}

	/**
	 * Long version of {@link #range(int, int)}.
	 */
	public Iterable<Long> range(final long from, final long to) {
		final long length = to > from ? to - from : 0L;
		if (total <= 0) {
			setTotal(length);
		}
		return () -> new Iterator<Long>() {

			private long index;

			@Override
			public boolean hasNext() {
				if (index < length) {
					return true;
				}
				complete();
				return false;
			}

			@Override
			public Long next() {
				if (index >= length) {
					throw new NoSuchElementException();
				}
				long value = from + index++;
				step();
				return value;
			}
		};
	}

	/**
	 * Returns a stream stepping this bar for each element (thread-safe for parallel streams) and completing it when the
	 * stream is closed, so use it in try-with-resources or close this bar after the terminal operation. A null stream
	 * returns an empty stream.
	 */
	public <T> Stream<T> wrap(Stream<T> stream) {
		Stream<T> source = stream != null ? stream : Stream.<T>empty();
		return source.peek(t -> step()).onClose(this::complete);
	}

	/**
	 * Like {@link #wrap(Stream)} and sets the total.
	 */
	public <T> Stream<T> wrap(Stream<T> stream, long total) {
		setTotal(total);
		return wrap(stream);
	}

	/**
	 * Returns an input stream stepping this bar by the count of bytes read; see {@link ProgressInputStream}.
	 */
	public ProgressInputStream wrap(InputStream in) {
		return new ProgressInputStream(in, this);
	}

	/**
	 * Returns an output stream stepping this bar by the count of bytes written; see {@link ProgressOutputStream}.
	 */
	public ProgressOutputStream wrap(OutputStream out) {
		return new ProgressOutputStream(out, this);
	}

	@Override
	public String toString() {
		return snapshot().toString();
	}

	/**
	 *
	 * Iterator stepping the bar on each element and completing it when exhausted.
	 *
	 * @author Fred Feng
	 * @date 2026-10-11
	 * @since 3.0.0
	 */
	private static class ProgressIterator<T> implements Iterator<T> {

		private final Iterator<T> delegate;
		private final ProgressBar bar;

		ProgressIterator(Iterator<T> delegate, ProgressBar bar) {
			this.delegate = delegate;
			this.bar = bar;
		}

		@Override
		public boolean hasNext() {
			boolean hasNext;
			try {
				hasNext = delegate != null && delegate.hasNext();
			} catch (RuntimeException | Error e) {
				bar.fail(e);
				throw e;
			}
			if (!hasNext) {
				bar.complete();
			}
			return hasNext;
		}

		@Override
		public T next() {
			if (delegate == null) {
				throw new NoSuchElementException();
			}
			T next;
			try {
				next = delegate.next();
			} catch (RuntimeException | Error e) {
				bar.fail(e);
				throw e;
			}
			bar.step();
			return next;
		}

		@Override
		public void remove() {
			if (delegate == null) {
				throw new IllegalStateException();
			}
			delegate.remove();
		}
	}

	/**
	 *
	 * Iterator over the indexes {@code [0, length)} mapped to elements.
	 *
	 * @author Fred Feng
	 * @date 2026-10-11
	 * @since 3.0.0
	 */
	private static class IndexIterator<T> implements Iterator<T> {

		private final ProgressBar bar;
		private final int length;
		private final java.util.function.IntFunction<T> mapper;
		private int index;

		IndexIterator(ProgressBar bar, int length, java.util.function.IntFunction<T> mapper) {
			this.bar = bar;
			this.length = length;
			this.mapper = mapper;
		}

		@Override
		public boolean hasNext() {
			if (index < length) {
				return true;
			}
			bar.complete();
			return false;
		}

		@Override
		public T next() {
			if (index >= length) {
				throw new NoSuchElementException();
			}
			T value = mapper.apply(index++);
			bar.step();
			return value;
		}
	}

	/**
	 *
	 * Builder of {@link ProgressBar}.
	 *
	 * @author Fred Feng
	 * @date 2026-10-11
	 * @since 3.0.0
	 */
	public static class Builder {

		private String taskName;
		private long total = -1L;
		private String unit;
		private int width = DEFAULT_WIDTH;
		private long refreshIntervalMillis = DEFAULT_REFRESH_INTERVAL_MILLIS;
		private ProgressRenderer renderer;
		private LongSupplier clock;

		protected Builder() {
		}

		public Builder taskName(String taskName) {
			this.taskName = taskName;
			return this;
		}

		/**
		 * Sets the total amount of work; a value &lt;= 0 means unknown (the default).
		 */
		public Builder total(long total) {
			this.total = total;
			return this;
		}

		/**
		 * Sets the unit of work such as "rows"; "B" or "bytes" makes renderers display sizes like "1.5 MB".
		 */
		public Builder unit(String unit) {
			this.unit = unit;
			return this;
		}

		/**
		 * Sets the width of the bar in characters (40 by default); 0 hides the bar.
		 *
		 * @throws IllegalArgumentException if negative
		 */
		public Builder width(int width) {
			if (width < 0) {
				throw new IllegalArgumentException("Negative width: " + width);
			}
			this.width = width;
			return this;
		}

		/**
		 * Sets the minimum interval between two renderings while running (200 ms by default).
		 *
		 * @throws IllegalArgumentException if negative or the unit is null
		 */
		public Builder refreshInterval(long interval, TimeUnit timeUnit) {
			if (interval < 0 || timeUnit == null) {
				throw new IllegalArgumentException("Invalid refresh interval: " + interval + " " + timeUnit);
			}
			this.refreshIntervalMillis = timeUnit.toMillis(interval);
			return this;
		}

		/**
		 * Sets the renderer, a {@link ConsoleProgressRenderer} on {@code System.out} by default.
		 */
		public Builder renderer(ProgressRenderer renderer) {
			this.renderer = renderer;
			return this;
		}

		/**
		 * Sets the time source in milliseconds; package private, for tests.
		 */
		Builder clock(LongSupplier clock) {
			this.clock = clock;
			return this;
		}

		public ProgressBar build() {
			return new ProgressBar(this);
		}
	}

}
