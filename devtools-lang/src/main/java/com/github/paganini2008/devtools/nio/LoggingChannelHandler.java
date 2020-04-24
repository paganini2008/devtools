package com.github.paganini2008.devtools.nio;

import java.io.IOException;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * 
 * LoggingChannelHandler
 *
 * @author Fred Feng
 * @since 1.0
 */
public class LoggingChannelHandler implements ChannelHandler {

	private static final Log log = LogFactory.getLog(LoggingChannelHandler.class);

	@Override
	public void fireChannelActive(Channel channel) throws IOException {
		log.info("Channel is active. Channel info: " + channel);
	}

	@Override
	public void fireChannelInactive(Channel channel) throws IOException {
		log.info("Channel is inactive. Channel info: " + channel);
	}

	@Override
	public void fireChannelReadable(Channel channel, MessagePacket packet) throws IOException {
		log.info("Channel read length: " + packet.getLength());
		packet.getMessages().forEach(data -> {
			log.info(data);
		});
	}

	@Override
	public void fireChannelFatal(Channel channel, Throwable e) {
		log.info("Channel has fatal error. Channel info: " + channel);
		log.error(e.getMessage(), e);
	}

}
