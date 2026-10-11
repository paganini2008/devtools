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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RecordingRenderer records every rendered snapshot and detects overlapping calls.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class RecordingRenderer implements ProgressRenderer {

	final List<ProgressSnapshot> rendered = new CopyOnWriteArrayList<ProgressSnapshot>();
	final List<ProgressSnapshot> finished = new CopyOnWriteArrayList<ProgressSnapshot>();
	final AtomicInteger inside = new AtomicInteger();
	final AtomicInteger overlaps = new AtomicInteger();

	@Override
	public void render(ProgressSnapshot snapshot) {
		if (inside.incrementAndGet() > 1) {
			overlaps.incrementAndGet();
		}
		rendered.add(snapshot);
		inside.decrementAndGet();
	}

	@Override
	public void onFinish(ProgressSnapshot snapshot) {
		finished.add(snapshot);
	}

	ProgressSnapshot last() {
		return rendered.get(rendered.size() - 1);
	}
}
