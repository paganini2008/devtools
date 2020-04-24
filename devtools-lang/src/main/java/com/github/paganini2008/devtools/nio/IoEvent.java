package com.github.paganini2008.devtools.nio;

import java.nio.channels.SelectionKey;

import com.github.paganini2008.devtools.event.Event;

/**
 * 
 * IoEvent
 *
 * @author Fred Feng
 * @since 1.0
 */
public class IoEvent extends Event<Object> {

	private static final long serialVersionUID = 836117816543430982L;
	
	public IoEvent(Reactor source, SelectionKey selectionKey) {
		this(source, selectionKey, null);
	}

	public IoEvent(Reactor source, SelectionKey selectionKey, Throwable cause) {
		super(source, null);
		this.selectionKey = selectionKey;
		this.cause = cause;
	}

	private final SelectionKey selectionKey;
	private final Throwable cause;

	public SelectionKey getSelectionKey() {
		return selectionKey;
	}

	public Throwable getCause() {
		return cause;
	}

	public EventType getEventType() {
		if (selectionKey.isAcceptable()) {
			return EventType.ACCEPTABLE;
		} else if (selectionKey.isConnectable()) {
			return EventType.CONNECTABLE;
		} else if (selectionKey.isReadable()) {
			return EventType.READABLE;
		} else if (selectionKey.isWritable()) {
			return EventType.WRITEABLE;
		}
		throw new IllegalStateException();
	}

	public static enum EventType {

		ACCEPTABLE(SelectionKey.OP_ACCEPT, false),

		CONNECTABLE(SelectionKey.OP_CONNECT, false),

		READABLE(SelectionKey.OP_READ, false),

		WRITEABLE(SelectionKey.OP_WRITE, true);

		private final int operationCode;
		private final boolean asynchronous;

		private EventType(int operationCode, boolean asynchronous) {
			this.operationCode = operationCode;
			this.asynchronous = asynchronous;
		}

		public int getOperationCode() {
			return operationCode;
		}

		public boolean isAsynchronous() {
			return asynchronous;
		}

	}

}
