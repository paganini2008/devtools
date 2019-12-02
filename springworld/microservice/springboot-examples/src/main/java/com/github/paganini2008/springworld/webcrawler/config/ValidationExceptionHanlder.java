package com.github.paganini2008.springworld.webcrawler.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.regex.RegexUtils;
import com.github.paganini2008.springworld.webcrawler.pojo.ResultVO;
import com.github.paganini2008.springworld.webcrawler.utils.TipInfoService;

/**
 * 
 * ValidationExceptionHanlder
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@Order(100)
@RestControllerAdvice
public class ValidationExceptionHanlder {

	private static final Logger logger = LoggerFactory.getLogger(ValidationExceptionHanlder.class);
	private static final String PATTERN_PLACEHOLDER = "\\{(.*)\\}";
	private static final String CODE_PATTERN = "HTTP_STATUS_%s_FAULT";

	@Autowired
	private TipInfoService tipInfoService;

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResultVO<?>> handleValidationException(HttpServletRequest request, MethodArgumentNotValidException e)
			throws JsonProcessingException {
		List<ObjectError> errors = e.getBindingResult().getAllErrors();
		ObjectError firstError = errors.get(0);
		String field = ((FieldError) firstError).getField();
		String msg = firstError.getDefaultMessage();
		msg = getMsg(msg);
		logger.info("Field: {}, Msg: {}", field, msg);
		ResultVO<?> resultVO = ResultVO.failure(msg);
		resultVO.setRequestUrl(request.getServletPath());
		HttpStatusCode statusCode = HttpStatusCode.BAD_REQUEST;
		resultVO.setCode(String.format(CODE_PATTERN, statusCode.name().toUpperCase()));
		resultVO.setErrorMsg(statusCode.getRepresent());
		resultVO.setResponseStatus(statusCode.getStatus().value());
		if (request.getAttribute("sign") != null) {
			long sign = (Long) request.getAttribute("sign");
			resultVO.setElapsed(System.currentTimeMillis() - sign);
		}
		return new ResponseEntity<ResultVO<?>>(resultVO, HttpStatus.OK);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ResultVO<?>> handleValidationException(HttpServletRequest request, ConstraintViolationException e) {
		List<ConstraintViolation<?>> errors = new ArrayList<ConstraintViolation<?>>(e.getConstraintViolations());
		ConstraintViolation<?> firstError = errors.get(0);
		String path = firstError.getPropertyPath().toString();
		String msg = firstError.getMessage();
		msg = getMsg(msg);
		logger.info("Path: {}, Msg: {}", path, msg);
		ResultVO<?> resultVO = ResultVO.failure(msg);
		resultVO.setRequestUrl(request.getServletPath());
		HttpStatusCode statusCode = HttpStatusCode.BAD_REQUEST;
		resultVO.setCode(String.format(CODE_PATTERN, statusCode.name().toUpperCase()));
		resultVO.setErrorMsg(statusCode.getRepresent());
		resultVO.setResponseStatus(statusCode.getStatus().value());
		if (request.getAttribute("sign") != null) {
			long sign = (Long) request.getAttribute("sign");
			resultVO.setElapsed(System.currentTimeMillis() - sign);
		}
		return new ResponseEntity<ResultVO<?>>(resultVO, HttpStatus.OK);
	}

	private String getMsg(String represent) {
		String msgCode = RegexUtils.match(represent, PATTERN_PLACEHOLDER, 0, 1, 0);
		String code = null, defaultMsg = msgCode;
		int index = msgCode.indexOf(':');
		if (index > 0) {
			code = msgCode.substring(0, index);
			defaultMsg = msgCode.substring(index + 1);
		}
		String realMsg = defaultMsg;
		if (StringUtils.isNotBlank(code)) {
			try {
				realMsg = this.tipInfoService.getTipMsg(code, defaultMsg);
			} catch (Exception ignored) {
				logger.error(ignored.getMessage(), ignored);
			}
		}
		return realMsg;
	}

}
