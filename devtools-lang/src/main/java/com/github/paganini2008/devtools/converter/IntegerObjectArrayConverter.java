package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Ints;

/**
 * IntegerObjectArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class IntegerObjectArrayConverter extends BasicConverter<Integer[]> {

	private final Converter<CharSequence, Integer[]> charSequenceConverter = new Converter<CharSequence, Integer[]>() {
		public Integer[] getValue(CharSequence source, Integer[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, config.getDelimiter());
			return result != null ? Ints.valuesOf(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<Number[], Integer[]> numberArrayConverter = new Converter<Number[], Integer[]>() {
		public Integer[] getValue(Number[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.valuesOf(source);
		}
	};

	private final Converter<String[], Integer[]> stringArrayConverter = new Converter<String[], Integer[]>() {
		public Integer[] getValue(String[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.valuesOf(source);
		}
	};

	private final Converter<byte[], Integer[]> byteArrayConverter = new Converter<byte[], Integer[]>() {
		public Integer[] getValue(byte[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.toWrappers(source);
		}
	};

	private final Converter<short[], Integer[]> shortArrayConverter = new Converter<short[], Integer[]>() {
		public Integer[] getValue(short[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.toWrappers(source);
		}
	};

	private final Converter<char[], Integer[]> charArrayConverter = new Converter<char[], Integer[]>() {
		public Integer[] getValue(char[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.toWrappers(source);
		}
	};

	private final Converter<int[], Integer[]> intArrayConverter = new Converter<int[], Integer[]>() {
		public Integer[] getValue(int[] source, Integer[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.toWrappers(source);
		}
	};

	public IntegerObjectArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Number[].class, numberArrayConverter);
		put(String[].class, stringArrayConverter);
		put(byte[].class, byteArrayConverter);
		put(char[].class, shortArrayConverter);
		put(short[].class, charArrayConverter);
		put(int[].class, intArrayConverter);
	}

}
