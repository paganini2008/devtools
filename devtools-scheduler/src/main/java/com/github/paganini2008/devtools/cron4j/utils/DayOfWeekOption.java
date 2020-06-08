package com.github.paganini2008.devtools.cron4j.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Month;
import com.github.paganini2008.devtools.cron4j.cron.OneDayOfWeek;

/**
 * 
 * DayOfWeekOption
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class DayOfWeekOption implements CronOption {

	private static final Map<String, Integer> weekdayMapping = new HashMap<>();

	static {
		weekdayMapping.put("SUN", Calendar.SUNDAY);
		weekdayMapping.put("MON", Calendar.MONDAY);
		weekdayMapping.put("TUES", Calendar.TUESDAY);
		weekdayMapping.put("WED", Calendar.WEDNESDAY);
		weekdayMapping.put("THUR", Calendar.THURSDAY);
		weekdayMapping.put("FRI", Calendar.FRIDAY);
		weekdayMapping.put("SAT", Calendar.SATURDAY);
	}

	private final String value;

	public DayOfWeekOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Month month = (Month) cronExpression;
		if (weekdayMapping.containsKey(value)) {
			return month.everyWeek().weekday(weekdayMapping.get(value));
		} else if (value.equals("*")) {
			return month.everyWeek().everyDay();
		} else if (value.contains("L")) {
			return month.lastWeek().weekday(Integer.parseInt(value.substring(0, 1)));
		} else if (value.contains("#")) {
			String[] args = value.split("#", 2);
			return month.week(Integer.parseInt(args[1])).weekday(Integer.parseInt(args[0]));
		} else if (value.contains(",")) {
			String[] args = value.split(",");
			OneDayOfWeek weekday = null;
			for (String arg : args) {
				if (weekday != null) {
					weekday = weekday.andDay(weekdayMapping.get(arg));
				} else {
					weekday = month.everyWeek().weekday(weekdayMapping.get(arg));
				}
			}
			return weekday;
		} else if (value.contains("-")) {
			String[] args = value.split("-", 2);
			OneDayOfWeek weekday = month.everyWeek().weekday(weekdayMapping.get(args[0]));
			return weekday.toDay(weekdayMapping.get(args[1]));
		} else if (value.contains("/")) {
			String[] args = value.split("\\/", 2);
			OneDayOfWeek weekday = month.everyWeek().weekday(Integer.parseInt(args[0]));
			return weekday.toDay(7, Integer.parseInt(args[1]));
		}
		throw new CronParserException(value);
	}

}
