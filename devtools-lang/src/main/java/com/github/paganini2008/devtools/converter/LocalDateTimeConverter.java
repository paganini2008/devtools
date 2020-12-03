package com.github.paganini2008.devtools.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.LocalDateUtils;

/**
 * 
 * LocalDateTimeConverter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class LocalDateTimeConverter extends BasicConverter<LocalDateTime> {

	private final Converter<Long, LocalDateTime> longConverter = new Converter<Long, LocalDateTime>() {
		public LocalDateTime convertValue(Long source, LocalDateTime defaultValue) {
			return LocalDateUtils.toLocalDateTime(source, zoneId, defaultValue);
		}
	};

	private final Converter<String, LocalDateTime> stringConverter = new Converter<String, LocalDateTime>() {
		public LocalDateTime convertValue(String source, LocalDateTime defaultValue) {
			return LocalDateUtils.parseLocalDateTime(source, dateTimeFormatter, defaultValue);
		}
	};

	private final Converter<Date, LocalDateTime> dateConverter = new Converter<Date, LocalDateTime>() {
		public LocalDateTime convertValue(Date source, LocalDateTime defaultValue) {
			return LocalDateUtils.toLocalDateTime(source, zoneId, defaultValue);
		}
	};

	private final Converter<Calendar, LocalDateTime> calendarConverter = new Converter<Calendar, LocalDateTime>() {
		public LocalDateTime convertValue(Calendar source, LocalDateTime defaultValue) {
			return LocalDateUtils.toLocalDateTime(source, zoneId, defaultValue);
		}
	};

	public LocalDateTimeConverter() {
		registerType(Long.class, longConverter);
		registerType(String.class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(Calendar.class, calendarConverter);
	}

	private ZoneId zoneId = ZoneId.systemDefault();
	private DateTimeFormatter dateTimeFormatter = LocalDateUtils.DEFAULT_DATETIME_FORMATTER;

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}

}
