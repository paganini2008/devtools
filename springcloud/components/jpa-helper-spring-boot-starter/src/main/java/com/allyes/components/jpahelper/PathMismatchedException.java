package com.allyes.components.jpahelper;

/**
 * 
 * PathMismatchedException
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public class PathMismatchedException extends IllegalArgumentException {

	private static final long serialVersionUID = 6298298843692196713L;

	public PathMismatchedException(String alias, String attributeName) {
		super(alias + "." + attributeName);
	}

}
