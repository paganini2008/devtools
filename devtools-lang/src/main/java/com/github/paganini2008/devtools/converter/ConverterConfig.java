package com.github.paganini2008.devtools.converter;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.date.LocalDateUtils;

/**
 * 
 * ConverterConfig
 * 
 * @author Fred Feng
 * @version 1.0
 */
public final class ConverterConfig {

	private String delimiter = ",";
	private DecimalFormat decimalFormatter = new DecimalFormat("0.##");
	private DateFormat dateFormat = DateUtils.DEFAULT_DATE_FORMATTER;
	private DateTimeFormatter dateFormatter = LocalDateUtils.DEFAULT_DATE_FORMATTER;
	private DateTimeFormatter timeFormatter = LocalDateUtils.DEFAULT_TIME_FORMATTER;
	private DateTimeFormatter dateTimeFormatter = LocalDateUtils.DEFAULT_DATETIME_FORMATTER;
	private Charset stringCharset = CharsetUtils.UTF_8;
	private String datePattern = DateUtils.DEFAULT_DATE_PATTERN;
	private TimeZone timeZone = TimeZone.getDefault();
	private ZoneId zoneId = ZoneId.systemDefault();

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public DecimalFormat getDecimalFormatter() {
		return decimalFormatter;
	}

	public void setDecimalFormatter(DecimalFormat decimalFormatter) {
		this.decimalFormatter = decimalFormatter;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public DateTimeFormatter getDateFormatter() {
		return dateFormatter;
	}

	public void setDateFormatter(DateTimeFormatter dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

	public DateTimeFormatter getTimeFormatter() {
		return timeFormatter;
	}

	public void setTimeFormatter(DateTimeFormatter timeFormatter) {
		this.timeFormatter = timeFormatter;
	}

	public DateTimeFormatter getDateTimeFormatter() {
		return dateTimeFormatter;
	}

	public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}

	public Charset getStringCharset() {
		return stringCharset;
	}

	public void setStringCharset(Charset stringCharset) {
		this.stringCharset = stringCharset;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public ZoneId getZoneId() {
		return zoneId;
	}

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

}
