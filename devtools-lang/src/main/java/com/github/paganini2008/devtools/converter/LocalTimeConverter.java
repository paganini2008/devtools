package com.github.paganini2008.devtools.converter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
		public LocalTime convertValue(Long source, LocalTime defaultValue) {
			return LocalDateUtils.toLocalTime(source, zoneId, defaultValue);
		}
	};

	private final Converter<String, LocalTime> stringConverter = new Converter<String, LocalTime>() {
		public LocalTime convertValue(String source, LocalTime defaultValue) {
			return LocalDateUtils.parseLocalTime(source, timeFormatter, defaultValue);
		}
	};

	private final Converter<Date, LocalTime> dateConverter = new Converter<Date, LocalTime>() {
		public LocalTime convertValue(Date source, LocalTime defaultValue) {
			return LocalDateUtils.toLocalTime(source, zoneId, defaultValue);
		}
	};

	private final Converter<Calendar, LocalTime> calendarConverter = new Converter<Calendar, LocalTime>() {
		public LocalTime convertValue(Calendar source, LocalTime defaultValue) {
			return LocalDateUtils.toLocalTime(source, zoneId, defaultValue);
		}
	};

	public LocalTimeConverter() {
		registerType(Long.class, longConverter);
		registerType(String.class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(Calendar.class, calendarConverter);
	}

	private ZoneId zoneId = ZoneId.systemDefault();
	private DateTimeFormatter timeFormatter = LocalDateUtils.DEFAULT_TIME_FORMATTER;

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public void setTimeFormatter(DateTimeFormatter timeFormatter) {
		this.timeFormatter = timeFormatter;
	}

}
