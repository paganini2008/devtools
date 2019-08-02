package com.github.paganini2008.devtools.converter;

import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.CalendarUtils;

/**
 * CalendarConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CalendarConverter extends BaseConverter<Calendar> {

	private final Converter<Long, Calendar> longConverter = new Converter<Long, Calendar>() {
		public Calendar getValue(Long source, Calendar defaultValue) {
			return CalendarUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Calendar> stringConverter = new Converter<String, Calendar>() {
		public Calendar getValue(String source, Calendar defaultValue) {
			return CalendarUtils.parse(source, config.getDatePatterns(), defaultValue);
		}
	};

	private final Converter<Date, Calendar> dateConverter = new Converter<Date, Calendar>() {
		public Calendar getValue(Date source, Calendar defaultValue) {
			return CalendarUtils.valueOf(source, defaultValue);
		}
	};

	public CalendarConverter() {
		put(Long.class, longConverter);
		put(String.class, stringConverter);
		put(Date.class, dateConverter);
	}

}
