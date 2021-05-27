package com.github.paganini2008.devtools.collection;

import java.util.Properties;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.Observer;

/**
 * 
 * EventBasedRefreshingProperties
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public abstract class EventBasedRefreshingProperties extends RefreshingProperties {

	private static final long serialVersionUID = 1L;

	private final Observable observable = Observable.repeatable();

	public void addEventListener(final PropertyChangeListener<Properties> listener) {
		observable.addObserver(new Observer() {
			@SuppressWarnings("unchecked")
			public void update(Observable ob, Object arg) {
				PropertyChangeEvent<Properties> event = (PropertyChangeEvent<Properties>) arg;
				listener.onEventFired(event);
			}
		});
	}

	protected final void onChange(Properties lastest, Properties current) {
		observable.notifyObservers(new PropertyChangeEvent<Properties>(lastest, current));
	}

}
