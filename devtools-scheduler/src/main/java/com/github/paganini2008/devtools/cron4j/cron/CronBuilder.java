package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;

import com.github.paganini2008.devtools.cron4j.parser.Epoch;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * CronBuilder
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class CronBuilder {

	public static ThatYear year() {
		return year(DateUtils.getYear());
	}

	public static ThatYear year(int year) {
		return new ThisYear(year);
	}

	public static ThatMonth month() {
		return year().month(DateUtils.getMonth());
	}

	public static ThatMonth month(int year, int month) {
		return year(year).month(month);
	}

	public static ThatWeek week() {
		return month().week(DateUtils.getWeekOfMonth());
	}

	public static ThatWeek week(int year, int month, int week) {
		return month(year, month).week(week);
	}

	public static ThatDay day() {
		return month().day(DateUtils.getDate());
	}

	public static ThatDay day(int year, int month, int day) {
		return month(year, month).day(day);
	}

	public static ThatHour hour() {
		return day().hour(DateUtils.getHourOfDay());
	}

	public static ThatHour hour(int hourOfDay) {
		return day().hour(hourOfDay);
	}

	public static ThatMinute minute() {
		return hour().minute(DateUtils.getMinute());
	}

	public static ThatMinute minute(int minute) {
		return hour().minute(minute);
	}

	public static ThatMinute at(int hourOfDay, int minute) {
		return day().hour(hourOfDay).minute(minute);
	}

	public static ThatSecond at(int hourOfDay, int minute, int second) {
		return day().hour(hourOfDay).minute(minute).second(second);
	}

	public static Year everyYear() {
		return everyYear(1);
	}

	public static Year everyYear(int interval) {
		return everyYear(DateUtils.getYear(), Epoch.MAX_YEAR, interval);
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
