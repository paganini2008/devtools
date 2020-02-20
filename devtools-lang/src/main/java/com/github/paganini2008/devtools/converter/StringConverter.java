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
		public String convertValue(UUID source, String defaultValue) {
			return source != null ? source.toString() : defaultValue;
		}
	};

	private final Converter<Boolean, String> booleanConverter = new Converter<Boolean, String>() {
		public String convertValue(Boolean source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Booleans.toString(source);
		}
	};

	private final Converter<Character, String> characterConverter = new Converter<Character, String>() {
		public String convertValue(Character source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.toString();
		}
	};

	private final Converter<Number, String> numberConverter = new Converter<Number, String>() {
		public String convertValue(Number source, String defaultValue) {
			return NumberUtils.format(source, config.getDecimalFormatter(), defaultValue);
		}
	};

	private final Converter<Date, String> dateConverter = new Converter<Date, String>() {
		public String convertValue(Date source, String defaultValue) {
			return DateUtils.format(source, config.getDateFormat(), defaultValue);
		}
	};

	private final Converter<Calendar, String> calendarConverter = new Converter<Calendar, String>() {
		public String convertValue(Calendar source, String defaultValue) {
			return CalendarUtils.format(source, config.getDateFormat(), defaultValue);
		}
	};

	private final Converter<URL, String> urlConverter = new Converter<URL, String>() {
		public String convertValue(URL source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			InputStream in = null;
			try {
				in = UrlUtils.openStream(source);
				return IOUtils.toString(in, config.getStringCharset());
			} catch (IOException e) {
				return defaultValue;
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	};

	private final Converter<Clob, String> clobConverter = new Converter<Clob, String>() {
		public String convertValue(Clob source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			Reader reader = null;
			try {
				reader = source.getCharacterStream();
				return IOUtils.toString(reader);
			} catch (Exception e) {
				return defaultValue;
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
	};

	private final Converter<InputStream, String> inputStreamConverter = new Converter<InputStream, String>() {
		public String convertValue(InputStream source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toString(source, config.getStringCharset());
			} catch (IOException e) {
				return defaultValue;
			} finally {
				IOUtils.closeQuietly(source);
			}
		}
	};

	private final Converter<Reader, String> readerConverter = new Converter<Reader, String>() {
		public String convertValue(Reader source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toString(source);
			} catch (IOException e) {
				return defaultValue;
			} finally {
				IOUtils.closeQuietly(source);
			}
		}
	};

	private final Converter<CharSequence, String> charSequenceConverter = new Converter<CharSequence, String>() {
		public String convertValue(CharSequence source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.toString();
		}
	};

	private final Converter<byte[], String> byteArrayConverter = new Converter<byte[], String>() {
		public String convertValue(byte[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return new String(source, config.getStringCharset());
		}
	};

	private final Converter<char[], String> charArrayConverter = new Converter<char[], String>() {
		public String convertValue(char[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return new String(source);
		}
	};

	private final Converter<short[], String> shortArrayConverter = new Converter<short[], String>() {
		public String convertValue(short[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.join(source, config.getDelimiter());
		}
	};

	private final Converter<int[], String> intArrayConverter = new Converter<int[], String>() {
		public String convertValue(int[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.join(source, config.getDelimiter());
		}
	};

	private final Converter<long[], String> longArrayConverter = new Converter<long[], String>() {
		public String convertValue(long[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.join(source, config.getDelimiter());
		}
	};

	private final Converter<float[], String> floatArrayConverter = new Converter<float[], String>() {
		public String convertValue(float[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.join(source, config.getDelimiter());
		}
	};

	private final Converter<double[], String> doubleArrayConverter = new Converter<double[], String>() {
		public String convertValue(double[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.join(source, config.getDelimiter());
		}
	};

	private final Converter<boolean[], String> booleanArrayConverter = new Converter<boolean[], String>() {
		public String convertValue(boolean[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Booleans.join(source, config.getDelimiter());
		}
	};

	private final Converter<Object[], String> objectArrayConverter = new Converter<Object[], String>() {
		public String convertValue(Object[] source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return ArrayUtils.join(source, config.getDelimiter());
		}
	};

	private final Converter<Collection<?>, String> collectionConverter = new Converter<Collection<?>, String>() {
		public String convertValue(Collection<?> source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return CollectionUtils.join(source, config.getDelimiter());
		}
	};

	private final Converter<Iterator<?>, String> iteratorConverter = new Converter<Iterator<?>, String>() {
		public String convertValue(Iterator<?> source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return CollectionUtils.join(source, config.getDelimiter());
		}
	};

	private final Converter<Enumeration<?>, String> enumerationConverter = new Converter<Enumeration<?>, String>() {
		public String convertValue(Enumeration<?> source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return CollectionUtils.join(source, config.getDelimiter());
		}
	};

	private final Converter<Map, String> mapConverter = new Converter<Map, String>() {
		public String convertValue(Map source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return MapUtils.join(source, config.getDelimiter());
		}
	};

	private final Converter<Charset, String> charsetConverter = new Converter<Charset, String>() {
		public String convertValue(Charset source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.name();
		}
	};

	private final Converter<Locale, String> localeConverter = new Converter<Locale, String>() {
		public String convertValue(Locale source, String defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.toString();
		}
	};

	public StringConverter() {

		registerType(Boolean.class, booleanConverter);
		registerType(Character.class, characterConverter);
		registerType(Number.class, numberConverter);
		registerType(CharSequence.class, charSequenceConverter);

		registerType(Date.class, dateConverter);
		registerType(Calendar.class, calendarConverter);

		registerType(boolean[].class, booleanArrayConverter);
		registerType(char[].class, charArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(short[].class, shortArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(float[].class, floatArrayConverter);
		registerType(double[].class, doubleArrayConverter);
		registerType(long[].class, longArrayConverter);
		registerType(Object[].class, objectArrayConverter);

		registerType(Collection.class, collectionConverter);
		registerType(Iterator.class, iteratorConverter);
		registerType(Enumeration.class, enumerationConverter);
		registerType(Map.class, mapConverter);

		registerType(URL.class, urlConverter);
		registerType(Clob.class, clobConverter);
		registerType(Reader.class, readerConverter);
		registerType(InputStream.class, inputStreamConverter);

		registerType(Charset.class, charsetConverter);
		registerType(Locale.class, localeConverter);
	}

}
