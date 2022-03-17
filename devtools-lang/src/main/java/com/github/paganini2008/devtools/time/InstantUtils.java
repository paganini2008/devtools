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
package com.github.paganini2008.devtools.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * InstantUtils
 *
 * @author Fred Feng
 * @since 2.0.4
 */
public abstract class InstantUtils {

	public static Instant toInstant(Long timeInMs) {
		return toInstant(timeInMs, null);
	}

	public static Instant toInstant(Long timeInMs, Instant defaultValue) {
		if (timeInMs == null) {
			return defaultValue;
		}
		return Instant.ofEpochMilli(timeInMs);
	}

	public static Instant toInstant(Date date) {
		return toInstant(date, null);
	}

	public static Instant toInstant(Date date, Instant defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		return date.toInstant();
	}

	public static Instant toInstant(Calendar calendar) {
		return toInstant(calendar, null);
	}

	public static Instant toInstant(Calendar calendar, Instant defaultValue) {
		if (calendar == null) {
			return defaultValue;
		}
		return calendar.toInstant();
	}

	public static Instant toInstant(LocalDateTime ldt, ZoneId zoneId) {
		return toInstant(ldt, zoneId, null);
	}

	public static Instant toInstant(LocalDateTime ldt, ZoneId zoneId, Instant defaultValue) {
		if (ldt == null) {
			return defaultValue;
		}
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		return ldt.toInstant(OffsetDateTime.now(zoneId).getOffset());
	}

	public static Instant toInstant(LocalDate ld, ZoneId zoneId) {
		return toInstant(ld, zoneId, null);
	}

	public static Instant toInstant(LocalDate ld, ZoneId zoneId, Instant defaultValue) {
		if (ld == null) {
			return defaultValue;
		}
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		return ld.atTime(0, 0, 0).toInstant(OffsetDateTime.now(zoneId).getOffset());
	}

}
