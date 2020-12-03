package com.github.paganini2008.devtools.io;

/**
 * 
 * ConfigError
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ConfigError extends Error {

	private static final long serialVersionUID = 5718003403300727778L;

	public ConfigError(String msg) {
		super(msg);
	}

	public ConfigError(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ConfigError(Throwable cause) {
		super(cause);
	}

}
