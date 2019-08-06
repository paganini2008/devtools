package com.github.paganini2008.springworld.support.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * ResultVO
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
@Getter
@Setter
@ApiModel(description = "结果集数据")
public class ResultVO<T> implements Serializable {

	private static final long serialVersionUID = -5079368708333321873L;

	@ApiModelProperty("返回数据")
	private T data;

	@ApiModelProperty("操作标识")
	private boolean success;

	@ApiModelProperty("错误码")
	private String code = "";

	@ApiModelProperty(value = "消息提示信息")
	private String msg;

	@ApiModelProperty(value = "请求路径")
	private String requestUrl;

	@ApiModelProperty(value = "错误简短信息", hidden = true)
	private String errorMsg;
	
	@ApiModelProperty(value = "错误堆栈信息", hidden = true)
	private String[] errorDetails;

	@ApiModelProperty(value = "请求响应耗时")
	private long elapsed = 0L;

	@ApiModelProperty(value = "HTTP响应码", hidden = true)
	private int responseStatus = 200;

	public ResultVO() {
	}

	public ResultVO(T data) {
		this.data = data;
	}

	public static <T> ResultVO<T> success() {
		return success("ok", null);
	}

	public static <T> ResultVO<T> success(T data) {
		return success("操作成功", data);
	}

	public static <T> ResultVO<T> success(String msg, T data) {
		ResultVO<T> rs = new ResultVO<T>(data);
		rs.setSuccess(true);
		rs.setMsg(msg);
		return rs;
	}

	public static <T> ResultVO<T> failure() {
		return failure("error");
	}

	public static <T> ResultVO<T> failure(String msg) {
		return failure(msg, null);
	}

	public static <T> ResultVO<T> failure(String msg, T data) {
		ResultVO<T> rs = new ResultVO<T>(data);
		rs.setSuccess(false);
		rs.setMsg(msg);
		return rs;
	}

	public String toString() {
		return ReflectionToStringBuilder.toStringExclude(this, new String[] { "data", "errorDetails" });
	}

}
