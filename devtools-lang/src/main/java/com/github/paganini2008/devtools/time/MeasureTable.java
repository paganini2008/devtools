package com.github.paganini2008.devtools.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * MeasureTable
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public interface MeasureTable<V> extends Map<Instant, V> {

	default Map<LocalDateTime, V> output() {
		return entrySet().stream().sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(e -> e.getKey().atZone(ZoneId.systemDefault()).toLocalDateTime(), e -> e.getValue(),
						(oldVal, newVal) -> oldVal, LinkedHashMap::new));
	}

}
