package com.github.paganini2008.devtools.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.date.LocalDateUtils;

/**
 * 
 * LocalDateTimeConverter
 *
 * @author Fred Feng
 * @version 1.0
 */
public class LocalDateTimeConverter extends BasicConverter<LocalDateTime> {

	private final Converter<Long, LocalDateTime> longConverter = new Converter<Long, LocalDateTime>() {
		public LocalDateTime getValue(Long source, LocalDateTime defaultValue) {
			return LocalDateUtils.toLocalDateTime(source, getConfig().getZoneId(), defaultValue);
		}
	};

	private final Converter<String, LocalDateTime> stringConverter = new Converter<String, LocalDateTime>() {
		public LocalDateTime getValue(String source, LocalDateTime defaultValue) {
			return LocalDateUtils.parseLocalDateTime(source, getConfig().getDateTimeFormatter(), defaultValue);
		}
	};

	private final Converter<Date, LocalDateTime> dateConverter = new Converter<Date, LocalDateTime>() {
		public LocalDateTime getValue(Date source, LocalDateTime defaultValue) {
			return LocalDateUtils.toLocalDateTime(source, getConfig().getZoneId(), defaultValue);
		}
	};

	private final Converter<Calendar, LocalDateTime> calendarConverter = new Converter<Calendar, LocalDateTime>() {
		public LocalDateTime getValue(Calendar source, LocalDateTime defaultValue) {
			return LocalDateUtils.toLocalDateTime(source, getConfig().getZoneId(), defaultValue);
		}
	};

	public LocalDateTimeConverter() {
		put(Long.class, longConverter);
		put(String.class, stringConverter);
		put(Date.class, dateConverter);
		put(Calendar.class, calendarConverter);
	}

}
