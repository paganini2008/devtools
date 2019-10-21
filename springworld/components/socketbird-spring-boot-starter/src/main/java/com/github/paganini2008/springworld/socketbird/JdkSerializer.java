package com.github.paganini2008.springworld.socketbird;

import com.github.paganini2008.devtools.io.SerializationUtils;

/**
 * 
 * JdkSerializer
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class JdkSerializer implements Serializer {

	public byte[] serialize(Tuple tuple) {
		return SerializationUtils.toByteArray(tuple);
	}

	public Tuple deserialize(byte[] bytes) {
		return SerializationUtils.readObject(bytes);
	}

	public static void main(String[] args) throws Exception {
		int N = 100;
		JdkSerializer serializer = new JdkSerializer();
		for (int i = 0; i < N; i++) {
			Tuple tuple = new TupleImpl();
			tuple.setField("Key_" + i, "Value_" + i);
			byte[] bytes = serializer.serialize(tuple);
			Tuple str = (Tuple) serializer.deserialize(bytes);
			System.out.println(str.toString());
		}
		System.out.println("Completed");
		System.in.read();
		System.out.println("Over.");
	}

}
