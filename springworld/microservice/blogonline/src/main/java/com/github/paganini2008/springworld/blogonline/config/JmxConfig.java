package com.github.paganini2008.springworld.blogonline.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import com.github.paganini2008.devtools.net.NetUtils;
import com.github.paganini2008.springworld.support.SysLogs;

/**
 * 
 * JmxConfig
 * 
 * @author Fred Feng
 * @version 1.0
 */
@Configuration
public class JmxConfig {

	private static final Logger logger = LoggerFactory.getLogger(JmxConfig.class);

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
		SysLogs.info("JmxServiceUrl: " + jmxServiceUrl);
		logger.info("JmxConfigBean create successfully.");
		return connectorServerFactoryBean;
	}
}
