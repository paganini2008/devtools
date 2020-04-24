package com.github.paganini2008.devtools.nio;

import java.nio.ByteBuffer;
import java.util.Queue;

import com.github.paganini2008.devtools.RandomStringUtils;
import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.collection.LruQueue;

/**
 * 
 * BufferPool
 *
 * @author Fred Feng
 * @version 1.0
 */
public class BufferPool {

	private final Queue<AppendableByteBuffer> queue;
	private final int bufferSize;

	public BufferPool(int maxSize) {
		this(maxSize, 64);
	}

	public BufferPool(int maxSize, int bufferSize) {
		this.queue = new LruQueue<AppendableByteBuffer>(maxSize);
		this.bufferSize = bufferSize;
	}

	public AppendableByteBuffer borrowBuffer() {
		AppendableByteBuffer buffer = queue.poll();
		if (buffer == null) {
			buffer = createBuffer();
		}
		buffer.clear();
		return buffer;
	}

	protected AppendableByteBuffer createBuffer() {
		return new AppendableByteBuffer(bufferSize);
	}

	public void givebackBuffer(AppendableByteBuffer buffer) {
		queue.add(buffer);
	}

	public int size() {
		return queue.size();
	}

	public static void main(String[] args) throws Exception {
		BufferPool bufferPool = new BufferPool(128, 64);
		for (int i = 0; i < 100; i++) {
			String str = RandomStringUtils.randomString(RandomUtils.randomInt(100, 10000));
			byte[] data = str.getBytes();
			AppendableByteBuffer buffer = bufferPool.borrowBuffer();
			buffer.append(data);
			ByteBuffer result = buffer.get();
			result.flip();
			int dataLength = result.getInt();
			data = new byte[dataLength];
			result.get(data);
			System.out.println("Answer: " + new String(data).equals(str));
			bufferPool.givebackBuffer(buffer);
		}
		System.out.println(bufferPool.size());
	}

}
