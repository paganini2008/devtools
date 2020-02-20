package com.github.paganini2008.devtools.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Blob;

import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.io.IOUtils;
import com.github.paganini2008.devtools.net.UrlUtils;
import com.github.paganini2008.devtools.primitives.Bytes;

/**
 * ByteArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ByteArrayConverter extends BasicConverter<byte[]> {

	private final Converter<CharSequence, byte[]> charSequenceConverter = new Converter<CharSequence, byte[]>() {
		public byte[] getValue(CharSequence source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.toString().getBytes();
		}
	};

	private final Converter<short[], byte[]> nativeShortArrayConverter = new Converter<short[], byte[]>() {
		public byte[] getValue(short[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<int[], byte[]> nativeIntArrayConverter = new Converter<int[], byte[]>() {
		public byte[] getValue(int[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<long[], byte[]> nativeLongArrayConverter = new Converter<long[], byte[]>() {
		public byte[] getValue(long[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<float[], byte[]> nativeFloatArrayConverter = new Converter<float[], byte[]>() {
		public byte[] getValue(float[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<double[], byte[]> nativeDoubleArrayConverter = new Converter<double[], byte[]>() {
		public byte[] getValue(double[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<char[], byte[]> nativeCharArrayConverter = new Converter<char[], byte[]>() {
		public byte[] getValue(char[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<boolean[], byte[]> nativeBooleanArrayConverter = new Converter<boolean[], byte[]>() {
		public byte[] getValue(boolean[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<Number[], byte[]> numberArrayConverter = new Converter<Number[], byte[]>() {
		public byte[] getValue(Number[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<String[], byte[]> stringArrayConverter = new Converter<String[], byte[]>() {
		public byte[] getValue(String[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.parses(source);
		}
	};

	private final Converter<URL, byte[]> urlConverter = new Converter<URL, byte[]>() {
		public byte[] getValue(URL source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toByteArray(UrlUtils.openStream(source));
			} catch (IOException e) {
				return defaultValue;
			}
		}
	};

	private final Converter<Blob, byte[]> blobConverter = new Converter<Blob, byte[]>() {
		public byte[] getValue(Blob source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toByteArray(source.getBinaryStream());
			} catch (Exception e) {
				return defaultValue;
			}
		}
	};

	private final Converter<InputStream, byte[]> inputStreamConverter = new Converter<InputStream, byte[]>() {
		public byte[] getValue(InputStream source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toByteArray(source);
			} catch (IOException e) {
				return defaultValue;
			}
		}
	};

	private final Converter<Reader, byte[]> readerConverter = new Converter<Reader, byte[]>() {
		public byte[] getValue(Reader source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toByteArray(source, charset);
			} catch (IOException e) {
				return defaultValue;
			}

		}
	};

	private Charset charset = CharsetUtils.UTF_8;

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public ByteArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Number[].class, numberArrayConverter);
		put(String[].class, stringArrayConverter);
		put(InputStream.class, inputStreamConverter);
		put(Reader.class, readerConverter);
		put(URL.class, urlConverter);
		put(Blob.class, blobConverter);
		put(char[].class, nativeCharArrayConverter);
		put(boolean[].class, nativeBooleanArrayConverter);
		put(short[].class, nativeShortArrayConverter);
		put(int[].class, nativeIntArrayConverter);
		put(long[].class, nativeLongArrayConverter);
		put(float[].class, nativeFloatArrayConverter);
		put(double[].class, nativeDoubleArrayConverter);
	}

}
