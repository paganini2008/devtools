package com.github.paganini2008.springworld.blogonline.entity;

import javax.persistence.CascadeType;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.github.paganini2008.springworld.support.BaseEntity;
import com.github.paganini2008.springworld.support.Comment;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * ArticleClick
 *
 * @author Fred Feng
 * @created 2019-07
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Comment("文章点击表")
@Table(name = "blog_article_click")
public class ArticleClick extends BaseEntity {

	private static final long serialVersionUID = -1615831514095437031L;

	@ManyToOne(targetEntity = Article.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "article_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("关联文章")
	private Article article;

	@ManyToOne(targetEntity = Blogger.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(nullable = true, name = "blogger_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("关联博主(用户)")
	private Blogger blogger;

}
