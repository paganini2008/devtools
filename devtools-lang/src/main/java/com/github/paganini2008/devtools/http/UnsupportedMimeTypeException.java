package com.github.paganini2008.devtools.http;

import java.io.IOException;

/**
 * UnsupportedMimeTypeException
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class UnsupportedMimeTypeException extends IOException {

	private static final long serialVersionUID = 4853305326101523342L;
	private String mimeType;
	private String url;

	public UnsupportedMimeTypeException(String message, String mimeType, String url) {
		super(message);
		this.mimeType = mimeType;
		this.url = url;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getUrl() {
		return url;
	}

	public String toString() {
		return super.toString() + ". Mimetype=" + mimeType + ", URL=" + url;
	}

}
