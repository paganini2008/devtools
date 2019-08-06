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
 * ArticleComment
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@Entity
@Comment("文章评论表")
@Table(name = "blog_article_comment")
public class ArticleComment extends BaseEntity {

	private static final long serialVersionUID = -1739225198206517797L;

	@ManyToOne(targetEntity = Article.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "article_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("关联文章")
	private Article article;

	@ManyToOne(targetEntity = Blogger.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "blogger_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("评论者")
	private Blogger blogger;

	@Column(name = "content", nullable = true, columnDefinition = "TEXT")
	@Comment("内容")
	private String content;

	@Column(name = "quotation", nullable = true)
	@Comment("上级评论id")
	private Long referId;

	public ArticleComment() {
	}

	public ArticleComment(Long id) {
		this.id = id;
	}
}
