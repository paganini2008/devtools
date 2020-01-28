package com.github.paganini2008.springworld.webcrawler.search;

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
@Document(indexName = "webcrawler_resources_0", type = "resource", shards = 3, replicas = 1)
public class IndexedResource {

	@Id
	@Field(type = FieldType.Long, store = true)
	private Long id;

	@Field(type = FieldType.Text, store = true, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
	private String title;

	@Field(type = FieldType.Text, store = true, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
	private String content;

	@Field(type = FieldType.Keyword, store = true)
	private String url;

	@Field(type = FieldType.Keyword, store = true)
	private String path;

	@Field(type = FieldType.Keyword, store = true)
	private String type;

	@Field(type = FieldType.Keyword, store = true)
	private String source;

	@Field(type = FieldType.Integer, store = true)
	private Integer order;

	@Field(type = FieldType.Long, store = true)
	private Long createDate;

	public String toString() {
		return "[IndexedResource] title: " + title + ", url: " + url;
	}

}
