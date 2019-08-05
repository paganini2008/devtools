package com.github.paganini2008.springboot.authorization;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnauthorizedReponseEntity implements Serializable {

	private static final long serialVersionUID = 223263953658254934L;
	private Object data;
	private boolean success;
	private String code = "";
	private String msg;
	private int responseStatus;

	UnauthorizedReponseEntity() {
	}

	public static UnauthorizedReponseEntity newInstance() {
		UnauthorizedReponseEntity responseEntity = new UnauthorizedReponseEntity();
		responseEntity.setSuccess(false);
		responseEntity.setResponseStatus(HttpStatus.UNAUTHORIZED.value());
		responseEntity.setMsg(HttpStatus.UNAUTHORIZED.name());
		responseEntity.setCode("UNAUTHORED_OPERATION_FAULT");
		return responseEntity;
	}

}
