package com.github.paganini2008.springworld.webcrawler.utils;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * SourceIndex
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class SourceIndex implements Serializable {

	private static final long serialVersionUID = 599930283370705308L;

	private Long id;
	private Long sourceId;
	private Date lastIndexedDate;
	private Integer version;

}
