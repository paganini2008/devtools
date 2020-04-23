package com.github.paganini2008.devtools.nio;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

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
		return null;
	}

	public static enum EventType {

		ACCEPTABLE(SelectionKey.OP_ACCEPT),

		CONNECTABLE(SelectionKey.OP_CONNECT),

		READABLE(SelectionKey.OP_READ),

		WRITEABLE(SelectionKey.OP_WRITE);

		private final int operationCode;

		private EventType(int operationCode) {
			this.operationCode = operationCode;
		}

		public int getOperationCode() {
			return operationCode;
		}

		private static final Map<Integer, EventType> types = new HashMap<Integer, EventType>();

		static {
			for (EventType eventType : EventType.values()) {
				types.put(eventType.getOperationCode(), eventType);
			}
		}

		public static EventType get(int operationCode) {
			return types.get(operationCode);
		}

	}

}
