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
package com.github.paganini2008.devtools;

import static com.github.paganini2008.devtools.RandomUtils.randomInt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * RandomDateUtils
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class RandomDateUtils {

	public static Year randomYear() {
		Year fromYear = Year.of(1970);
		Year toYear = Year.of(DateUtils.getYear() + 1);
		return randomYear(fromYear, toYear);
	}

	public static Year randomYear(Year fromYear, Year toYear) {
		int year = randomInt(fromYear.getValue(), toYear.getValue() + 1);
		return Year.of(year);
	}

	public static YearMonth randomYearMonth(Year year) {
		return randomYearMonth(year, Month.JANUARY, Month.DECEMBER);
	}

	public static YearMonth randomYearMonth(Year year, Month fromMonth, Month toMonth) {
		Month month = Month.values()[randomInt(fromMonth.ordinal(), toMonth.ordinal())];
		return year.atMonth(month);
	}

	public static YearMonth randomYearMonth(Year fromYear, Year toYear, Month month) {
		Year year = randomYear(fromYear, toYear);
		return year.atMonth(month);
	}

	public static YearMonth randomYearMonth(Year fromYear, Year toYear, Month fromMonth, Month toMonth) {
		Year year = randomYear(fromYear, toYear);
		return randomYearMonth(year, fromMonth, toMonth);
	}

	public static LocalDate randomLocalDate() {
		return randomLocalDate(Year.now());
	}

	public static LocalDate randomLocalDate(Year year) {
		return randomLocalDate(year, 1, Integer.MAX_VALUE);
	}

	public static LocalDate randomLocalDate(Year year, int fromDayOfYear, int toDayOfYear) {
		int from = Math.max(fromDayOfYear, 1);
		int to = Math.min(toDayOfYear, year.isLeap() ? 366 : 365) + 1;
		return year.atDay(randomInt(from, to));
	}

	public static LocalDate randomLocalDate(Year year, Month month) {
		return randomLocalDate(year, month, 1, Integer.MAX_VALUE);
	}

	public static LocalDate randomLocalDate(YearMonth yearMonth) {
		return randomLocalDate(yearMonth, 1, Integer.MAX_VALUE);
	}

	public static LocalDate randomLocalDate(YearMonth yearMonth, int fromDayOfMonth, int toDayOfMonth) {
		int from = Math.max(1, fromDayOfMonth);
		int to = Math.min(yearMonth.atEndOfMonth().getDayOfMonth(), toDayOfMonth);
		return yearMonth.atDay(randomInt(from, to));
	}

	public static LocalDate randomLocalDate(Year year, Month month, int fromDayOfMonth, int toDayOfMonth) {
		YearMonth yearMonth = year.atMonth(month);
		return randomLocalDate(yearMonth, fromDayOfMonth, toDayOfMonth);
	}

	public static LocalDate randomLocalDate(Year year, Month fromMonth, Month toMonth, int fromDayOfMonth, int toDayOfMonth) {
		YearMonth yearMonth = randomYearMonth(year, fromMonth, toMonth);
		return randomLocalDate(yearMonth, fromDayOfMonth, toDayOfMonth);
	}

	public static LocalDate randomLocalDate(Year fromYear, Year toYear, Month fromMonth, Month toMonth, int fromDayOfMonth,
			int toDayOfMonth) {
		Year year = randomYear(fromYear, toYear);
		YearMonth yearMonth = randomYearMonth(year, fromMonth, toMonth);
		return randomLocalDate(yearMonth, fromDayOfMonth, toDayOfMonth);
	}

	public static LocalDate randomLocalDate(String from, String to) {
		return randomLocalDate(from, to, DateTimeFormatter.ISO_LOCAL_DATE);
	}

	public static LocalDate randomLocalDate(String from, String to, DateTimeFormatter df) {
		LocalDate fromLocalDate = LocalDate.parse(from, df);
		LocalDate toLocalDate = LocalDate.parse(to, df);
		return randomLocalDate(fromLocalDate, toLocalDate);
	}

	public static LocalDate randomLocalDate(LocalDate from, LocalDate to) {
		Year fromYear = Year.of(from.getYear());
		Year toYear = Year.of(to.getYear());
		Month fromMonth = from.getMonth();
		Month toMonth = to.getMonth();
		int fromDayOfMonth = from.getDayOfMonth();
		int toDayOfMonth = to.getDayOfMonth();
		return randomLocalDate(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
	}

	public static LocalDateTime randomLocalDateTime() {
		return randomLocalDateTime(Year.now());
	}

	public static LocalDateTime randomLocalDateTime(Year year) {
		return randomLocalDateTime(year, 1, Integer.MAX_VALUE);
	}

	public static LocalDateTime randomLocalDateTime(Year year, int fromDayOfYear, int toDayOfYear) {
		LocalDate localDate = randomLocalDate(year, fromDayOfYear, toDayOfYear);
		return localDate.atTime(LocalTime.of(randomInt(0, 24), randomInt(0, 60), randomInt(0, 60)));
	}

	public static LocalDateTime randomLocalDateTime(Year year, int dayOfYear, String from, String to) {
		return randomLocalDateTime(year, dayOfYear, from, to, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	public static LocalDateTime randomLocalDateTime(Year year, int dayOfYear, String from, String to, DateTimeFormatter df) {
		return randomLocalDateTime(year, dayOfYear, LocalTime.parse(from, df), LocalTime.parse(to, df));
	}

	public static LocalDateTime randomLocalDateTime(Year year, int dayOfYear, LocalTime from, LocalTime to) {
		LocalDate localDate = DateUtils.setDayOfYear(year, dayOfYear);
		return randomLocalDateTime(localDate, from, to);
	}

	public static LocalDateTime randomLocalDateTime(LocalDate localDate, String from, String to) {
		return randomLocalDateTime(localDate, from, to, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	public static LocalDateTime randomLocalDateTime(LocalDate localDate, String from, String to, DateTimeFormatter df) {
		return randomLocalDateTime(localDate, LocalTime.parse(from, df), LocalTime.parse(to, df));
	}

	public static LocalDateTime randomLocalDateTime(LocalDate localDate, LocalTime from, LocalTime to) {
		int fh = from.getHour();
		int fm = from.getMinute();
		int fs = from.getSecond();
		int th = to.getHour();
		int tm = to.getMinute();
		int ts = to.getSecond();
		return localDate.atTime(randomInt(fh, th + 1), randomInt(fm, tm + 1), randomInt(fs, ts + 1));
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month) {
		return randomLocalDateTime(year, month, randomInt(1, month.maxLength() + 1));
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month, int dayOfMonth) {
		LocalDate localDate = DateUtils.setDayOfMonth(year, month, dayOfMonth);
		return randomLocalDateTime(localDate);
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month, int fromDayOfMonth, int toDayOfMonth) {
		LocalDate localDate = randomLocalDate(year, month, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate);
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month fromMonth, Month toMonth, int fromDayOfMonth, int toDayOfMonth) {
		LocalDate localDate = randomLocalDate(year, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate);
	}

	public static LocalDateTime randomLocalDateTime(Year fromYear, Year toYear, Month fromMonth, Month toMonth, int fromDayOfMonth,
			int toDayOfMonth) {
		LocalDate localDate = randomLocalDate(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate);
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month, int dayOfMonth, String from, String to) {
		return randomLocalDateTime(year, month, dayOfMonth, from, to, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month, int dayOfMonth, String from, String to, DateTimeFormatter df) {
		return randomLocalDateTime(year, month, dayOfMonth, LocalTime.parse(from, df), LocalTime.parse(to, df));
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month, int dayOfMonth, LocalTime fromTime, LocalTime toTime) {
		LocalDate localDate = DateUtils.setDayOfMonth(year, month, dayOfMonth);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month, int fromDayOfMonth, int toDayOfMonth, LocalTime fromTime,
			LocalTime toTime) {
		LocalDate localDate = randomLocalDate(year, month, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month fromMonth, Month toMonth, int fromDayOfMonth, int toDayOfMonth,
			LocalTime fromTime, LocalTime toTime) {
		LocalDate localDate = randomLocalDate(year, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	public static LocalDateTime randomLocalDateTime(Year fromYear, Year toYear, Month fromMonth, Month toMonth, int fromDayOfMonth,
			int toDayOfMonth, LocalTime fromTime, LocalTime toTime) {
		LocalDate localDate = randomLocalDate(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	public static LocalDateTime randomLocalDateTime(LocalDate localDate) {
		return localDate.atTime(randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static LocalDateTime randomLocalDateTime(LocalDate fromLocalDate, LocalDate toLocalDate) {
		LocalDate localDate = randomLocalDate(fromLocalDate, toLocalDate);
		return randomLocalDateTime(localDate);
	}

	public static LocalDateTime randomLocalDateTime(LocalDate fromLocalDate, LocalDate toLocalDate, LocalTime fromTime, LocalTime toTime) {
		LocalDate localDate = randomLocalDate(fromLocalDate, toLocalDate);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	public static LocalTime randomLocalTime() {
		return LocalTime.of(randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static LocalTime randomLocalTime(String from, String to) {
		return randomLocalTime(from, to, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	public static LocalTime randomLocalTime(String from, String to, DateTimeFormatter df) {
		return randomLocalTime(LocalTime.parse(from, df), LocalTime.parse(to, df));
	}

	public static LocalTime randomLocalTime(LocalTime from, LocalTime to) {
		int fh = from.getHour();
		int fm = from.getMinute();
		int fs = from.getSecond();
		int th = to.getHour();
		int tm = to.getMinute();
		int ts = to.getSecond();
		return LocalTime.of(randomInt(fh, th + 1), randomInt(fm, tm + 1), randomInt(fs, ts + 1));
	}

	public static Date randomDate() {
		return randomDate(DateUtils.getYear());
	}

	public static Date randomDate(int year) {
		return randomDate(year, randomInt(1, 13));
	}

	public static Date randomDate(int year, int month) {
		Date current = DateUtils.valueOf(year, month, 1);
		int date = randomInt(1, DateUtils.getLastDay(current) + 1);
		return DateUtils.setDay(current, date);
	}

	public static Date randomDate(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		int year = randomInt(fromYear, toYear);
		int month = randomInt(fromMonth, toMonth);
		int dayOfMonth = randomInt(fromDayOfMonth, toDayOfMonth);
		return DateUtils.valueOf(year, month, dayOfMonth);
	}
	
	public static Date randomDateTime() {
		return randomDateTime(DateUtils.getYear());
	}

	public static Date randomDateTime(int year) {
		return randomDateTime(year, randomInt(1, 13));
	}

	public static Date randomDateTime(int year, int month) {
		Date current = DateUtils.valueOf(year, month, 1);
		int dayOfMonth = randomInt(1, DateUtils.getLastDay(current) + 1);
		return DateUtils.valueOf(year, month, dayOfMonth, randomInt(0, 24), randomInt(0, 60), randomInt(0, 60));
	}

	public static Date randomDateTime(int year, int month, int dayOfMonth) {
		Date date = DateUtils.valueOf(year, month, dayOfMonth);
		return randomDateTime(date, 0, 24, 0, 60, 0, 60);
	}

	public static Date randomDateTime(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		Date date = randomDate(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomDateTime(date, 0, 24, 0, 60, 0, 60);
	}

	public static Date randomDateTime(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth,
			int fromHour, int toHour, int fromMinute, int toMinute, int fromSecond, int toSecond) {
		Date date = randomDate(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomDateTime(date, fromHour, toHour, fromMinute, toMinute, fromSecond, toSecond);
	}

	public static Date randomDateTime(Date date, int fromHour, int toHour, int fromMinute, int toMinute, int fromSecond, int toSecond) {
		int hourOfDay = randomInt(fromHour, toHour);
		int minute = randomInt(fromMinute, toMinute);
		int second = randomInt(fromSecond, toSecond);
		return DateUtils.setTime(date, hourOfDay, minute, second);
	}

	public static Date randomDateTime(Date fromDate, Date toDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(fromDate);
		int fromYear = c.get(Calendar.YEAR);
		int fromMonth = c.get(Calendar.MONTH);
		int fromDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		int fromHour = c.get(Calendar.HOUR_OF_DAY);
		int fromMinute = c.get(Calendar.MINUTE);
		int fromSecond = c.get(Calendar.SECOND);
		c.setTime(toDate);
		int toYear = c.get(Calendar.YEAR);
		int toMonth = c.get(Calendar.MONTH);
		int toDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		int toHour = c.get(Calendar.HOUR_OF_DAY);
		int toMinute = c.get(Calendar.MINUTE);
		int toSecond = c.get(Calendar.SECOND);
		return randomDateTime(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth, fromHour, toHour, fromMinute, toMinute,
				fromSecond, toSecond);
	}

}
