package com.github.paganini2008.devtools.beans.oq;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 
 * Page
 * 
 * @author Fred Feng
 *
 */
public class Page<E> implements Pageable<E> {

	private final List<E> content;

	Page(List<E> content) {
		this.content = content;
	}

	private int offset;
	private int limit = 10;

	public int offset() {
		return offset;
	}

	public Pageable<E> offset(int offset) {
		this.offset = offset;
		return this;
	}

	public int limit() {
		return limit;
	}

	public Pageable<E> limit(int limit) {
		this.limit = limit;
		return this;
	}

	public List<E> list() {
		return content.stream().limit(limit).skip(offset).collect(Collectors.toCollection(new Supplier<List<E>>() {
			public List<E> get() {
				return new ArrayList<E>();
			}
		}));
	}

	public List<E> distinctList() {
		return content.stream().distinct().limit(limit).skip(offset)
				.collect(Collectors.toCollection(new Supplier<List<E>>() {
					public List<E> get() {
						return new ArrayList<E>();
					}
				}));
	}

}
