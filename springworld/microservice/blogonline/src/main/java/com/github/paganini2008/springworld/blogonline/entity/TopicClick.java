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
 * TopicClick
 * 
 * @author Fred Feng
 * @create 2019-03
 */
@Getter
@Setter
@Entity
@Comment("话题点击表")
@Table(name = "blog_topic_click")
public class TopicClick extends BaseEntity {

	private static final long serialVersionUID = -2010563280906353858L;

	@Comment("话题ID")
	@ManyToOne(targetEntity = Topic.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "topic_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Topic topic;

	@Comment("博主ID")
	@ManyToOne(targetEntity = Blogger.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(nullable = true, name = "blogger_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Blogger blogger;

}
