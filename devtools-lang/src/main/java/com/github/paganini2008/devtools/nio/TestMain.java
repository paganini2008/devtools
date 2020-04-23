package com.github.paganini2008.devtools.nio;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TestMain {

	public static void main(String[] args) {
		Transformer transformer = new SerializationTransformer();
		Serialization serialization = new ObjectSerialization();
		AppendableByteBuffer byteBuffer = new AppendableByteBuffer(64);
		for (int i = 0; i < 10; i++) {
			byteBuffer.append(transformer.transferTo(new Item("fengy_" + i, UUID.randomUUID().toString())));
		}
		ByteBuffer buffer = byteBuffer.get();
		buffer.flip();
		System.out.println(buffer);
		System.out.println(buffer.getInt());
	}

}
