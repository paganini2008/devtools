package com.github.paganini2008.devtools.date;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 
 * DateBuilder
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DateBuilder implements Serializable, Cloneable {

	private static final long serialVersionUID = 5293454843108004183L;

	private Calendar c;

	public DateBuilder() {
		this(Calendar.getInstance());
	}

	public DateBuilder(Calendar c) {
		this.c = c;
	}

	public void setTimeZone(String str) {
		c.setTimeZone(TimeZone.getTimeZone(str));
	}

	public DateBuilder setYear(int year) {
		c.set(Calendar.YEAR, year);
		return this;
	}

	public DateBuilder setMonth(int month) {
		c.set(Calendar.MONTH, month);
		return this;
	}

	public DateBuilder setJanuary() {
		return setMonth(Calendar.JANUARY);
	}

	public DateBuilder setFebruary() {
		return setMonth(Calendar.FEBRUARY);
	}

	public DateBuilder setMarch() {
		return setMonth(Calendar.MARCH);
	}

	public DateBuilder setApril() {
		return setMonth(Calendar.APRIL);
	}

	public DateBuilder setMay() {
		return setMonth(Calendar.MAY);
	}

	public DateBuilder setJune() {
		return setMonth(Calendar.JUNE);
	}

	public DateBuilder setJuly() {
		return setMonth(Calendar.JULY);
	}

	public DateBuilder setAugust() {
		return setMonth(Calendar.AUGUST);
	}

	public DateBuilder setSeptember() {
		return setMonth(Calendar.SEPTEMBER);
	}

	public DateBuilder setOctober() {
		return setMonth(Calendar.OCTOBER);
	}

	public DateBuilder setNovember() {
		return setMonth(Calendar.NOVEMBER);
	}

	public DateBuilder setDecember() {
		return setMonth(Calendar.DECEMBER);
	}

	public DateBuilder setWeekOfMonth(int weekOfMonth) {
		c.set(Calendar.WEEK_OF_MONTH, weekOfMonth);
		return this;
	}

	public DateBuilder setWeekOfYear(int weekOfYear) {
		c.set(Calendar.WEEK_OF_YEAR, weekOfYear);
		return this;
	}

	public DateBuilder setDayOfMonth(int day) {
		c.set(Calendar.DAY_OF_MONTH, day);
		return this;
	}

	public DateBuilder setDayOfWeekInMonth(int dayOfWeekInMonth, int dayOfWeek) {
		c.set(Calendar.DAY_OF_WEEK_IN_MONTH, dayOfWeekInMonth);
		c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		return this;
	}

	public DateBuilder setLastDayOfMonth() {
		c.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth());
		return this;
	}

	public DateBuilder setSunday() {
		return setDayOfWeek(Calendar.SUNDAY);
	}

	public DateBuilder setSunday(int dayOfWeekInMonth) {
		return setDayOfWeekInMonth(dayOfWeekInMonth, Calendar.SUNDAY);
	}

	public DateBuilder setMonday() {
		return setDayOfWeek(Calendar.MONDAY);
	}

	public DateBuilder setMonday(int dayOfWeekInMonth) {
		return setDayOfWeekInMonth(dayOfWeekInMonth, Calendar.MONDAY);
	}

	public DateBuilder setTuesday() {
		return setDayOfWeek(Calendar.TUESDAY);
	}

	public DateBuilder setTuesday(int dayOfWeekInMonth) {
		return setDayOfWeekInMonth(dayOfWeekInMonth, Calendar.TUESDAY);
	}

	public DateBuilder setWednesday() {
		return setDayOfWeek(Calendar.WEDNESDAY);
	}

	public DateBuilder setWednesday(int dayOfWeekInMonth) {
		return setDayOfWeekInMonth(dayOfWeekInMonth, Calendar.WEDNESDAY);
	}

	public DateBuilder setThursday() {
		return setDayOfWeek(Calendar.THURSDAY);
	}

	public DateBuilder setThursday(int dayOfWeekInMonth) {
		return setDayOfWeekInMonth(dayOfWeekInMonth, Calendar.THURSDAY);
	}

	public DateBuilder setFriday() {
		return setDayOfWeek(Calendar.FRIDAY);
	}

	public DateBuilder setFriday(int dayOfWeekInMonth) {
		return setDayOfWeekInMonth(dayOfWeekInMonth, Calendar.FRIDAY);
	}

	public DateBuilder setSaturday() {
		return setDayOfWeek(Calendar.SATURDAY);
	}

	public DateBuilder setSaturday(int dayOfWeekInMonth) {
		return setDayOfWeekInMonth(dayOfWeekInMonth, Calendar.SATURDAY);
	}

	public DateBuilder setDayOfWeek(int dayOfWeek) {
		c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		return this;
	}

	public DateBuilder setDayOfYear(int dayOfYear) {
		c.set(Calendar.DAY_OF_YEAR, dayOfYear);
		return this;
	}

	public DateBuilder setSecond(int second) {
		c.set(Calendar.SECOND, second);
		return this;
	}

	public DateBuilder setTime(int minute, int second) {
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return this;
	}

	public DateBuilder setTime(int hourOfDay, int minute, int second) {
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return this;
	}

	public DateBuilder setAM(int hour, int minute, int second) {
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return this;
	}

	public DateBuilder setPM(int hour, int minute, int second) {
		c.set(Calendar.AM_PM, Calendar.PM);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return this;
	}

	public Calendar getCalendar() {
		return c;
	}

	public int getYear() {
		return c.get(Calendar.YEAR);
	}

	public int getMonth() {
		return c.get(Calendar.MONTH);
	}

	public int getDayOfMonth() {
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public int getDayOfYear() {
		return c.get(Calendar.DAY_OF_YEAR);
	}

	public int getDayOfWeek() {
		return c.get(Calendar.DAY_OF_WEEK);
	}

	public int getDayOfWeekInMonth() {
		return c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
	}

	public int getWeekOfMonth() {
		return c.get(Calendar.WEEK_OF_MONTH);
	}

	public int getWeekOfYear() {
		return c.get(Calendar.WEEK_OF_YEAR);
	}

	public int getHourOfDay() {
		return c.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		return c.get(Calendar.MINUTE);
	}

	public int getSecond() {
		return c.get(Calendar.SECOND);
	}

	public int getLastDayOfMonth() {
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public int getLastDayOfWeek() {
		return c.getActualMaximum(Calendar.DAY_OF_WEEK);
	}

	public int getLastDayOfWeekInMonth() {
		return c.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH);
	}

	public int getLastDayOfYear() {
		return c.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	public DateBuilder afterYears(int interval) {
		return after(Calendar.YEAR, interval);
	}

	public DateBuilder afterMonths(int interval) {
		return after(Calendar.MONTH, interval);
	}

	public DateBuilder afterWeeks(int interval) {
		return after(Calendar.WEEK_OF_MONTH, interval);
	}

	public DateBuilder afterDays(int interval) {
		return after(Calendar.DAY_OF_MONTH, interval);
	}

	public DateBuilder afterHours(int interval) {
		return after(Calendar.HOUR_OF_DAY, interval);
	}

	public DateBuilder afterMinutes(int interval) {
		return after(Calendar.MINUTE, interval);
	}

	public DateBuilder afterSeconds(int interval) {
		return after(Calendar.SECOND, interval);
	}

	public DateBuilder after(int calendarField, int interval) {
		c.add(calendarField, interval);
		return this;
	}
	
	public DateBuilder clone() {
		DateBuilder builder;
		try {
			builder = (DateBuilder) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new UnsupportedOperationException();
		}
		builder.c = (Calendar) this.c.clone();
		return builder;
	}

	public String toString() {
		return c.getTime().toString();
	}

	public static void main(String[] args) {
		DateBuilder db = new DateBuilder();
		// db.setFebruary();
		System.out.println(db.getLastDayOfWeek());
		// db.setLastDayOfMonth(Calendar.JUNE);
		;
		db.setSaturday(-2);
		System.out.println(db);
		// db.afterMonths(1);
		// System.out.println(db);
		// db.afterMonths(1);
		// System.out.println(db);
		// db.afterMonths(1);
		// System.out.println(db);
		// db.afterMonths(1);
		// System.out.println(db);
	}

}
