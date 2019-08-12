package com.github.paganini2008.springworld.blogonline.config;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.paganini2008.springworld.fastjpa.support.EntityDaoFactoryBean;

/**
 * 
 * JpaConfig
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(repositoryFactoryBeanClass = EntityDaoFactoryBean.class, basePackages = { "com.github.paganini2008.springworld.blogonline.dao" })
public class JpaConfig {

	public static final String PRIMARY_ENTITY_FACTORY_BEAN_NAME = "entityManagerFactory";

	@Primary
	@Bean(PRIMARY_ENTITY_FACTORY_BEAN_NAME)
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, EntityManagerFactoryBuilder builder,
			JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
		Map<String, Object> properties = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(),
				new HibernateSettings());
		return builder.dataSource(dataSource).properties(properties).packages("com.github.paganini2008.springworld.blogonline.entity").build();
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

}
