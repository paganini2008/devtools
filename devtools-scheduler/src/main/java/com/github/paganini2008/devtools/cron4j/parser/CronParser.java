package com.github.paganini2008.devtools.cron4j.parser;

import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Epoch;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * CronParser
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public abstract class CronParser {

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
		List<Clause> parsers = new ArrayList<Clause>(7);
		String value = clauses.get(0);
		parsers.add(new YearClause(value));

		value = clauses.get(1);
		parsers.add(new MonthClause(value));

		value = clauses.get(2);
		if (!value.equals("?")) {
			parsers.add(new WeekDayClause(value));
		}

		value = clauses.get(3);
		if (!value.equals("?")) {
			parsers.add(new DayClause(value));
		}

		value = clauses.get(4);
		parsers.add(new HourClause(value));

		value = clauses.get(5);
		parsers.add(new MinuteClause(value));

		value = clauses.get(6);
		parsers.add(new SecondClause(value));

		CronExpression cronExpression = new Epoch();
		try {
			for (Clause clause : parsers) {
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
		CronExpression cronExpression = CronParser.parse("0 0 12 ? 3,4 5L 2020-2025");
		AtomicInteger counter = new AtomicInteger();
		cronExpression.forEach(date -> {
			System.out.println(DateUtils.format(date));
			if (counter.incrementAndGet() > 10) {
				throw new IllegalSelectorException();
			}
		});
	}

}
