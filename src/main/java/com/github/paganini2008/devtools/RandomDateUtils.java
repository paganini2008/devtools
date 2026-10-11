/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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

import static com.github.paganini2008.devtools.RandomUtils.randomDayOfMonth;
import static com.github.paganini2008.devtools.RandomUtils.randomDayOfYear;
import static com.github.paganini2008.devtools.RandomUtils.randomInt;
import static com.github.paganini2008.devtools.RandomUtils.randomLong;
import static com.github.paganini2008.devtools.RandomUtils.randomMonth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.time.DateUtils;
import com.github.paganini2008.devtools.time.LocalDateTimeUtils;
import com.github.paganini2008.devtools.time.LocalDateUtils;
import com.github.paganini2008.devtools.time.LocalTimeUtils;
import com.github.paganini2008.devtools.time.YearUtils;

/**
 * 
 * RandomDateUtils
 * 
 * Random date and time generators for {@code java.time} types and {@link Date}. Year, month, day and
 * time-field bounds are inclusive; bounds given as two points in time ({@code from}/{@code to} dates,
 * times or strings) form the range {@code [from, to)}, and equal points return {@code from}. Date
 * results use the system default time zone.
 *
 * @author Fred Feng
 * @date 2021-10-25
 *
 * @since 2.0.4
 */
public abstract class RandomDateUtils {

	/**
	 * Returns a random year between 1970 and the current year.
	 */
	public static Year randomYear() {
		return randomYear(YearUtils.MIN_YEAR, Year.now());
	}

	/**
	 * Returns a random year in {@code [fromYear, toYear]}.
	 */
	public static Year randomYear(Year fromYear, Year toYear) {
		return randomYear(fromYear.getValue(), toYear.getValue());
	}

	/**
	 * Returns a random year in {@code [fromYear, toYear]}.
	 */
	public static Year randomYear(int fromYear, int toYear) {
		return Year.of(RandomUtils.randomYear(fromYear, toYear));
	}

	/**
	 * Returns a random year-month within the given year and month bounds.
	 */
	public static YearMonth randomYearMonth(Year year) {
		return randomYearMonth(year, Month.JANUARY, Month.DECEMBER);
	}

	/**
	 * Returns a random year-month within the given year and month bounds.
	 */
	public static YearMonth randomYearMonth(Year year, Month fromMonth, Month toMonth) {
		Month month = randomMonth(fromMonth, toMonth);
		return year.atMonth(month);
	}

	/**
	 * Returns a random year-month within the given year and month bounds.
	 */
	public static YearMonth randomYearMonth(Year fromYear, Year toYear, Month fromMonth, Month toMonth) {
		Year year = randomYear(fromYear, toYear);
		return randomYearMonth(year, fromMonth, toMonth);
	}

	/**
	 * Returns a random year-month within the given year and month bounds.
	 */
	public static YearMonth randomYearMonth(int year) {
		return randomYearMonth(year, 1, 12);
	}

	/**
	 * Returns a random year-month within the given year and month bounds.
	 */
	public static YearMonth randomYearMonth(int year, int fromMonth, int toMonth) {
		Year isoYear = Year.of(year);
		Month month = Month.values()[randomMonth(fromMonth, toMonth) - 1];
		return isoYear.atMonth(month);
	}

	/**
	 * Returns a random year-month within the given year and month bounds.
	 */
	public static YearMonth randomYearMonth(int fromYear, int toYear, int fromMonth, int toMonth) {
		int year = RandomUtils.randomYear(fromYear, toYear);
		return randomYearMonth(year, fromMonth, toMonth);
	}

