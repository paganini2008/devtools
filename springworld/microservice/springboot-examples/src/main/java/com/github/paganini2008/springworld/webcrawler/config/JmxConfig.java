package com.github.paganini2008.springworld.webcrawler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import com.github.paganini2008.devtools.net.NetUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JmxConfig
 * 
 * @author Fred Feng
 * @version 1.0
 */
@Slf4j
@Configuration
public class JmxConfig {

	public JmxConfig() {
		this.rmiHost = NetUtils.getLocalHost();
		this.rmiPort = NetUtils.getRandomPort(51000, 52000);
	}

	private String rmiHost;

	private Integer rmiPort;

	@Bean
	public RmiRegistryFactoryBean rmiRegistry() {
		final RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
		rmiRegistryFactoryBean.setPort(rmiPort);
		rmiRegistryFactoryBean.setAlwaysCreate(true);
		return rmiRegistryFactoryBean;
	}

	@Bean
	@DependsOn("rmiRegistry")
	public ConnectorServerFactoryBean connectorServerFactoryBean() throws Exception {
		final ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
		connectorServerFactoryBean.setObjectName("connector:name=rmi");
		final String jmxServiceUrl = String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", rmiHost, rmiPort, rmiHost,
				rmiPort);
		connectorServerFactoryBean.setServiceUrl(jmxServiceUrl);
		log.info("JmxServiceUrl: " + jmxServiceUrl);
		log.info("JmxConfigBean create successfully.");
		return connectorServerFactoryBean;
	}
}
