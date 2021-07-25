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
package com.github.paganini2008.devtools.cron4j;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Cancellables
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class Cancellables {

	public static Cancellable cancelIfRuns(int count) {
		final AtomicInteger total = new AtomicInteger(0);
		return taskDetail -> {
			return count > 0 && total.getAndIncrement() >= count;
		};
	}

	public static Cancellable cancelWhen(Date future) {
		return taskDetail -> {
			return System.currentTimeMillis() >= future.getTime();
		};
	}

}
