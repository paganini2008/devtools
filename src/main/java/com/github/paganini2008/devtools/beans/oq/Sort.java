package com.github.paganini2008.devtools.beans.oq;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.beans.BeanComparator;
import com.github.paganini2008.devtools.beans.Getter;

/**
 * 
 * Sort
 * 
 * @author Fred Feng
 *
 */
public class Sort<E> implements Sortable<E> {

	private final BeanComparator<E> beanComparator = new BeanComparator<E>();
	private final List<E> content;

	Sort(List<E> content) {
		this.content = content;
	}

	public <T extends Comparable<T>> Sortable<E> asc(Getter<E, T> getter) {
		beanComparator.asc(getter);
		return this;
	}

	public <T extends Comparable<T>> Sortable<E> desc(Getter<E, T> getter) {
		beanComparator.desc(getter);
		return this;
	}

	public List<E> list() {
		return content;
	}

	public List<E> distinctList() {
		return content.stream().distinct().collect(Collectors.toCollection(new Supplier<List<E>>() {
			public List<E> get() {
				return new ArrayList<E>();
			}
		}));
	}

	public Pageable<E> page() {
		return new Page<E>(list());
	}
}
