package com.github.paganini2008.transport.netty;

import com.github.paganini2008.transport.serializer.KryoSerializer;
import com.github.paganini2008.transport.serializer.Serializer;

import io.netty.channel.ChannelHandler;

/**
 * 
 * NettySerializationCodecFactory
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class NettySerializationCodecFactory {

	private final Serializer serializer;

	public NettySerializationCodecFactory() {
		this(new KryoSerializer());
	}

	public NettySerializationCodecFactory(Serializer serializer) {
		this.serializer = serializer;
	}

	public ChannelHandler getEncoder() {
		return new NettySerializationEncoderDecoders.NettySerializationEncoder(serializer);
	}

	public ChannelHandler getDecoder() {
		return new NettySerializationEncoderDecoders.NettySerializationDecorder(serializer);
	}

	public Serializer getSerializer() {
		return serializer;
	}

}
