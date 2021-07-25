/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * CronExpressionBuilder
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class CronExpressionBuilder {

	public static TheYear year() {
		return year(DateUtils.getYear());
	}

	public static TheYear year(int year) {
		return new ThisYear(year);
	}

	public static TheMonth month() {
		return year().month(DateUtils.getMonth());
	}

	public static TheMonth month(int year, int month) {
		return year(year).month(month);
	}

	public static TheWeek week() {
		return month().week(DateUtils.getWeekOfMonth());
	}

	public static TheWeek week(int year, int month, int week) {
		return month(year, month).week(week);
	}

	public static TheDay day() {
		return month().day(DateUtils.getDate());
	}

	public static TheDay day(int year, int month, int day) {
		return month(year, month).day(day);
	}

	public static TheHour hour() {
		return day().hour(DateUtils.getHourOfDay());
	}

	public static TheHour hour(int hourOfDay) {
		return day().hour(hourOfDay);
	}

	public static TheMinute minute() {
		return hour().minute(DateUtils.getMinute());
	}

	public static TheMinute minute(int minute) {
		return hour().minute(minute);
	}

	public static TheMinute at(int hourOfDay, int minute) {
		return day().hour(hourOfDay).minute(minute);
	}

	public static TheSecond at(int hourOfDay, int minute, int second) {
		return day().hour(hourOfDay).minute(minute).second(second);
	}

	public static Year everyYear() {
		return everyYear(1);
	}

	public static Year everyYear(int interval) {
		return everyYear(DateUtils.getYear(), Year.MAX_YEAR, interval);
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
			return m.getLastDay();
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
