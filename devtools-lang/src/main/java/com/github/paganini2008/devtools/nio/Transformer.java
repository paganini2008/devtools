package com.github.paganini2008.devtools.nio;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 
 * Transformer
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface Transformer {

	void setSerialization(Serialization serialization);
	
	ByteBuffer transferTo(Object value);

	Object transferFrom(AppendableByteBuffer b);

	void transferFrom(AppendableByteBuffer b, List<Object> output);

}
