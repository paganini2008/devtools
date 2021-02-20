package com.github.paganini2008.devtools.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Blob;

import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.io.IOUtils;
import com.github.paganini2008.devtools.net.Urls;
import com.github.paganini2008.devtools.primitives.Bytes;

/**
 * ByteArrayConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ByteArrayConverter extends BasicConverter<byte[]> {

	private final Converter<CharSequence, byte[]> charSequenceConverter = new Converter<CharSequence, byte[]>() {
		public byte[] convertValue(CharSequence source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.toString().getBytes();
		}
	};

	private final Converter<short[], byte[]> shortArrayConverter = new Converter<short[], byte[]>() {
		public byte[] convertValue(short[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<int[], byte[]> intArrayConverter = new Converter<int[], byte[]>() {
		public byte[] convertValue(int[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<long[], byte[]> longArrayConverter = new Converter<long[], byte[]>() {
		public byte[] convertValue(long[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<float[], byte[]> floatArrayConverter = new Converter<float[], byte[]>() {
		public byte[] convertValue(float[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<double[], byte[]> doubleArrayConverter = new Converter<double[], byte[]>() {
		public byte[] convertValue(double[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<char[], byte[]> charArrayConverter = new Converter<char[], byte[]>() {
		public byte[] convertValue(char[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<boolean[], byte[]> booleanArrayConverter = new Converter<boolean[], byte[]>() {
		public byte[] convertValue(boolean[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<Number[], byte[]> numberArrayConverter = new Converter<Number[], byte[]>() {
		public byte[] convertValue(Number[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.casts(source);
		}
	};

	private final Converter<String[], byte[]> stringArrayConverter = new Converter<String[], byte[]>() {
		public byte[] convertValue(String[] source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.parseMany(source);
		}
	};

	private final Converter<ByteBuffer, byte[]> byteBufferConverter = new Converter<ByteBuffer, byte[]>() {
		public byte[] convertValue(ByteBuffer source, byte[] defaultValue) {
			return Bytes.toByteArray(source);
		}
	};
	private final Converter<URL, byte[]> urlConverter = new Converter<URL, byte[]>() {
		public byte[] convertValue(URL source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			InputStream in = null;
			try {
				in = Urls.openStream(source);
				return IOUtils.toByteArray(in);
			} catch (IOException e) {
				return defaultValue;
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	};

	private final Converter<Blob, byte[]> blobConverter = new Converter<Blob, byte[]>() {
		public byte[] convertValue(Blob source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			InputStream in = null;
			try {
				in = source.getBinaryStream();
				return IOUtils.toByteArray(in);
			} catch (Exception e) {
				return defaultValue;
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	};

	private final Converter<InputStream, byte[]> inputStreamConverter = new Converter<InputStream, byte[]>() {
		public byte[] convertValue(InputStream source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toByteArray(source);
			} catch (IOException e) {
				return defaultValue;
			} finally {
				IOUtils.closeQuietly(source);
			}
		}
	};

	private final Converter<Reader, byte[]> readerConverter = new Converter<Reader, byte[]>() {
		public byte[] convertValue(Reader source, byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toByteArray(source, charset);
			} catch (IOException e) {
				return defaultValue;
			} finally {
				IOUtils.closeQuietly(source);
			}
		}
	};

	public ByteArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
		registerType(InputStream.class, inputStreamConverter);
		registerType(Reader.class, readerConverter);
		registerType(URL.class, urlConverter);
		registerType(Blob.class, blobConverter);
		registerType(char[].class, charArrayConverter);
		registerType(boolean[].class, booleanArrayConverter);
		registerType(short[].class, shortArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(long[].class, longArrayConverter);
		registerType(float[].class, floatArrayConverter);
		registerType(double[].class, doubleArrayConverter);
		registerType(ByteBuffer.class, byteBufferConverter);
	}

	private Charset charset = CharsetUtils.DEFAULT;

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

}
