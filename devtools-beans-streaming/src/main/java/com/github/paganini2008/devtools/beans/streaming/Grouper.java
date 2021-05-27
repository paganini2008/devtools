package com.github.paganini2008.devtools.beans.streaming;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * Grouper
 *
 * @author Fred Feng
 * @version 1.0
 */
public class Grouper<E> implements Groupable<E> {

	private List<Group<E>> groups = new ArrayList<Group<E>>();

	Grouper(List<E> elements) {
		groups.add(new GroupImpl<E>(new LinkedHashMap<>(), elements, null));
	}

	public Groupable<E> having(Predicate<Group<E>> predicate) {
		groups = groups.stream().filter(predicate).collect(Collectors.toList());
		return this;
	}

	public Groupable<E> orderBy(Sorter<Group<E>> sort) {
		this.groups.sort(sort);
		return this;
	}

	public <T> Groupable<E> groupBy(Function<E, T> function, String attributeName) {
		List<Group<E>> updated = new ArrayList<Group<E>>();
		for (Group<E> group : new ArrayList<Group<E>>(groups)) {
			updated.addAll(divide(group, function, attributeName));
		}
		groups.clear();
		groups.addAll(updated);
		return this;
	}

	public <T> ResultSetSlice<T> setTransformer(Transformer<Group<E>, T> transformer) {
		return new MemoryResultSetSlice<>(groups, transformer);
	}

	private <T> List<Group<E>> divide(Group<E> group, Function<E, T> function, String attributeName) {
		List<Group<E>> groups = new ArrayList<Group<E>>();
		Map<T, List<E>> data = group.elements().stream().collect(Collectors.groupingBy(function, Collectors.toList()));
		for (Map.Entry<T, List<E>> entry : data.entrySet()) {
			Map<String, Object> attributes = new LinkedHashMap<String, Object>(group.getAttributes());
			attributes.put(attributeName, entry.getKey());
			groups.add(new GroupImpl<E>(attributes, entry.getValue(), group));
		}
		return groups;
	}

	/**
	 * 
	 * GroupImpl
	 * 
	 * @author Fred Feng
	 * 
	 * @version 1.0
	 */
	static class GroupImpl<E> implements Group<E>, Serializable {

		private static final long serialVersionUID = -5752728250597378290L;
		private final Map<String, Object> attributes;
		private final List<E> elements;
		private final Group<E> rollup;

		GroupImpl(Map<String, Object> attributes, List<E> elements, Group<E> rollup) {
			this.attributes = attributes;
			this.elements = elements;
			this.rollup = rollup;
		}

		public <T> T summarize(Calculation<E, T> calculation) {
			return calculation.getResult(elements);
		}

		public Map<String, Object> getAttributes() {
			return attributes;
		}

		public List<E> elements() {
			return elements;
		}

		public Group<E> rollup() {
			return rollup;
		}

	}

}
