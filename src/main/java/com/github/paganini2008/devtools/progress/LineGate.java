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
 * Package private helper deciding whether a line-oriented renderer should emit a line for a snapshot: the first
 * snapshot, every crossed percent step (known total), every time interval (unknown total) and the final snapshot.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
final class LineGate {

	private final int percentStep;
	private final long intervalMillis;
	private int lastBucket = -1;
	private long lastElapsed = -1L;

	LineGate(int percentStep, long intervalMillis) {
		if (percentStep <= 0 || percentStep > 100) {
			throw new IllegalArgumentException("Percent step must be between 1 and 100: " + percentStep);
		}
		if (intervalMillis < 0) {
			throw new IllegalArgumentException("Negative interval: " + intervalMillis);
		}
		this.percentStep = percentStep;
		this.intervalMillis = intervalMillis;
	}

	boolean accept(ProgressSnapshot snapshot) {
		boolean first = lastBucket < 0 && lastElapsed < 0;
		boolean accepted;
		if (first || snapshot.getState().isFinished()) {
			accepted = true;
		} else if (snapshot.isTotalKnown()) {
			int bucket = (int) (snapshot.getPercent() / percentStep);
			accepted = bucket > lastBucket && snapshot.getPercent() < 100d;
		} else {
			accepted = snapshot.getElapsedMillis() - lastElapsed >= intervalMillis;
		}
		if (accepted) {
			lastBucket = snapshot.isTotalKnown() ? (int) (snapshot.getPercent() / percentStep) : 0;
			lastElapsed = snapshot.getElapsedMillis();
		}
		return accepted;
	}

	void reset() {
		lastBucket = -1;
		lastElapsed = -1L;
	}

}
