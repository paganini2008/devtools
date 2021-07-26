/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
 * @since 2.0.1
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
