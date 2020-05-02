package com.github.paganini2008.devtools.nio.examples;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.github.paganini2008.devtools.multithreads.AtomicUnsignedInteger;

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

	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.allocate(20);
		buffer.putInt(3);
		buffer.putDouble(16.2D);
		System.out.println(buffer);
		
		buffer.flip();
		int i = buffer.getInt();
		System.out.println(i);
		System.out.println(buffer);
		buffer.compact();
		System.out.println(buffer);
		buffer.flip();
		double d = buffer.getDouble();
		System.out.println(d);
	}

}
