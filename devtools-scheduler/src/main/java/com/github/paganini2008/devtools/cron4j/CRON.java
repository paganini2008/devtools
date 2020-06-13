package com.github.paganini2008.devtools.cron4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.EveryYear;
import com.github.paganini2008.devtools.cron4j.cron.TheDayOfWeek;
import com.github.paganini2008.devtools.cron4j.parser.CronOption;
import com.github.paganini2008.devtools.cron4j.parser.DayOfWeekOption;
import com.github.paganini2008.devtools.cron4j.parser.DayOption;
import com.github.paganini2008.devtools.cron4j.parser.Epoch;
import com.github.paganini2008.devtools.cron4j.parser.HourOption;
import com.github.paganini2008.devtools.cron4j.parser.MalformedCronException;
import com.github.paganini2008.devtools.cron4j.parser.MinuteOption;
import com.github.paganini2008.devtools.cron4j.parser.MonthOption;
import com.github.paganini2008.devtools.cron4j.parser.SecondOption;
import com.github.paganini2008.devtools.cron4j.parser.YearOption;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * CRON
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public abstract class CRON {

	public static String toCronString(CronExpression cronExpression) {
		StringBuilder cron = new StringBuilder();
		
		CronExpression second = cronExpression;
		CronExpression minute = second.getParent();
		CronExpression hour = minute.getParent();
		cron.append(second.toCronString()).append(" ").append(minute.toCronString()).append(" ").append(hour.toCronString()).append(" ");

		CronExpression day = hour.getParent();
		boolean hasDayOfWeek = false;
		CronExpression week = null;
		if (day instanceof TheDayOfWeek) {
			hasDayOfWeek = true;
		}
		if (hasDayOfWeek) {
			cron.append("?").append(" ");
			week = day.getParent();
		} else {
			cron.append(day.toCronString()).append(" ");
		}

		CronExpression month = week != null ? week.getParent() : day.getParent();
		cron.append(month.toCronString()).append(" ");

		if (hasDayOfWeek) {
			cron.append(day.toCronString());
		} else {
			cron.append("?");
		}

		CronExpression year = month.getParent();
		if (!(year instanceof EveryYear)) {
			cron.append(" ").append(year.toCronString());
		}
		return cron.toString();
	}

	public static CronExpression parse(String cronString) {
		List<String> clauses = StringUtils.split(cronString, " ");
		if (clauses.size() == 6) {
			clauses.add("*");
		}
		if (clauses.size() != 7) {
			throw new MalformedCronException(cronString);
		}
		Collections.reverse(clauses);
		Collections.swap(clauses, 1, 2);
		List<CronOption> parsers = new ArrayList<CronOption>(7);
		String value = clauses.get(0);
		parsers.add(new YearOption(value));

		value = clauses.get(1);
		parsers.add(new MonthOption(value));

		boolean hasDay = false;
		value = clauses.get(2);
		if (!value.equals("?")) {
			parsers.add(new DayOfWeekOption(value));
			hasDay = true;
		}

		value = clauses.get(3);
		if (!value.equals("?")) {
			if (hasDay) {
				throw new MalformedCronException(cronString);
			}
			parsers.add(new DayOption(value));
		}

		value = clauses.get(4);
		parsers.add(new HourOption(value));

		value = clauses.get(5);
		parsers.add(new MinuteOption(value));

		value = clauses.get(6);
		parsers.add(new SecondOption(value));

		CronExpression cronExpression = Epoch.getInstance();
		try {
			for (CronOption clause : parsers) {
				cronExpression = clause.join(cronExpression);
			}
			return cronExpression;
		} catch (RuntimeException e) {
			if (e instanceof MalformedCronException) {
				throw e;
			}
			throw new MalformedCronException(cronString, e);
		}
	}

	public static void main(String[] args) {
		CronExpression cronExpression = CRON.parse("0 10 23 ? * 6#3,5#2,6L");
		cronExpression.forEach(date -> {
			System.out.println(DateUtils.format(date));
		}, 20);
		System.out.println(CRON.toCronString(cronExpression));
	}

}
