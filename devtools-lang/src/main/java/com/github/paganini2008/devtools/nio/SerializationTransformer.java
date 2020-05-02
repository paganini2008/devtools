package com.github.paganini2008.devtools.nio;

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
	public void transferTo(Object value, AppendableByteBuffer byteBuffer) {
		byte[] bytes = serialization.serialize(value);
		byteBuffer.append(bytes);
	}

	@Override
	public void transferFrom(AppendableByteBuffer byteBuffer, List<Object> output) {
		Object object;
		while (byteBuffer.hasRemaining(4)) {
			byte[] bytes = byteBuffer.getBytes();
			if (bytes != null) {
				object = serialization.deserialize(bytes);
				output.add(object);
			} else {
				break;
			}
		}
	}

}
