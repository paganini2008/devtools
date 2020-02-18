package com.github.paganini2008.devtools.scheduler;

import com.github.paganini2008.devtools.multithreads.Executable;

/**
 * 
 * Task
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Task extends Executable {

	default Cancellable cancellable() {
		return Cancellables.cancelIfRuns(-1);
	}

}
