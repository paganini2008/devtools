package com.github.paganini2008.springworld.config;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * GitConfigProperties
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public class GitConfigProperties extends GitRepoProperties {

	private static final long serialVersionUID = 3977102427571801020L;

	private final Map<String, String> defaultConfig;

	public GitConfigProperties() {
		this(new HashMap<String, String>());
	}

	public GitConfigProperties(Map<String, String> defaultConfig) {
		this.defaultConfig = defaultConfig;
	}

	protected Properties createObject() throws Exception {
		Properties p = super.createObject();
		for (Map.Entry<String, String> entry : defaultConfig.entrySet()) {
			p.setProperty(entry.getKey(), entry.getValue());
		}
		return p;
	}

	protected void sort(File[] files) {
		ApplicationPropertiesLoadingComparator.sort(files);
	}

	public static void main(String[] args) throws Exception {
		GitConfigProperties gitlabProperties = new GitConfigProperties();
		gitlabProperties.setUrl("https://git.d-linking.tech/Java/mec-syscfg.git");
		gitlabProperties.setBranch("hlsh-v2");
		gitlabProperties.setUsername("fengyan");
		gitlabProperties.setPassword("20180514");
		gitlabProperties.setSearchPath("/hlsh-config/cloud");
		gitlabProperties.setApplicationName("zuul-service");
		gitlabProperties.setEnv("dev");
		gitlabProperties.refresh();

		Enumeration<?> en = gitlabProperties.propertyNames();
		for (; en.hasMoreElements();) {
			System.out.println(en.nextElement());
		}
	}

}
