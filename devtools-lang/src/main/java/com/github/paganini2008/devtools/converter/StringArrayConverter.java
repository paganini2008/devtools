package com.github.paganini2008.devtools.converter;

import java.text.DateFormat;
import java.text.DecimalFormat;
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
import com.github.paganini2008.devtools.primitives.Booleans;
import com.github.paganini2008.devtools.primitives.Bytes;
import com.github.paganini2008.devtools.primitives.Chars;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * StringArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class StringArrayConverter extends BasicConverter<String[]> {

	private final Converter<CharSequence, String[]> charSequenceConverter = new Converter<CharSequence, String[]>() {
		public String[] convertValue(CharSequence source, String[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> results = StringUtils.split(source, delimiter);
			return results.toArray(new String[results.size()]);
		}
	};

	private final Converter<Date[], String[]> dateArrayConverter = new Converter<Date[], String[]>() {
		public String[] convertValue(Date[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return DateUtils.formatMany(source, dateFormat);
		}
	};

	private final Converter<Calendar[], String[]> calendarArrayConverter = new Converter<Calendar[], String[]>() {
		public String[] convertValue(Calendar[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return CalendarUtils.formatMany(source, dateFormat);
		}
	};

	private final Converter<Number[], String[]> numberArrayConverter = new Converter<Number[], String[]>() {
		public String[] convertValue(Number[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return NumberUtils.toStringArray(source, decimalFormat);
		}
	};

	private final Converter<Object[], String[]> objectArrayConverter = new Converter<Object[], String[]>() {
		public String[] convertValue(Object[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return ArrayUtils.toStringArray(source);
		}
	};

	private final Converter<double[], String[]> doubleArrayConverter = new Converter<double[], String[]>() {
		public String[] convertValue(double[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toStringArray(source, decimalFormat);
		}
	};

	private final Converter<float[], String[]> floatArrayConverter = new Converter<float[], String[]>() {
		public String[] convertValue(float[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toStringArray(source, decimalFormat);
		}
	};

	private final Converter<long[], String[]> longArrayConverter = new Converter<long[], String[]>() {
		public String[] convertValue(long[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.toStringArray(source, decimalFormat);
		}
	};

	private final Converter<int[], String[]> intArrayConverter = new Converter<int[], String[]>() {
		public String[] convertValue(int[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.toStringArray(source, decimalFormat);
		}
	};

	private final Converter<short[], String[]> shortArrayConverter = new Converter<short[], String[]>() {
		public String[] convertValue(short[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.toStringArray(source, decimalFormat);
		}
	};

	private final Converter<byte[], String[]> byteArrayConverter = new Converter<byte[], String[]>() {
		public String[] convertValue(byte[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.toStringArray(source, decimalFormat);
		}
	};

	private final Converter<char[], String[]> charArrayConverter = new Converter<char[], String[]>() {
		public String[] convertValue(char[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Chars.toStringArray(source);
		}
	};

	private final Converter<boolean[], String[]> booleanArrayConverter = new Converter<boolean[], String[]>() {
		public String[] convertValue(boolean[] source, String[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Booleans.toStringArray(source);
		}
	};

	private final Converter<Collection<?>, String[]> collectionConverter = new Converter<Collection<?>, String[]>() {
		public String[] convertValue(Collection<?> source, String[] defaultValue) {
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
		public String[] convertValue(Iterator<?> source, String[] defaultValue) {
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
		public String[] convertValue(Enumeration<?> source, String[] defaultValue) {
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

	public StringArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);

		registerType(boolean[].class, booleanArrayConverter);
		registerType(char[].class, charArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(short[].class, shortArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(float[].class, floatArrayConverter);
		registerType(double[].class, doubleArrayConverter);
		registerType(long[].class, longArrayConverter);

		registerType(Date[].class, dateArrayConverter);
		registerType(Calendar[].class, calendarArrayConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(Object[].class, objectArrayConverter);

		registerType(Collection.class, collectionConverter);
		registerType(Iterator.class, iteratorConverter);
		registerType(Enumeration.class, enumerationConverter);
	}

	private DecimalFormat decimalFormat = new DecimalFormat("0.##");
	private DateFormat dateFormat = DateUtils.DEFAULT_DATE_FORMATTER;
	private String delimiter = ",";

	public void setDecimalFormat(DecimalFormat decimalFormat) {
		this.decimalFormat = decimalFormat;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
