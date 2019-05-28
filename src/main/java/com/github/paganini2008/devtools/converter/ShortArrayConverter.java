package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.Shorts;
import com.github.paganini2008.devtools.StringUtils;

/**
 * ShortArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ShortArrayConverter extends BaseConverter<short[]> {

	private final Converter<CharSequence, short[]> charSequenceConverter = new Converter<CharSequence, short[]>() {
		public short[] getValue(CharSequence source, short[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> results = StringUtils.split(source, config.getDelimiter());
			return Shorts.parses(results.toArray(new String[results.size()]));
		}
	};

	private final Converter<String[], short[]> stringArrayConverter = new Converter<String[], short[]>() {
		public short[] getValue(String[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.parses(source);
		}
	};

	private final Converter<Number[], short[]> numberArrayConverter = new Converter<Number[], short[]>() {
		public short[] getValue(Number[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<char[], short[]> charArrayConverter = new Converter<char[], short[]>() {
		public short[] getValue(char[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<boolean[], short[]> booleanArrayConverter = new Converter<boolean[], short[]>() {
		public short[] getValue(boolean[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<byte[], short[]> byteArrayConverter = new Converter<byte[], short[]>() {
		public short[] getValue(byte[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<int[], short[]> intArrayConverter = new Converter<int[], short[]>() {
		public short[] getValue(int[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<long[], short[]> longArrayConverter = new Converter<long[], short[]>() {
		public short[] getValue(long[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<float[], short[]> floatArrayConverter = new Converter<float[], short[]>() {
		public short[] getValue(float[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	private final Converter<double[], short[]> doubleArrayConverter = new Converter<double[], short[]>() {
		public short[] getValue(double[] source, short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.casts(source);
		}
	};

	public ShortArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Number[].class, numberArrayConverter);
		put(String[].class, stringArrayConverter);
		put(char[].class, charArrayConverter);
		put(boolean[].class, booleanArrayConverter);
		put(byte[].class, byteArrayConverter);
		put(int[].class, intArrayConverter);
		put(long[].class, longArrayConverter);
		put(float[].class, floatArrayConverter);
		put(double[].class, doubleArrayConverter);
	}

}
