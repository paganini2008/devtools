package com.allyes.mec.cloud.code.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.allyes.developer.utils.ArrayUtils;
import com.allyes.developer.utils.io.ExceptionUtils;
import com.allyes.mec.cloud.ResultVO;
import com.allyes.mec.common.BizException;
import com.allyes.mec.common.utils.TipInfoUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 * 
 * @author ken_guo
 *
 */
@Slf4j
@Order(200)
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = BizException.class)
	public ResponseEntity<ResultVO<?>> handleBizException(HttpServletRequest request, BizException e) throws Exception {
		log.error(e.getMessage(), e);

		ResultVO<Object> rs = new ResultVO<Object>();
		BizException cause = (BizException) e;
		rs.setCode(cause.getCode());
		String msg;
		if (ArrayUtils.isNotEmpty(cause.getArguments())) {
			msg = TipInfoUtils.getMessage(cause.getCode(), cause.getArguments(), cause.getDefaultMessage());
		} else {
			msg = TipInfoUtils.getMessage(cause.getCode(), cause.getDefaultMessage());
		}
		rs.setMsg(msg);
		rs.setRequestUrl(request.getServletPath());
		rs.setErrorMsg(cause.getMessage());
		rs.setErrorDetails(cause.getStackTraceDetails());
		rs.setSuccess(false);
		rs.setResponseStatus(500);
		if (request.getAttribute("sign") != null) {
			long sign = (Long) request.getAttribute("sign");
			rs.setElapsed(System.currentTimeMillis() - sign);
		}
		return new ResponseEntity<ResultVO<?>>(rs, HttpStatus.OK);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ResultVO<?>> handleException(HttpServletRequest request, Exception e) throws Exception {
		log.error(e.getMessage(), e);
		ResultVO<Object> rs = new ResultVO<>();
		rs.setMsg("系统内部错误");
		rs.setRequestUrl(request.getServletPath());
		rs.setErrorMsg(e.getMessage());
		rs.setErrorDetails(ExceptionUtils.toArray(e));
		rs.setCode("INTERNAL_SERVER_ERROR_FAULT");
		rs.setSuccess(false);
		rs.setResponseStatus(500);
		if (request.getAttribute("sign") != null) {
			long sign = (Long) request.getAttribute("sign");
			rs.setElapsed(System.currentTimeMillis() - sign);
		}
		return new ResponseEntity<ResultVO<?>>(rs, HttpStatus.OK);
	}

}