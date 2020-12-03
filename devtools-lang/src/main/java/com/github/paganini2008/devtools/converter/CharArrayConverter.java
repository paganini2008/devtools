package com.github.paganini2008.devtools.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.io.IOUtils;

/**
 * CharArrayConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CharArrayConverter extends BasicConverter<char[]> {

	private final Converter<CharSequence, char[]> charSequenceConverter = new Converter<CharSequence, char[]>() {
		public char[] convertValue(CharSequence source, char[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.toString().toCharArray();
		}
	};

	private final Converter<InputStream, char[]> inputStreamConverter = new Converter<InputStream, char[]>() {
		public char[] convertValue(InputStream source, char[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toCharArray(source, charset);
			} catch (IOException e) {
				return defaultValue;
			} finally {
				IOUtils.closeQuietly(source);
			}
		}
	};

	private final Converter<Reader, char[]> readerConverter = new Converter<Reader, char[]>() {
		public char[] convertValue(Reader source, char[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			try {
				return IOUtils.toCharArray(source);
			} catch (IOException e) {
				return defaultValue;
			} finally {
				IOUtils.closeQuietly(source);
			}
		}
	};

	public CharArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(InputStream.class, inputStreamConverter);
		registerType(Reader.class, readerConverter);
	}

	private Charset charset = CharsetUtils.DEFAULT;

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

}
