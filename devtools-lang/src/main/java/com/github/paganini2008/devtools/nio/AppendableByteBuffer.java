package com.github.paganini2008.devtools.nio;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.CharsetUtils;

/**
 * 
 * AppendableByteBuffer
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class AppendableByteBuffer {

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
		return new String(getBytes(), CharsetUtils.toCharset(charset));
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
		int dataLength = buffer.getInt();
		byte[] bytes = new byte[dataLength];
		buffer.get(bytes);
		length.addAndGet(-dataLength);
		return bytes;
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

	public void flip() {
		buffer.flip();
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

	public void get(byte[] bytes) {
		buffer.get(bytes);
	}

	public void clear() {
		buffer.clear();
		length.set(0);
	}

	public ByteBuffer get() {
		return get(true);
	}

	public ByteBuffer get(boolean clear) {
		ByteBuffer result = buffer.duplicate();
		if (clear) {
			buffer = buffer.isDirect() ? ByteBuffer.allocateDirect(bufferSize) : ByteBuffer.allocate(bufferSize);
		}
		return result;
	}
	
	public String toString() {
		return buffer.toString();
	}

	public static void main(String[] args) {
		AppendableByteBuffer byteBuffer = new AppendableByteBuffer(32);
		byteBuffer.append(10).append('f').append(3.14f).append(5.618902d).append("Hello new world! 中国人", CharsetUtils.UTF_8);
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			str.append(UUID.randomUUID().toString());
		}
		byteBuffer.append(str.toString());
		System.out.println("Total: " + byteBuffer.length());

		byteBuffer.flip();
		System.out.println(byteBuffer.getInt());
		System.out.println(byteBuffer.getChar());
		System.out.println(byteBuffer.getFloat());
		System.out.println(byteBuffer.getDouble());
		System.out.println(byteBuffer.getString(CharsetUtils.UTF_8));
		System.out.println(byteBuffer.getString());

		System.out.println("---------------------------------------------------");
		System.out.println(byteBuffer.length());
		System.out.println(byteBuffer.hasRemaining());
		System.out.println(byteBuffer.position());
		System.out.println(byteBuffer.limit());
	}

}
