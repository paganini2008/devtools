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

/**
 *
 * An immutable view of a {@link ProgressBar} taken at one instant, passed to {@link ProgressRenderer renderers}.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public final class ProgressSnapshot {

	private final String taskName;
	private final long completed;
	private final long total;
	private final String unit;
	private final int width;
	private final double percent;
	private final long elapsedMillis;
	private final double ratePerSecond;
	private final long etaMillis;
	private final String extraMessage;
	private final ProgressState state;
	private final Throwable failure;

	/**
	 * Creates a snapshot; derived values (percent, rate, ETA) are computed from {@code completed}, {@code total} and
	 * {@code elapsedMillis} with the same rules as {@link ProgressBar}.
	 */
	public ProgressSnapshot(String taskName, long completed, long total, String unit, int width, long elapsedMillis,
			String extraMessage, ProgressState state, Throwable failure) {
		this.taskName = taskName != null ? taskName : "";
		this.completed = completed;
		this.total = total > 0 ? total : -1L;
		this.unit = unit != null ? unit : "";
		this.width = width;
		this.elapsedMillis = Math.max(0L, elapsedMillis);
		this.extraMessage = extraMessage;
		this.state = state != null ? state : ProgressState.NEW;
		this.failure = failure;
		this.percent = percent(completed, this.total);
		this.ratePerSecond = rate(completed, this.elapsedMillis);
		this.etaMillis = eta(completed, this.total, this.elapsedMillis);
	}

	static double percent(long completed, long total) {
		if (total <= 0) {
			return -1d;
		}
		return Math.min(100d, completed * 100d / total);
	}

	static double rate(long completed, long elapsedMillis) {
		return elapsedMillis > 0 ? completed * 1000d / elapsedMillis : 0d;
	}

	static long eta(long completed, long total, long elapsedMillis) {
		if (total <= 0 || completed <= 0 || elapsedMillis <= 0) {
			return -1L;
		}
		if (completed >= total) {
			return 0L;
		}
		return (long) ((double) (total - completed) * elapsedMillis / completed);
	}

	/**
	 * Returns the task name, never null.
	 */
	public String getTaskName() {
		return taskName;
	}

	public long getCompleted() {
		return completed;
	}

	/**
	 * Returns the total amount of work, -1 if unknown.
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * Returns true if the total amount of work is known.
	 */
	public boolean isTotalKnown() {
		return total > 0;
	}

	/**
	 * Returns the unit of work, such as "rows" or "B"; never null.
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Returns the preferred width (in characters) of the bar.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the completed percentage between 0 and 100, -1 if the total is unknown.
	 */
	public double getPercent() {
		return percent;
	}

	public long getElapsedMillis() {
		return elapsedMillis;
	}

	/**
	 * Returns the average count of work units completed per second since start.
	 */
	public double getRatePerSecond() {
		return ratePerSecond;
	}

	/**
	 * Returns the estimated remaining time in milliseconds, -1 if it cannot be estimated.
	 */
	public long getEtaMillis() {
		return etaMillis;
	}

	/**
	 * Returns the extra message, may be null.
	 */
	public String getExtraMessage() {
		return extraMessage;
	}

	public ProgressState getState() {
		return state;
	}

	/**
	 * Returns the cause given to {@link ProgressBar#fail(Throwable)}, may be null.
	 */
	public Throwable getFailure() {
		return failure;
	}

	@Override
	public String toString() {
		return ProgressFormats.formatLine(this, true, '=', '>', ' ', true, true);
	}

}
