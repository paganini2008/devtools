package com.github.paganini2008.transport.netty;

import java.net.SocketAddress;

import com.github.paganini2008.transport.ChannelContext;
import com.github.paganini2008.transport.ChannelEvent;
import com.github.paganini2008.transport.ChannelEvent.EventType;
import com.github.paganini2008.transport.ChannelEventListener;
import com.github.paganini2008.transport.ConnectionWatcher;
import com.github.paganini2008.transport.Tuple;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * NettyChannelContextAware
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public abstract class NettyChannelContextAware extends ChannelInboundHandlerAdapter implements ChannelContext<Channel> {

	private ConnectionWatcher connectionWatcher;
	private ChannelEventListener<Channel> channelEventListener;

	public ConnectionWatcher getConnectionWatcher() {
		return connectionWatcher;
	}

	public void setConnectionWatcher(ConnectionWatcher connectionWatcher) {
		this.connectionWatcher = connectionWatcher;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		addChannel(ctx.channel());

		fireChannelEvent(ctx.channel(), EventType.CONNECTED, null);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		SocketAddress remoteAddress = ctx.channel().remoteAddress();
		removeChannel(remoteAddress);

		fireReconnectionIfNecessary(remoteAddress);
		fireChannelEvent(ctx.channel(), EventType.CLOSED, null);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.channel().close();

		SocketAddress remoteAddress = ctx.channel().remoteAddress();
		removeChannel(remoteAddress);

		fireReconnectionIfNecessary(remoteAddress);
		fireChannelEvent(ctx.channel(), EventType.FAULTY, cause);

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
		if (isPong(data)) {
			fireChannelEvent(ctx.channel(), EventType.PONG, null);
		}
	}

	private boolean isPong(Object data) {
		return (data instanceof Tuple) && "PONG".equals(((Tuple) data).getField("content"));
	}

	@Override
	public void setChannelEventListener(ChannelEventListener<Channel> channelEventListener) {
		this.channelEventListener = channelEventListener;
	}

	public ChannelEventListener<Channel> getChannelEventListener() {
		return channelEventListener;
	}

	private void fireChannelEvent(Channel channel, EventType eventType, Throwable cause) {
		if (channelEventListener != null) {
			channelEventListener.fireChannelEvent(new ChannelEvent<Channel>(channel, eventType, cause));
		}
	}

	private void fireReconnectionIfNecessary(SocketAddress remoteAddress) {
		if (connectionWatcher != null) {
			connectionWatcher.reconnect(remoteAddress);
		}
	}

}
