package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;

public abstract class Utils {

	public static Calendar getInstance(int calendarField, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendarField, value);
		switch (calendarField) {
		case Calendar.YEAR:
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
		case Calendar.MONTH:
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		case Calendar.DAY_OF_MONTH:
			calendar.set(Calendar.HOUR_OF_DAY, 0);
		case Calendar.HOUR:
			calendar.set(Calendar.MINUTE, 0);
		case Calendar.MINUTE:
			calendar.set(Calendar.SECOND, 0);
			break;
		}
		return calendar;
	}

}
