package com.github.paganini2008.devtools.cron4j.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Epoch;
import com.github.paganini2008.devtools.cron4j.cron.EveryYear;
import com.github.paganini2008.devtools.cron4j.cron.OneDayOfWeek;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * Cron
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public abstract class Cron {

	public static String toCronString(CronExpression cronExpression) {
		StringBuilder cron = new StringBuilder();
		CronExpression second = cronExpression;
		CronExpression minute = second.getParent();
		CronExpression hour = minute.getParent();
		cron.append(second.toCronString()).append(" ").append(minute.toCronString()).append(" ").append(hour.toCronString()).append(" ");

		CronExpression day = hour.getParent();
		boolean hasDayOfWeek = false;
		CronExpression week = null;
		if (day instanceof OneDayOfWeek) {
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
			throw new CronParserException(cronString);
		}
		Collections.reverse(clauses);
		Collections.swap(clauses, 1, 2);
		List<CronOption> parsers = new ArrayList<CronOption>(7);
		String value = clauses.get(0);
		parsers.add(new YearOption(value));

		value = clauses.get(1);
		parsers.add(new MonthOption(value));

		value = clauses.get(2);
		if (!value.equals("?")) {
			parsers.add(new DayOfWeekOption(value));
		}

		value = clauses.get(3);
		if (!value.equals("?")) {
			parsers.add(new DayOption(value));
		}

		value = clauses.get(4);
		parsers.add(new HourOption(value));

		value = clauses.get(5);
		parsers.add(new MinuteOption(value));

		value = clauses.get(6);
		parsers.add(new SecondOption(value));

		CronExpression cronExpression = new Epoch();
		try {
			for (CronOption clause : parsers) {
				cronExpression = clause.join(cronExpression);
			}
			return cronExpression;
		} catch (RuntimeException e) {
			if (e instanceof CronParserException) {
				throw e;
			}
			throw new CronParserException(cronString, e);
		}
	}

	public static void main(String[] args) {
		CronExpression cronExpression = Cron.parse("0 0 12 ? 3,4 5L 2020-2025");
		cronExpression.forEach(date -> {
			System.out.println(DateUtils.format(date));
		}, 10);
	}

}
