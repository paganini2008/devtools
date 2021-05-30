package com.github.paganini2008.devtools.cron4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Day;
import com.github.paganini2008.devtools.cron4j.cron.EveryYear;
import com.github.paganini2008.devtools.cron4j.cron.Hour;
import com.github.paganini2008.devtools.cron4j.cron.Minute;
import com.github.paganini2008.devtools.cron4j.cron.Month;
import com.github.paganini2008.devtools.cron4j.cron.TheDayOfWeek;
import com.github.paganini2008.devtools.cron4j.cron.TheDayOfWeekInMonth;
import com.github.paganini2008.devtools.cron4j.cron.Week;
import com.github.paganini2008.devtools.cron4j.cron.Year;
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
		CronExpression copy = cronExpression.copy();
		if (copy instanceof Year) {
			copy = ((Year) copy).Jan().day(1).at(0, 0, 0);
		} else if (copy instanceof Month) {
			copy = ((Month) copy).day(1).at(0, 0, 0);
		} else if (copy instanceof Week) {
			copy = ((Week) copy).Mon().at(0, 0, 0);
		} else if (copy instanceof Day) {
			copy = ((Day) copy).at(0, 0, 0);
		} else if (copy instanceof Hour) {
			copy = ((Hour) copy).at(0, 0);
		} else if (copy instanceof Minute) {
			copy = ((Minute) copy).second(0);
		}

		final StringBuilder cron = new StringBuilder();
		CronExpression second = copy;
		CronExpression minute = second.getParent();
		CronExpression hour = minute.getParent();
		cron.append(second.toCronString()).append(" ").append(minute.toCronString()).append(" ").append(hour.toCronString()).append(" ");

		CronExpression day = hour.getParent();
		boolean hasDayOfWeek = false;
		if (day instanceof TheDayOfWeek || day instanceof TheDayOfWeekInMonth) {
			hasDayOfWeek = true;
		}
		if (hasDayOfWeek) {
			cron.append("?").append(" ");
		} else {
			cron.append(day.toCronString()).append(" ");
		}

		CronExpression month;
		if (hasDayOfWeek && day.getParent() instanceof Week) {
			month = day.getParent().getParent();
		} else {
			month = day.getParent();
		}

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

}
