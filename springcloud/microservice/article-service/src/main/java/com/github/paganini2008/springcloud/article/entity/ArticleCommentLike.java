package com.allyes.mec.cloud.entity;

import javax.persistence.CascadeType;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * ArticleCommentLike
 * 
 * @author Fred Feng
 *
 */
@Getter
@Setter
@Entity
@Comment("文章评论点赞表")
@Table(name = "mec_article_comment_like")
public class ArticleCommentLike extends BaseEntity {

	private static final long serialVersionUID = -2929369267762789597L;

	@ManyToOne(targetEntity = ArticleComment.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "article_comment_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("关联文章评论")
	private ArticleComment articleComment;

	@ManyToOne(targetEntity = Article.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "article_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("关联文章")
	private Article article;

	@ManyToOne(targetEntity = Blogger.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "blogger_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("关联用户")
	private Blogger blogger;

}
