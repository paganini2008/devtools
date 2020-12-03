package com.github.paganini2008.devtools.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * 
 * Configuration
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class Configuration {

	private static final String SYS_PROP_PREFIX = "devtools.logging.";

	private static final Comparator<String> nameComparator = new Comparator<String>() {
		public int compare(String left, String right) {
			int result = right.length() - left.length();
			if (result == 0) {
				return left.compareTo(right);
			}
			return result;
		}
	};

	private final Map<String, Handler> handlers = new HashMap<String, Handler>();
	private final Map<String, List<String>> loggings = new TreeMap<String, List<String>>(nameComparator);
	private final Map<String, Level> levels = new TreeMap<String, Level>(nameComparator);

	public void addHandler(String name, Handler handler) {
		if (handler != null) {
			handlers.put(name, handler);
		}
	}

	public void bindHandler(String name, String handler) {
		bindHandler(name, new String[] { handler });
	}

	public void bindHandler(String name, String[] handlers) {
		loggings.put(name, Arrays.asList(handlers));
	}

	public synchronized Handler[] getHandlers(String loggerName) {
		List<Handler> list = new ArrayList<Handler>();
		for (Map.Entry<String, List<String>> entry : loggings.entrySet()) {
			if (loggerName.equals(entry.getKey()) || loggerName.startsWith(entry.getKey())) {
				for (String handlerName : entry.getValue()) {
					if (handlers.containsKey(handlerName)) {
						list.add(handlers.get(handlerName));
					}
				}
			}
		}
		return list.toArray(new Handler[0]);
	}

	public void addLevel(String loggerName, String level) {
		levels.put(loggerName, Levels.getByName(level));
	}

	public synchronized Level getLevel(String loggerName) {
		String syskey = SYS_PROP_PREFIX + loggerName;
		if (System.getProperties().containsKey(syskey)) {
			String levelName = System.getProperties().getProperty(syskey);
			return Levels.getByName(levelName);
		}
		for (Map.Entry<String, Level> entry : levels.entrySet()) {
			if (loggerName.equals(entry.getKey()) || loggerName.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return Levels.OFF;
	}

	public static void main(String[] args) {
		Configuration conf = new Configuration();
		conf.addLevel("a.b.c", "info");
		conf.addLevel("a.b.c.Name", "warn");
		conf.addLevel("a.b.c.Pywd", "error");
		conf.addLevel("a.b.c.UserName", "debug");
		System.out.println(conf.getLevel("a.b.c.ABC"));
		System.out.println(conf.getLevel("a.b.c.Name"));
		System.out.println(conf.getLevel("a.b.c.Pywd"));
		System.out.println(conf.getLevel("a.b.c.UserName"));
	}

}
