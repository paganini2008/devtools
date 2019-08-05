package com.allyes.components.config;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.allyes.developer.utils.StringUtils;
import com.allyes.developer.utils.io.PathUtils;

/**
 * 
 * ApplicationPropertiesLoadingComparator
 * 
 * @author Fred Feng
 * @created 2019-03
 */
public class ApplicationPropertiesLoadingComparator implements Comparator<File> {

	private static final ApplicationPropertiesLoadingComparator instance = new ApplicationPropertiesLoadingComparator();

	private static final List<String> alternativeNames = Collections
			.unmodifiableList(Arrays.asList(new String[] { "bootstrap", "application" }));
	private static final List<String> alternativeEnvironments = Collections
			.unmodifiableList(Arrays.asList(new String[] { "default", "local", "dev", "fat", "test", "uat", "prod" }));
	private static final List<String> alternativeExtentsions = Collections
			.unmodifiableList(Arrays.asList(new String[] { "yaml", "yml", "xml", "properties" }));

	public final int compare(File left, File right) {
		String lseq = sequenceFor(left.getName());
		String rseq = sequenceFor(right.getName());
		return lseq.compareTo(rseq);
	}

	private String sequenceFor(String fileName) {
		final String baseName = PathUtils.getBaseName(fileName);
		final String extension = PathUtils.getExtension(fileName);
		int position = baseName.lastIndexOf("-");
		if (position == -1) {
			position = baseName.lastIndexOf("_");
		}
		String name = baseName, env = "";
		if (position > 0) {
			name = baseName.substring(0, position);
			env = baseName.substring(position + 1);
		}
		if (StringUtils.isBlank(env)) {
			env = "default";
		}
		int a = alternativeNames.indexOf(name.toLowerCase());
		if (a < 0) {
			a = 9;
		}
		int b = alternativeEnvironments.indexOf(env.toLowerCase());
		if (b < 0) {
			b = 0;
		}
		int c = alternativeExtentsions.indexOf(extension.toLowerCase());
		if (c < 0) {
			c = 9;
		}
		return String.valueOf(a) + c + b;
	}

	public static void sort(List<File> files) {
		Collections.sort(files, instance);
	}

	public static void sort(File[] files) {
		Arrays.sort(files, instance);
	}

	private ApplicationPropertiesLoadingComparator() {
	}

	public static void main(String[] args) {
		File[] files = { new File("d:/cad/application-fat.xml"),new File("d:/abc/application-dev.yml"), new File("d:/abc/common.properties"),
				new File("d:/abc/application.properties"), new File("d:/abc/bootstrap.properties"), new File("d:/abc/application-prod.yml"),
				new File("d:/abc/common-local.properties"), new File("d:/abc/application.yml"), new File("d:/abc/bootstrap.yml") };
		sort(files);
		for (File file : files) {
			System.out.println(file);
		}
	}

}
