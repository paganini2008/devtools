package com.github.paganini2008.springworld.cluster.pool;

import java.io.Serializable;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * SignatureInfo
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Getter
@Setter
public class SignatureInfo implements Signature, Serializable {

	private static final long serialVersionUID = -5401293046063974728L;

	private String beanName;
	private String beanClassName;
	private String methodName;
	private Object[] arguments = new Object[0];
	private final long timestamp;

	public SignatureInfo(String beanName, String beanClassName, String methodName) {
		this.beanName = beanName;
		this.beanClassName = beanClassName;
		this.methodName = methodName;
		this.timestamp = System.currentTimeMillis();
	}

	public SignatureInfo() {
		this.timestamp = System.currentTimeMillis();
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[beanName: ").append(beanName).append("] ");
		str.append(beanClassName).append(".").append(methodName).append("(").append(Arrays.toString(arguments)).append(")");
		return str.toString();
	}

}
