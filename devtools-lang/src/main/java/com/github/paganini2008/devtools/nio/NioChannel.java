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
				reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(this, EventType.INACTIVE));
			} catch (IOException e) {
				reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(this, EventType.FATAL, null, e));
			}
		}
	}

	public int write(Object message, int batchSize) throws IOException {
		writerQueue.add(message);
		if (batchSize > 0 && writerQueue.size() % batchSize == 0) {
			return flush();
		}
		return 0;
	}

	public int read(int bufferSize) throws IOException {
		final AppendableByteBuffer byteBuffer = new AppendableByteBuffer(bufferSize);
		int readerBufferSize;
		readerBufferSize = channel.socket().getReceiveBufferSize();
		ByteBuffer readerBuffer = ByteBuffer.allocate(readerBufferSize);
		int length, total = 0;
		synchronized (this) {
			try {
				while ((length = channel.read(readerBuffer)) > 0) {
					readerBuffer.flip();
					byteBuffer.append(readerBuffer);
					readerBuffer.clear();
					total += length;
				}
			} catch (IOException e) {
				reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(this, EventType.FATAL, null, e));
				close();
			}
		}
		List<Object> output = new ArrayList<Object>();
		transformer.transferFrom(byteBuffer, output);
		if (output.size() > 0) {
			reactor.getChannelEventPublisher()
					.publishChannelEvent(new ChannelEvent(this, EventType.READABLE, MessagePacket.of(output, total), null));
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
			reactor.getChannelEventPublisher().publishChannelEvent(new ChannelEvent(this, EventType.FATAL, null, e));
		}
		return isActive();
	}

	public int flush() throws IOException {
		int length = 0;
		AppendableByteBuffer byteBuffer = new AppendableByteBuffer(128);
		List<Object> list = new ArrayList<Object>();
		if (writerQueue.drainTo(list) > 0) {
			ByteBuffer buffer;
			for (Object object : list) {
				buffer = transformer.transferTo(object);
				byteBuffer.append(buffer);
			}
			ByteBuffer data = byteBuffer.get();
			synchronized (this) {
				data.flip();
				try {
					while (data.hasRemaining()) {
						length += channel.write(data);
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
