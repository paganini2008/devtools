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

import com.github.paganini2008.devtools.time.LocalDateUtils;

/**
 * 
 * LocalDateConverter
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class LocalDateConverter extends BasicConverter<LocalDate> {

	private final Converter<Long, LocalDate> longConverter = new Converter<Long, LocalDate>() {
		public LocalDate convertValue(Long source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, zoneId, defaultValue);
		}
	};

	private final Converter<Instant, LocalDate> instantConverter = new Converter<Instant, LocalDate>() {
		public LocalDate convertValue(Instant source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, zoneId, defaultValue);
		}
	};

	private final Converter<String, LocalDate> stringConverter = new Converter<String, LocalDate>() {
		public LocalDate convertValue(String source, LocalDate defaultValue) {
			return LocalDateUtils.parseLocalDate(source, dateFormatter, defaultValue);
		}
	};

	private final Converter<Date, LocalDate> dateConverter = new Converter<Date, LocalDate>() {
		public LocalDate convertValue(Date source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, zoneId, defaultValue);
		}
	};

	private final Converter<Calendar, LocalDate> calendarConverter = new Converter<Calendar, LocalDate>() {
		public LocalDate convertValue(Calendar source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, zoneId, defaultValue);
		}
	};

	private final Converter<LocalDateTime, LocalDate> localDateTimeConverter = new Converter<LocalDateTime, LocalDate>() {
		public LocalDate convertValue(LocalDateTime source, LocalDate defaultValue) {
			return LocalDateUtils.toLocalDate(source, defaultValue);
		}
	};

	public LocalDateConverter() {
		registerType(Long.class, longConverter);
		registerType(Instant.class, instantConverter);
		registerType(String.class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(Calendar.class, calendarConverter);
		registerType(LocalDateTime.class, localDateTimeConverter);
	}

	private ZoneId zoneId = ZoneId.systemDefault();
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public void setDateFormatter(DateTimeFormatter dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

}
