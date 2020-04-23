package com.github.paganini2008.devtools.nio;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * 
 * SerializationTransformer
 *
 * @author Fred Feng
 * @since 1.0
 */
public class SerializationTransformer implements Transformer {

	public SerializationTransformer() {
	}

	private Serialization serialization = new ObjectSerialization();

	public void setSerialization(Serialization serialization) {
		this.serialization = serialization;
	}

	@Override
	public ByteBuffer transferTo(Object value) {
		byte[] bytes = serialization.serialize(value);
		int dataLength = bytes.length;
		ByteBuffer byteBuffer = ByteBuffer.allocate(dataLength + 4);
		byteBuffer.putInt(dataLength);
		byteBuffer.put(bytes);
		byteBuffer.flip();
		return byteBuffer;
	}

	@Override
	public Object transferFrom(AppendableByteBuffer b) {
		b.flip();
		int dataLength = b.getInt();
		if (dataLength <= 0) {
			throw new BufferUnderflowException();
		}
		byte[] bytes = new byte[dataLength];
		b.get(bytes);
		return serialization.deserialize(bytes);
	}

	@Override
	public void transferFrom(AppendableByteBuffer b, List<Object> output) {
		b.flip();
		int dataLength;
		Object object;
		while (b.hasRemaining()) {
			dataLength = b.getInt();
			if (dataLength <= 0) {
				throw new BufferUnderflowException();
			}
			byte[] bytes = new byte[dataLength];
			try {
				b.get(bytes);
				object = serialization.deserialize(bytes);
				output.add(object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
