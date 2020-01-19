package com.github.paganini2008.springworld.webcrawler.search;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * IndexedResource
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Getter
@Setter
@Document(indexName = "ind_webcrawler_resource", type = "resource")
public class IndexedResource {

	@Id
	@Field(type = FieldType.Keyword, store = true, index = false)
	private String id;

	@Field(type = FieldType.Text, store = true, index = true, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
	private String title;

	@Field(type = FieldType.Text, store = true, index = true, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
	private String content;

	@Field(type = FieldType.Keyword, store = true, index = false)
	private String url;

	@Field(type = FieldType.Keyword, store = true, index = false)
	private String type;

	@Field(type = FieldType.Integer, store = true, index = false)
	private Integer order;

	@Field(type = FieldType.Date, store = true, index = false)
	private Date createDate;
	
	public String toString() {
		return "[IndexedResource] title: " + title + ", url: " + url;
	}

}
