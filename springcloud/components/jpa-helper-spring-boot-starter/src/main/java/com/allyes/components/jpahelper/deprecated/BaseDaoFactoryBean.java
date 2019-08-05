package com.allyes.components.jpahelper.deprecated;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

/**
 * 
 * BaseDaoFactoryBean
 * 
 * @author Fred Feng
 * @version 1.0
 */
@Deprecated
@SuppressWarnings("all")
public class BaseDaoFactoryBean<R extends JpaRepository<E, ID>, E, ID> extends JpaRepositoryFactoryBean<R, E, ID> {

	public BaseDaoFactoryBean(Class<R> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
		return new BaseDaoFactory<E, ID>(em);
	}

	@Deprecated
	private static class BaseDaoFactory<E, ID> extends JpaRepositoryFactory {

		private final EntityManager entityManager;

		public BaseDaoFactory(EntityManager em) {
			super(em);
			this.entityManager = em;
		}

		@Override
		protected SimpleJpaRepository<E, ID> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
			Object repository = super.getTargetRepository(information, entityManager);
			Assert.isInstanceOf(BaseDaoImpl.class, repository);
			return (BaseDaoImpl) repository;
		}

		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return BaseDaoImpl.class;
		}
	}

}
