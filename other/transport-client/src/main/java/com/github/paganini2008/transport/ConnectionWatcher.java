package com.github.paganini2008.transport;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * ConnectionWatcher
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public final class ConnectionWatcher {

	private final int interval;
	private final TimeUnit timeUnit;
	private final NioConnection connection;

	public ConnectionWatcher(int interval, TimeUnit timeUnit, NioConnection connection) {
		this.interval = interval;
		this.timeUnit = timeUnit;
		this.connection = connection;
	}

	private final Observable observable = Observable.unrepeatable();

	public void reconnect(SocketAddress remoteAddress) {
		observable.notifyObservers(remoteAddress);
	}

	public void watch(final SocketAddress remoteAddress, final HandshakeCallback callback) {
		observable.addObserver((ob, arg) -> {
			connection.connect(remoteAddress, callback);
			while (!connection.isConnected(remoteAddress)) {
				ThreadUtils.sleep(interval, timeUnit);
				connection.connect(remoteAddress, callback);
			}
		});
	}

}
