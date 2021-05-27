package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * ShortObjectArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ShortObjectArrayConverter extends BasicConverter<Short[]> {

	private final Converter<CharSequence, Short[]> charSequenceConverter = new Converter<CharSequence, Short[]>() {
		public Short[] convertValue(CharSequence source, Short[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, ",");
			return result != null ? Shorts.valueOf(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<Number[], Short[]> numberArrayConverter = new Converter<Number[], Short[]>() {
		public Short[] convertValue(Number[] source, Short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.valueOf(source);
		}
	};

	private final Converter<String[], Short[]> stringArrayConverter = new Converter<String[], Short[]>() {
		public Short[] convertValue(String[] source, Short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.valueOf(source);
		}
	};

	private final Converter<byte[], Short[]> byteArrayConverter = new Converter<byte[], Short[]>() {
		public Short[] convertValue(byte[] source, Short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.toWrappers(source);
		}
	};

	private final Converter<short[], Short[]> shortArrayConverter = new Converter<short[], Short[]>() {
		public Short[] convertValue(short[] source, Short[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Shorts.toWrappers(source);
		}
	};

	public ShortObjectArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(short[].class, shortArrayConverter);
	}

}
