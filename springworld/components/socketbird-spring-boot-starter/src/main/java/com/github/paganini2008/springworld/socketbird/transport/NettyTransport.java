package com.github.paganini2008.springworld.socketbird.transport;

import java.util.List;

import com.github.paganini2008.springworld.socketbird.Serializer;
import com.github.paganini2008.springworld.socketbird.Tuple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * NettyTransport
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class NettyTransport implements Transport {

	public NioClient getNioClient() {
		return new InternalNettyClient();
	}

	public NioServer getNioServer() {
		return new NettyServer();
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
