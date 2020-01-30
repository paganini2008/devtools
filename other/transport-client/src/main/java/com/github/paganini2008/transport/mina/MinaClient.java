package com.github.paganini2008.transport.mina;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.github.paganini2008.devtools.SystemPropertyUtils;
import com.github.paganini2008.transport.HandshakeCompletedListener;
import com.github.paganini2008.transport.NioClient;
import com.github.paganini2008.transport.Partitioner;
import com.github.paganini2008.transport.TransportClientException;
import com.github.paganini2008.transport.Tuple;
import com.github.paganini2008.transport.serializer.Serializer;

/**
 * 
 * MinaClient
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class MinaClient implements NioClient {

	static {
		IoBuffer.setUseDirectBuffer(SystemPropertyUtils.getBoolean("transport.nioclient.mina.useDirectBuffer", false));
		IoBuffer.setAllocator(new SimpleBufferAllocator());
	}

	private final MinaChannelContext channelContext = new MinaChannelContext();
	private final AtomicBoolean opened = new AtomicBoolean(false);
	private MinaSerializationCodecFactory codecFactory;
	private NioSocketConnector connector;

	@Override
	public void open() {
		final int nThreads = SystemPropertyUtils.getInteger("transport.nioclient.threads", Runtime.getRuntime().availableProcessors() * 2);
		connector = new NioSocketConnector(nThreads);
		connector.setConnectTimeoutMillis(60000);
		SocketSessionConfig sessionConfig = connector.getSessionConfig();
		sessionConfig.setKeepAlive(true);
		sessionConfig.setTcpNoDelay(true);
		sessionConfig.setSendBufferSize(1024 * 1024);
		if (codecFactory == null) {
			codecFactory = new MinaSerializationCodecFactory();
		}
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));
		connector.setHandler(channelContext);

		opened.set(true);
	}

	@Override
	public void close() {
		try {
			channelContext.getChannels().forEach(ioSession -> {
				ioSession.closeNow();
			});
		} catch (Exception e) {
			throw new TransportClientException(e.getMessage(), e);
		}
		try {
			if (connector != null) {
				connector.getFilterChain().clear();
				connector.dispose();
				connector = null;
			}
		} catch (Exception e) {
			throw new TransportClientException(e.getMessage(), e);
		}

		opened.set(false);
	}

	@Override
	public boolean isOpened() {
		return opened.get();
	}

	@Override
	public void connect(final SocketAddress address, final HandshakeCompletedListener completedListener) {
		if (isConnected(address)) {
			return;
		}
		try {
			connector.connect(address).addListener(new IoFutureListener<IoFuture>() {
				public void operationComplete(IoFuture future) {
					completedListener.operationComplete(address);
				}
			}).awaitUninterruptibly();
		} catch (Exception e) {
			throw new TransportClientException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isConnected(SocketAddress address) {
		IoSession ioSession = channelContext.getChannel(address);
		return ioSession != null && ioSession.isActive();
	}

	@Override
	public void setSerializer(Serializer serializer) {
		this.codecFactory = new MinaSerializationCodecFactory(serializer);
	}

	@Override
	public void send(Tuple tuple) {
		channelContext.getChannels().forEach(ioSession -> {
			doSend(ioSession, tuple);
		});
	}

	@Override
	public void send(Tuple tuple, Partitioner partitioner) {
		IoSession ioSession = channelContext.selectChannel(tuple, partitioner);
		if (ioSession != null) {
			doSend(ioSession, tuple);
		}
	}

	protected void doSend(IoSession ioSession, Tuple tuple) {
		try {
			ioSession.write(tuple);
		} catch (Exception e) {
			throw new TransportClientException(e.getMessage(), e);
		}
	}

}
