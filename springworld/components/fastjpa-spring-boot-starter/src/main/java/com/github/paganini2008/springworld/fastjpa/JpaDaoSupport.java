package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

/**
 * 
 * JpaDaoSupport
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public class JpaDaoSupport<E, ID> extends SimpleJpaRepository<E, ID> implements JpaCustomUpdate<E>, JpaCustomQuery<E> {

	public JpaDaoSupport(Class<E> entityClass, EntityManager em) {
		super(entityClass, em);
		this.em = em;
	}

	protected final EntityManager em;

	public <T> T getSingleResult(JpaQueryCallback<T> callback) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = callback.doInJpa(builder);
		TypedQuery<T> typedQuery = em.createQuery(query);
		return typedQuery.getSingleResult();
	}

	public <T> List<T> getResultList(JpaQueryCallback<T> callback) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = callback.doInJpa(builder);
		TypedQuery<T> typedQuery = em.createQuery(query);
		return typedQuery.getResultList();
	}

	public <T> List<T> getResultList(JpaQueryCallback<T> callback, int maxResults, int firstResult) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = callback.doInJpa(builder);
		TypedQuery<T> typedQuery = em.createQuery(query);
		if (firstResult >= 0) {
			typedQuery.setFirstResult(firstResult);
		}
		if (maxResults > 0) {
			typedQuery.setMaxResults(maxResults);
		}
		return typedQuery.getResultList();
	}

	public int executeUpdate(JpaDeleteCallback<E> callback) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaDelete<E> delete = callback.doInJpa(builder);
		Query query = em.createQuery(delete);
		return query.executeUpdate();
	}

	public int executeUpdate(JpaUpdateCallback<E> callback) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaUpdate<E> update = callback.doInJpa(builder);
		Query query = em.createQuery(update);
		return query.executeUpdate();
	}

	public JpaUpdate<E> update(Class<E> entityClass) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaUpdate<E> update = builder.createCriteriaUpdate(entityClass);
		Root<E> root = update.from(entityClass);
		return new JpaUpdateImpl<E>(new RootModel<E>(root, Model.ROOT, em.getMetamodel()), update, builder, this);
	}

	public JpaDelete<E> delete(Class<E> entityClass) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaDelete<E> delete = builder.createCriteriaDelete(entityClass);
		Root<E> root = delete.from(entityClass);
		return new JpaDeleteImpl<E>(new RootModel<E>(root, Model.ROOT, em.getMetamodel()), delete, builder, this);
	}

	public JpaQuery<E> select(Class<E> entityClass, String alias) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = builder.createTupleQuery();
		Root<E> root = query.from(entityClass);
		return new JpaQueryImpl<E>(new RootModel<E>(root, alias, em.getMetamodel()), query, builder, this);
	}

}
