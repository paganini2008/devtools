package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ThisWeekOfYear
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisWeekOfYear implements TheWeek, Serializable {

	private static final long serialVersionUID = -3294283555586718358L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Year year;
	private int index;
	private Calendar week;
	private int lastWeek;

	ThisWeekOfYear(Year year, int week) {
		CalendarAssert.checkWeekOfYear(year, week);
		this.year = year;
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.WEEK_OF_YEAR, week);
		this.siblings.put(week, calendar);
		this.week = calendar;
		this.lastWeek = week;
	}

	public TheWeek andWeek(int week) {
		CalendarAssert.checkWeekOfYear(year, week);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.WEEK_OF_YEAR, week);
		this.siblings.put(week, calendar);
		this.lastWeek = week;
		return this;
	}

	public TheWeek toWeek(int week, int interval) {
		CalendarAssert.checkWeekOfYear(year, week);
		for (int i = lastWeek + interval; i < week; i += interval) {
			andWeek(i);
		}
		return this;
	}

	public Date getTime() {
		return week.getTime();
	}

	public long getTimeInMillis() {
		return week.getTimeInMillis();
	}

	public int getYear() {
		return week.get(Calendar.YEAR);
	}

	public int getMonth() {
		return week.get(Calendar.MONTH);
	}

	public int getWeek() {
		return week.get(Calendar.WEEK_OF_MONTH);
	}

	public int getWeekOfYear() {
		return week.get(Calendar.WEEK_OF_YEAR);
	}

	public TheDayOfWeek day(int day) {
		final Week copy = (Week) this;
		return new ThisDayOfWeek(CollectionUtils.getFirst(copy), day);
	}

	public Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		final Week copy = (Week) this;
		return new EveryDayOfWeek(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (year.hasNext()) {
				year = year.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Week next() {
		Map.Entry<Integer, Calendar> entry = MapUtils.getEntry(siblings, index++);
		week = entry.getValue();
		week.set(Calendar.YEAR, year.getYear());
		week.set(Calendar.WEEK_OF_YEAR, Math.min(entry.getKey(), year.getWeekCount()));
		return this;
	}

	public CronExpression getParent() {
		return year;
	}
}
