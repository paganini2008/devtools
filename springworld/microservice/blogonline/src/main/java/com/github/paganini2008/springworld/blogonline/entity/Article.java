package com.github.paganini2008.springworld.blogonline.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.github.paganini2008.springworld.support.BaseEntity;
import com.github.paganini2008.springworld.support.Comment;

/**
 * 
 * Article
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@Entity
@Comment("文章表")
@Table(name = "blog_article")
public class Article extends BaseEntity {

	private static final long serialVersionUID = 341390762846075624L;

	@Column(name = "title", nullable = false, length = 255)
	private String title;

	@Column(name = "content", nullable = true, columnDefinition = "TEXT")
	private String content;

	@Column(name = "header_url", nullable = true, length = 1024)
	private String headerUrl;

	@ManyToOne(targetEntity = ArticleType.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(nullable = true, name = "article_type_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("关联文章类型表")
	private ArticleType articleType;

	@ManyToOne(targetEntity = Blogger.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(nullable = true, name = "blogger_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("发布人")
	private Blogger blogger;

	@Column(name = "sort", nullable = true)
	private Integer sort = 0;

	@Column(name = "visible", nullable = true)
	@org.hibernate.annotations.Type(type = "yes_no")
	@Comment("是否隐藏 Y.显示,N.隐藏")
	private Boolean visible = true;

	public Article() {
	}

	public Article(Long id) {
		this.id = id;
	}

}
