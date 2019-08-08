package com.github.paganini2008.springworld.amber.rabbitmq;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.paganini2008.springworld.amber.config.JobDispatcher;

/**
 * 
 * RabbitmqConfig
 * 
 * @author Fred Feng
 * @create 2018-03
 */
@Configuration
@ConditionalOnProperty(prefix = "amber.dispatcher", name = "type", havingValue = "rabbitmq")
public class RabbitmqConfig {

	@Value("${amber.rabbitmq.addresses}")
	private String addresses;

	@Value("${amber.rabbitmq.username}")
	private String username;

	@Value("${amber.rabbitmq.password}")
	private String password;

	@Value("${amber.rabbitmq.vhost}")
	private String vhost;

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connection = new CachingConnectionFactory();
		connection.setAddresses(addresses);
		connection.setUsername(username);
		connection.setPassword(password);
		connection.setVirtualHost(vhost);
		connection.setPublisherConfirms(true);
		return connection;
	}

	@ConditionalOnMissingBean(RabbitTemplate.class)
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		return rabbitTemplate;
	}

	@Bean
	public JobDispatcher rabbitmqDispatcher() {
		return new RabbitmqDispatcher();
	}
	
	@Bean
	public RabbitmqJobProcessor rabbitmqJobProcessor() {
		return new RabbitmqJobProcessor();
	}
}
