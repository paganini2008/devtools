package com.github.paganini2008.devtools;

/**
 * NumberOverflowException
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class NumberOverflowException extends IllegalArgumentException {

	private static final long serialVersionUID = 783931000803834979L;
	
	public NumberOverflowException() {
		super();
	}

	public NumberOverflowException(Number number) {
		super(number.toString());
	}

}
