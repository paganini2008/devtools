package com.github.paganini2008.springworld.cluster.pool;

/**
 * 
 * InvocationResult
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public final class InvocationResult {

	private final ThreadLocal<Boolean> threadLocal = new ThreadLocal<Boolean>() {

		@Override
		protected Boolean initialValue() {
			return Boolean.FALSE;
		}
		
	};
	
	public void setCompleted() {
		threadLocal.set(Boolean.TRUE);
	}
	
	public boolean isCompleted() {
		boolean result = threadLocal.get();
		threadLocal.remove();
		return result;
	}

}
