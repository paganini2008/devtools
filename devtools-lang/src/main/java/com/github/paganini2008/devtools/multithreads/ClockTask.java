/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.multithreads;

import java.util.UUID;

/**
 * 
 * ClockTask
 *
 * @author Fred Feng
 * @since 1.0
 */
public abstract class ClockTask implements Runnable {

	private final String taskId;
	private volatile boolean cancelled;

	protected ClockTask() {
		this.taskId = UUID.randomUUID().toString();
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean cancel() {
		this.cancelled = true;
		return true;
	}

	public String getTaskId() {
		return taskId;
	}

	public void run() {
		if (!isCancelled()) {
			runTask();
		}
	}

	protected abstract void runTask();

}
