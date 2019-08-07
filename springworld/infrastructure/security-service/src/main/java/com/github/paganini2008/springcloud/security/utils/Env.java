package com.github.paganini2008.springcloud.security.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 
 * Env
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public abstract class Env {

	public static int getPid() {
		try {
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			return Integer.parseInt(runtimeMXBean.getName().split("@")[0]);
		} catch (Exception e) {
			return 0;
		}
	}

}
