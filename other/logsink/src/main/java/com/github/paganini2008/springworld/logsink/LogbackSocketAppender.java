package com.github.paganini2008.springworld.logsink;

import com.github.paganini2008.springworld.socketbird.Tuple;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

/**
 * 
 * LogbackSocketAppender
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class LogbackSocketAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	private NioClient nioClient;

	private String host;
	private int port;

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	protected void append(ILoggingEvent eventObject) {
		Tuple tuple = Tuple.newTuple();
		tuple.setField("message", eventObject.getFormattedMessage());
		tuple.setField("level", eventObject.getLevel().levelStr);
		tuple.setField("cause", ThrowableProxyUtil.asString(eventObject.getThrowableProxy()));
		tuple.setField("timestamp", eventObject.getTimeStamp());
		nioClient.send(tuple);
	}

	@Override
	public void start() {
		nioClient = new NettyClient();
		try {
			nioClient.connect(host, port);
			super.start();
		} catch (Exception e) {
			addError("LogbackSocketAppender cannot start.", e);
		}
	}

	@Override
	public void stop() {
		super.stop();

		if (nioClient != null) {
			nioClient.close();
		}
	}

}
