package com.allyes.mec.cloud.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ArticleComment
 * 
 * @author Fred Feng
 * @create 2019-03
 */
@Entity
@Comment("文章评论表")
@Table(name = "mec_article_comment")
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
	
	@ManyToOne(targetEntity = Blogger.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(nullable = true, name = "refer_blogger_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("上级评论者")
	private Blogger refer;

	@Column(name = "content", nullable = true, columnDefinition = "TEXT")
	@Comment("内容")
	private String content;

	@Column(name = "quotation", nullable = true)
	@Comment("上级评论id")
	private Long quotation;

	@Column(name = "ext1", nullable = true)
	@Comment("评论一级id")
	private Long ext1;

	@Column(name = "ext2", nullable = true)
	@Comment("评论二级id")
	private Long ext2;

	@ManyToOne(targetEntity = Blogger.class, cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(nullable = true, name = "ext3", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	@Comment("一级评论人id")
	private Blogger ext3;

	public ArticleComment() {
	}

	public ArticleComment(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public Blogger getBlogger() {
		return blogger;
	}

	public void setBlogger(Blogger blogger) {
		this.blogger = blogger;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public Long getQuotation() {
		return quotation;
	}

	public void setQuotation(Long quotation) {
		this.quotation = quotation;
	}

	public Blogger getRefer() {
		return refer;
	}

	public void setRefer(Blogger refer) {
		this.refer = refer;
	}

	public Long getExt1() {
		return ext1;
	}

	public void setExt1(Long ext1) {
		this.ext1 = ext1;
	}

	public Long getExt2() {
		return ext2;
	}

	public void setExt2(Long ext2) {
		this.ext2 = ext2;
	}

	public Blogger getExt3() {
		return ext3;
	}

	public void setExt3(Blogger ext3) {
		this.ext3 = ext3;
	}
}
