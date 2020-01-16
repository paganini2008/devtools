package com.github.paganini2008.springworld.webcrawler.utils;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

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
public class Resource {

	private String id;
	private String title;
	private String html;
	private String url;
	private String type;
	private Date createDate;
	private Integer version;

	public String toString() {
		return "[Resource] title: " + title + ", url: " + url + ", version: " + version;
	}

}
