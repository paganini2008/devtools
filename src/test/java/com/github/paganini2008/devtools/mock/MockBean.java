/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * MockBean, a bean with all kinds of mockable properties
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MockBean {

	public enum Style {
		A, B, C
	}

	public static class Inner {

		@Example(length = 4, digit = true, lowerCaseLetter = false, upperCaseLetter = false)
		private String code;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}

	@IntRange(from = 10, to = 20)
	private int intValue;
	@LongRange(from = 1, to = 5)
	private Long longValue;
	@FloatRange(from = 1, to = 2, scale = 2)
	private float floatValue;
	@FloatRange(precision = 3, scale = 1)
	private Float floatPrecision;
	@DoubleRange(from = 5, to = 6, scale = 3)
	private double doubleValue;
	@DoubleRange(precision = 2, scale = 2)
	private Double doublePrecision;
	@LongRange(from = 0, to = 10)
	private BigInteger bigInteger;
	@DoubleRange(from = 50, to = 200, scale = 2)
	private BigDecimal bigDecimal;
	@DoubleRange(precision = 3, scale = 1)
	private BigDecimal bigDecimalPrecision;
	@NumberRange(from = 1, to = 100, scale = 1)
	private BigDecimal numberDecimal;
	@NumberRange(from = 1, to = 100)
	private BigInteger numberInteger;
	@NumberRange(precision = 4)
	private BigInteger numberIntegerPrecision;
	@NumberRange(precision = 3, scale = 2)
	private BigDecimal numberDecimalPrecision;

	@Example(length = 8, prefix = "P_", suffix = "_S", digit = true, lowerCaseLetter = false, upperCaseLetter = false)
	private String code;
	@Example(style = "ab", length = 5)
	private String styled;
	@Example("username")
	private String name;
	@Example
	private String nick;
	private String plain;

	@DateRange(from = "2020-01-01", to = "2020-12-31")
	private Date date;
	@TimeRange(from = "10:00:00", to = "11:00:00")
	private Date time;
	@DateRange(from = "2020-01-01", to = "2020-01-31")
	@TimeRange(from = "08:00:00", to = "09:00:00")
	private Date dateTime;
	private Date plainDate;

	@DateRange(from = "2020-01-01", to = "2020-01-10")
	private LocalDate localDate;
	@DateRange(from = "2020-01-01")
	private LocalDate openLocalDate;
	private LocalDate plainLocalDate;

	@TimeRange(from = "10:00:00", to = "10:30:00")
	private LocalTime localTime;
	private LocalTime plainLocalTime;

	@DateRange(from = "2020-01-01", to = "2020-01-10")
	@TimeRange(from = "08:00:00", to = "09:00:00")
	private LocalDateTime localDateTime;
	@TimeRange(from = "08:00:00", to = "09:00:00")
	private LocalDateTime localDateTimeOnlyTime;
	@DateRange(from = "2020-01-01", to = "2020-01-10")
	private LocalDateTime localDateTimeOnlyDate;
	private LocalDateTime plainLocalDateTime;

	private boolean flag;
	private Boolean flag2;
	private char ch;
	private byte b;
	private Short sh;
	private Style style;
	@Recur
	private Inner inner;
	private Object other;

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public Long getLongValue() {
		return longValue;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public float getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}

	public Float getFloatPrecision() {
		return floatPrecision;
	}

	public void setFloatPrecision(Float floatPrecision) {
		this.floatPrecision = floatPrecision;
	}

	public double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public Double getDoublePrecision() {
		return doublePrecision;
	}

	public void setDoublePrecision(Double doublePrecision) {
		this.doublePrecision = doublePrecision;
	}

	public BigInteger getBigInteger() {
		return bigInteger;
	}

	public void setBigInteger(BigInteger bigInteger) {
		this.bigInteger = bigInteger;
	}

	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}

	public void setBigDecimal(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}

	public BigDecimal getBigDecimalPrecision() {
		return bigDecimalPrecision;
	}

	public void setBigDecimalPrecision(BigDecimal bigDecimalPrecision) {
		this.bigDecimalPrecision = bigDecimalPrecision;
	}

	public BigDecimal getNumberDecimal() {
		return numberDecimal;
	}

	public void setNumberDecimal(BigDecimal numberDecimal) {
		this.numberDecimal = numberDecimal;
	}

	public BigInteger getNumberInteger() {
		return numberInteger;
	}

	public void setNumberInteger(BigInteger numberInteger) {
		this.numberInteger = numberInteger;
	}

	public BigInteger getNumberIntegerPrecision() {
		return numberIntegerPrecision;
	}

	public void setNumberIntegerPrecision(BigInteger numberIntegerPrecision) {
		this.numberIntegerPrecision = numberIntegerPrecision;
	}

	public BigDecimal getNumberDecimalPrecision() {
		return numberDecimalPrecision;
	}

	public void setNumberDecimalPrecision(BigDecimal numberDecimalPrecision) {
		this.numberDecimalPrecision = numberDecimalPrecision;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStyled() {
		return styled;
	}

	public void setStyled(String styled) {
		this.styled = styled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getPlain() {
		return plain;
	}

	public void setPlain(String plain) {
		this.plain = plain;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Date getPlainDate() {
		return plainDate;
	}

	public void setPlainDate(Date plainDate) {
		this.plainDate = plainDate;
	}

	public LocalDate getLocalDate() {
		return localDate;
	}

	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}

	public LocalDate getOpenLocalDate() {
		return openLocalDate;
	}

	public void setOpenLocalDate(LocalDate openLocalDate) {
		this.openLocalDate = openLocalDate;
	}

	public LocalDate getPlainLocalDate() {
		return plainLocalDate;
	}

	public void setPlainLocalDate(LocalDate plainLocalDate) {
		this.plainLocalDate = plainLocalDate;
	}

	public LocalTime getLocalTime() {
		return localTime;
	}

	public void setLocalTime(LocalTime localTime) {
		this.localTime = localTime;
	}

	public LocalTime getPlainLocalTime() {
		return plainLocalTime;
	}

	public void setPlainLocalTime(LocalTime plainLocalTime) {
		this.plainLocalTime = plainLocalTime;
	}

	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

	public LocalDateTime getLocalDateTimeOnlyTime() {
		return localDateTimeOnlyTime;
	}

	public void setLocalDateTimeOnlyTime(LocalDateTime localDateTimeOnlyTime) {
		this.localDateTimeOnlyTime = localDateTimeOnlyTime;
	}

	public LocalDateTime getLocalDateTimeOnlyDate() {
		return localDateTimeOnlyDate;
	}

	public void setLocalDateTimeOnlyDate(LocalDateTime localDateTimeOnlyDate) {
		this.localDateTimeOnlyDate = localDateTimeOnlyDate;
	}

	public LocalDateTime getPlainLocalDateTime() {
		return plainLocalDateTime;
	}

	public void setPlainLocalDateTime(LocalDateTime plainLocalDateTime) {
		this.plainLocalDateTime = plainLocalDateTime;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public Boolean getFlag2() {
		return flag2;
	}

	public void setFlag2(Boolean flag2) {
		this.flag2 = flag2;
	}

	public char getCh() {
		return ch;
	}

	public void setCh(char ch) {
		this.ch = ch;
	}

	public byte getB() {
		return b;
	}

	public void setB(byte b) {
		this.b = b;
	}

	public Short getSh() {
		return sh;
	}

	public void setSh(Short sh) {
		this.sh = sh;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public Inner getInner() {
		return inner;
	}

	public void setInner(Inner inner) {
		this.inner = inner;
	}

	public Object getOther() {
		return other;
	}

	public void setOther(Object other) {
		this.other = other;
	}

}
