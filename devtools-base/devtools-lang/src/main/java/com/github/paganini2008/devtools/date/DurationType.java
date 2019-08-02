package com.github.paganini2008.devtools.date;

/**
 * DurationType
 * 
 * @author Fred Feng
 * @version 1.0
 */
public enum DurationType {
	DAY {
		long[] toArray(long ms) {
			long day = ms / d;
			long hour = (ms - day * d) / H;
			long minute = (ms - day * d - hour * H) / m;
			long second = (ms - day * d - hour * H - minute * m) / s;
			long millisecond = ms - day * d - hour * H - minute * m - second * s;
			return new long[] { day, hour, minute, second, millisecond };
		}
	},
	HOUR {
		long[] toArray(long ms) {
			long hour = ms / H;
			long minute = (ms - hour * H) / m;
			long second = (ms - hour * H - minute * m) / s;
			long millisecond = ms - hour * H - minute * m - second * s;
			return new long[] { hour, minute, second, millisecond };
		}
	},
	MINUTE {
		long[] toArray(long ms) {
			long minute = ms / m;
			long second = (ms - minute * m) / s;
			long millisecond = ms - minute * m - second * s;
			return new long[] { minute, second, millisecond };
		}
	};

	private static final long s = 1000L;

	private static final long m = s * 60;

	private static final long H = m * 60;

	private static final long d = H * 24;

	abstract long[] toArray(long ms);

}
