package com.github.paganini2008.springworld.support;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * BaseEntity
 * 
 * @author Fred Feng
 * @created 2019-05
 */
@MappedSuperclass
@EntityListeners(BasicEntityListener.class)
@SuppressWarnings("serial")
public abstract class BaseEntity extends AbstractEntity {

	@Id
	@Column(name = "id", nullable = false, unique = true)
	protected Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date", columnDefinition = "timestamp null default current_timestamp")
	protected Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_date", columnDefinition = "timestamp null default current_timestamp")
	protected Date updateDate;

	@Column(name = "del", nullable = true)
	@org.hibernate.annotations.Type(type = "yes_no")
	private Boolean del = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Boolean getDel() {
		return del;
	}

	public void setDel(Boolean del) {
		this.del = del;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(getClass().getSimpleName());
		str.append(" [id=" + id + "]");
		return str.toString();
	}

}