	/**
	 * Returns a random date in the current year.
	 */
	public static LocalDate randomLocalDate() {
		return randomLocalDate(Year.now());
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(Year year) {
		return randomLocalDate(year, 1, 366);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(Year year, int fromDayOfYear, int toDayOfYear) {
		int dayOfYear = randomDayOfYear(year, fromDayOfYear, toDayOfYear);
		return year.atDay(dayOfYear);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(Year fromYear, Year toYear, int fromDayOfYear, int toDayOfYear) {
		Year year = randomYear(fromYear, toYear);
		return randomLocalDate(year, fromDayOfYear, toDayOfYear);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(Year year, Month month) {
		return randomLocalDate(year, month, 1, 31);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(YearMonth yearMonth) {
		return randomLocalDate(yearMonth, 1, 31);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(YearMonth yearMonth, int fromDayOfMonth, int toDayOfMonth) {
		int dayOfMonth = randomDayOfMonth(yearMonth, fromDayOfMonth, toDayOfMonth);
		return yearMonth.atDay(dayOfMonth);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(Year year, Month month, int fromDayOfMonth, int toDayOfMonth) {
		return randomLocalDate(year.atMonth(month), fromDayOfMonth, toDayOfMonth);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(Year year, Month fromMonth, Month toMonth, int fromDayOfMonth, int toDayOfMonth) {
		YearMonth yearMonth = randomYearMonth(year, fromMonth, toMonth);
		return randomLocalDate(yearMonth, fromDayOfMonth, toDayOfMonth);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(Year fromYear, Year toYear, Month fromMonth, Month toMonth, int fromDayOfMonth,
			int toDayOfMonth) {
		YearMonth yearMonth = randomYearMonth(fromYear, toYear, fromMonth, toMonth);
		return randomLocalDate(yearMonth, fromDayOfMonth, toDayOfMonth);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		int dayOfMonth = randomDayOfMonth(year, month, fromDayOfMonth, toDayOfMonth);
		return LocalDateUtils.of(year, month, dayOfMonth);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(int year, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		YearMonth yearMonth = randomYearMonth(year, fromMonth, toMonth);
		return randomLocalDate(yearMonth, fromDayOfMonth, toDayOfMonth);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static LocalDate randomLocalDate(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		YearMonth yearMonth = randomYearMonth(fromYear, toYear, fromMonth, toMonth);
		return randomLocalDate(yearMonth, fromDayOfMonth, toDayOfMonth);
	}

	/**
	 * Returns a random date in {@code [from, to)}, parsing ISO dates such as {@code 2021-01-31}.
	 */
	public static LocalDate randomLocalDate(String from, String to) {
		return randomLocalDate(from, to, DateTimeFormatter.ISO_LOCAL_DATE);
	}

	/**
	 * Returns a random date in {@code [from, to)}, parsing the strings with the formatter.
	 */
	public static LocalDate randomLocalDate(String from, String to, DateTimeFormatter df) {
		LocalDate fromLocalDate = LocalDate.parse(from, df);
		LocalDate toLocalDate = LocalDate.parse(to, df);
		return randomLocalDate(fromLocalDate, toLocalDate);
	}

	/**
	 * Returns a random date in {@code [from, to)}.
	 *
	 * @throws IllegalArgumentException if {@code from} is after {@code to}
	 */
	public static LocalDate randomLocalDate(LocalDate from, LocalDate to) {
		long days = from.until(to, ChronoUnit.DAYS);
		checkOrder(days);
		return from.plusDays(randomLong(0, days));
	}

	/**
	 * Returns a random date-time in the current year.
	 */
	public static LocalDateTime randomLocalDateTime() {
		return randomLocalDateTime(Year.now());
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(Year year) {
		return randomLocalDateTime(year, 1, 366);
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, int fromDayOfYear, int toDayOfYear) {
		LocalDate localDate = randomLocalDate(year, fromDayOfYear, toDayOfYear);
		LocalTime localTime = randomLocalTime();
		return localDate.atTime(localTime);
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, int dayOfYear, String from, String to) {
		return randomLocalDateTime(year, dayOfYear, from, to, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, int dayOfYear, String from, String to, DateTimeFormatter df) {
		return randomLocalDateTime(year, dayOfYear, LocalTime.parse(from, df), LocalTime.parse(to, df));
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, int dayOfYear, LocalTime from, LocalTime to) {
		LocalDate localDate = LocalDateUtils.of(year, dayOfYear);
		return randomLocalDateTime(localDate, from, to);
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, int fromDayOfYear, int toDayOfYear, LocalTime from, LocalTime to) {
		LocalDate localDate = randomLocalDate(year, fromDayOfYear, toDayOfYear);
		return randomLocalDateTime(localDate, from, to);
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(Year fromYear, Year toYear, int fromDayOfYear, int toDayOfYear, LocalTime from,
			LocalTime to) {
		LocalDate localDate = randomLocalDate(fromYear, toYear, fromDayOfYear, toDayOfYear);
		return randomLocalDateTime(localDate, from, to);
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(LocalDate localDate, String from, String to) {
		return randomLocalDateTime(localDate, from, to, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(LocalDate localDate, String from, String to, DateTimeFormatter df) {
		return randomLocalDateTime(localDate, LocalTime.parse(from, df), LocalTime.parse(to, df));
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(LocalDate localDate, LocalTime from, LocalTime to) {
		return randomLocalDateTime(localDate.atTime(from), localDate.atTime(to));
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, Month month) {
		return randomLocalDateTime(year, month, randomInt(1, year.atMonth(month).lengthOfMonth() + 1));
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, Month month, int dayOfMonth) {
		LocalDate localDate = year.atMonth(month).atDay(dayOfMonth);
		return randomLocalDateTime(localDate);
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, Month month, int fromDayOfMonth, int toDayOfMonth) {
		LocalDate localDate = randomLocalDate(year, month, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate);
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, Month fromMonth, Month toMonth, int fromDayOfMonth, int toDayOfMonth) {
		LocalDate localDate = randomLocalDate(year, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate);
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(Year fromYear, Year toYear, Month fromMonth, Month toMonth, int fromDayOfMonth,
			int toDayOfMonth) {
		LocalDate localDate = randomLocalDate(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate);
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, Month month, int dayOfMonth, String from, String to) {
		return randomLocalDateTime(year, month, dayOfMonth, from, to, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, Month month, int dayOfMonth, String from, String to, DateTimeFormatter df) {
		return randomLocalDateTime(year, month, dayOfMonth, LocalTime.parse(from, df), LocalTime.parse(to, df));
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, Month month, int dayOfMonth, LocalTime fromTime, LocalTime toTime) {
		LocalDate localDate = year.atMonth(month).atDay(dayOfMonth);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, Month month, int fromDayOfMonth, int toDayOfMonth, LocalTime fromTime,
			LocalTime toTime) {
		LocalDate localDate = randomLocalDate(year, month, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(Year year, Month fromMonth, Month toMonth, int fromDayOfMonth, int toDayOfMonth,
			LocalTime fromTime, LocalTime toTime) {
		LocalDate localDate = randomLocalDate(year, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(Year fromYear, Year toYear, Month fromMonth, Month toMonth, int fromDayOfMonth,
			int toDayOfMonth, LocalTime fromTime, LocalTime toTime) {
		LocalDate localDate = randomLocalDate(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(LocalDate fromLocalDate, LocalDate toLocalDate) {
		LocalDate localDate = randomLocalDate(fromLocalDate, toLocalDate);
		return randomLocalDateTime(localDate);
	}

	/**
	 * Returns a random date-time whose date is chosen within the given bounds and whose time lies in
	 * {@code [from, to)}.
	 */
	public static LocalDateTime randomLocalDateTime(LocalDate fromLocalDate, LocalDate toLocalDate, LocalTime fromTime, LocalTime toTime) {
		LocalDate localDate = randomLocalDate(fromLocalDate, toLocalDate);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	/**
	 * Returns a random date-time in {@code [startTime, endTime)} with second precision.
	 *
	 * @throws IllegalArgumentException if {@code startTime} is after {@code endTime}
	 */
	public static LocalDateTime randomLocalDateTime(LocalDateTime startTime, LocalDateTime endTime) {
		long seconds = startTime.until(endTime, ChronoUnit.SECONDS);
		checkOrder(seconds);
		return startTime.plusSeconds(randomLong(0, seconds));
	}

	/**
	 * Returns a random date-time in {@code [from, to)}, parsing ISO date-times such as {@code 2021-01-31T10:15:30}.
	 */
	public static LocalDateTime randomLocalDateTime(String from, String to) {
		return randomLocalDateTime(from, to, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	/**
	 * Returns a random date-time in {@code [from, to)}, parsing the strings with the formatter.
	 */
	public static LocalDateTime randomLocalDateTime(String from, String to, DateTimeFormatter dtf) {
		LocalDateTime startTime = LocalDateTime.parse(from, dtf);
		LocalDateTime endTime = LocalDateTime.parse(to, dtf);
		return randomLocalDateTime(startTime, endTime);
	}

	/**
	 * Returns a random time on the given date.
	 */
	public static LocalDateTime randomLocalDateTime(LocalDate localDate) {
		return randomLocalDateTime(localDate, 0, 23, 0, 59, 0, 59);
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(LocalDate localDate, int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute,
			int fromSecond, int toSecond) {
		LocalTime startTime = LocalTimeUtils.of(fromHourOfDay, fromMinute, fromSecond);
		LocalTime endTime = LocalTimeUtils.of(toHourOfDay, toMinute, toSecond);
		return randomLocalDateTime(LocalDateTime.of(localDate, startTime), LocalDateTime.of(localDate, endTime));
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(int year, int month, int dayOfMonth, int fromHourOfDay, int toHourOfDay, int fromMinute,
			int toMinute, int fromSecond, int toSecond) {
		LocalDate localDate = LocalDateUtils.of(year, month, dayOfMonth);
		return randomLocalDateTime(localDate, fromHourOfDay, toHourOfDay, fromMinute, toMinute, fromSecond, toSecond);
	}

	/**
	 * Returns a random date-time within the given date (and time-field) bounds.
	 */
	public static LocalDateTime randomLocalDateTime(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth,
			int toDayOfMonth, int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute, int fromSecond, int toSecond) {
		LocalDateTime startTime = LocalDateTimeUtils.of(fromYear, fromMonth, fromDayOfMonth, fromHourOfDay, fromMinute, fromSecond);
		LocalDateTime endTime = LocalDateTimeUtils.of(toYear, toMonth, toDayOfMonth, toHourOfDay, toMinute, toSecond);
		return randomLocalDateTime(startTime, endTime);
	}

	/**
	 * Returns a random time of day.
	 */
	public static LocalTime randomLocalTime() {
		return randomLocalTime(0, 23, 0, 59);
	}

	/**
	 * Returns a random time in {@code [from, to)}, where both bounds are built from the given fields.
	 */
	public static LocalTime randomLocalTime(int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute) {
		return randomLocalTime(fromHourOfDay, toHourOfDay, fromMinute, toMinute, 0, 59);
	}

	/**
	 * Returns a random time in {@code [from, to)}, where both bounds are built from the given fields.
	 */
	public static LocalTime randomLocalTime(int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute, int fromSecond,
			int toSecond) {
		LocalTime startTime = LocalTimeUtils.of(fromHourOfDay, fromMinute, fromSecond);
		LocalTime endTime = LocalTimeUtils.of(toHourOfDay, toMinute, toSecond);
		return randomLocalTime(startTime, endTime);
	}

	/**
	 * Returns a random time in {@code [from, to)}, parsing ISO times such as {@code 10:15:30}.
	 */
	public static LocalTime randomLocalTime(String from, String to) {
		return randomLocalTime(from, to, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	/**
	 * Returns a random time in {@code [from, to)}, parsing the strings with the formatter.
	 */
	public static LocalTime randomLocalTime(String from, String to, DateTimeFormatter df) {
		return randomLocalTime(LocalTime.parse(from, df), LocalTime.parse(to, df));
	}

	/**
	 * Returns a random time in {@code [from, to)} with second precision.
	 *
	 * @throws IllegalArgumentException if {@code from} is after {@code to}
	 */
	public static LocalTime randomLocalTime(LocalTime from, LocalTime to) {
		long seconds = from.until(to, ChronoUnit.SECONDS);
		checkOrder(seconds);
		return from.plusSeconds(randomLong(0, seconds));
	}

	/**
	 * Returns a random date (at midnight) in the current year.
	 */
	public static Date randomDate() {
		return randomDate(DateUtils.getYear());
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static Date randomDate(int year) {
		return randomDate(year, randomMonth(1, 12));
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static Date randomDate(int year, int month) {
		return randomDate(year, month, 1, 31);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static Date randomDate(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		int dayOfMonth = randomDayOfMonth(year, month, fromDayOfMonth, toDayOfMonth);
		LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
		return DateUtils.toDate(localDate, null);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static Date randomDate(int year, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		Date fromDate = DateUtils.of(year, fromMonth, fromDayOfMonth);
		Date toDate = DateUtils.of(year, toMonth, toDayOfMonth);
		return randomDate(fromDate, toDate);
	}

	/**
	 * Returns a random date within the given year, month and day bounds.
	 */
	public static Date randomDate(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		Date fromDate = DateUtils.of(fromYear, fromMonth, fromDayOfMonth);
		Date toDate = DateUtils.of(toYear, toMonth, toDayOfMonth);
		return randomDate(fromDate, toDate);
	}

	/**
	 * Returns a random date between the two dates parsed with the pattern.
	 */
	public static Date randomDate(String from, String to, String datePattern) {
		Date fromDate = DateUtils.parse(from, datePattern);
		Date toDate = DateUtils.parse(to, datePattern);
		return randomDate(fromDate, toDate);
	}

	/**
	 * Returns {@code from} plus a random number of whole days, staying before {@code to}.
	 *
	 * @throws IllegalArgumentException if {@code from} is after {@code to}
	 */
	public static Date randomDate(Date from, Date to) {
		long days = DateUtils.until(from, to, TimeUnit.DAYS);
		checkOrder(days);
		return DateUtils.addDayOfMonth(from, (int) randomLong(0, days));
	}

	/**
	 * Returns a random date-time in the current year.
	 */
	public static Date randomDateTime() {
		return randomDateTime(DateUtils.getYear());
	}

	/**
	 * Returns a random date-time within the given date and time-field bounds.
	 */
	public static Date randomDateTime(int year) {
		return randomDateTime(year, randomMonth(1, 12));
	}

	/**
	 * Returns a random date-time within the given date and time-field bounds.
	 */
	public static Date randomDateTime(int year, int month) {
		return randomDateTime(year, month, 1, YearMonth.of(year, month).lengthOfMonth());
	}

	/**
	 * Returns a random date-time within the given date and time-field bounds.
	 */
	public static Date randomDateTime(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		return randomDateTime(year, month, fromDayOfMonth, toDayOfMonth, 0, 23, 0, 59, 0, 59);
	}

	/**
	 * Returns a random date-time within the given date and time-field bounds.
	 */
	public static Date randomDateTime(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		return randomDateTime(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth, 0, 23, 0, 59, 0, 59);
	}

	/**
	 * Returns a random date-time within the given date and time-field bounds.
	 */
	public static Date randomDateTime(int year, int month, int dayOfMonth) {
		return randomDateTime(year, month, dayOfMonth, 0, 23, 0, 59, 0, 59);
	}

	/**
	 * Returns a random date-time within the given date and time-field bounds.
	 */
	public static Date randomDateTime(int year, int month, int dayOfMonth, int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute,
			int fromSecond, int toSecond) {
		Date date = DateUtils.of(year, month, dayOfMonth);
		return randomDateTime(date, fromHourOfDay, toHourOfDay, fromMinute, toMinute, fromSecond, toSecond);
	}

	/**
	 * Returns a random date-time within the given date and time-field bounds.
	 */
	public static Date randomDateTime(int year, int month, int fromDayOfMonth, int toDayOfMonth, int fromHourOfDay, int toHourOfDay,
			int fromMinute, int toMinute, int fromSecond, int toSecond) {
		Date from = DateUtils.of(year, month, fromDayOfMonth, fromHourOfDay, fromMinute, fromSecond);
		Date to = DateUtils.of(year, month, toDayOfMonth, toHourOfDay, toMinute, toSecond);
		return randomDateTime(from, to);
	}

	/**
	 * Returns a random date-time within the given date and time-field bounds.
	 */
	public static Date randomDateTime(int year, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth, int fromHourOfDay,
			int toHourOfDay, int fromMinute, int toMinute, int fromSecond, int toSecond) {
		Date from = DateUtils.of(year, fromMonth, fromDayOfMonth, fromHourOfDay, fromMinute, fromSecond);
		Date to = DateUtils.of(year, toMonth, toDayOfMonth, toHourOfDay, toMinute, toSecond);
		return randomDateTime(from, to);
	}

	/**
	 * Returns a random date-time within the given date and time-field bounds.
	 */
	public static Date randomDateTime(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth,
			int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute, int fromSecond, int toSecond) {
		Date from = DateUtils.of(fromYear, fromMonth, fromDayOfMonth, fromHourOfDay, fromMinute, fromSecond);
		Date to = DateUtils.of(toYear, toMonth, toDayOfMonth, toHourOfDay, toMinute, toSecond);
		return randomDateTime(from, to);
	}

	/**
	 * Returns a random date-time within the given date and time-field bounds.
	 */
	public static Date randomDateTime(Date date, int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute, int fromSecond,
			int toSecond) {
		Date from = DateUtils.setTime(date, fromHourOfDay, fromMinute, fromSecond);
		Date to = DateUtils.setTime(date, toHourOfDay, toMinute, toSecond);
		return randomDateTime(from, to);
	}

	/**
	 * Returns a random time on the given date between the two times parsed with the pattern; a blank
	 * {@code to} means now.
	 */
	public static Date randomDateTime(Date date, String from, String to, String datePattern) {
		Date fromTime = DateUtils.parse(from, datePattern);
		Date toTime = StringUtils.isNotBlank(to) ? DateUtils.parse(to, datePattern) : new Date();
		Date fromDateTime = DateUtils.setTime(date, fromTime);
		Date toDateTime = DateUtils.setTime(date, toTime);
		return randomDateTime(fromDateTime, toDateTime);
	}

	/**
	 * Returns a random date-time between the two values parsed with the pattern; a blank {@code to}
	 * means now.
	 */
	public static Date randomDateTime(String from, String to, String datePattern) {
		Date fromDateTime = DateUtils.parse(from, datePattern);
		Date toDateTime = StringUtils.isNotBlank(to) ? DateUtils.parse(to, datePattern) : new Date();
		return randomDateTime(fromDateTime, toDateTime);
	}

	/**
	 * Returns a random date-time in {@code [from, to)} with second precision.
	 *
	 * @throws IllegalArgumentException if {@code from} is after {@code to}
	 */
	public static Date randomDateTime(Date from, Date to) {
		long seconds = DateUtils.until(from, to, TimeUnit.SECONDS);
		checkOrder(seconds);
		return new Date(from.getTime() + TimeUnit.SECONDS.toMillis(randomLong(0, seconds)));
	}

	private static void checkOrder(long amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("The start time must not be after the end time.");
		}
	}

}
