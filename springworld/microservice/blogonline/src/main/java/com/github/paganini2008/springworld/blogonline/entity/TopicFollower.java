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

/**
 * 
 * TopicFollower
 *
 * @author Fred Feng
 * @created 2019-07
 * @version 1.0
 */
@Entity
@Table(name = "blog_topic_follower")
public class TopicFollower extends BaseEntity {

	private static final long serialVersionUID = 7386100530365946295L;

	@ManyToOne(targetEntity = Blogger.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "blogger_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Blogger blogger;

	@ManyToOne(targetEntity = Topic.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "topic_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Topic topic;

}
