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
import com.github.paganini2008.devtools.date.LocalDateTimeUtils;
import com.github.paganini2008.devtools.date.LocalDateUtils;
import com.github.paganini2008.devtools.date.LocalTimeUtils;

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
		return randomYear(Year.of(1970), Year.now());
	}

	public static Year randomYear(Year fromYear, Year toYear) {
		return randomYear(fromYear.getValue(), toYear.getValue());
	}

	public static Year randomYear(int fromYear, int toYear) {
		return Year.of(randomValueOfYear(fromYear, toYear));
	}

	public static YearMonth randomYearMonth(Year year) {
		return randomYearMonth(year, Month.JANUARY, Month.DECEMBER);
	}

	public static YearMonth randomYearMonth(Year year, Month fromMonth, Month toMonth) {
		Month month = Month.values()[randomInt(fromMonth.ordinal(), toMonth.ordinal())];
		return year.atMonth(month);
	}

	public static YearMonth randomYearMonth(Year fromYear, Year toYear, Month fromMonth, Month toMonth) {
		Year year = randomYear(fromYear, toYear);
		return randomYearMonth(year, fromMonth, toMonth);
	}

	public static YearMonth randomYearMonth(int year) {
		return randomYearMonth(year, 0, 11);
	}

	public static YearMonth randomYearMonth(int year, int fromMonth, int toMonth) {
		Year isoYear = Year.of(year);
		Month month = Month.values()[randomValueOfMonth(fromMonth, toMonth)];
		return isoYear.atMonth(month);
	}

	public static YearMonth randomYearMonth(int fromYear, int toYear, int fromMonth, int toMonth) {
		int year = randomValueOfYear(fromYear, toYear);
		return randomYearMonth(year, fromMonth, toMonth);
	}

	public static LocalDate randomLocalDate() {
		return randomLocalDate(Year.now());
	}

	public static LocalDate randomLocalDate(Year year) {
		return randomLocalDate(year, 1, 366);
	}

	public static LocalDate randomLocalDate(Year year, int fromDayOfYear, int toDayOfYear) {
		int dayOfYear = randomValueOfDayOfYear(year, fromDayOfYear, toDayOfYear);
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
		int dayOfMonth = randomValueOfDayOfMonth(yearMonth, fromDayOfMonth, toDayOfMonth);
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
		int dayOfMonth = randomValueOfDayOfMonth(year, month, fromDayOfMonth, toDayOfMonth);
		return LocalDateUtils.valueOf(year, month, dayOfMonth);
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
		LocalDate localDate = LocalDateUtils.valueOf(year, dayOfYear);
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
		return localDate.atTime(randomValueOfHourOfDay(fh, th), randomValueOfMinuteOrSecond(fm, tm), randomValueOfMinuteOrSecond(fs, ts));
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
		return LocalDateTimeUtils.valueOf(localDate, randomValueOfHourOfDay(0, 23), randomValueOfMinuteOrSecond(0, 59),
				randomValueOfMinuteOrSecond(0, 59));
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
		return LocalTimeUtils.valueOf(randomValueOfHourOfDay(0, 23), randomValueOfMinuteOrSecond(0, 59),
				randomValueOfMinuteOrSecond(0, 59));
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
		return randomDate(year, 11);
	}

	public static Date randomDate(int year, int month) {
		return randomDate(year, month, 1, 31);
	}

	public static Date randomDate(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		int dateOfMonth = randomValueOfDayOfMonth(year, month, fromDayOfMonth, toDayOfMonth);
		return DateUtils.valueOf(year, month, dateOfMonth);
	}

	public static Date randomDate(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		int year = randomValueOfYear(fromYear, toYear);
		int month = randomValueOfMonth(fromMonth, toMonth);
		int dayOfMonth = randomValueOfDayOfMonth(year, month, fromDayOfMonth, toDayOfMonth);
		return DateUtils.valueOf(year, month, dayOfMonth);
	}

	public static Date randomDateTime() {
		return randomDateTime(DateUtils.getYear());
	}

	public static Date randomDateTime(int year) {
		return randomDateTime(year, 11);
	}

	public static Date randomDateTime(int year, int month) {
		return randomDateTime(year, month, 1, 31);
	}

	public static Date randomDateTime(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		return randomDateTime(year, month, fromDayOfMonth, toDayOfMonth, 0, 23, 0, 59, 0, 59);
	}

	public static Date randomDateTime(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth) {
		Date date = randomDate(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomDateTime(date, 0, 23, 0, 59, 0, 59);
	}

	public static Date randomDateTime(int year, int month, int fromDayOfMonth, int toDayOfMonth, int fromHourOfDay, int toHourOfDay,
			int fromMinute, int toMinute, int fromSecond, int toSecond) {
		int dayOfMonth = randomValueOfDayOfMonth(year, month, fromDayOfMonth, toDayOfMonth);
		Date date = DateUtils.valueOf(year, month, dayOfMonth);
		return randomDateTime(date, fromHourOfDay, toHourOfDay, fromMinute, toMinute, fromSecond, toSecond);
	}

	public static Date randomDateTime(int fromYear, int toYear, int fromMonth, int toMonth, int fromDayOfMonth, int toDayOfMonth,
			int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute, int fromSecond, int toSecond) {
		Date date = randomDate(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth);
		return randomDateTime(date, fromHourOfDay, toHourOfDay, fromMinute, toMinute, fromSecond, toSecond);
	}

	public static Date randomDateTime(Date date, int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute, int fromSecond,
			int toSecond) {
		int hourOfDay = randomValueOfHourOfDay(fromHourOfDay, toHourOfDay);
		int minute = randomValueOfMinuteOrSecond(fromMinute, toMinute);
		int second = randomValueOfMinuteOrSecond(fromSecond, toSecond);
		return DateUtils.setTime(date, hourOfDay, minute, second);
	}

	public static Date randomDateTime(String from, String to, String datePattern) {
		Date fromDate = DateUtils.parse(from, datePattern);
		Date toDate = StringUtils.isNotBlank(to) ? DateUtils.parse(to, datePattern) : new Date();
		return randomDateTime(fromDate, toDate);
	}

	public static Date randomDateTime(Date date, String from, String to, String datePattern) {
		Date fromTime = DateUtils.parse(from, datePattern);
		Date toTime = StringUtils.isNotBlank(to) ? DateUtils.parse(to, datePattern) : new Date();
		Date fromDate = DateUtils.setTime(date, fromTime);
		Date toDate = DateUtils.setTime(date, toTime);
		return randomDateTime(fromDate, toDate);
	}

	public static Date randomDateTime(Date fromDate, Date toDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(fromDate);
		int fromYear = c.get(Calendar.YEAR);
		int fromMonth = c.get(Calendar.MONTH);
		int fromDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		int fromHourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int fromMinute = c.get(Calendar.MINUTE);
		int fromSecond = c.get(Calendar.SECOND);
		c.setTime(toDate);
		int toYear = c.get(Calendar.YEAR);
		int toMonth = c.get(Calendar.MONTH);
		int toDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		int toHourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int toMinute = c.get(Calendar.MINUTE);
		int toSecond = c.get(Calendar.SECOND);
		return randomDateTime(fromYear, toYear, fromMonth, toMonth, fromDayOfMonth, toDayOfMonth, fromHourOfDay, toHourOfDay, fromMinute,
				toMinute, fromSecond, toSecond);
	}

	private static int randomValueOfYear(int fromYear, int toYear) {
		Assert.lt(fromYear, 1970, "FromYear starts from 1970");
		return randomInt(fromYear, Math.min(9999, toYear) + 1);
	}

	private static int randomValueOfMonth(int fromMonth, int toMonth) {
		Assert.lt(fromMonth, 0, "FromMonth starts from 0");
		Assert.gt(toMonth, 11, "ToMonth ends with 11");
		return randomInt(fromMonth, toMonth + 1);
	}

	private static int randomValueOfDayOfYear(Year year, int fromDayOfYear, int toDayOfYear) {
		Assert.lt(fromDayOfYear, 1, "FromDay of Year starts from 1");
		int theDayOfYear = Math.min(toDayOfYear, year.isLeap() ? 366 : 365) + 1;
		return randomInt(fromDayOfYear, theDayOfYear);
	}

	private static int randomValueOfDayOfMonth(YearMonth yearMonth, int fromDayOfMonth, int toDayOfMonth) {
		Assert.lt(fromDayOfMonth, 1, "FromDay of Month starts from 1");
		int lastDayOfMonth = Math.min(toDayOfMonth, yearMonth.atEndOfMonth().getDayOfMonth());
		return randomInt(fromDayOfMonth, lastDayOfMonth + 1);
	}

	private static int randomValueOfDayOfMonth(int year, int month, int fromDayOfMonth, int toDayOfMonth) {
		Assert.lt(fromDayOfMonth, 1, "FromDay of Month starts from 1");
		int lastDayOfMonth = Math.min(toDayOfMonth, DateUtils.getLastDayOfMonth(year, month));
		return randomInt(fromDayOfMonth, lastDayOfMonth + 1);
	}

	private static int randomValueOfHourOfDay(int fromHourOfDay, int toHourOfDay) {
		Assert.lt(fromHourOfDay, 0, "FromHour of Day starts from 0");
		Assert.gt(toHourOfDay, 23, "FromHour of Day ends with 23");
		return randomInt(fromHourOfDay, toHourOfDay + 1);
	}

	private static int randomValueOfMinuteOrSecond(int from, int to) {
		Assert.lt(from, 0, "FromMinute Or FromSecond  starts from 0");
		Assert.gt(to, 59, "FromMinute Or FromSecond ends with 59");
		return randomInt(from, to + 1);
	}

}
