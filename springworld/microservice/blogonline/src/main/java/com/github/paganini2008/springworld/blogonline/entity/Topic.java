package com.github.paganini2008.springworld.blogonline.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.github.paganini2008.springworld.support.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * Topic
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "blog_topic")
public class Topic extends BaseEntity {

	private static final long serialVersionUID = 8588425426037806552L;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "header_url", nullable = true, length = 1024)
	private String headerUrl;

	@Column(name = "description", nullable = true, length = 1024)
	private String description;

	@Column(name = "sort", nullable = true)
	private Integer sort = 0;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "hidden", nullable = true)
	@org.hibernate.annotations.Type(type = "yes_no")
	private Boolean hidden = false;

	@ManyToOne(targetEntity = Blogger.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(nullable = true, name = "blogger_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Blogger blogger;

	public Topic() {
	}

	public Topic(Long id) {
		this.id = id;
	}

}
