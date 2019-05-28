package com.github.paganini2008.devtools.beans.oq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.beans.Getter;

/**
 * 
 * Group
 * 
 * @author Fred Feng
 *
 */
public class Group<E> implements Groupable<E> {

	private final List<E> content;
	private Map<Map<String, Object>, Unitable<E>> store = new LinkedHashMap<Map<String, Object>, Unitable<E>>();

	Group(List<E> content) {
		this.content = content;
	}

	public <T> Groupable<E> by(final Getter<E, T> getter, String alias) {
		if (store.isEmpty()) {
			doBy(content, getter, alias, store);
		} else {
			final Map<Map<String, Object>, Unitable<E>> copy = new LinkedHashMap<Map<String, Object>, Unitable<E>>(
					store);
			store.clear();
			for (Map.Entry<Map<String, Object>, Unitable<E>> outter : copy.entrySet()) {
				doBy(outter.getValue().list(), getter, alias, store);
			}
		}
		return this;
	}

	private <T> void doBy(Collection<E> list, final Getter<E, T> getter, String alias,
			Map<Map<String, Object>, Unitable<E>> store) {
		Map<T, List<E>> data = list.stream().collect(Collectors.groupingBy(new Function<E, T>() {
			public T apply(E entity) {
				return getter.apply(entity);
			}
		}, Collectors.toCollection(new Supplier<List<E>>() {
			public List<E> get() {
				return new ArrayList<E>();
			}
		})));
		for (Map.Entry<T, List<E>> inner : data.entrySet()) {
			Map<String, Object> keys = new LinkedHashMap<String, Object>();
			keys.put(alias, inner.getKey());
			store.put(keys, new Unit<E>(inner.getValue()));
		}
	}

	public Viewable<E, Map<String, Object>> view() {
		return new View<E>(store);
	}

}
