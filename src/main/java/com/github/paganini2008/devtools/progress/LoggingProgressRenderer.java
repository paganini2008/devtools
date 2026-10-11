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

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 *
 * Renders a progress bar as INFO log lines: at start, at every crossed percent step (10% by default) or time interval
 * when the total is unknown (10 seconds by default), and at the end; a failure is logged at ERROR level with its cause.
 *
 * <p>
 * One instance keeps the state of one bar at a time; do not share an instance between bars running concurrently.
 * </p>
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LoggingProgressRenderer implements ProgressRenderer {

	private final Log log;
	private final LineGate gate;

	/**
	 * Logs with the log of this class every 10% or 10 seconds.
	 */
	public LoggingProgressRenderer() {
		this(null);
	}

	/**
	 * Logs with the given log (the log of this class if null) every 10% or 10 seconds.
	 */
	public LoggingProgressRenderer(Log log) {
		this(log, 10, 10000L);
	}

	/**
	 * Logs with the given log (the log of this class if null) every {@code percentStep} percent when the total is known,
	 * every {@code intervalMillis} otherwise.
	 *
	 * @throws IllegalArgumentException if the step is not in [1, 100] or the interval is negative
	 */
	public LoggingProgressRenderer(Log log, int percentStep, long intervalMillis) {
		this.gate = new LineGate(percentStep, intervalMillis);
		this.log = log != null ? log : LogFactory.getLog(LoggingProgressRenderer.class);
	}

	@Override
	public synchronized void render(ProgressSnapshot snapshot) {
		if (!gate.accept(snapshot)) {
			return;
		}
		String line = ProgressFormats.formatLine(snapshot, false, '=', '>', ' ', true, true);
		if (snapshot.getState() == ProgressState.FAILED) {
			log.error(line, snapshot.getFailure());
		} else {
			log.info(line);
		}
	}

	@Override
	public synchronized void onFinish(ProgressSnapshot snapshot) {
		gate.reset();
	}

}
