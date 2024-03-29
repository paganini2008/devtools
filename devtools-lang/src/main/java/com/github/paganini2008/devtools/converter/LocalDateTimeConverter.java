/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.time.LocalDateTimeUtils;

/**
 * 
 * LocalDateTimeConverter
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class LocalDateTimeConverter extends BasicConverter<LocalDateTime> {

	private final Converter<Long, LocalDateTime> longConverter = new Converter<Long, LocalDateTime>() {
		public LocalDateTime convertValue(Long source, LocalDateTime defaultValue) {
			return LocalDateTimeUtils.toLocalDateTime(source, zoneId, defaultValue);
		}
	};

	private final Converter<Instant, LocalDateTime> instantConverter = new Converter<Instant, LocalDateTime>() {
		public LocalDateTime convertValue(Instant source, LocalDateTime defaultValue) {
			return LocalDateTimeUtils.toLocalDateTime(source, zoneId, defaultValue);
		}
	};

	private final Converter<String, LocalDateTime> stringConverter = new Converter<String, LocalDateTime>() {
		public LocalDateTime convertValue(String source, LocalDateTime defaultValue) {
			return LocalDateTimeUtils.parseLocalDateTime(source, dateTimeFormatter, defaultValue);
		}
	};

	private final Converter<Date, LocalDateTime> dateConverter = new Converter<Date, LocalDateTime>() {
		public LocalDateTime convertValue(Date source, LocalDateTime defaultValue) {
			return LocalDateTimeUtils.toLocalDateTime(source, zoneId, defaultValue);
		}
	};

	private final Converter<Calendar, LocalDateTime> calendarConverter = new Converter<Calendar, LocalDateTime>() {
		public LocalDateTime convertValue(Calendar source, LocalDateTime defaultValue) {
			return LocalDateTimeUtils.toLocalDateTime(source, zoneId, defaultValue);
		}
	};

	private final Converter<LocalDate, LocalDateTime> localDateConverter = new Converter<LocalDate, LocalDateTime>() {
		public LocalDateTime convertValue(LocalDate source, LocalDateTime defaultValue) {
			return LocalDateTimeUtils.toLocalDateTime(source, defaultValue);
		}
	};

	public LocalDateTimeConverter() {
		registerType(Long.class, longConverter);
		registerType(Instant.class, instantConverter);
		registerType(String.class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(Calendar.class, calendarConverter);
		registerType(LocalDate.class, localDateConverter);
	}

	private ZoneId zoneId = ZoneId.systemDefault();
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}

}
