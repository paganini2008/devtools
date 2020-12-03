package com.github.paganini2008.devtools.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.github.paganini2008.devtools.date.CalendarUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * CalendarConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CalendarConverter extends BasicConverter<Calendar> {

	private final Converter<Long, Calendar> longConverter = new Converter<Long, Calendar>() {
		public Calendar convertValue(Long source, Calendar defaultValue) {
			return CalendarUtils.toCalendar(source, timeZone, defaultValue);
		}
	};

	private final Converter<String, Calendar> stringConverter = new Converter<String, Calendar>() {
		public Calendar convertValue(String source, Calendar defaultValue) {
			return CalendarUtils.parse(source, datePattern, defaultValue);
		}
	};

	private final Converter<Date, Calendar> dateConverter = new Converter<Date, Calendar>() {
		public Calendar convertValue(Date source, Calendar defaultValue) {
			return CalendarUtils.toCalendar(source, timeZone, defaultValue);
		}
	};

	private final Converter<LocalDate, Calendar> localDateConverter = new Converter<LocalDate, Calendar>() {
		public Calendar convertValue(LocalDate source, Calendar defaultValue) {
			return CalendarUtils.toCalendar(source, timeZone, defaultValue);
		}
	};

	private final Converter<LocalDateTime, Calendar> localDateTimeConverter = new Converter<LocalDateTime, Calendar>() {
		public Calendar convertValue(LocalDateTime source, Calendar defaultValue) {
			return CalendarUtils.toCalendar(source, timeZone, defaultValue);
		}
	};

	public CalendarConverter() {
		registerType(Long.class, longConverter);
		registerType(String.class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(LocalDate.class, localDateConverter);
		registerType(LocalDateTime.class, localDateTimeConverter);
	}

	private TimeZone timeZone = TimeZone.getDefault();
	private String datePattern = DateUtils.DEFAULT_DATE_PATTERN;

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

}
