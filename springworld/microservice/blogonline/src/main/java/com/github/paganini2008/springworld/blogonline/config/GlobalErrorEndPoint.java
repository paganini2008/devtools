package com.github.paganini2008.springworld.blogonline.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.springworld.support.vo.ResultVO;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * GlobalErrorEndPoint
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@Slf4j
@RestController
public class GlobalErrorEndPoint extends AbstractErrorController {

	private static final String ERROR_PATH = "/error";
	private static final String CODE_PATTERN = "HTTP_STATUS_%s_FAULT";

	@Autowired
	public GlobalErrorEndPoint(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	@RequestMapping(value = ERROR_PATH, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ResultVO<Object>> error(HttpServletRequest request, HttpServletResponse response) {
		final Map<String, Object> body = getErrorAttributes(request, true);
		log.error("ErrorAttributes: " + body.toString());
		ResultVO<Object> rs = new ResultVO<Object>();
		HttpStatusCode status = HttpStatusCode.valueOf(response.getStatus());
		rs.setCode(String.format(CODE_PATTERN, status.name().toUpperCase()));
		rs.setMsg(status.getRepresent());
		rs.setErrorMsg((String) body.get("error"));
		rs.setRequestUrl((String) body.get("path"));
		rs.setSuccess(false);
		rs.setResponseStatus(status.getStatus().value());
		if (request.getAttribute("sign") != null) {
			long sign = (Long) request.getAttribute("sign");
			rs.setElapsed(System.currentTimeMillis() - sign);
		}
		return new ResponseEntity<ResultVO<Object>>(rs, HttpStatus.OK);
	}

}
