package com.allyes.mec.cloud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * ArticleType
 * 
 * @author Fred Feng
 *
 */
@Getter
@Setter
@Entity
@Comment("文章分类表")
@Table(name = "mec_article_type")
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
