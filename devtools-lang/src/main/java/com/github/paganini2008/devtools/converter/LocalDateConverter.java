package com.github.paganini2008.devtools.converter;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.LocalDateUtils;

/**
 * 
 * LocalDateConverter
 *
 * @author Fred Feng
 * @version 1.0
 */
public class LocalDateConverter extends BasicConverter<LocalDate> {

	private final Converter<Long, LocalDate> longConverter = new Converter<Long, LocalDate>() {
		public LocalDate convertValue(Long source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, getConfig().getZoneId(), defaultValue);
		}
	};

	private final Converter<String, LocalDate> stringConverter = new Converter<String, LocalDate>() {
		public LocalDate convertValue(String source, LocalDate defaultValue) {
			return LocalDateUtils.parseLocalDate(source, getConfig().getDateFormatter(), defaultValue);
		}
	};

	private final Converter<Date, LocalDate> dateConverter = new Converter<Date, LocalDate>() {
		public LocalDate convertValue(Date source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, getConfig().getZoneId(), defaultValue);
		}
	};

	private final Converter<Calendar, LocalDate> calendarConverter = new Converter<Calendar, LocalDate>() {
		public LocalDate convertValue(Calendar source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, getConfig().getZoneId(), defaultValue);
		}
	};

	public LocalDateConverter() {
		registerType(Long.class, longConverter);
		registerType(String.class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(Calendar.class, calendarConverter);
	}

}
