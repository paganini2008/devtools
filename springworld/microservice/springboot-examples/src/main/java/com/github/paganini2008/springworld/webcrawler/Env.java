package com.github.paganini2008.springworld.webcrawler;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * Env
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public class Env {

	public static int getPid() {
		try {
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			return Integer.parseInt(runtimeMXBean.getName().split("@")[0]);
		} catch (Exception e) {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getInitParameters(String[] arguments) {
		if (arguments == null || arguments.length == 0) {
			return Collections.EMPTY_MAP;
		}
		Map<String, String> params = new HashMap<String, String>();
		for (String argument : arguments) {
			if (argument.startsWith("-D")) {
				String arg = argument.substring(2);
				if (StringUtils.isNotBlank(arg)) {
					int index = arg.indexOf("=");
					if (index > 0) {
						params.put(arg.substring(0, index).trim(), arg.substring(index + 1).trim());
					}
				}
			}
		}
		return params;
	}

	private Env() {
	}

	public static void main(String[] args) {
	}

}
