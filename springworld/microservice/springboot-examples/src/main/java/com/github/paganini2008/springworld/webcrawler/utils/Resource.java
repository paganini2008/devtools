package com.github.paganini2008.springworld.webcrawler.utils;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * Resource
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class Resource {

	private String title;
	private String content;
	private String path;
	private Date createDate;

}
