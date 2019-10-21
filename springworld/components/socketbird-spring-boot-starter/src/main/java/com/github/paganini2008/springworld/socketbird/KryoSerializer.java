package com.github.paganini2008.springworld.socketbird;

import java.util.HashMap;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.esotericsoftware.kryo.util.Pool;

/**
 * 
 * KryoSerializer
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class KryoSerializer implements Serializer {

	private final Pool<Kryo> pool;
	private final Pool<Output> outputPool;
	private final Pool<Input> inputPool;

	public KryoSerializer() {
		this(8, 16, 16);
	}

	public KryoSerializer(int poolSize, int outputSize, int inputSize) {
		pool = new Pool<Kryo>(true, false, poolSize) {

			@Override
			protected Kryo create() {
				Kryo kryo = new Kryo();
				kryo.setReferences(false);
				kryo.setRegistrationRequired(false);
				kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
				kryo.register(Tuple.class, new MapSerializer<HashMap<String, Object>>());
				return kryo;
			}

		};

		outputPool = new Pool<Output>(true, false, outputSize) {
			protected Output create() {
				return new Output(2 * 1024, -1);
			}
		};

		inputPool = new Pool<Input>(true, false, inputSize) {
			protected Input create() {
				return new Input(2 * 1024);
			}
		};

	}

	public byte[] serialize(Tuple tuple) {
		Kryo kryo = pool.obtain();
		Output output = outputPool.obtain();
		try {
			output.reset();
			kryo.writeObject(output, tuple);
			return output.getBuffer();
		} finally {
			outputPool.free(output);
			pool.free(kryo);
		}
	}

	public Tuple deserialize(byte[] bytes) {
		Kryo kryo = pool.obtain();
		Input input = inputPool.obtain();
		try {
			input.setBuffer(bytes);
			return kryo.readObject(input, TupleImpl.class);
		} finally {
			inputPool.free(input);
			pool.free(kryo);
		}
	}

	public static void main(String[] args) throws Exception {
		int N = 100;
		KryoSerializer serializer = new KryoSerializer();
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