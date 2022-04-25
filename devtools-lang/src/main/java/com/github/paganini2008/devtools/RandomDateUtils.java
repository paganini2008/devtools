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

import static com.github.paganini2008.devtools.RandomUtils.randomDayOfMonth;
import static com.github.paganini2008.devtools.RandomUtils.randomDayOfYear;
import static com.github.paganini2008.devtools.RandomUtils.randomHourOfDay;
import static com.github.paganini2008.devtools.RandomUtils.randomInt;
import static com.github.paganini2008.devtools.RandomUtils.randomMinuteOrSecond;
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
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class RandomDateUtils {

	public static Year randomYear() {
		return randomYear(YearUtils.MIN_YEAR, Year.now());
	}

	public static Year randomYear(Year fromYear, Year toYear) {
		return randomYear(fromYear.getValue(), toYear.getValue());
	}

	public static Year randomYear(int fromYear, int toYear) {
		return Year.of(RandomUtils.randomYear(fromYear, toYear));
	}

	public static YearMonth randomYearMonth(Year year) {
		return randomYearMonth(year, Month.JANUARY, Month.DECEMBER);
	}

	public static YearMonth randomYearMonth(Year year, Month fromMonth, Month toMonth) {
		Month month = randomMonth(fromMonth, toMonth);
		return year.atMonth(month);
	}

	public static YearMonth randomYearMonth(Year fromYear, Year toYear, Month fromMonth, Month toMonth) {
		Year year = randomYear(fromYear, toYear);
		return randomYearMonth(year, fromMonth, toMonth);
	}

	public static YearMonth randomYearMonth(int year) {
		return randomYearMonth(year, 0, 12);
	}

	public static YearMonth randomYearMonth(int year, int fromMonth, int toMonth) {
		Year isoYear = Year.of(year);
		Month month = Month.values()[randomMonth(fromMonth, toMonth) - 1];
		return isoYear.atMonth(month);
	}

	public static YearMonth randomYearMonth(int fromYear, int toYear, int fromMonth, int toMonth) {
		int year = RandomUtils.randomYear(fromYear, toYear);
		return randomYearMonth(year, fromMonth, toMonth);
	}

	public static LocalDate randomLocalDate() {
		return randomLocalDate(Year.now());
	}

	public static LocalDate randomLocalDate(Year year) {
		return randomLocalDate(year, 1, 366);
	}

	public static LocalDate randomLocalDate(Year year, int fromDayOfYear, int toDayOfYear) {
		int dayOfYear = randomDayOfYear(year, fromDayOfYear, toDayOfYear);
		return year.atDay(dayOfYear);
	}

	public static LocalDate randomLocalDate(Year fromYear, Year toYear, int fromDayOfYear, int toDayOfYear) {
		Year year = randomYear(fromYear, toYear);
		return randomLocalDate(year, fromDayOfYear, toDayOfYear);
	}

	public static LocalDate randomLocalDate(Year year, Month month) {
		return randomLocalDate(year, month, 1, 31);
	}

	public static LocalDate randomLocalDate(YearMonth yearMonth) {
		return randomLocalDate(yearMonth, 1, 31);
	}

	public static LocalDate randomLocalDate(YearMonth yearMonth, int fromDayOfMonth, int toDayOfMonth) {
		int dayOfMonth = randomDayOfMonth(yearMonth, fromDayOfMonth, toDayOfMonth);
		return yearMonth.atDay(dayOfMonth);
	}

	public static LocalDate randomLocalDate(Year year, Month month, int fromDayOfMonth, int toDayOfMonth) {
		return randomLocalDate(year.atMonth(month), fromDayOfMonth, toDayOfMonth);
	}

	public static LocalDate randomLocalDate(Year year, Month fromMonth, Month toMonth, int fromDayOfMonth, int toDayOfMonth) {
		YearMonth yearMonth = randomYearMonth(year, fromMonth, toMonth);
		return randomLocalDate(yearMonth, fromDayOfMonth, toDayOfMonth);
	}

	public static LocalDate randomLocalDate(Year fromYear, Year toYear, Month fromMonth, Month toMonth, int fromDayOfMonth,
			int toDayOfMonth) {
		YearMonth yearMonth = randomYearMonth(fromYear, toYear, fromMonth, toMonth);
		return randomLocalDate(yearMonth, fromDayOfMonth, toDayOfMonth);
	}

	public static LocalDate randomLocalDate(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		int dayOfMonth = randomDayOfMonth(year, month, fromDayOfMonth, toDayOfMonth);
		return LocalDateUtils.of(year, month, dayOfMonth);
	}

	public static LocalDate randomLocalDate(int year, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		YearMonth yearMonth = randomYearMonth(year, fromMonth, toMonth);
		return randomLocalDate(yearMonth, fromDayOfMonth, toDayOfMonth);
	}

	public static LocalDate randomLocalDate(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		YearMonth yearMonth = randomYearMonth(fromYear, toYear, fromMonth, toMonth);
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
		long days = from.until(to, ChronoUnit.DAYS);
		return from.plusDays(randomInt(0, (int) days));
	}

	public static LocalDateTime randomLocalDateTime() {
		return randomLocalDateTime(Year.now());
	}

	public static LocalDateTime randomLocalDateTime(Year year) {
		return randomLocalDateTime(year, 1, 366);
	}

	public static LocalDateTime randomLocalDateTime(Year year, int fromDayOfYear, int toDayOfYear) {
		LocalDate localDate = randomLocalDate(year, fromDayOfYear, toDayOfYear);
		LocalTime localTime = randomLocalTime();
		return localDate.atTime(localTime);
	}

	public static LocalDateTime randomLocalDateTime(Year year, int dayOfYear, String from, String to) {
		return randomLocalDateTime(year, dayOfYear, from, to, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	public static LocalDateTime randomLocalDateTime(Year year, int dayOfYear, String from, String to, DateTimeFormatter df) {
		return randomLocalDateTime(year, dayOfYear, LocalTime.parse(from, df), LocalTime.parse(to, df));
	}

	public static LocalDateTime randomLocalDateTime(Year year, int dayOfYear, LocalTime from, LocalTime to) {
		LocalDate localDate = LocalDateUtils.of(year, dayOfYear);
		return randomLocalDateTime(localDate, from, to);
	}

	public static LocalDateTime randomLocalDateTime(Year year, int fromDayOfYear, int toDayOfYear, LocalTime from, LocalTime to) {
		LocalDate localDate = randomLocalDate(year, fromDayOfYear, toDayOfYear);
		return randomLocalDateTime(localDate, from, to);
	}

	public static LocalDateTime randomLocalDateTime(Year fromYear, Year toYear, int fromDayOfYear, int toDayOfYear, LocalTime from,
			LocalTime to) {
		LocalDate localDate = randomLocalDate(fromYear, toYear, fromDayOfYear, toDayOfYear);
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
		return localDate.atTime(randomHourOfDay(fh, th), randomMinuteOrSecond(fm, tm), randomMinuteOrSecond(fs, ts));
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month) {
		return randomLocalDateTime(year, month, randomInt(1, month.maxLength() + 1));
	}

	public static LocalDateTime randomLocalDateTime(Year year, Month month, int dayOfMonth) {
		LocalDate localDate = year.atMonth(month).atDay(dayOfMonth);
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
		LocalDate localDate = year.atMonth(month).atDay(dayOfMonth);
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

	public static LocalDateTime randomLocalDateTime(LocalDate fromLocalDate, LocalDate toLocalDate) {
		LocalDate localDate = randomLocalDate(fromLocalDate, toLocalDate);
		return randomLocalDateTime(localDate);
	}

	public static LocalDateTime randomLocalDateTime(LocalDate fromLocalDate, LocalDate toLocalDate, LocalTime fromTime, LocalTime toTime) {
		LocalDate localDate = randomLocalDate(fromLocalDate, toLocalDate);
		return randomLocalDateTime(localDate, fromTime, toTime);
	}

	public static LocalDateTime randomLocalDateTime(LocalDateTime startTime, LocalDateTime endTime) {
		long seconds = startTime.until(endTime, ChronoUnit.SECONDS);
		return startTime.plusSeconds(randomInt(0, (int) seconds));
	}

	public static LocalDateTime randomLocalDateTime(String from, String to) {
		return randomLocalDateTime(from, to, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	public static LocalDateTime randomLocalDateTime(String from, String to, DateTimeFormatter dtf) {
		LocalDateTime startTime = LocalDateTime.parse(from, dtf);
		LocalDateTime endTime = LocalDateTime.parse(to, dtf);
		return randomLocalDateTime(startTime, endTime);
	}

	public static LocalDateTime randomLocalDateTime(LocalDate localDate) {
		return randomLocalDateTime(localDate, 0, 23, 0, 59, 0, 59);
	}

	public static LocalDateTime randomLocalDateTime(LocalDate localDate, int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute,
			int fromSecond, int toSecond) {
		LocalTime startTime = LocalTimeUtils.of(fromHourOfDay, fromMinute, fromSecond);
		LocalTime endTime = LocalTimeUtils.of(toHourOfDay, toMinute, toSecond);
		return randomLocalDateTime(LocalDateTime.of(localDate, startTime), LocalDateTime.of(localDate, endTime));
	}

	public static LocalDateTime randomLocalDateTime(int year, int month, int dayOfMonth, int fromHourOfDay, int toHourOfDay, int fromMinute,
			int toMinute, int fromSecond, int toSecond) {
		LocalDate localDate = LocalDateUtils.of(year, month, dayOfMonth);
		return randomLocalDateTime(localDate, fromHourOfDay, toHourOfDay, fromMinute, toMinute, fromSecond, toSecond);
	}

	public static LocalDateTime randomLocalDateTime(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth,
			int toDayOfMonth, int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute, int fromSecond, int toSecond) {
		LocalDateTime startTime = LocalDateTimeUtils.of(fromYear, fromMonth, fromDayOfMonth, fromHourOfDay, fromMinute, fromSecond);
		LocalDateTime endTime = LocalDateTimeUtils.of(toYear, toMonth, toDayOfMonth, toHourOfDay, toMinute, toSecond);
		return randomLocalDateTime(startTime, endTime);
	}

	public static LocalTime randomLocalTime() {
		return randomLocalTime(0, 23, 0, 59);
	}

	public static LocalTime randomLocalTime(int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute) {
		return randomLocalTime(fromHourOfDay, toHourOfDay, fromMinute, toMinute, 0, 59);
	}

	public static LocalTime randomLocalTime(int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute, int fromSecond,
			int toSecond) {
		LocalTime startTime = LocalTimeUtils.of(fromHourOfDay, fromMinute, fromSecond);
		LocalTime endTime = LocalTimeUtils.of(toHourOfDay, toMinute, toSecond);
		return randomLocalTime(startTime, endTime);
	}

	public static LocalTime randomLocalTime(String from, String to) {
		return randomLocalTime(from, to, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	public static LocalTime randomLocalTime(String from, String to, DateTimeFormatter df) {
		return randomLocalTime(LocalTime.parse(from, df), LocalTime.parse(to, df));
	}

	public static LocalTime randomLocalTime(LocalTime from, LocalTime to) {
		long seconds = from.until(to, ChronoUnit.SECONDS);
		return from.plusSeconds(randomInt(0, (int) seconds));
	}

	public static Date randomDate() {
		return randomDate(DateUtils.getYear());
	}

	public static Date randomDate(int year) {
		return randomDate(year, randomMonth(1, 12));
	}

	public static Date randomDate(int year, int month) {
		return randomDate(year, month, 1, 31);
	}

	public static Date randomDate(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		int dayOfMonth = randomDayOfMonth(year, month, fromDayOfMonth, toDayOfMonth);
		LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
		return DateUtils.toDate(localDate, null);
	}

	public static Date randomDate(int year, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		Date fromDate = DateUtils.of(year, fromMonth, fromDayOfMonth);
		Date toDate = DateUtils.of(year, toMonth, toDayOfMonth);
		return randomDate(fromDate, toDate);
	}

	public static Date randomDate(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		Date fromDate = DateUtils.of(fromYear, fromMonth, fromDayOfMonth);
		Date toDate = DateUtils.of(toYear, toMonth, toDayOfMonth);
		return randomDate(fromDate, toDate);
	}

	public static Date randomDate(String from, String to, String datePattern) {
		Date fromDate = DateUtils.parse(from, datePattern);
		Date toDate = DateUtils.parse(to, datePattern);
		return randomDate(fromDate, toDate);
	}

	public static Date randomDate(Date from, Date to) {
		long days = DateUtils.until(from, to, TimeUnit.DAYS);
		return DateUtils.addDayOfMonth(from, randomInt(0, (int) days));
	}

	public static Date randomDateTime() {
		return randomDateTime(DateUtils.getYear());
	}

	public static Date randomDateTime(int year) {
		return randomDateTime(year, randomMonth(1, 12));
	}

	public static Date randomDateTime(int year, int month) {
		return randomDateTime(year, month, 1, 31);
	}

	public static Date randomDateTime(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		return randomDateTime(year, month, fromDayOfMonth, toDayOfMonth, 0, 23, 0, 59, 0, 59);
	}

	public static Date randomDateTime(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		return randomDateTime(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth, 0, 23, 0, 59, 0, 59);
	}

	public static Date randomDateTime(int year, int month, int dayOfMonth) {
		return randomDateTime(year, month, dayOfMonth, 0, 23, 0, 59, 0, 59);
	}

	public static Date randomDateTime(int year, int month, int dayOfMonth, int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute,
			int fromSecond, int toSecond) {
		Date date = DateUtils.of(year, month, dayOfMonth);
		return randomDateTime(date, fromHourOfDay, toHourOfDay, fromMinute, toMinute, fromSecond, toSecond);
	}

	public static Date randomDateTime(int year, int month, int fromDayOfMonth, int toDayOfMonth, int fromHourOfDay, int toHourOfDay,
			int fromMinute, int toMinute, int fromSecond, int toSecond) {
		Date from = DateUtils.of(year, month, fromDayOfMonth, fromHourOfDay, fromMinute, fromSecond);
		Date to = DateUtils.of(year, month, toDayOfMonth, toHourOfDay, toMinute, toSecond);
		return randomDateTime(from, to);
	}

	public static Date randomDateTime(int year, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth, int fromHourOfDay,
			int toHourOfDay, int fromMinute, int toMinute, int fromSecond, int toSecond) {
		Date from = DateUtils.of(year, fromMonth, fromDayOfMonth, fromHourOfDay, fromMinute, fromSecond);
		Date to = DateUtils.of(year, toMonth, toDayOfMonth, toHourOfDay, toMinute, toSecond);
		return randomDateTime(from, to);
	}

	public static Date randomDateTime(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth,
			int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute, int fromSecond, int toSecond) {
		Date from = DateUtils.of(fromYear, fromMonth, fromDayOfMonth, fromHourOfDay, fromMinute, fromSecond);
		Date to = DateUtils.of(toYear, toMonth, toDayOfMonth, toHourOfDay, toMinute, toSecond);
		return randomDateTime(from, to);
	}

	public static Date randomDateTime(Date date, int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute, int fromSecond,
			int toSecond) {
		Date from = DateUtils.setTime(date, fromHourOfDay, fromMinute, fromSecond);
		Date to = DateUtils.setTime(date, toHourOfDay, toMinute, toSecond);
		return randomDateTime(from, to);
	}

	public static Date randomDateTime(Date date, String from, String to, String datePattern) {
		Date fromTime = DateUtils.parse(from, datePattern);
		Date toTime = StringUtils.isNotBlank(to) ? DateUtils.parse(to, datePattern) : new Date();
		Date fromDateTime = DateUtils.setTime(date, fromTime);
		Date toDateTime = DateUtils.setTime(date, toTime);
		return randomDateTime(fromDateTime, toDateTime);
	}

	public static Date randomDateTime(String from, String to, String datePattern) {
		Date fromDateTime = DateUtils.parse(from, datePattern);
		Date toDateTime = StringUtils.isNotBlank(to) ? DateUtils.parse(to, datePattern) : new Date();
		return randomDateTime(fromDateTime, toDateTime);
	}

	public static Date randomDateTime(Date from, Date to) {
		long seconds = DateUtils.until(from, to, TimeUnit.SECONDS);
		return DateUtils.addSeconds(from, randomInt(0, (int) seconds));
	}

}
