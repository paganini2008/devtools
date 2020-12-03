package com.github.paganini2008.devtools;

/**
 * ShutdownHook
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class ShutdownHook implements Comparable<ShutdownHook> {

	protected abstract void process();

	protected void ignoreException(Throwable e) {
	}

	protected int getPriority() {
		return 0;
	}

	public int compareTo(ShutdownHook other) {
		return getPriority() - other.getPriority();
	}

}
