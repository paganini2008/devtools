package com.github.paganini2008.devtools.converter;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.LocalDateUtils;

/**
 * 
 * LocalTimeConverter 
 *
 * @author Fred Feng
 * @version 1.0
 */
public class LocalTimeConverter extends BasicConverter<LocalTime> {

	private final Converter<Long, LocalTime> longConverter = new Converter<Long, LocalTime>() {
		public LocalTime getValue(Long source, LocalTime defaultValue) {
			return LocalDateUtils.toLocalTime(source, getConfig().getZoneId(), defaultValue);
		}
	};

	private final Converter<String, LocalTime> stringConverter = new Converter<String, LocalTime>() {
		public LocalTime getValue(String source, LocalTime defaultValue) {
			return LocalDateUtils.parseLocalTime(source, getConfig().getTimeFormatter(), defaultValue);
		}
	};

	private final Converter<Date, LocalTime> dateConverter = new Converter<Date, LocalTime>() {
		public LocalTime getValue(Date source, LocalTime defaultValue) {
			return LocalDateUtils.toLocalTime(source, getConfig().getZoneId(), defaultValue);
		}
	};

	private final Converter<Calendar, LocalTime> calendarConverter = new Converter<Calendar, LocalTime>() {
		public LocalTime getValue(Calendar source, LocalTime defaultValue) {
			return LocalDateUtils.toLocalTime(source, getConfig().getZoneId(), defaultValue);
		}
	};

	public LocalTimeConverter() {
		put(Long.class, longConverter);
		put(String.class, stringConverter);
		put(Date.class, dateConverter);
		put(Calendar.class, calendarConverter);
	}
}
