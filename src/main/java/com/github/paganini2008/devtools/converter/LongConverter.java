package com.github.paganini2008.devtools.converter;

import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.Longs;
import com.github.paganini2008.devtools.date.CalendarUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * LongConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class LongConverter extends BaseConverter<Long> {

	private final Converter<Boolean, Long> booleanConverter = new Converter<Boolean, Long>() {
		public Long getValue(Boolean source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Long> characterConverter = new Converter<Character, Long>() {
		public Long getValue(Character source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Long> numberConverter = new Converter<Number, Long>() {
		public Long getValue(Number source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Long> stringConverter = new Converter<String, Long>() {
		public Long getValue(String source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<Date, Long> dateConverter = new Converter<Date, Long>() {
		public Long getValue(Date source, Long defaultValue) {
			return DateUtils.getTimeInMillis(source, defaultValue);
		}
	};

	private final Converter<Calendar, Long> calendarConverter = new Converter<Calendar, Long>() {
		public Long getValue(Calendar source, Long defaultValue) {
			return CalendarUtils.getTimeInMillis(source, defaultValue);
		}
	};

	public LongConverter() {
		put(Boolean.class, booleanConverter);
		put(Character.class, characterConverter);
		put(Number.class, numberConverter);
		put(String.class, stringConverter);
		put(Date.class, dateConverter);
		put(Calendar.class, calendarConverter);
	}

}
