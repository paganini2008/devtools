package com.github.paganini2008.devtools.date;

import com.github.paganini2008.devtools.StringUtils;

/**
 * Duration
 * 
 * @author Fred Feng
 * @version 1.0
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

	public abstract Number[] toArray(long ms);

	public String format(long ms) {
		return format(ms, null);
	}

	@SuppressWarnings("all")
	public String format(long ms, String pattern) {
		Number[] array = toArray(ms);
		return StringUtils.format(StringUtils.isNotBlank(pattern) ? pattern : getPattern(), "#", array);
	}

}
