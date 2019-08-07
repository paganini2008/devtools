package com.github.paganini2008.springworld.blogonline.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.github.paganini2008.springworld.support.BaseEntity;
import com.github.paganini2008.springworld.support.Comment;
import com.github.paganini2008.springworld.support.enums.Gender;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * Blogger
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Comment("博主表")
@Table(name = "blog_blogger")
public class Blogger extends BaseEntity {

	private static final long serialVersionUID = 5562371131507292009L;

	@Column(name = "phone", nullable = false, length = 50)
	@Comment("电话")
	private String phone;

	@Column(name = "username", nullable = false, length = 255)
	@Comment("用户名")
	private String username;

	@Column(name = "password", nullable = true, length = 255)
	private String password;

	@Column(name = "nickname", nullable = true, length = 255)
	@Comment("昵称")
	private String nickname;

	@Column(name = "address", nullable = true, length = 50)
	private String address;

	@Column(name = "head_url", nullable = true, length = 1024)
	@Comment("头像url")
	private String headUrl;

	@Column(name = "qrcode_url", nullable = true, length = 1024)
	private String qrcodeUrl;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "gender", nullable = true)
	@Comment("性别: 0.男 1.女")
	private Gender gender;

	@Column(name = "sort", nullable = true)
	@Comment("排序")
	private Integer sort = 0;

	@Column(name = "enbaled", nullable = true)
	@org.hibernate.annotations.Type(type = "yes_no")
	@Comment("用户状态: N.冻结 Y.正常")
	private Boolean enbaled = true;

	@Column(name = "certified", nullable = true)
	@org.hibernate.annotations.Type(type = "yes_no")
	@Comment("博主类型: Y.认证博主 N.普通用户")
	private Boolean certified = false;

	@Column(name = "sign", nullable = true, length = 255)
	@Comment("签名")
	private String sign;

	public Blogger() {
	}

	public Blogger(Long id) {
		this.id = id;
	}
}
