package com.github.paganini2008.devtools.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.IOUtils;

/**
 * HttpResponse
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface HttpResponse extends HttpBase<HttpResponse> {

	int statusCode();

	String statusMessage();

	String contentType();

	String body();

	byte[] bytes();

	long length();

	HttpResponse previous();

	int numRedirects();
	
	long elapsedTime();
	
	void elapsedTime(long time);

	void saveAs(Writer writer, String charset) throws IOException;

	void saveAs(OutputStream os) throws IOException;

	default void saveAs(File f) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = FileUtils.openOutputStream(f);
			saveAs(fos);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}
}
