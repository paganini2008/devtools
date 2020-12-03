package com.github.paganini2008.devtools.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SocketHandler;

/**
 * 
 * Handlers
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class Handlers {

	public static Handler socketHandler(String host, int port, String level) throws IOException {
		SocketHandler handler = new SocketHandler(host, port);
		handler.setLevel(Levels.getByName(level));
		return handler;
	}

	public static Handler fileHandler(String pattern, int limit, String level) throws IOException {
		return fileHandler(pattern, limit, 1, true, level);
	}

	public static Handler fileHandler(String pattern, int limit, int count, boolean append, String level) throws IOException {
		FileHandler handler = new FileHandler(pattern, limit, count, append);
		handler.setLevel(Levels.getByName(level));
		return handler;
	}
	
	private Handlers() {
	}

}
