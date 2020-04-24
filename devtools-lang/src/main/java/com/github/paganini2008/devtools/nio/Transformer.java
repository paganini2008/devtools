package com.github.paganini2008.devtools.nio;

import java.io.IOException;
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

	void transferTo(Object value, AppendableByteBuffer byteBuffer) throws IOException;

	void transferFrom(AppendableByteBuffer b, List<Object> output) throws IOException;

}
