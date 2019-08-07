package com.github.paganini2008.springworld.config;

import java.io.Serializable;

/**
 * 
 * ResultEntity
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-05
 * @version 1.0
 */
public class ResultEntity<T> implements Serializable {

	private static final long serialVersionUID = -6546340835097498558L;

	private boolean success;
	private String msg;
	private String errorInfo;
	private T body;

	ResultEntity(boolean success, String msg, T body) {
		this.success = success;
		this.msg = msg;
		this.body = body;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

	public static <T> ResultEntity<T> onFailure(String msg) {
		return new ResultEntity<T>(false, msg, null);
	}

	public static <T> ResultEntity<T> onSuccess(T body) {
		return onSuccess("ok", body);
	}

	public static <T> ResultEntity<T> onSuccess(String msg, T body) {
		return new ResultEntity<T>(true, msg, body);
	}

}
