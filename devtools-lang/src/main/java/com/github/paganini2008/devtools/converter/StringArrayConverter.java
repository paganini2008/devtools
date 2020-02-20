package com.github.paganini2008.devtools.converter;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.NumberUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.date.CalendarUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * StringArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class StringArrayConverter extends BasicConverter<String[]> {

	private final Converter<CharSequence, String[]> charSequenceConverter = new Converter<CharSequence, String[]>() {
		public String[] getValue(CharSequence source, String[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> results = StringUtils.split(source, config.getDelimiter());
			return results.toArray(new String[results.size()]);
		}
	};

	private final Converter<Date[], String[]> dateArrayConverter = new Converter<Date[], String[]>() {
		public String[] getValue(Date[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return DateUtils.formatMany(source, config.getDateFormat());
		}
	};

	private final Converter<Calendar[], String[]> calendarArrayConverter = new Converter<Calendar[], String[]>() {
		public String[] getValue(Calendar[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return CalendarUtils.formatMany(source, config.getDateFormat());
		}
	};

	private final Converter<Number[], String[]> numberArrayConverter = new Converter<Number[], String[]>() {
		public String[] getValue(Number[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return NumberUtils.formats(source, config.getDecimalFormatter());
		}
	};

	private final Converter<Object[], String[]> arrayConverter = new Converter<Object[], String[]>() {
		public String[] getValue(Object[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return ArrayUtils.toStringArray(source);
		}
	};

	private final Converter<Collection<?>, String[]> collectionConverter = new Converter<Collection<?>, String[]>() {
		public String[] getValue(Collection<?> source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			List<String> result = ListUtils.toStringList(source);
			if (result == null) {
				return defaultValue;
			}
			return result.toArray(new String[result.size()]);
		}
	};

	private final Converter<Iterator<?>, String[]> iteratorConverter = new Converter<Iterator<?>, String[]>() {
		public String[] getValue(Iterator<?> source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			List<String> result = ListUtils.toStringList(source);
			if (result == null) {
				return defaultValue;
			}
			return result.toArray(new String[result.size()]);
		}
	};

	private final Converter<Enumeration<?>, String[]> enumerationConverter = new Converter<Enumeration<?>, String[]>() {
		public String[] getValue(Enumeration<?> source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			List<String> result = ListUtils.toStringList(source);
			if (result == null) {
				return defaultValue;
			}
			return result.toArray(new String[result.size()]);
		}
	};

	public StringArrayConverter(StringConverter handler) {
		put(CharSequence.class, charSequenceConverter);

		put(boolean[].class, arrayConverter);
		put(char[].class, arrayConverter);
		put(byte[].class, arrayConverter);
		put(short[].class, arrayConverter);
		put(int[].class, arrayConverter);
		put(float[].class, arrayConverter);
		put(double[].class, arrayConverter);
		put(long[].class, arrayConverter);

		put(Date[].class, dateArrayConverter);
		put(Calendar[].class, calendarArrayConverter);
		put(Number[].class, numberArrayConverter);
		put(Object[].class, arrayConverter);

		put(Collection.class, collectionConverter);
		put(Iterator.class, iteratorConverter);
		put(Enumeration.class, enumerationConverter);
	}

}
