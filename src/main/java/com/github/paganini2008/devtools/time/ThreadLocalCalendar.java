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

import java.util.Calendar;
import java.util.function.Supplier;

/**
 * A {@link ThreadLocal} of {@link Calendar}, so that a mutable calendar can be reused safely within one thread.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class ThreadLocalCalendar extends ThreadLocal<Calendar> {

	private final Supplier<Calendar> supplier;

	ThreadLocalCalendar(Supplier<Calendar> supplier) {
		this.supplier = supplier;
	}

	@Override
	protected final Calendar initialValue() {
		return supplier.get();
	}

	private static final ThreadLocalCalendar instance = get(() -> Calendar.getInstance());

	/**
	 * Returns the shared thread-local holder of {@code Calendar.getInstance()}.
	 */
	public static ThreadLocalCalendar current() {
		return instance;
	}

	/**
	 * Creates a thread-local holder whose initial value of each thread comes from the supplier.
	 */
	public static ThreadLocalCalendar get(Supplier<Calendar> supplier) {
		return new ThreadLocalCalendar(supplier);
	}

}
