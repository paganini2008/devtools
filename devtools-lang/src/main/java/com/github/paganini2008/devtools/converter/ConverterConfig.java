package com.github.paganini2008.devtools.converter;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.github.paganini2008.devtools.CharsetUtils;

/**
 * 
 * ConverterConfig
 * 
 * @author Fred Feng
 * @version 1.0
 */
public final class ConverterConfig {

	private String delimiter = ",";
	private DecimalFormat decimalFormat = new DecimalFormat("0.##");
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Charset charset = CharsetUtils.UTF_8;
	private String[] datePatterns = new String[] { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss", "yyyyMMddHHmmss", "yyyyMMdd",
			"HHmmss" };

	public String[] getDatePatterns() {
		return datePatterns;
	}

	public void setDatePatterns(String[] datePatterns) {
		this.datePatterns = datePatterns;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setDecimalFormat(DecimalFormat decimalFormat) {
		this.decimalFormat = decimalFormat;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public Charset getCharset() {
		return charset;
	}

}
