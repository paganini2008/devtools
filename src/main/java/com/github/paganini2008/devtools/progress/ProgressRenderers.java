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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Factory methods of common {@link ProgressRenderer renderers}.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class ProgressRenderers {

	private static final ProgressRenderer NOOP = snapshot -> {
	};

	/**
	 * Returns a renderer which renders nothing.
	 */
	public static ProgressRenderer noop() {
		return NOOP;
	}

	/**
	 * Returns a renderer forwarding to every given renderer in order; null elements are skipped. A null or empty array
	 * gives {@link #noop()}.
	 */
	public static ProgressRenderer composite(ProgressRenderer... renderers) {
		final List<ProgressRenderer> list = new ArrayList<ProgressRenderer>();
		if (renderers != null) {
			for (ProgressRenderer renderer : renderers) {
				if (renderer != null) {
					list.add(renderer);
				}
			}
		}
		if (list.isEmpty()) {
			return NOOP;
		}
		return new ProgressRenderer() {

			@Override
			public void render(ProgressSnapshot snapshot) {
				for (ProgressRenderer renderer : list) {
					renderer.render(snapshot);
				}
			}

			@Override
			public void onFinish(ProgressSnapshot snapshot) {
				for (ProgressRenderer renderer : list) {
					renderer.onFinish(snapshot);
				}
			}
		};
	}

}
