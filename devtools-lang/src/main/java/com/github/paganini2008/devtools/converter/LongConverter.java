package com.github.paganini2008.devtools.converter;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.CalendarUtils;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.date.LocalDateUtils;
import com.github.paganini2008.devtools.primitives.Longs;

/**
 * LongConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class LongConverter extends BasicConverter<Long> {

	private final Converter<Boolean, Long> booleanConverter = new Converter<Boolean, Long>() {
		public Long convertValue(Boolean source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Long> characterConverter = new Converter<Character, Long>() {
		public Long convertValue(Character source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Long> numberConverter = new Converter<Number, Long>() {
		public Long convertValue(Number source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Long> stringConverter = new Converter<String, Long>() {
		public Long convertValue(String source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<Date, Long> dateConverter = new Converter<Date, Long>() {
		public Long convertValue(Date source, Long defaultValue) {
			return DateUtils.getTimeInMillis(source, defaultValue);
		}
	};

	private final Converter<Calendar, Long> calendarConverter = new Converter<Calendar, Long>() {
		public Long convertValue(Calendar source, Long defaultValue) {
			return CalendarUtils.getTimeInMillis(source, defaultValue);
		}
	};

	private final Converter<Instant, Long> instantConverter = new Converter<Instant, Long>() {
		public Long convertValue(Instant source, Long defaultValue) {
			return LocalDateUtils.getTimeInMillis(source, defaultValue);
		}
	};

	public LongConverter() {
		registerType(Boolean.class, booleanConverter);
		registerType(Character.class, characterConverter);
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(Calendar.class, calendarConverter);
		registerType(Instant.class, instantConverter);
	}

}
