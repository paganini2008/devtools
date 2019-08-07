package com.github.paganini2008.springworld.blogonline.service;

import org.springframework.stereotype.Service;

import com.github.paganini2008.springworld.blogonline.dao.ArticleDao;
import com.github.paganini2008.springworld.blogonline.entity.Article;
import com.github.paganini2008.springworld.support.BaseService;

/**
 * 
 * ArticleService
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Service
public class ArticleService extends BaseService<Article, Long, ArticleDao> {

	public ArticleService(ArticleDao dao) {
		super(dao);
	}

}
