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
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.CalendarUtils;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.primitives.Longs;

/**
 * LongConverter
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class LongConverter extends BasicConverter<Long> {

	private final Converter<Boolean, Long> booleanConverter = new Converter<Boolean, Long>() {
		public Long convertValue(Boolean source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Long> characterConverter = new Converter<Character, Long>() {
		public Long convertValue(Character source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Long> numberConverter = new Converter<Number, Long>() {
		public Long convertValue(Number source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Long> stringConverter = new Converter<String, Long>() {
		public Long convertValue(String source, Long defaultValue) {
			return Longs.valueOf(source, defaultValue);
		}
	};

	private final Converter<Date, Long> dateConverter = new Converter<Date, Long>() {
		public Long convertValue(Date source, Long defaultValue) {
			return DateUtils.getTimeInMillis(source, defaultValue);
		}
	};

	private final Converter<Calendar, Long> calendarConverter = new Converter<Calendar, Long>() {
		public Long convertValue(Calendar source, Long defaultValue) {
			return CalendarUtils.getTimeInMillis(source, defaultValue);
		}
	};

	private final Converter<Instant, Long> instantConverter = new Converter<Instant, Long>() {
		public Long convertValue(Instant source, Long defaultValue) {
			return DateUtils.getTimeInMillis(source, defaultValue);
		}
	};

	public LongConverter() {
		registerType(Boolean.class, booleanConverter);
		registerType(Character.class, characterConverter);
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
		registerType(Date.class, dateConverter);
		registerType(Calendar.class, calendarConverter);
		registerType(Instant.class, instantConverter);
	}

}
