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
		public LocalDate getValue(Long source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, getConfig().getZoneId(), defaultValue);
		}
	};

	private final Converter<String, LocalDate> stringConverter = new Converter<String, LocalDate>() {
		public LocalDate getValue(String source, LocalDate defaultValue) {
			return LocalDateUtils.parseLocalDate(source, getConfig().getDateFormatter(), defaultValue);
		}
	};

	private final Converter<Date, LocalDate> dateConverter = new Converter<Date, LocalDate>() {
		public LocalDate getValue(Date source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, getConfig().getZoneId(), defaultValue);
		}
	};

	private final Converter<Calendar, LocalDate> calendarConverter = new Converter<Calendar, LocalDate>() {
		public LocalDate getValue(Calendar source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, getConfig().getZoneId(), defaultValue);
		}
	};

	public LocalDateConverter() {
		put(Long.class, longConverter);
		put(String.class, stringConverter);
		put(Date.class, dateConverter);
		put(Calendar.class, calendarConverter);
	}

}
