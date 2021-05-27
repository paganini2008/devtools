package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * ThisWeek
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisWeek implements TheWeek, Serializable {

	private static final long serialVersionUID = -4563991137870265612L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Month month;
	private int index;
	private Calendar week;
	private int lastWeek;
	private final StringBuilder cron;

	ThisWeek(Month month, int week) {
		CalendarAssert.checkWeekOfMonth(month, week);
		this.month = month;
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		this.siblings.put(week, calendar);
		this.week = calendar;
		this.lastWeek = week;
		this.cron = new StringBuilder().append("%s#").append(week);
	}

	public ThisWeek andWeek(int week) {
		return andWeek(week, true);
	}

	public ThisWeek andWeek(int week, boolean writeCron) {
		CalendarAssert.checkWeekOfMonth(month, week);
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		this.siblings.put(week, calendar);
		this.lastWeek = week;
		if (writeCron) {
			this.cron.append(",%s#").append(week);
		}
		return this;
	}

	public ThisWeek toWeek(int week, int interval) {
		CalendarAssert.checkWeekOfMonth(month, week);
		List<Integer> weeks = new ArrayList<Integer>();
		for (int i = lastWeek + interval; i < week; i += interval) {
			andWeek(i, false);
			weeks.add(i);
		}
		for (int w : weeks) {
			this.cron.append(",%s#").append(w);
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
		final Week copy = (Week) this.copy();
		return new ThisDayOfWeek(CollectionUtils.getFirst(copy), day);
	}

	public Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		final Week copy = (Week) this.copy();
		return new EveryDayOfWeek(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Week next() {
		Map.Entry<Integer, Calendar> entry = MapUtils.getEntry(siblings, index++);
		week = entry.getValue();
		week.set(Calendar.YEAR, month.getYear());
		week.set(Calendar.MONTH, month.getMonth());
		week.set(Calendar.WEEK_OF_MONTH, Math.min(entry.getKey(), month.getWeekCount()));
		return this;
	}

	public CronExpression getParent() {
		return month;
	}

	public String toCronString() {
		return this.cron.toString();
	}

	public String toString() {
		return CRON.toCronString(this);
	}

}
