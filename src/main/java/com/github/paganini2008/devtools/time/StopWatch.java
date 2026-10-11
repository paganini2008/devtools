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
package com.github.paganini2008.devtools.time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Stop watch based on {@link System#nanoTime()} that times a sequence of named tasks, with
 * support for splits (laps) and suspend/resume.
 *
 * <p>
 * Lifecycle: {@code start(name)} begins a task, {@code split()} records a lap and keeps
 * running, {@code suspend()}/{@code resume()} pause the current task, and {@code stop()}
 * finishes it; after {@code stop()} a new task may be started and its time is added to the
 * total. Illegal state transitions throw {@link IllegalStateException}.
 *
 * <pre>
 * StopWatch watch = new StopWatch("import");
 * watch.start("load");
 * // ... 100 ms
 * watch.stop();
 * watch.start("parse");
 * // ... 300 ms
 * watch.stop();
 * watch.getTime();                 // =&gt; 400
 * watch.getTaskInfos().get(0)      // =&gt; load: 100000000 ns
 * </pre>
 *
 * <p>
 * This class is <b>not thread-safe</b>; confine an instance to one thread or synchronize
 * externally.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class StopWatch {

	private enum State {
		UNSTARTED, RUNNING, SUSPENDED, STOPPED
	}

	/**
	 * Name and elapsed time of one recorded task or lap.
	 */
	public static final class TaskInfo {

		private final String taskName;
		private final long timeNanos;

		TaskInfo(String taskName, long timeNanos) {
			this.taskName = taskName;
			this.timeNanos = timeNanos;
		}

		public String getTaskName() {
			return taskName;
		}

		public long getTimeNanos() {
			return timeNanos;
		}

		public long getTimeMillis() {
			return TimeUnit.NANOSECONDS.toMillis(timeNanos);
		}

		/**
		 * Returns the elapsed time converted (truncated) to the given unit.
		 */
		public long getTime(TimeUnit unit) {
			return unit.convert(timeNanos, TimeUnit.NANOSECONDS);
		}

		@Override
		public String toString() {
			return taskName + ": " + timeNanos + " ns";
		}
	}

	private final String id;
	private final List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();

	private State state = State.UNSTARTED;
	private String currentTaskName;
	private long segmentStartNanos;
	private long suspendStartNanos;
	private int splitCount;
	private int startCount;
	private long recordedNanos;

	/**
	 * Creates an unnamed stop watch.
	 */
	public StopWatch() {
		this("");
	}

	/**
	 * Creates a stop watch with the given id (used by {@link #prettyPrint()} and
	 * {@link #toString()}); null is treated as empty.
	 */
	public StopWatch(String id) {
		this.id = id != null ? id : "";
	}

	/**
	 * Creates a stop watch and starts a default task.
	 */
	public static StopWatch createStarted() {
		StopWatch watch = new StopWatch();
		watch.start();
		return watch;
	}

	public String getId() {
		return id;
	}

	/**
	 * Starts a task with a default name ("task-1", "task-2", ...).
	 *
	 * @throws IllegalStateException if a task is already running or suspended
	 */
	public void start() {
		start(null);
	}

	/**
	 * Starts a new named task. Null uses a default name ("task-N").
	 *
	 * @throws IllegalStateException if a task is already running or suspended
	 */
	public void start(String taskName) {
		if (state == State.RUNNING || state == State.SUSPENDED) {
			throw new IllegalStateException("StopWatch is already running task '" + currentTaskName + "'");
		}
		startCount++;
		currentTaskName = taskName != null ? taskName : "task-" + startCount;
		splitCount = 0;
		segmentStartNanos = nanoTime();
		state = State.RUNNING;
	}

	/**
	 * Records a lap of the current task (time since start or the previous split) and keeps
	 * running. Laps are named "taskName#1", "taskName#2", ...; the segment finished by the
	 * following {@link #stop()} is then named "taskName#(n+1)".
	 *
	 * @return the lap time in nanoseconds
	 * @throws IllegalStateException if the stop watch is not running
	 */
	public long split() {
		if (state != State.RUNNING) {
			throw new IllegalStateException("StopWatch is not running");
		}
		long now = nanoTime();
		long lap = now - segmentStartNanos;
		splitCount++;
		record(currentTaskName + "#" + splitCount, lap);
		segmentStartNanos = now;
		return lap;
	}

	/**
	 * Suspends the current task; suspended time is not counted.
	 *
	 * @throws IllegalStateException if the stop watch is not running
	 */
	public void suspend() {
		if (state != State.RUNNING) {
			throw new IllegalStateException("StopWatch must be running to suspend");
		}
		suspendStartNanos = nanoTime();
		state = State.SUSPENDED;
	}

	/**
	 * Resumes a suspended task.
	 *
	 * @throws IllegalStateException if the stop watch is not suspended
	 */
	public void resume() {
		if (state != State.SUSPENDED) {
			throw new IllegalStateException("StopWatch must be suspended to resume");
		}
		segmentStartNanos += nanoTime() - suspendStartNanos;
		state = State.RUNNING;
	}

	/**
	 * Stops the current (running or suspended) task and records it.
	 *
	 * @return the time of the recorded segment in nanoseconds
	 * @throws IllegalStateException if no task is running or suspended
	 */
	public long stop() {
		if (state != State.RUNNING && state != State.SUSPENDED) {
			throw new IllegalStateException("StopWatch is not running");
		}
		long elapsed = currentSegmentNanos();
		record(splitCount > 0 ? currentTaskName + "#" + (splitCount + 1) : currentTaskName, elapsed);
		state = State.STOPPED;
		return elapsed;
	}

	/**
	 * Clears all recorded tasks and returns to the unstarted state.
	 */
	public void reset() {
		taskInfos.clear();
		state = State.UNSTARTED;
		currentTaskName = null;
		splitCount = 0;
		startCount = 0;
		recordedNanos = 0;
	}

	public boolean isRunning() {
		return state == State.RUNNING;
	}

	public boolean isSuspended() {
		return state == State.SUSPENDED;
	}

	/**
	 * Returns the total elapsed time in nanoseconds: all recorded tasks plus the in-progress
	 * segment (excluding suspended time). Returns 0 before the first start.
	 */
	public long getNanoTime() {
		long total = recordedNanos;
		if (state == State.RUNNING || state == State.SUSPENDED) {
			total += currentSegmentNanos();
		}
		return total;
	}

	/**
	 * Returns the total elapsed time in milliseconds.
	 */
	public long getTime() {
		return TimeUnit.NANOSECONDS.toMillis(getNanoTime());
	}

	/**
	 * Returns the total elapsed time converted (truncated) to the given unit.
	 *
	 * @throws IllegalArgumentException if unit is null
	 */
	public long getTime(TimeUnit unit) {
		if (unit == null) {
			throw new IllegalArgumentException("TimeUnit must not be null");
		}
		return unit.convert(getNanoTime(), TimeUnit.NANOSECONDS);
	}

	/**
	 * Returns an unmodifiable snapshot of the recorded tasks and laps in order.
	 */
	public List<TaskInfo> getTaskInfos() {
		return Collections.unmodifiableList(new ArrayList<TaskInfo>(taskInfos));
	}

	/**
	 * Returns the number of recorded tasks and laps.
	 */
	public int getTaskCount() {
		return taskInfos.size();
	}

	/**
	 * Returns a table of recorded tasks with their time and share of the recorded total.
	 *
	 * <pre>
	 * StopWatch 'import': running time = 400000000 ns (400 ms)
	 * ---------------------------------------------
	 *               ns        %  Task name
	 * ---------------------------------------------
	 *        100000000   25.00%  load
	 *        300000000   75.00%  parse
	 * </pre>
	 */
	public String prettyPrint() {
		String nl = "\n";
		String line = "---------------------------------------------";
		StringBuilder sb = new StringBuilder();
		sb.append("StopWatch '").append(id).append("': running time = ").append(recordedNanos).append(" ns (")
				.append(TimeUnit.NANOSECONDS.toMillis(recordedNanos)).append(" ms)").append(nl);
		sb.append(line).append(nl);
		sb.append(String.format(Locale.ROOT, "%17s %8s  %s", "ns", "%", "Task name")).append(nl);
		sb.append(line).append(nl);
		if (taskInfos.isEmpty()) {
			sb.append("No task info kept").append(nl);
		}
		for (TaskInfo info : taskInfos) {
			double percent = recordedNanos > 0 ? info.getTimeNanos() * 100.0d / recordedNanos : 0.0d;
			sb.append(String.format(Locale.ROOT, "%17d %7.2f%%  %s", info.getTimeNanos(), percent, info.getTaskName())).append(nl);
		}
		return sb.toString();
	}

	/**
	 * Returns a short summary such as {@code "StopWatch 'import': state=STOPPED, tasks=2, total=400 ms"}.
	 */
	@Override
	public String toString() {
		return "StopWatch '" + id + "': state=" + state + ", tasks=" + taskInfos.size() + ", total=" + getTime() + " ms";
	}

	/**
	 * Returns the current value of the time source in nanoseconds; override to supply a custom
	 * clock.
	 */
	protected long nanoTime() {
		return System.nanoTime();
	}

	private long currentSegmentNanos() {
		long end = state == State.SUSPENDED ? suspendStartNanos : nanoTime();
		return end - segmentStartNanos;
	}

	private void record(String name, long nanos) {
		taskInfos.add(new TaskInfo(name, nanos));
		recordedNanos += nanos;
	}

}
