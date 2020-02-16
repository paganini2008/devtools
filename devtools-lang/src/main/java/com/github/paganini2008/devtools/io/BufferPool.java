package com.github.paganini2008.devtools.io;

import java.nio.ByteBuffer;

import com.github.paganini2008.devtools.RandomStringUtils;
import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.collection.LruQueue;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * BufferPool
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class BufferPool {

	private final LruQueue<ByteBuffer> queue;

	public BufferPool(int maxSize) {
		queue = new LruQueue<ByteBuffer>(maxSize);
	}

	public ByteBuffer poll(int capacity) {
		ByteBuffer buffer = queue.poll();
		if (buffer == null) {
			buffer = ByteBuffer.allocate(capacity);
		} else if (buffer.capacity() < capacity) {
			queue.offer(buffer);
			buffer = ByteBuffer.allocate(capacity);
		}
		buffer.clear();
		buffer.limit(capacity);
		return buffer;
	}

	public void offer(ByteBuffer buffer) {
		queue.offer(buffer);
	}

	public int size() {
		return queue.size();
	}

	public static void main(String[] args) throws Exception {
		BufferPool bufferPool = new BufferPool(128);
		ThreadUtils.loop(8, 100, i -> {
			String str = RandomStringUtils.randomString(RandomUtils.randomInt(100, 10000));
			byte[] data = str.getBytes();
			ByteBuffer buffer = bufferPool.poll(data.length);
			buffer.put(data);
			buffer.flip();
			data = new byte[data.length];
			buffer.get(data);
			System.out.println("Answer: " + new String(data).equals(str));
			bufferPool.offer(buffer);
		});
	}

}
