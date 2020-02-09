package com.github.paganini2008.transport.mina;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.github.paganini2008.transport.ChannelEvent;
import com.github.paganini2008.transport.ChannelEvent.EventType;
import com.github.paganini2008.transport.ChannelEventListener;
import com.github.paganini2008.transport.ConnectionWatcher;
import com.github.paganini2008.transport.HandshakeCallback;
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
		IoBuffer.setUseDirectBuffer(false);
		IoBuffer.setAllocator(new SimpleBufferAllocator());
	}
	private static final String PING = "PING";
	private static final String PONG = "PONG";

	private final MinaChannelContext channelContext = new MinaChannelContext();
	private final AtomicBoolean opened = new AtomicBoolean(false);
	private MinaSerializationCodecFactory codecFactory;
	private NioSocketConnector connector;
	private int idleTimeout = 30;
	private int threadCount = Runtime.getRuntime().availableProcessors() * 2;

	@Override
	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	@Override
	public void watchConnection(int interval, TimeUnit timeUnit) {
		this.channelContext.setConnectionWatcher(new ConnectionWatcher(interval, timeUnit, this));
	}

	@Override
	public void setThreadCount(int nThreads) {
		this.threadCount = nThreads;
	}

	@Override
	public void open() {
		connector = new NioSocketConnector(threadCount);
		connector.setConnectTimeoutMillis(60000);
		SocketSessionConfig sessionConfig = connector.getSessionConfig();
		sessionConfig.setKeepAlive(true);
		sessionConfig.setTcpNoDelay(true);
		sessionConfig.setSendBufferSize(1024 * 1024);
		if (codecFactory == null) {
			codecFactory = new MinaSerializationCodecFactory();
		}

		KeepAliveFilter heartBeat = new KeepAliveFilter(new ClientKeepAliveMessageFactory(), IdleStatus.WRITER_IDLE);
		heartBeat.setForwardEvent(false);
		heartBeat.setRequestTimeout(idleTimeout);
		heartBeat.setRequestTimeoutHandler(KeepAliveRequestTimeoutHandler.LOG);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));
		connector.getFilterChain().addLast("heartbeat", heartBeat);
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
	public void connect(final SocketAddress remoteAddress, final HandshakeCallback handshakeCallback) {
		if (isConnected(remoteAddress)) {
			return;
		}
		try {
			connector.connect(remoteAddress).addListener(new IoFutureListener<IoFuture>() {
				public void operationComplete(IoFuture future) {
					ConnectionWatcher connectionWatcher = channelContext.getConnectionWatcher();
					if (connectionWatcher != null) {
						connectionWatcher.watch(remoteAddress, handshakeCallback);
					}

					handshakeCallback.operationComplete(remoteAddress);
				}
			}).awaitUninterruptibly();
		} catch (Exception e) {
			throw new TransportClientException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isConnected(SocketAddress remoteAddress) {
		IoSession ioSession = channelContext.getChannel(remoteAddress);
		return ioSession != null && ioSession.isConnected();
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

	class ClientKeepAliveMessageFactory implements KeepAliveMessageFactory {

		public boolean isRequest(IoSession session, Object message) {
			return false;
		}

		public boolean isResponse(IoSession session, Object message) {
			return (message instanceof Tuple) && PONG.equals(((Tuple) message).getField("content"));
		}

		public Object getRequest(IoSession session) {
			return Tuple.by(PING);
		}

		public Object getResponse(IoSession session, Object request) {
			ChannelEventListener<IoSession> channelEventListener = channelContext.getChannelEventListener();
			if (channelEventListener != null) {
				channelEventListener.fireChannelEvent(new ChannelEvent<IoSession>(session, EventType.PONG, null));
			}
			return null;
		}
	}

}
