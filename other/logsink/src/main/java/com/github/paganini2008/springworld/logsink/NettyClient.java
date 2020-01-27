package com.github.paganini2008.springworld.logsink;

import java.net.InetSocketAddress;
import java.util.List;

import com.github.paganini2008.devtools.SystemPropertyUtils;
import com.github.paganini2008.springworld.socketbird.Tuple;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyClient implements NioClient {

	private EventLoopGroup workerGroup;
	private Bootstrap bootstrap;
	private Channel channel;
	private Serializer serializer = new KryoSerializer();

	public void connect(String host, int port) throws Exception {

		final int nThreads = SystemPropertyUtils.getInteger("socketbird.nioclient.threads", Runtime.getRuntime().availableProcessors() * 2);
		workerGroup = new NioEventLoopGroup(nThreads);
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).option(ChannelOption.SO_SNDBUF, 1024 * 1024);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new TupleToByteEncoder(serializer), new ByteToTupleDecorder(serializer));
			}
		});
		channel = bootstrap.connect(new InetSocketAddress(host, port)).sync().channel();
	}

	public void send(Tuple tuple) {
		channel.writeAndFlush(tuple);
	}

	public void close() {
		channel.close();
		workerGroup.shutdownGracefully();
	}

	public static class TupleToByteEncoder extends MessageToByteEncoder<Tuple> {

		private final Serializer serializer;

		public TupleToByteEncoder(Serializer serializer) {
			this.serializer = serializer;
		}

		@Override
		protected void encode(ChannelHandlerContext ctx, Tuple tuple, ByteBuf out) throws Exception {
			byte[] data = serializer.serialize(tuple);
			out.writeInt(data.length);
			out.writeBytes(data);
		}

	}

	public static class ByteToTupleDecorder extends ByteToMessageDecoder {

		private final Serializer serializer;

		public ByteToTupleDecorder(Serializer serializer) {
			this.serializer = serializer;
		}

		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
			if (in.readableBytes() < 4) {
				return;
			}
			in.markReaderIndex();
			int dataLength = in.readInt();
			if (dataLength < 0) {
				ctx.close();
			}

			if (in.readableBytes() < dataLength) {
				in.resetReaderIndex();
				return;
			}

			byte[] body = new byte[dataLength];
			in.readBytes(body);
			Tuple tuple = serializer.deserialize(body);
			out.add(tuple);
		}

	}

}
