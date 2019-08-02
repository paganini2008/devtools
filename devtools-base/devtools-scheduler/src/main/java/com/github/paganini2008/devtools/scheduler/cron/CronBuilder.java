package com.github.paganini2008.devtools.scheduler.cron;

import java.util.Calendar;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * CronBuilder
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public abstract class CronBuilder {

	private static final int END_YEAR_OF_THE_WORLD = 9999;

	public static ConcreteYear thisYear() {
		return year(DateUtils.getYear());
	}

	public static ConcreteYear year(int year) {
		return new SingleYear(year);
	}

	public static ConcreteMonth thisMonth() {
		return thisYear().month(DateUtils.getMonth());
	}

	public static ConcreteMonth month(int year, int month) {
		return year(year).month(month);
	}

	public static ConcreteWeek thisWeek() {
		return thisMonth().week(DateUtils.getWeekOfMonth());
	}

	public static ConcreteWeek week(int year, int month, int week) {
		return month(year, month).week(week);
	}

	public static ConcreteDay today() {
		return thisMonth().day(DateUtils.getDate());
	}

	public static ConcreteDay day(int year, int month, int day) {
		return month(year, month).day(day);
	}

	public static Year everyYear() {
		return everyYear(1);
	}

	public static Year everyYear(int interval) {
		return everyYear(DateUtils.getYear(), END_YEAR_OF_THE_WORLD, interval);
	}

	public static Year everyYear(int fromYear, int toYear, int interval) {
		return new EveryYear(fromYear, y -> toYear, interval);
	}

	public static Month everyMonth() {
		return everyMonth(1);
	}

	public static Month everyMonth(int interval) {
		return everyMonth(Calendar.JANUARY, Calendar.DECEMBER, interval);
	}

	public static Month everyMonth(int fromMonth, int toMonth, int interval) {
		return everyYear().everyMonth(fromMonth, toMonth, interval);
	}

	public static Week everyWeek() {
		return everyWeek(1);
	}

	public static Week everyWeek(int interval) {
		return everyMonth().everyWeek(m -> 1, m -> {
			return m.getWeekCount();
		}, interval);
	}

	public static Week everyWeek(int fromWeek, int toWeek, int interval) {
		return everyMonth().everyWeek(fromWeek, toWeek, interval);
	}

	public static Day everyDay() {
		return everyDay(1);
	}

	public static Day everyDay(int interval) {
		return everyMonth().everyDay(m -> 1, m -> {
			return m.getLasyDay();
		}, interval);
	}

	public static Day everyDay(int fromDay, int toDay, int interval) {
		return everyMonth().everyDay(fromDay, toDay, interval);
	}

	public static Hour everyHour() {
		return everyHour(1);
	}

	public static Hour everyHour(int interval) {
		return everyHour(0, 23, interval);
	}

	public static Hour everyHour(int fromHour, int toHour, int interval) {
		return everyDay().everyHour(fromHour, toHour, interval);
	}

	public static Minute everyMinute() {
		return everyMinute(1);
	}

	public static Minute everyMinute(int interval) {
		return everyMinute(0, 59, interval);
	}

	public static Minute everyMinute(int fromMinute, int toMinute, int interval) {
		return everyHour().everyMinute(fromMinute, toMinute, interval);
	}

	public static Second everySecond() {
		return everySecond(1);
	}

	public static Second everySecond(int interval) {
		return everySecond(0, 59, interval);
	}

	public static Second everySecond(int fromSecond, int toSecond, int interval) {
		return everyMinute().everySecond(fromSecond, toSecond, interval);
	}

}
