package com.github.paganini2008.devtools.nio;

import java.io.Serializable;

public class Item implements Serializable {

	private static final long serialVersionUID = -4323424140330874831L;

	private String name;
	private Object value;

	public Item() {
	}

	public Item(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Item [name=" + name + ", value=" + value + "]";
	}

}
