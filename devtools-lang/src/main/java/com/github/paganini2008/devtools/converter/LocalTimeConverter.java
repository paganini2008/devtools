/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.time.LocalTimeUtils;

/**
 * 
 * LocalTimeConverter
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class LocalTimeConverter extends BasicConverter<LocalTime> {

	private final Converter<Long, LocalTime> longConverter = new Converter<Long, LocalTime>() {
		public LocalTime convertValue(Long source, LocalTime defaultValue) {
			return LocalTimeUtils.toLocalTime(source, zoneId, defaultValue);
		}
	};
	
	private final Converter<Instant, LocalTime> instantConverter = new Converter<Instant, LocalTime>() {
		public LocalTime convertValue(Instant source, LocalTime defaultValue) {
			return LocalTimeUtils.toLocalTime(source, zoneId, defaultValue);
		}
	};

	private final Converter<String, LocalTime> stringConverter = new Converter<String, LocalTime>() {
		public LocalTime convertValue(String source, LocalTime defaultValue) {
			return LocalTimeUtils.parseLocalTime(source, timeFormatter, defaultValue);
		}
	};

	private final Converter<Date, LocalTime> dateConverter = new Converter<Date, LocalTime>() {
		public LocalTime convertValue(Date source, LocalTime defaultValue) {
			return LocalTimeUtils.toLocalTime(source, zoneId, defaultValue);
		}
	};

	private final Converter<Calendar, LocalTime> calendarConverter = new Converter<Calendar, LocalTime>() {
		public LocalTime convertValue(Calendar source, LocalTime defaultValue) {
			return LocalTimeUtils.toLocalTime(source, zoneId, defaultValue);
		}
	};

	public LocalTimeConverter() {
		registerType(Long.class, longConverter);
		registerType(Instant.class, instantConverter);
		registerType(String.class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(Calendar.class, calendarConverter);
	}

	private ZoneId zoneId = ZoneId.systemDefault();
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public void setTimeFormatter(DateTimeFormatter timeFormatter) {
		this.timeFormatter = timeFormatter;
	}

}
