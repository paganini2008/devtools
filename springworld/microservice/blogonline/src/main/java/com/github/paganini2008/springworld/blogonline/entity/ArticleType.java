package com.github.paganini2008.springworld.blogonline.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.github.paganini2008.springworld.support.BaseEntity;
import com.github.paganini2008.springworld.support.Comment;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * ArticleType
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Comment("文章分类表")
@Table(name = "blog_article_type")
public class ArticleType extends BaseEntity {

	private static final long serialVersionUID = -4052832399346504550L;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "description", nullable = false, length = 255)
	private String description;

	@Column(name = "sort", nullable = true)
	private Integer sort = 0;

	public ArticleType() {
	}

	public ArticleType(Long id) {
		this.id = id;
	}
}
