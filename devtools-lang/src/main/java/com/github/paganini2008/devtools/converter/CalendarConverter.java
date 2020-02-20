package com.github.paganini2008.devtools.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.CalendarUtils;

/**
 * CalendarConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CalendarConverter extends BasicConverter<Calendar> {

	private final Converter<Long, Calendar> longConverter = new Converter<Long, Calendar>() {
		public Calendar convertValue(Long source, Calendar defaultValue) {
			return CalendarUtils.toCalendar(source, getConfig().getTimeZone(), defaultValue);
		}
	};

	private final Converter<String, Calendar> stringConverter = new Converter<String, Calendar>() {
		public Calendar convertValue(String source, Calendar defaultValue) {
			return CalendarUtils.parse(source, config.getDatePattern(), defaultValue);
		}
	};

	private final Converter<Date, Calendar> dateConverter = new Converter<Date, Calendar>() {
		public Calendar convertValue(Date source, Calendar defaultValue) {
			return CalendarUtils.toCalendar(source, getConfig().getTimeZone(), defaultValue);
		}
	};

	private final Converter<LocalDate, Calendar> localDateConverter = new Converter<LocalDate, Calendar>() {
		public Calendar convertValue(LocalDate source, Calendar defaultValue) {
			return CalendarUtils.toCalendar(source, getConfig().getTimeZone(), defaultValue);
		}
	};

	private final Converter<LocalDateTime, Calendar> localDateTimeConverter = new Converter<LocalDateTime, Calendar>() {
		public Calendar convertValue(LocalDateTime source, Calendar defaultValue) {
			return CalendarUtils.toCalendar(source, getConfig().getTimeZone(), defaultValue);
		}
	};

	public CalendarConverter() {
		registerType(Long.class, longConverter);
		registerType(String.class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(LocalDate.class, localDateConverter);
		registerType(LocalDateTime.class, localDateTimeConverter);
	}

}
