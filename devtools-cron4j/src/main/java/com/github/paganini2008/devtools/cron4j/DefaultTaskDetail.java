/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.cron4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.cron4j.TaskExecutor.TaskDetail;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * DefaultTaskDetail
 *
 * @author Fred Feng
 * @version 1.0
 */
public class DefaultTaskDetail implements TaskDetail {

	final Task task;
	final Trigger trigger;
	final AtomicBoolean running = new AtomicBoolean(false);
	final AtomicInteger completedCount = new AtomicInteger(0);
	final AtomicInteger failedCount = new AtomicInteger(0);
	volatile long lastExectionTime = -1;
	volatile long nextExectionTime = -1;

	DefaultTaskDetail(Task task, Trigger trigger) {
		this.task = task;
		this.trigger = trigger;
	}

	public boolean isRunning() {
		return running.get();
	}

	public int completedCount() {
		return completedCount.get();
	}

	public int failedCount() {
		return failedCount.get();
	}

	public long lastExectionTime() {
		return lastExectionTime;
	}

	public long nextExectionTime() {
		return nextExectionTime;
	}

	public void completedCount(int count) {
		completedCount.set(count);
	}

	public void failedCount(int count) {
		failedCount.set(count);
	}

	public void nextExectionTime(long time) {
		nextExectionTime = time;
	}

	public Task getTaskObject() {
		return task;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[TaskDetail] Task: ").append(task.getClass().getName());
		str.append(", Running: ").append(isRunning());
		str.append(", CompletedCount: ").append(completedCount());
		str.append(", FailedCount: ").append(failedCount());
		str.append(", LastExecuted: ").append(lastExectionTime() > 0 ? DateUtils.format(lastExectionTime()) : "-");
		str.append(", NextExecuted: ").append(nextExectionTime() > 0 ? DateUtils.format(nextExectionTime()) : "-");
		return str.toString();
	}

}