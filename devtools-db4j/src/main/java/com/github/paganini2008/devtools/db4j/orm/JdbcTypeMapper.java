package com.github.paganini2008.devtools.db4j.orm;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * 
 * JdbcTypeMapper
 *
 * @author Fred Feng
 * @since 2.0.5
 */
public class JdbcTypeMapper {

	private final Map<JdbcType, String> defaults = new HashMap<JdbcType, String>();
	private final Map<JdbcType, Map<Long, String>> weights = new HashMap<JdbcType, Map<Long, String>>();

	public String get(JdbcType jdbcType) throws TypeMapperException {
		String result = defaults.get(jdbcType);
		if (result == null) {
			throw new TypeMapperException("No feature mapping for jdbc type: " + jdbcType);
		}
		return result;
	}

	public String get(JdbcType jdbcType, long size) {
		final Map<Long, String> map = weights.get(jdbcType);
		if (map != null && map.size() > 0) {
			if (size < 0) {
				for (Map.Entry<Long, String> entry : map.entrySet()) {
					return replace(entry.getValue(), entry.getKey());
				}
			} else {
				for (Map.Entry<Long, String> entry : map.entrySet()) {
					if (size >= 0 && size <= entry.getKey()) {
						return replace(entry.getValue(), size);
					}
				}
			}
		}
		String part = get(jdbcType);
		if (size < 0 && part.contains("$l")) {
			throw new TypeMapperException("Please specify data length.");
		}
		return replace(part, size);
	}

	public String get(JdbcType jdbcType, int m, int d) throws TypeMapperException {
		String part = get(jdbcType);
		if (m > 0 && d > 0) {
			if (m > d) {
				return replace(part, m, d);
			}
		} else if (m > 0 && d < 0) {
			return replace(part, m, 0);
		} else if (m < 0 && d < 0) {
			return getShort(part);
		}
		throw new TypeMapperException();
	}

	private static String replace(String type, int m, int d) {
		type = replaceFirst(type, "$p", Integer.toString(m));
		return replaceFirst(type, "$s", Integer.toString(d));
	}

	private static String replace(String type, long size) {
		return replaceFirst(type, "$l", Long.toString(size));
	}

	private static String getShort(String type) {
		int i = type.indexOf("(");
		return i > 0 ? type.substring(0, i) : type;
	}

	private static String replaceFirst(String template, String placeholder, String replacement) {
		int loc = template.indexOf(placeholder);
		if (loc < 0) {
			return template;
		}
		return template.substring(0, loc).concat(replacement).concat(template.substring(loc + placeholder.length()));
	}

	public void put(JdbcType jdbcType, long capacity, String name) {
		Map<Long, String> map = weights.get(jdbcType);
		if (map == null) {
			weights.put(jdbcType, new TreeMap<Long, String>());
			map = weights.get(jdbcType);
		}
		map.put(capacity, name);
	}

	public void put(JdbcType jdbcType, String name) {
		defaults.put(jdbcType, name);
	}
}
