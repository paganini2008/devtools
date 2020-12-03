package com.github.paganini2008.devtools.cron4j;

import com.github.paganini2008.devtools.multithreads.Executable;

/**
 * 
 * Task
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Task extends Executable {

	default Cancellable cancellable() {
		return Cancellables.cancelIfRuns(-1);
	}

}
