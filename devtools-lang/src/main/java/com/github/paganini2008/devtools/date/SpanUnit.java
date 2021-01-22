package com.github.paganini2008.devtools.date;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * 
 * SpanUnit
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public enum SpanUnit {

	HOUR {

		@Override
		public Calendar startsWith(Calendar c, long timestamp, int span) {
			c.setTimeInMillis(timestamp);
			int hour = c.get(Calendar.HOUR_OF_DAY);
			c.set(Calendar.HOUR_OF_DAY, hour - hour % span);
			return c;
		}

		@Override
		public long startsInMsWith(Calendar c, long timestamp, int span) {
			c = startsWith(c, timestamp, span);
			return TimeUnit.HOURS.convert(c.getTimeInMillis(), TimeUnit.MILLISECONDS) * 60 * 60 * 1000;
		}

	},
	MINUTE {

		@Override
		public Calendar startsWith(Calendar c, long timestamp, int span) {
			c.setTimeInMillis(timestamp);
			int minute = c.get(Calendar.MINUTE);
			c.set(Calendar.MINUTE, minute - minute % span);
			return c;
		}

		@Override
		public long startsInMsWith(Calendar c, long timestamp, int span) {
			c = startsWith(c, timestamp, span);
			return TimeUnit.MINUTES.convert(c.getTimeInMillis(), TimeUnit.MILLISECONDS) * 60 * 1000;
		}

	},
	SECOND {

		@Override
		public Calendar startsWith(Calendar c, long timestamp, int span) {
			c.setTimeInMillis(timestamp);
			int second = c.get(Calendar.SECOND);
			c.set(Calendar.SECOND, second - second % span);
			return c;
		}

		@Override
		public long startsInMsWith(Calendar c, long timestamp, int span) {
			c = startsWith(c, timestamp, span);
			return TimeUnit.SECONDS.convert(c.getTimeInMillis(), TimeUnit.MILLISECONDS) * 1000;
		}

	};

	public abstract Calendar startsWith(Calendar c, long timestamp, int span);

	public abstract long startsInMsWith(Calendar c, long timestamp, int span);

}
