package com.github.paganini2008.devtools.beans;

/**
 * PropertyEditorException
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class PropertyEditorException extends RuntimeException {

	private static final long serialVersionUID = 2675526285967166675L;

	public PropertyEditorException(String msg, String propertyName) {
		super(msg);
		this.propertyName = propertyName;
	}

	public PropertyEditorException(String msg, String propertyName, Throwable e) {
		super(msg, e);
		this.propertyName = propertyName;
	}

	private final String propertyName;

	public String toString() {
		return super.toString() + ", PropertyName: " + propertyName;
	}

}
