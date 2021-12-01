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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.time.DateUtils;

/**
 * DateConverter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class DateConverter extends BasicConverter<Date> {

	private final Converter<Long, Date> longConverter = new Converter<Long, Date>() {
		public Date convertValue(Long source, Date defaultValue) {
			return DateUtils.toDate(source, defaultValue);
		}
	};

	private final Converter<Instant, Date> instantConverter = new Converter<Instant, Date>() {
		public Date convertValue(Instant source, Date defaultValue) {
			return DateUtils.toDate(source, defaultValue);
		}
	};

	private final Converter<String, Date> stringConverter = new Converter<String, Date>() {
		public Date convertValue(String source, Date defaultValue) {
			return DateUtils.parse(source, datePatterns, defaultValue);
		}
	};

	private final Converter<Calendar, Date> calendarConverter = new Converter<Calendar, Date>() {
		public Date convertValue(Calendar source, Date defaultValue) {
			return DateUtils.toDate(source, defaultValue);
		}
	};

	private final Converter<int[], Date> intArrayConverter = new Converter<int[], Date>() {
		public Date convertValue(int[] source, Date defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			if (source.length == 3) {
				return DateUtils.of(source[0], source[1], source[2]);
			} else if (source.length == 6) {
				return DateUtils.of(source[0], source[1], source[2], source[3], source[4], source[5]);
			}
			throw new IllegalArgumentException("Int array's length need to be 3 or 6.");
		}
	};

	private final Converter<LocalDate, Date> localDateConverter = new Converter<LocalDate, Date>() {
		public Date convertValue(LocalDate source, Date defaultValue) {
			return DateUtils.toDate(source, zoneId, defaultValue);
		}
	};

	private final Converter<LocalDateTime, Date> localDateTimeConverter = new Converter<LocalDateTime, Date>() {
		public Date convertValue(LocalDateTime source, Date defaultValue) {
			return DateUtils.toDate(source, zoneId, defaultValue);
		}
	};

	public DateConverter() {
		registerType(Long.class, longConverter);
		registerType(Instant.class, instantConverter);
		registerType(String.class, stringConverter);
		registerType(Calendar.class, calendarConverter);
		registerType(int[].class, intArrayConverter);
		registerType(LocalDate.class, localDateConverter);
		registerType(LocalDateTime.class, localDateTimeConverter);
	}

	private ZoneId zoneId = ZoneId.systemDefault();
	private String[] datePatterns = new String[] { DateUtils.DEFAULT_DATE_PATTERN, "yyyy-MM-dd", "yyyyMMddHHmmss" };

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public void setDatePatterns(String[] datePatterns) {
		this.datePatterns = datePatterns;
	}

}
