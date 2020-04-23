package com.github.paganini2008.devtools.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.nio.ChannelEvent.EventType;

/**
 * 
 * NioChannel
 *
 * @author Fred Feng
 * @since 1.0
 */
public class NioChannel implements Executable, Channel {

	private final BlockingQueue<Object> writerQueue = new LinkedBlockingQueue<Object>();
	private final Reactor reactor;
	private final SocketChannel channel;
	private final Transformer transformer;

	public NioChannel(Reactor reactor, SocketChannel channel, Transformer transformer) {
		this.reactor = reactor;
		this.channel = channel;
		this.transformer = transformer;
		ThreadUtils.scheduleWithFixedDelay(this, 3, TimeUnit.SECONDS);
	}

	public void close() {
		if (isActive()) {
			try {
				channel.close();
				reactor.publishChannelEvent(this, EventType.INACTIVE);
			} catch (IOException e) {
				reactor.publishChannelEvent(this, EventType.FATAL, null, e);
			}
		}
	}

	public void write(Object message, int batchSize) throws IOException {
		writerQueue.add(message);
		if (writerQueue.size() % batchSize == 0) {
			flush();
		}
	}

	public int read() throws IOException {
		System.out.println("开始读");
		final AppendableByteBuffer byteBuffer = new AppendableByteBuffer(64);
		int readerBufferSize;
		readerBufferSize = channel.socket().getReceiveBufferSize();
		ByteBuffer readerBuffer = ByteBuffer.allocate(readerBufferSize);
		int length, total = 0;
		synchronized (this) {
			try {
				while ((length = channel.read(readerBuffer)) > 0) {
					System.out.println("读了" + length + "个字节");
					readerBuffer.flip();
					byteBuffer.append(readerBuffer);
					readerBuffer.clear();
					total += length;
				}
			} catch (IOException e) {
				reactor.publishChannelEvent(this, EventType.FATAL, null, e);
				close();
			}
		}
		System.out.println("预接受数据：" + byteBuffer);
		List<Object> output = new ArrayList<Object>();
		transformer.transferFrom(byteBuffer, output);
		if (output.size() > 0) {
			reactor.publishChannelEvent(this, EventType.READABLE, output, null);
		}
		return total;
	}

	public boolean isActive() {
		return channel.isConnected();
	}

	@Override
	public boolean execute() {
		if (writerQueue.isEmpty()) {
			return true;
		}
		try {
			flush();
		} catch (Exception e) {
			reactor.publishChannelEvent(this, EventType.FATAL, null, e);
		}
		return isActive();
	}

	int flush() throws IOException {
		int length = 0;
		AppendableByteBuffer byteBuffer = new AppendableByteBuffer(64);
		List<Object> list = new ArrayList<Object>();
		if (writerQueue.drainTo(list) > 0) {
			ByteBuffer buffer;
			for (Object object : list) {
				buffer = transformer.transferTo(object);
				byteBuffer.append(buffer);
				System.out.println("写入： " + object);
			}
			ByteBuffer data = byteBuffer.get();
			System.out.println("待发送数据： " + data);
			synchronized (this) {
				data.flip();
				try {
					while (data.hasRemaining()) {
						length += channel.write(data);
						System.out.println("写入" + length + "个字节");
					}
				} catch (IOException e) {
					close();
					throw e;
				}
			}
		}
		return length;
	}

	public String toString() {
		return channel.toString();
	}

}
