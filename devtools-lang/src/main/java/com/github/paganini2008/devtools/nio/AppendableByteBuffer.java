package com.github.paganini2008.devtools.nio;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.nio.examples.Item;

/**
 * 
 * AppendableByteBuffer
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class AppendableByteBuffer {

	public static final AtomicLong increment = new AtomicLong();
	private final AtomicLong length = new AtomicLong(0);
	private ByteBuffer buffer;
	private final int bufferSize;

	public AppendableByteBuffer() {
		this(64);
	}

	public AppendableByteBuffer(int bufferSize) {
		this(false, bufferSize);
	}

	public AppendableByteBuffer(boolean direct, int bufferSize) {
		this.buffer = direct ? ByteBuffer.allocateDirect(bufferSize) : ByteBuffer.allocate(bufferSize);
		this.bufferSize = bufferSize;
	}

	public AppendableByteBuffer append(String value) {
		return append(value, Charset.defaultCharset());
	}

	public AppendableByteBuffer append(String value, Charset charset) {
		byte[] bytes = value.toString().getBytes(CharsetUtils.toCharset(charset));
		return append(bytes);
	}

	public String getString() {
		return getString(Charset.defaultCharset());
	}

	public String getString(Charset charset) {
		byte[] bytes = getBytes();
		if (bytes == null) {
			throw new BufferOverflowException();
		}
		return new String(bytes, CharsetUtils.toCharset(charset));
	}

	public AppendableByteBuffer append(ByteBuffer bb) {
		int dataLength = bb.limit();
		autoExpandIfPossible(dataLength);
		buffer.put(bb);
		length.addAndGet(dataLength);
		return this;
	}

	public AppendableByteBuffer append(byte[] bytes) {
		int dataLength = bytes.length;
		autoExpandIfPossible(dataLength + 4);
		buffer.putInt(dataLength);
		buffer.put(bytes);
		length.addAndGet(dataLength);
		return this;
	}

	public byte[] getBytes() {
		if (!hasRemaining(4)) {
			return null;
		}
		buffer.mark();
		int dataLength = buffer.getInt();
		if (hasRemaining(dataLength)) {
			byte[] bytes = new byte[dataLength];
			buffer.get(bytes);
			length.addAndGet(-dataLength);
			return bytes;
		} else {
			buffer.reset();
			return null;
		}
	}

	public AppendableByteBuffer append(double value) {
		autoExpandIfPossible(8);
		buffer.putDouble(value);
		length.addAndGet(8);
		return this;
	}

	public double getDouble() {
		length.addAndGet(-8);
		return buffer.getDouble();
	}

	public AppendableByteBuffer append(long value) {
		autoExpandIfPossible(8);
		buffer.putLong(value);
		length.addAndGet(8);
		return this;
	}

	public long getLong() {
		length.addAndGet(-8);
		return buffer.getLong();
	}

	public AppendableByteBuffer append(float value) {
		autoExpandIfPossible(4);
		buffer.putFloat(value);
		length.addAndGet(4);
		return this;
	}

	public float getFloat() {
		length.addAndGet(-4);
		return buffer.getFloat();
	}

	public AppendableByteBuffer append(int value) {
		autoExpandIfPossible(4);
		buffer.putInt(value);
		length.addAndGet(4);
		return this;
	}

	public int getInt() {
		length.addAndGet(-4);
		return buffer.getInt();
	}

	public AppendableByteBuffer append(short value) {
		autoExpandIfPossible(2);
		buffer.putShort(value);
		length.addAndGet(2);
		return this;
	}

	public short getShort() {
		length.addAndGet(-2);
		return buffer.getShort();
	}

	public AppendableByteBuffer append(char value) {
		autoExpandIfPossible(2);
		buffer.putChar(value);
		length.addAndGet(2);
		return this;
	}

	public char getChar() {
		length.addAndGet(-2);
		return buffer.getChar();
	}

	public AppendableByteBuffer append(byte value) {
		autoExpandIfPossible(1);
		buffer.put(value);
		length.addAndGet(1);
		return this;
	}

	public byte getByte() {
		length.addAndGet(-1);
		return buffer.get();
	}

	private void autoExpandIfPossible(int dataLength) {
		int position = buffer.position();
		int limit = buffer.limit();
		int exceeded = position + dataLength - limit;
		if (exceeded > 0) {
			int newLength = limit + exceeded + bufferSize;
			ByteBuffer newBuffer = buffer.isDirect() ? ByteBuffer.allocateDirect(newLength) : ByteBuffer.allocate(newLength);
			buffer.flip();
			newBuffer.put(buffer);
			buffer = newBuffer;
		}
	}

	public long length() {
		return length.get();
	}

	public AppendableByteBuffer flip() {
		buffer.flip();
		return this;
	}

	public AppendableByteBuffer limit(int limit) {
		buffer.limit(limit);
		return this;
	}

	public int limit() {
		return buffer.limit();
	}

	public int position() {
		return buffer.position();
	}

	public int remaining() {
		return buffer.remaining();
	}

	public boolean hasRemaining() {
		return buffer.hasRemaining();
	}

	public boolean hasRemaining(int length) {
		return buffer.limit() - buffer.position() >= length;
	}

	public void get(byte[] bytes) {
		buffer.get(bytes);
	}

	public void reset() {
		if (buffer.hasRemaining()) {
			int position = buffer.remaining();
			buffer.compact();
			buffer.limit(position);
		}
	}

	public void clear() {
		buffer.clear();
		length.set(0);
	}

	public ByteBuffer get() {
		ByteBuffer result = buffer.duplicate();
		clear();
		return result;
	}

	public String toString() {
		return buffer.toString();
	}

	public static void main(String[] args) {
		AppendableByteBuffer byteBuffer = new AppendableByteBuffer(32);
		byteBuffer.append('A');
		byteBuffer.append("Test123");
		byteBuffer.append(10000);
		byteBuffer.append(56);
		byteBuffer.append("Hello world Hello world Hello worldHello worldHello worldHello worldHello worldHello worldHello world");

		byteBuffer.flip();
		System.out.println(byteBuffer.getChar());
		System.out.println(byteBuffer.getString());
		System.out.println(byteBuffer.getBytes());

		byteBuffer.reset();
		byteBuffer.flip();

		System.out.println(byteBuffer.getInt());
		System.out.println(byteBuffer.getInt());
		System.out.println(byteBuffer.getString());
	}

	public static void main3(String[] args) {
		AppendableByteBuffer byteBuffer = new AppendableByteBuffer();
		byteBuffer.append('A');
		byteBuffer.append("Test123");
		byteBuffer.append(100);
		byteBuffer.append(56);
		byteBuffer.append("Hello world");
		byteBuffer.flip();
		System.out.println(byteBuffer.getChar());
		System.out.println(byteBuffer.getString());
		System.out.println(byteBuffer.getBytes());
		System.out.println(byteBuffer.getInt());
		System.out.println(byteBuffer.getInt());
		System.out.println(byteBuffer.getString());
		// System.out.println(byteBuffer.hasRemaining());
		// System.out.println(byteBuffer);
		// System.out.println(byteBuffer);
		byteBuffer.reset();
		// byteBuffer.append(3.14d);
		// byteBuffer.append((short)28);

		if (byteBuffer.hasRemaining()) {
			byteBuffer.flip();
			System.out.println(byteBuffer.getInt());
			System.out.println(byteBuffer.getInt());
			System.out.println(byteBuffer.getString());
		}
		// System.out.println(byteBuffer.getDouble());
		// System.out.println(byteBuffer.getShort());
		// System.out.println(byteBuffer.hasRemaining());
		// byteBuffer.reset();
		// byteBuffer.append('Y');
		// byteBuffer.append(3.14F);
		// byteBuffer.flip();
		// System.out.println(byteBuffer.getChar());
		// System.out.println(byteBuffer.getFloat());
		// System.out.println(byteBuffer.hasRemaining());
	}

	public static void main2(String[] args) throws Exception {
		Transformer transformer = new SerializationTransformer();
		AppendableByteBuffer byteBuffer = new AppendableByteBuffer();
		for (int i = 0; i < 3; i++) {
			transformer.transferTo(new Item("fengy_" + i, toFullString()), byteBuffer);
		}
		System.out.println(byteBuffer);
		List<Object> output = new ArrayList<Object>();
		transformer.transferFrom(byteBuffer, output);
		for (Object object : output) {
			System.out.println(object);
		}
	}

	private static String toFullString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			str.append(UUID.randomUUID().toString());
		}
		return str.toString();
	}

}
