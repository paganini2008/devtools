package com.github.paganini2008.devtools.converter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.date.CalendarUtils;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.primitives.Longs;

/**
 * LongObjectArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class LongObjectArrayConverter extends BasicConverter<Long[]> {

	private final Converter<CharSequence, Long[]> charSequenceConverter = new Converter<CharSequence, Long[]>() {
		public Long[] convertValue(CharSequence source, Long[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, config.getDelimiter());
			return Longs.valueOf(result.toArray(new String[result.size()]));
		}
	};

	private final Converter<Character[], Long[]> characterObjectArrayConverter = new Converter<Character[], Long[]>() {
		public Long[] convertValue(Character[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.valueOf(source);
		}
	};

	private final Converter<Boolean[], Long[]> booleanObjectArrayConverter = new Converter<Boolean[], Long[]>() {
		public Long[] convertValue(Boolean[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.valueOf(source);
		}
	};

	private final Converter<Number[], Long[]> numberArrayConverter = new Converter<Number[], Long[]>() {
		public Long[] convertValue(Number[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.valueOf(source);
		}
	};

	private final Converter<boolean[], Long[]> booleanArrayConverter = new Converter<boolean[], Long[]>() {
		public Long[] convertValue(boolean[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<byte[], Long[]> byteArrayConverter = new Converter<byte[], Long[]>() {
		public Long[] convertValue(byte[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<short[], Long[]> shortArrayConverter = new Converter<short[], Long[]>() {
		public Long[] convertValue(short[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<char[], Long[]> charArrayConverter = new Converter<char[], Long[]>() {
		public Long[] convertValue(char[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<int[], Long[]> intArrayConverter = new Converter<int[], Long[]>() {
		public Long[] convertValue(int[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<long[], Long[]> longArrayConverter = new Converter<long[], Long[]>() {
		public Long[] convertValue(long[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<String[], Long[]> stringConverter = new Converter<String[], Long[]>() {
		public Long[] convertValue(String[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.valueOf(source);
		}
	};

	private final Converter<Date[], Long[]> dateConverter = new Converter<Date[], Long[]>() {
		public Long[] convertValue(Date[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return DateUtils.getTimeInMillis(source);
		}
	};

	private final Converter<Calendar[], Long[]> calendarConverter = new Converter<Calendar[], Long[]>() {
		public Long[] convertValue(Calendar[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return CalendarUtils.getTimeInMillis(source);
		}
	};

	public LongObjectArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Boolean[].class, booleanObjectArrayConverter);
		registerType(Character[].class, characterObjectArrayConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(Calendar.class, calendarConverter);
		registerType(char[].class, charArrayConverter);
		registerType(boolean[].class, booleanArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(short[].class, shortArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(long[].class, longArrayConverter);
	}

}
