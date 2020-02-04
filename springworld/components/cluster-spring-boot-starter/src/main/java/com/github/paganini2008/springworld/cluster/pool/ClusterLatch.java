package com.github.paganini2008.springworld.cluster.pool;

import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.multithreads.latch.Latch;
import com.github.paganini2008.springworld.cluster.ContextMasterStandbyEvent;

/**
 * 
 * ClusterLatch
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public interface ClusterLatch extends Latch, ApplicationListener<ContextMasterStandbyEvent> {
}
