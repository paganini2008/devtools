package com.github.paganini2008.transport.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.github.paganini2008.transport.mina.MinaSerializationEncoderDecoders.MinaSerializationDecoder;
import com.github.paganini2008.transport.mina.MinaSerializationEncoderDecoders.MinaSerializationEncoder;
import com.github.paganini2008.transport.serializer.KryoSerializer;
import com.github.paganini2008.transport.serializer.Serializer;

/**
 * 
 * MinaSerializationCodecFactory
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class MinaSerializationCodecFactory implements ProtocolCodecFactory {

	private final MinaSerializationEncoder encoder;
	private final MinaSerializationDecoder decoder;

	public MinaSerializationCodecFactory() {
		this(new KryoSerializer());
	}

	public MinaSerializationCodecFactory(Serializer serializer) {
		encoder = new MinaSerializationEncoder(serializer);
		decoder = new MinaSerializationDecoder(serializer);
	}

	public ProtocolEncoder getEncoder(IoSession session) {
		return encoder;
	}

	public ProtocolDecoder getDecoder(IoSession session) {
		return decoder;
	}

	public int getEncoderMaxObjectSize() {
		return encoder.getMaxObjectSize();
	}

	public void setEncoderMaxObjectSize(int maxObjectSize) {
		encoder.setMaxObjectSize(maxObjectSize);
	}

	public int getDecoderMaxObjectSize() {
		return decoder.getMaxObjectSize();
	}

	public void setDecoderMaxObjectSize(int maxObjectSize) {
		decoder.setMaxObjectSize(maxObjectSize);
	}
}
