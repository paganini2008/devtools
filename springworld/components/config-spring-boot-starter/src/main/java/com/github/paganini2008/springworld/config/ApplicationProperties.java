package com.github.paganini2008.springworld.config;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.github.paganini2008.devtools.collection.EventBasedRefreshingProperties;
import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.IOUtils;
import com.github.paganini2008.devtools.io.PathUtils;

/**
 * 
 * ApplicationProperties
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public abstract class ApplicationProperties extends EventBasedRefreshingProperties {

	private static final long serialVersionUID = -4386392261078899614L;

	protected String applicationName;
	protected String env = "dev";

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	@Override
	public void store(File outputFile, String comments) throws IOException {
		OutputStream out = null;
		try {
			String baseName = PathUtils.getBaseName(outputFile.getName());
			String extension = PathUtils.getExtension(outputFile.getName());
			String fileName = baseName + "_" + env + "." + extension;
			out = FileUtils.openOutputStream(new File(outputFile.getParentFile(), fileName), false);
			delegate.store(out, comments);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

}
