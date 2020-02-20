package com.github.paganini2008.devtools.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * DateConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DateConverter extends BasicConverter<Date> {

	private final Converter<Long, Date> longConverter = new Converter<Long, Date>() {
		public Date convertValue(Long source, Date defaultValue) {
			return DateUtils.toDate(source, defaultValue);
		}
	};

	private final Converter<String, Date> stringConverter = new Converter<String, Date>() {
		public Date convertValue(String source, Date defaultValue) {
			return DateUtils.parse(source, config.getDatePattern(), defaultValue);
		}
	};

	private final Converter<Calendar, Date> calendarConverter = new Converter<Calendar, Date>() {
		public Date convertValue(Calendar source, Date defaultValue) {
			return DateUtils.toDate(source, defaultValue);
		}
	};

	private final Converter<int[], Date> arrayConverter = new Converter<int[], Date>() {
		public Date convertValue(int[] source, Date defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			if (source.length == 3) {
				return DateUtils.valueOf(source[0], source[1], source[2]);
			} else if (source.length == 6) {
				return DateUtils.valueOf(source[0], source[1], source[2], source[3], source[4], source[5]);
			}
			throw new IllegalArgumentException("Please define the array's length to 3 or 6.");
		}
	};

	private final Converter<LocalDate, Date> localDateConverter = new Converter<LocalDate, Date>() {
		public Date convertValue(LocalDate source, Date defaultValue) {
			return DateUtils.toDate(source, getConfig().getZoneId(), defaultValue);
		}
	};

	private final Converter<LocalDateTime, Date> localDateTimeConverter = new Converter<LocalDateTime, Date>() {
		public Date convertValue(LocalDateTime source, Date defaultValue) {
			return DateUtils.toDate(source, getConfig().getZoneId(), defaultValue);
		}
	};

	public DateConverter() {
		registerType(Long.class, longConverter);
		registerType(String.class, stringConverter);
		registerType(Calendar.class, calendarConverter);
		registerType(int[].class, arrayConverter);
		registerType(LocalDate.class, localDateConverter);
		registerType(LocalDateTime.class, localDateTimeConverter);
	}

}
