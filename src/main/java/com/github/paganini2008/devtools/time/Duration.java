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

import com.github.paganini2008.devtools.StringUtils;

/**
 * Formats a duration in milliseconds as days, hours or minutes, e.g. {@code 1d:2h:3m:4s:5ms}.
 * 
 * @author Fred Feng
 * @date 2020-02-20
 * @since 2.0.1
 */
public enum Duration {

	DAY("#d:#h:#m:#s:#ms") {
		public Number[] toArray(long ms) {
			long day = ms / d;
			long hour = (ms - day * d) / H;
			long minute = (ms - day * d - hour * H) / m;
			long second = (ms - day * d - hour * H - minute * m) / s;
			long millisecond = ms - day * d - hour * H - minute * m - second * s;
			return new Long[] { day, hour, minute, second, millisecond };
		}
	},

	HOUR("#h:#m:#s:#ms") {
		public Number[] toArray(long ms) {
			long hour = ms / H;
			long minute = (ms - hour * H) / m;
			long second = (ms - hour * H - minute * m) / s;
			long millisecond = ms - hour * H - minute * m - second * s;
			return new Long[] { hour, minute, second, millisecond };
		}
	},

	MINUTE("#m:#s:#ms") {
		public Number[] toArray(long ms) {
			long minute = ms / m;
			long second = (ms - minute * m) / s;
			long millisecond = ms - minute * m - second * s;
			return new Long[] { minute, second, millisecond };
		}
	};

	private static final long s = 1000L;

	private static final long m = s * 60;

	private static final long H = m * 60;

	private static final long d = H * 24;

	private final String pattern;

	private Duration(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}

	/**
	 * Splits a duration in milliseconds into the fields of this unit, the first field is not capped.
	 *
	 * <pre>
	 * Duration.HOUR.toArray(93784005L) = [26, 3, 4, 5]
	 * </pre>
	 */
	public abstract Number[] toArray(long ms);

	/**
	 * Formats a duration in milliseconds with the default pattern of this unit.
	 *
	 * <pre>
	 * Duration.DAY.format(93784005L)    = "1d:2h:3m:4s:5ms"
	 * Duration.HOUR.format(93784005L)   = "26h:3m:4s:5ms"
	 * Duration.MINUTE.format(93784005L) = "1563m:4s:5ms"
	 * </pre>
	 */
	public String format(long ms) {
		return format(ms, null);
	}

	/**
	 * Formats a duration in milliseconds, each {@code #} in the pattern is replaced by the next field.
	 * A blank pattern means the default pattern.
	 *
	 * <pre>
	 * Duration.DAY.format(93784005L, "# days # hours") = "1 days 2 hours"
	 * </pre>
	 */
	@SuppressWarnings("all")
	public String format(long ms, String pattern) {
		Number[] array = toArray(ms);
		return StringUtils.format(StringUtils.isNotBlank(pattern) ? pattern : getPattern(), "#", array);
	}

}
