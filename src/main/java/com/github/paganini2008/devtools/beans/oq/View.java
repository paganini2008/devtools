package com.github.paganini2008.devtools.beans.oq;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.beans.Getter;

/**
 * 
 * View
 * 
 * @author Fred Feng
 *
 */
public class View<E> implements Viewable<E, Map<String, Object>> {

	private final Map<Map<String, Object>, Unitable<E>> store;

	View(Map<Map<String, Object>, Unitable<E>> store) {
		this.store = store;
	}

	public Sortable<Map<String, Object>> sort() {
		return new Sort<Map<String, Object>>(list());
	}

	public Viewable<E, Map<String, Object>> having(Having<E> having) {
		Map<Map<String, Object>, Unitable<E>> results = new LinkedHashMap<Map<String, Object>, Unitable<E>>();
		for (Map.Entry<Map<String, Object>, Unitable<E>> entry : store.entrySet()) {
			if (having.accept(entry.getValue())) {
				results.put(entry.getKey(), entry.getValue());
			}
		}
		return new View<E>(results);
	}

	public Viewable<E, Map<String, Object>> count(String alias) {
		for (Map.Entry<Map<String, Object>, Unitable<E>> entry : store.entrySet()) {
			entry.getKey().put(alias, entry.getValue().count());
		}
		return this;
	}

	public <T extends Comparable<T>> Viewable<E, Map<String, Object>> max(final Getter<E, T> getter, String alias) {
		for (Map.Entry<Map<String, Object>, Unitable<E>> entry : store.entrySet()) {
			entry.getKey().put(alias, entry.getValue().max(getter));
		}
		return this;
	}

	public <T extends Comparable<T>> Viewable<E, Map<String, Object>> min(final Getter<E, T> getter, String alias) {
		for (Map.Entry<Map<String, Object>, Unitable<E>> entry : store.entrySet()) {
			entry.getKey().put(alias, entry.getValue().min(getter));
		}
		return this;
	}

	public <T extends Number> Viewable<E, Map<String, Object>> sum(final Getter<E, T> getter, String alias) {
		for (Map.Entry<Map<String, Object>, Unitable<E>> entry : store.entrySet()) {
			entry.getKey().put(alias, entry.getValue().sum(getter));
		}
		return this;
	}

	public <T extends Number> Viewable<E, Map<String, Object>> avg(final Getter<E, T> getter, String alias, int scale,
			RoundingMode roundingMode) {
		for (Map.Entry<Map<String, Object>, Unitable<E>> entry : store.entrySet()) {
			entry.getKey().put(alias, entry.getValue().avg(getter, scale, roundingMode));
		}
		return this;
	}

	public List<Map<String, Object>> list() {
		return new ArrayList<Map<String, Object>>(store.keySet());
	}

	public List<Map<String, Object>> distinctList() {
		return list().stream().distinct().collect(Collectors.toCollection(new Supplier<List<Map<String, Object>>>() {
			public List<Map<String, Object>> get() {
				return new ArrayList<Map<String, Object>>();
			}
		}));
	}

}
