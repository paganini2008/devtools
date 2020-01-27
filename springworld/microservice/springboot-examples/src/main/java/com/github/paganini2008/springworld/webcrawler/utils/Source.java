package com.github.paganini2008.springworld.webcrawler.utils;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * Source
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class Source implements Serializable {

	private static final long serialVersionUID = 1980884447290929341L;
	private Long id;
	private String name;
	private String type;
	private String url;
	private String pathPattern;
	private String excludedPathPattern;
	private Date createDate;

}
