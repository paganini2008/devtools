package com.github.paganini2008.springworld.blogonline.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.github.paganini2008.springworld.support.BaseEntity;
import com.github.paganini2008.springworld.support.Comment;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * ArticleTopic
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Comment("文章话题表")
@Table(name = "blog_article_topic")
public class ArticleTopic extends BaseEntity {

	private static final long serialVersionUID = -8391358748013283159L;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(targetEntity = Article.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "article_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("关联文章")
	private Article article;

	@ManyToOne(targetEntity = Topic.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "topic_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("关联话题")
	private Topic topic;

	public ArticleTopic() {
	}

	public ArticleTopic(Long id) {
		this.id = id;
	}

}
