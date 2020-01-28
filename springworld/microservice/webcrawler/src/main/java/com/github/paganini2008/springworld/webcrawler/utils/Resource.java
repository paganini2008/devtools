package com.github.paganini2008.springworld.webcrawler.utils;

import java.io.Serializable;
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
public class Resource implements Serializable {

	private static final long serialVersionUID = -4629236151028422706L;
	private Long id;
	private String title;
	private String html;
	private String url;
	private String type;
	private Date createDate;
	private Integer version;
	private Long sourceId;

	public String toString() {
		return "[Resource] id: " + id + ", title: " + title + ", url: " + url;
	}

}
