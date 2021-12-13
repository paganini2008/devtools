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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * TimeSlotMap
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public interface TimeSlotMap<V> extends Map<Instant, V> {
	
	Instant mutate(Object inputKey);

	default V merge(Instant ins, V newValue, MergedFunction<V> fun) {
		return merge(ins, newValue, (left, right) -> fun.merge(ins, left, right));
	}

	default Map<LocalDateTime, V> output() {
		return entrySet().stream().sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(e -> e.getKey().atZone(ZoneId.systemDefault()).toLocalDateTime(), e -> e.getValue(),
						(oldVal, newVal) -> oldVal, LinkedHashMap::new));
	}

}
