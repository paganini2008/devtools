package com.github.paganini2008.springworld.support.logback;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.github.paganini2008.devtools.StringUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * 
 * MarkerThresholdFilter
 * 
 * @author Fred Feng
 * @create 2019-03
 */
public class MarkerThresholdFilter extends AbstractMatcherFilter<ILoggingEvent> {

	Marker marker;
	Level level;

	@Override
	public void start() {
		if (this.level != null) {
			super.start();
		}
	}

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (!isStarted()) {
			return FilterReply.NEUTRAL;
		}
		Marker currentMarker = event.getMarker();
		if (currentMarker != null && marker != null) {
			if (marker.contains(currentMarker)) {
				return onMatch;
			}
		} else {
			if (event.getLevel().isGreaterOrEqual(level)) {
				return FilterReply.NEUTRAL;
			} else {
				return FilterReply.DENY;
			}
		}
		return onMismatch;
	}

	public void setLevel(String level) {
		if (StringUtils.isNotBlank(level)) {
			this.level = Level.toLevel(level);
		}
	}

	public void setMarker(String markerStr) {
		if (StringUtils.isNotBlank(markerStr)) {
			marker = MarkerFactory.getMarker(markerStr);
		}
	}
}
