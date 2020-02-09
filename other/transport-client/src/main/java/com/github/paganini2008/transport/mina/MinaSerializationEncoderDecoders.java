package com.github.paganini2008.transport.mina;

import org.apache.mina.core.buffer.BufferDataException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.github.paganini2008.transport.Tuple;
import com.github.paganini2008.transport.serializer.Serializer;

/**
 * 
 * MinaSerializationEncoderDecoders
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public abstract class MinaSerializationEncoderDecoders {

	public static class MinaSerializationEncoder extends ProtocolEncoderAdapter {
		private final Serializer serializer;
		private int maxObjectSize = Integer.MAX_VALUE;

		public MinaSerializationEncoder(Serializer serializer) {
			this.serializer = serializer;
		}

		public int getMaxObjectSize() {
			return maxObjectSize;
		}

		public void setMaxObjectSize(int maxObjectSize) {
			if (maxObjectSize <= 0) {
				throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
			}
			this.maxObjectSize = maxObjectSize;
		}

		@Override
		public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
			byte[] data = serializer.serialize((Tuple) message);
			IoBuffer buf = IoBuffer.allocate(data.length + 4);
			buf.putInt(data.length);
			buf.put(data);

			int objectSize = buf.position() - 4;
			if (objectSize > maxObjectSize) {
				throw new IllegalArgumentException("The encoded object is too big: " + objectSize + " (> " + maxObjectSize + ')');
			}
			buf.flip();
			out.write(buf);
		}
	}

	public static class MinaSerializationDecoder extends CumulativeProtocolDecoder {

		private final Serializer serializer;
		private int maxObjectSize = 8 * 1024 * 1024;

		public MinaSerializationDecoder(Serializer serializer) {
			this.serializer = serializer;
		}

		public int getMaxObjectSize() {
			return maxObjectSize;
		}

		public void setMaxObjectSize(int maxObjectSize) {
			if (maxObjectSize <= 0) {
				throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
			}
			this.maxObjectSize = maxObjectSize;
		}

		@Override
		protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
			if (!in.prefixedDataAvailable(4, maxObjectSize)) {
				return false;
			}
			int length = in.getInt();
			if (length <= 4) {
				throw new BufferDataException("Object length should be greater than 4: " + length);
			}
			byte[] data = new byte[length];
			in.get(data);
			Tuple tuple = serializer.deserialize(data);
			out.write(tuple);
			return true;
		}

	}

}
