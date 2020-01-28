package com.github.paganini2008.transport.netty;

import java.util.List;

import com.github.paganini2008.transport.Serializer;
import com.github.paganini2008.transport.Tuple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * NettyEncoderDecoders
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public abstract class NettyEncoderDecoders {

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
