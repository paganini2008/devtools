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
		public Long[] getValue(CharSequence source, Long[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, config.getDelimiter());
			return Longs.valuesOf(result.toArray(new String[result.size()]));
		}
	};

	private final Converter<Character[], Long[]> characterObjectArrayConverter = new Converter<Character[], Long[]>() {
		public Long[] getValue(Character[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.valuesOf(source);
		}
	};

	private final Converter<Boolean[], Long[]> booleanObjectArrayConverter = new Converter<Boolean[], Long[]>() {
		public Long[] getValue(Boolean[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.valuesOf(source);
		}
	};

	private final Converter<Number[], Long[]> numberArrayConverter = new Converter<Number[], Long[]>() {
		public Long[] getValue(Number[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.valuesOf(source);
		}
	};

	private final Converter<boolean[], Long[]> booleanArrayConverter = new Converter<boolean[], Long[]>() {
		public Long[] getValue(boolean[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<byte[], Long[]> byteArrayConverter = new Converter<byte[], Long[]>() {
		public Long[] getValue(byte[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<short[], Long[]> shortArrayConverter = new Converter<short[], Long[]>() {
		public Long[] getValue(short[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<char[], Long[]> charArrayConverter = new Converter<char[], Long[]>() {
		public Long[] getValue(char[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<int[], Long[]> intArrayConverter = new Converter<int[], Long[]>() {
		public Long[] getValue(int[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<long[], Long[]> longArrayConverter = new Converter<long[], Long[]>() {
		public Long[] getValue(long[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toWrappers(source);
		}
	};

	private final Converter<String[], Long[]> stringConverter = new Converter<String[], Long[]>() {
		public Long[] getValue(String[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.valuesOf(source);
		}
	};

	private final Converter<Date[], Long[]> dateConverter = new Converter<Date[], Long[]>() {
		public Long[] getValue(Date[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return DateUtils.getTimeInMillis(source);
		}
	};

	private final Converter<Calendar[], Long[]> calendarConverter = new Converter<Calendar[], Long[]>() {
		public Long[] getValue(Calendar[] source, Long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return CalendarUtils.getTimeInMillis(source);
		}
	};

	public LongObjectArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Boolean[].class, booleanObjectArrayConverter);
		put(Character[].class, characterObjectArrayConverter);
		put(Number[].class, numberArrayConverter);
		put(String[].class, stringConverter);
		put(Date.class, dateConverter);
		put(Calendar.class, calendarConverter);
		put(char[].class, charArrayConverter);
		put(boolean[].class, booleanArrayConverter);
		put(byte[].class, byteArrayConverter);
		put(short[].class, shortArrayConverter);
		put(int[].class, intArrayConverter);
		put(long[].class, longArrayConverter);
	}

}
