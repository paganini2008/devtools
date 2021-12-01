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
package com.github.paganini2008.devtools.multithreads;

import static com.github.paganini2008.devtools.time.DateUtils.convertToMillis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.beans.EqualsBuilder;
import com.github.paganini2008.devtools.beans.HashCodeBuilder;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ConcurrentTimer
 *
 * @author Fred Feng
 *
 * @since 2.0.3
 */
public class ConcurrentTimer {

	private final Map<TimerFeature, Timer> timers = new ConcurrentHashMap<TimerFeature, Timer>();
	private final Map<TimerFeature, List<Executable>> executables = new ConcurrentHashMap<TimerFeature, List<Executable>>();

	public Timer scheduleWithFixedDelay(Executable e, Date firstTime, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, firstTime, convertToMillis(interval, timeUnit));
	}

	public Timer scheduleWithFixedDelay(Executable e, Date firstTime, long interval) {
		if (firstTime.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + firstTime);
		}
		return scheduleWithFixedDelay(e, firstTime.getTime() - System.currentTimeMillis(), interval);
	}

	public Timer scheduleWithFixedDelay(Executable e, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, interval, interval, timeUnit);
	}

	public Timer scheduleWithFixedDelay(Executable e, long delay, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, delay, timeUnit, interval, timeUnit);
	}

	public Timer scheduleWithFixedDelay(Executable e, long delay, TimeUnit delayTimeUnit, long interval, TimeUnit intervalTimeUnit) {
		return scheduleWithFixedDelay(e, convertToMillis(delay, delayTimeUnit), convertToMillis(interval, intervalTimeUnit));
	}

	public Timer scheduleWithFixedDelay(Executable e, long delay, long interval) {
		TimerFeature feature = new TimerFeature(delay, interval, false);
		List<Executable> list = MapUtils.get(executables, feature, () -> {
			return new CopyOnWriteArrayList<Executable>();
		});
		list.add(e);
		return MapUtils.get(timers, feature, () -> {
			return ThreadUtils.scheduleWithFixedDelay(new SerialExecutable(list), delay, interval);
		});
	}

	public Timer scheduleAtFixedRate(Executable e, Date firstTime, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, firstTime, convertToMillis(interval, timeUnit));
	}

	public Timer scheduleAtFixedRate(Executable e, Date firstTime, long interval) {
		if (firstTime.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + firstTime);
		}
		return scheduleAtFixedRate(e, firstTime.getTime() - System.currentTimeMillis(), interval);
	}

	public Timer scheduleAtFixedRate(Executable e, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, interval, interval, timeUnit);
	}

	public Timer scheduleAtFixedRate(Executable e, long delay, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, delay, timeUnit, interval, timeUnit);
	}

	public Timer scheduleAtFixedRate(Executable e, long delay, TimeUnit delayTimeUnit, long interval, TimeUnit intervalTimeUnit) {
		return scheduleAtFixedRate(e, convertToMillis(delay, delayTimeUnit), convertToMillis(interval, intervalTimeUnit));
	}

	public Timer scheduleAtFixedRate(Executable e, long delay, long interval) {
		TimerFeature feature = new TimerFeature(delay, interval, true);
		List<Executable> list = MapUtils.get(executables, feature, () -> {
			return new CopyOnWriteArrayList<Executable>();
		});
		list.add(e);
		return MapUtils.get(timers, feature, () -> {
			return ThreadUtils.scheduleAtFixedRate(new SerialExecutable(list), delay, interval);
		});
	}

	public void cancel() {
		for (Map.Entry<TimerFeature, Timer> entry : timers.entrySet()) {
			entry.getValue().cancel();
			List<Executable> executables = this.executables.get(entry.getKey());
			if (CollectionUtils.isNotEmpty(executables)) {
				executables.forEach(e -> e.onCancellation(null));
			}
		}
		executables.clear();
		timers.clear();
	}

	/**
	 * 
	 * SerialExecutable
	 *
	 * @author Fred Feng
	 *
	 * @since 2.0.3
	 */
	static class SerialExecutable implements Executable {

		private final Collection<Executable> executables;

		SerialExecutable(Collection<Executable> executables) {
			this.executables = executables;
		}

		@Override
		public boolean execute() throws Throwable {
			boolean always = true, everyTime;
			List<Executable> cancels = new ArrayList<Executable>();
			for (Executable executable : executables) {
				if ((everyTime = executable.execute()) == false) {
					executables.remove(executable);
					cancels.add(executable);
				}
				always |= everyTime;
			}
			for (Executable executable : cancels) {
				executable.onCancellation(null);
			}
			return always;
		}

		@Override
		public boolean onError(Throwable e) {
			boolean always = true, everyTime;
			List<Executable> cancels = new ArrayList<Executable>();
			for (Executable executable : executables) {
				if ((everyTime = executable.onError(e)) == false) {
					executables.remove(executable);
					cancels.add(executable);
				}
				always |= everyTime;
			}
			for (Executable executable : cancels) {
				executable.onCancellation(e);
			}
			return always;
		}

		@Override
		public void onCancellation(Throwable e) {
			for (Executable executable : executables) {
				executable.onCancellation(e);
				executables.remove(executable);
			}
		}
	}

	static class TimerFeature {

		private long delay;
		private long interval;
		private boolean fixed;

		TimerFeature(long delay, long interval, boolean fixed) {
			this.delay = delay;
			this.interval = interval;
			this.fixed = fixed;
		}

		public long getDelay() {
			return delay;
		}

		public void setDelay(long delay) {
			this.delay = delay;
		}

		public long getInterval() {
			return interval;
		}

		public void setInterval(long interval) {
			this.interval = interval;
		}

		public boolean isFixed() {
			return fixed;
		}

		public void setFixed(boolean fixed) {
			this.fixed = fixed;
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}

		@Override
		public boolean equals(Object target) {
			return EqualsBuilder.reflectionEquals(this, target);
		}

	}

}
