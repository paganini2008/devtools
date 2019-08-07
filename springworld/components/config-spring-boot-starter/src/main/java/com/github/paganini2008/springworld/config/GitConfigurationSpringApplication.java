package com.github.paganini2008.springworld.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.PathUtils;

/**
 * 
 * GitConfigurationSpringApplication
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public class GitConfigurationSpringApplication extends ExternalConfigurationSpringApplication {

	private static final String CONFIG_FILE_NAME_PREFIX = "gitcfg:";
	private static final String GLOBAL_CONFIG_NAME = "default-settings";

	public GitConfigurationSpringApplication(Class<?>... mainClasses) {
		super(mainClasses);
	}

	protected void applySettings(String applicationName, String env, ConfigurableEnvironment environment) throws IOException {
		final boolean fetchLatest = environment.getProperty("spring.config.git.fetchLatest", Boolean.class, true);
		final boolean useDefaultSettings = environment.getProperty("spring.config.git.useDefaultSettings", Boolean.class, true);
		String url = environment.getProperty("spring.config.git.uri");
		String branch = environment.getProperty("spring.config.git.branch");
		String username = environment.getProperty("spring.config.git.username");
		String password = environment.getProperty("spring.config.git.password");
		String searchPath = environment.getProperty("spring.config.git.searchPath");
		String localRepoPath = environment.getProperty("spring.config.git.localRepoPath");
		String[] fileNames = environment.containsProperty("spring.config.git.fileNames")
				? environment.getProperty("spring.config.git.fileNames").split(",")
				: new String[0];
		File localRepo;
		if (StringUtils.isNotBlank(localRepoPath)) {
			localRepo = new File(localRepoPath);
		} else {
			localRepo = FileUtils.getFile(FileUtils.getUserDirectory(), ".gitcfg", applicationName);
		}

		if (fetchLatest) {
			boolean pull;
			Git git = null;
			boolean exists = FileUtils.mkdirs(localRepo);
			if (exists) {
				try {
					git = Git.open(localRepo);
					git.pull().setRemoteBranchName(branch)
							.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
					pull = true;
				} catch (Throwable ignored) {
					FileUtils.deleteDirectory(localRepo);
					pull = false;
				}
			} else {
				pull = false;
			}
			if (!pull) {
				FileUtils.mkdirs(localRepo);
				try {
					git = Git.cloneRepository().setURI(url).setBranch(branch).setDirectory(localRepo)
							.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
					pull = true;
				} catch (Throwable ignored) {
					pull = false;
				} finally {
					if (git != null) {
						git.close();
					}
				}
			}
			if (!pull) {
				throw new IOException("Can not pull configuration from git repository.");
			}
		} else if (!localRepo.exists()) {
			throw new IOException("Can not pull configuration from git repository.");
		}

		File[] fileArray = null;
		if (fileNames == null || fileNames.length == 0) {
			File searchDir = FileUtils.getFile(localRepo, searchPath, applicationName, env);
			if (searchDir.exists()) {
				fileArray = searchDir.listFiles((file) -> {
					final String fileName = file.getName().toLowerCase();
					return fileName.endsWith(".xml") || fileName.endsWith(".properties") || fileName.endsWith(".yml")
							|| fileName.endsWith(".yaml");
				});
			} else {
				System.out
						.println("[Warning] Config home '" + searchDir + "'  doesn't exist and default configuration will be overwrited.");
			}
		} else {
			fileArray = FileUtils.getFiles(fileNames.clone());
		}

		if (fileArray == null && useDefaultSettings) {
			File globalConfigDir = FileUtils.getFile(localRepo, searchPath, GLOBAL_CONFIG_NAME, env);
			if (globalConfigDir.exists()) {
				fileArray = globalConfigDir.listFiles((file) -> {
					final String fileName = file.getName().toLowerCase();
					return fileName.endsWith(".xml") || fileName.endsWith(".properties") || fileName.endsWith(".yml")
							|| fileName.endsWith(".yaml");
				});
			}
		}

		if (fileArray == null || fileArray.length == 0) {
			throw new IOException("No matched config files on this searchPath.");
		}
		List<File> configFiles = new ArrayList<File>();
		configFiles.addAll(Arrays.asList(fileArray));
		sort(configFiles);

		reconfigureEnvironment(configFiles.toArray(new File[0]), environment);
	}

	protected void reconfigureEnvironment(File[] configFiles, ConfigurableEnvironment environment) throws IOException {
		for (File configFile : configFiles) {
			if (configFile.exists()) {
				PropertySourceLoader loader = null;
				String extension = PathUtils.getExtension(configFile.getName());
				switch (extension.toLowerCase()) {
				case "xml":
				case "properties":
					loader = new PropertiesPropertySourceLoader();
					break;
				case "yaml":
				case "yml":
					loader = new YamlPropertySourceLoader();
					break;
				}
				if (loader != null) {
					Resource resource = new FileSystemResource(configFile.getAbsolutePath());
					List<PropertySource<?>> sources = loader.load(CONFIG_FILE_NAME_PREFIX + configFile.getName(), resource);
					for (PropertySource<?> source : sources) {
						environment.getPropertySources().addLast(source);
					}
				}
			} else {
				System.out.println("[Warning] ConfigFile '" + configFile + "' is not existed.");
			}
		}
	}

	protected void sort(List<File> files) {
		ApplicationPropertiesLoadingComparator.sort(files);
	}

	public static ConfigurableApplicationContext run(Class<?> mainClass, String[] args) {
		return new GitConfigurationSpringApplication(mainClass).run(args);
	}

}
