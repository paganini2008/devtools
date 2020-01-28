
package com.github.paganini2008.springworld.webcrawler.utils;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.ExceptionUtils;
import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * BizException
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-05
 * @version 1.0
 */
public class BizException extends RuntimeException {

	private static final long serialVersionUID = -4831317450198278821L;

	public BizException(String code, String defaultMessage) {
		this(code, ArrayUtils.EMPTY_OBJECT_ARRAY, defaultMessage);
	}

	public BizException(String code, Object[] arguments, String defaultMessage) {
		super(code);
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
	}

	public BizException(String code, String defaultMessage, Throwable e) {
		this(code, ArrayUtils.EMPTY_OBJECT_ARRAY, defaultMessage, e);
	}

	public BizException(String code, Object[] arguments, String defaultMessage, Throwable e) {
		super(code, e);
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
	}

	private final String defaultMessage;
	private final Object[] arguments;

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public String getCode() {
		return super.getMessage();
	}

	public Object[] getArguments() {
		return arguments;
	}

	public String getMessage() {
		String msg = super.getMessage();
		return StringUtils.isNotBlank(msg) ? msg : defaultMessage;
	}

	public String[] getStackTraceDetails() {
		return ExceptionUtils.toArray(getCause());
	}

}
