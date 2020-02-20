package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Ints;

public class IntArrayConverter extends BasicConverter<int[]> {

	private final Converter<CharSequence, int[]> charSequenceConverter = new Converter<CharSequence, int[]>() {
		public int[] getValue(CharSequence source, int[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, ",");
			return result != null ? Ints.parses(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<String[], int[]> stringArrayConverter = new Converter<String[], int[]>() {
		public int[] getValue(String[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.parses(source);
		}
	};

	private final Converter<Number[], int[]> numberArrayConverter = new Converter<Number[], int[]>() {
		public int[] getValue(Number[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<char[], int[]> nativeCharArrayConverter = new Converter<char[], int[]>() {
		public int[] getValue(char[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<boolean[], int[]> nativeBooleanArrayConverter = new Converter<boolean[], int[]>() {
		public int[] getValue(boolean[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<byte[], int[]> nativeByteArrayConverter = new Converter<byte[], int[]>() {
		public int[] getValue(byte[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<short[], int[]> nativeShortArrayConverter = new Converter<short[], int[]>() {
		public int[] getValue(short[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<long[], int[]> nativeLongArrayConverter = new Converter<long[], int[]>() {
		public int[] getValue(long[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<float[], int[]> nativeFloatArrayConverter = new Converter<float[], int[]>() {
		public int[] getValue(float[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	private final Converter<double[], int[]> nativeDoubleArrayConverter = new Converter<double[], int[]>() {
		public int[] getValue(double[] source, int[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Ints.casts(source);
		}
	};

	public IntArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Number[].class, numberArrayConverter);
		put(String[].class, stringArrayConverter);
		put(char[].class, nativeCharArrayConverter);
		put(boolean[].class, nativeBooleanArrayConverter);
		put(byte[].class, nativeByteArrayConverter);
		put(int[].class, nativeShortArrayConverter);
		put(long[].class, nativeLongArrayConverter);
		put(float[].class, nativeFloatArrayConverter);
		put(double[].class, nativeDoubleArrayConverter);
	}

}
