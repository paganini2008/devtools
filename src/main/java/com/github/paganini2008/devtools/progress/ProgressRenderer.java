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
 * Displays the snapshots of a {@link ProgressBar}, for example on the console or in a log.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public interface ProgressRenderer {

	/**
	 * Renders a snapshot. Called at start, at most once per refresh interval while running, and once when the bar is
	 * finished (completed or failed). Calls of one bar never overlap.
	 */
	void render(ProgressSnapshot snapshot);

	/**
	 * Called once after the final {@link #render(ProgressSnapshot)} of a finished bar.
	 */
	default void onFinish(ProgressSnapshot snapshot) {
	}

}
