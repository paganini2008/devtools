package com.github.paganini2008.devtools.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.NumberUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.date.CalendarUtils;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.io.IOUtils;
import com.github.paganini2008.devtools.net.UrlUtils;
import com.github.paganini2008.devtools.primitives.Booleans;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * StringConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class StringConverter extends BasicConverter<String> {

	private final Converter<UUID, String> uuidConverter = new Converter<UUID, String>() {
		public String getValue(UUID source, String defaultValue) {
			return source != null ? source.toString() : defaultValue;
		}
	};

	private final Converter<Boolean, String> booleanConverter = new Converter<Boolean, String>() {
		public String getValue(Boolean source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Booleans.toString(source);
		}
	};

	private final Converter<Character, String> characterConverter = new Converter<Character, String>() {
		public String getValue(Character source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.toString();
		}
	};

	private final Converter<Number, String> numberConverter = new Converter<Number, String>() {
		public String getValue(Number source, String defaultValue) {
			return NumberUtils.format(source, config.getDecimalFormatter(), defaultValue);
		}
	};

	private final Converter<Date, String> dateConverter = new Converter<Date, String>() {
		public String getValue(Date source, String defaultValue) {
			return DateUtils.format(source, config.getDateFormat(), defaultValue);
		}
	};

	private final Converter<Calendar, String> calendarConverter = new Converter<Calendar, String>() {
		public String getValue(Calendar source, String defaultValue) {
			return CalendarUtils.format(source, config.getDateFormat(), defaultValue);
		}
	};

	private final Converter<URL, String> urlConverter = new Converter<URL, String>() {
		public String getValue(URL source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return UrlUtils.toString(source, config.getCharset());
			} catch (IOException e) {
				return defaultValue;
			}
		}
	};

	private final Converter<Clob, String> clobConverter = new Converter<Clob, String>() {
		public String getValue(Clob source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toString(source.getCharacterStream());
			} catch (Exception e) {
				return defaultValue;
			}
		}
	};

	private final Converter<InputStream, String> inputStreamConverter = new Converter<InputStream, String>() {
		public String getValue(InputStream source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toString(source, config.getCharset());
			} catch (IOException e) {
				return defaultValue;
			}
		}
	};

	private final Converter<Reader, String> readerConverter = new Converter<Reader, String>() {
		public String getValue(Reader source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toString(source);
			} catch (IOException e) {
				return defaultValue;
			}
		}
	};

	private final Converter<CharSequence, String> charSequenceConverter = new Converter<CharSequence, String>() {
		public String getValue(CharSequence source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.toString();
		}
	};

	private final Converter<byte[], String> byteArrayConverter = new Converter<byte[], String>() {
		public String getValue(byte[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return new String(source, config.getCharset());
		}
	};

	private final Converter<char[], String> charArrayConverter = new Converter<char[], String>() {
		public String getValue(char[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return new String(source);
		}
	};

	private final Converter<short[], String> shortArrayConverter = new Converter<short[], String>() {
		public String getValue(short[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.join(source, config.getDelimiter());
		}
	};

	private final Converter<int[], String> intArrayConverter = new Converter<int[], String>() {
		public String getValue(int[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.join(source, config.getDelimiter());
		}
	};

	private final Converter<long[], String> longArrayConverter = new Converter<long[], String>() {
		public String getValue(long[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.join(source, config.getDelimiter());
		}
	};

	private final Converter<float[], String> floatArrayConverter = new Converter<float[], String>() {
		public String getValue(float[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.join(source, config.getDelimiter());
		}
	};

	private final Converter<double[], String> doubleArrayConverter = new Converter<double[], String>() {
		public String getValue(double[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.join(source);
		}
	};

	private final Converter<boolean[], String> booleanArrayConverter = new Converter<boolean[], String>() {
		public String getValue(boolean[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Booleans.join(source, config.getDelimiter());
		}
	};

	private final Converter<Object[], String> objectArrayConverter = new Converter<Object[], String>() {
		public String getValue(Object[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return ArrayUtils.join(source, config.getDelimiter());
		}
	};

	private final Converter<Collection<?>, String> collectionConverter = new Converter<Collection<?>, String>() {
		public String getValue(Collection<?> source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return CollectionUtils.join(source, config.getDelimiter());
		}
	};

	private final Converter<Iterator<?>, String> iteratorConverter = new Converter<Iterator<?>, String>() {
		public String getValue(Iterator<?> source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return CollectionUtils.join(source, config.getDelimiter());
		}
	};

	private final Converter<Enumeration<?>, String> enumerationConverter = new Converter<Enumeration<?>, String>() {
		public String getValue(Enumeration<?> source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return CollectionUtils.join(source, config.getDelimiter());
		}
	};

	private final Converter<Map, String> mapConverter = new Converter<Map, String>() {
		public String getValue(Map source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return MapUtils.join(source, config.getDelimiter());
		}
	};

	private final Converter<Charset, String> charsetConverter = new Converter<Charset, String>() {
		public String getValue(Charset source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.name();
		}
	};

	private final Converter<Locale, String> localeConverter = new Converter<Locale, String>() {
		public String getValue(Locale source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.toString();
		}
	};

	public StringConverter() {

		put(Boolean.class, booleanConverter);
		put(Character.class, characterConverter);
		put(Number.class, numberConverter);
		put(CharSequence.class, charSequenceConverter);

		put(Date.class, dateConverter);
		put(Calendar.class, calendarConverter);

		put(boolean[].class, booleanArrayConverter);
		put(char[].class, charArrayConverter);
		put(byte[].class, byteArrayConverter);
		put(short[].class, shortArrayConverter);
		put(int[].class, intArrayConverter);
		put(float[].class, floatArrayConverter);
		put(double[].class, doubleArrayConverter);
		put(long[].class, longArrayConverter);
		put(Object[].class, objectArrayConverter);

		put(Collection.class, collectionConverter);
		put(Iterator.class, iteratorConverter);
		put(Enumeration.class, enumerationConverter);
		put(Map.class, mapConverter);

		put(URL.class, urlConverter);
		put(Clob.class, clobConverter);
		put(Reader.class, readerConverter);
		put(InputStream.class, inputStreamConverter);

		put(Charset.class, charsetConverter);
		put(Locale.class, localeConverter);
	}

}
