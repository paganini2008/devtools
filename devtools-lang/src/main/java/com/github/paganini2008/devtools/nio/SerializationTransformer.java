package com.github.paganini2008.devtools.nio;

import java.io.IOException;
import java.nio.BufferUnderflowException;
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
	public void transferTo(Object value, AppendableByteBuffer byteBuffer) throws IOException {
		byte[] bytes = serialization.serialize(value);
		byteBuffer.append(bytes);
	}

	@Override
	public void transferFrom(AppendableByteBuffer byteBuffer, List<Object> output) throws IOException {
		byteBuffer.flip();
		int dataLength;
		Object object;
		while (byteBuffer.hasRemaining()) {
			dataLength = byteBuffer.getInt();
			if (dataLength <= 0) {
				throw new BufferUnderflowException();
			}
			byte[] bytes = new byte[dataLength];
			try {
				byteBuffer.get(bytes);
				object = serialization.deserialize(bytes);
				output.add(object);
			} catch (RuntimeException e) {
				throw new IllegalStateException("ReadBufferSize is so small that can not fetch all data.", e);
			}
		}
	}

}
