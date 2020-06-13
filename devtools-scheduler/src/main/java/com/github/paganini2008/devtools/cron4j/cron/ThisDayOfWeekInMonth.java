package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ThisDayOfWeekInMonth
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class ThisDayOfWeekInMonth implements TheDayOfWeekInMonth, Serializable {

	private static final long serialVersionUID = -5853750543470928852L;
	private final TreeMap<String, Calendar> siblings = new TreeMap<String, Calendar>();
	private final StringBuilder cron = new StringBuilder();
	private Month month;
	private Calendar day;
	private int index;

	ThisDayOfWeekInMonth(Month month, int week, int dayOfWeek) {
		CalendarAssert.checkWeekOfMonth(month, week);
		CalendarAssert.checkDayOfWeek(dayOfWeek);
		this.month = month;
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		calendar = CalendarUtils.setField(calendar, Calendar.DAY_OF_WEEK, dayOfWeek);
		this.siblings.put(day + "#" + week, calendar);
	}

	@Override
	public int getYear() {
		return day.get(Calendar.YEAR);
	}

	@Override
	public int getMonth() {
		return day.get(Calendar.MONTH);
	}

	@Override
	public int getDay() {
		return day.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public int getDayOfWeek() {
		return day.get(Calendar.DAY_OF_WEEK);
	}

	@Override
	public int getDayOfYear() {
		return day.get(Calendar.DAY_OF_YEAR);
	}

	@Override
	public TheHour hour(int hourOfDay) {
		final Day copy = (Day) this.copy();
		return new ThisHour(copy, hourOfDay);
	}

	@Override
	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		final Day copy = (Day) this.copy();
		return new EveryHour(copy, from, to, interval);
	}

	@Override
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

	@Override
	public Day next() {
		Map.Entry<String, Calendar> entry = MapUtils.getEntry(siblings, index++);
		String cron = entry.getKey();
		String[] args = cron.split("#", 2);
		day = entry.getValue();
		day.set(Calendar.YEAR, month.getYear());
		day.set(Calendar.MONTH, month.getMonth());
		day.set(Calendar.WEEK_OF_MONTH, Math.min(Integer.parseInt(args[1]), month.getWeekCount()));
		day.set(Calendar.DAY_OF_WEEK, Integer.parseInt(args[0]));
		return this;
	}

	@Override
	public Date getTime() {
		return day.getTime();
	}

	@Override
	public long getTimeInMillis() {
		return day.getTimeInMillis();
	}

	@Override
	public TheDayOfWeekInMonth and(int week, int day) {
		CalendarAssert.checkWeekOfMonth(month, week);
		CalendarAssert.checkDayOfWeek(day);
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		calendar = CalendarUtils.setField(calendar, Calendar.DAY_OF_WEEK, day);
		this.siblings.put(day + "#" + week, calendar);
		this.cron.append(",").append(day + "#" + week);
		return this;
	}

	@Override
	public TheDayOfWeekInMonth andLast(int datOfWeek) {
		return and(month.getWeekCount(), datOfWeek);
	}

	@Override
	public CronExpression getParent() {
		return month;
	}

	@Override
	public String toCronString() {
		return this.cron.toString();
	}

}
