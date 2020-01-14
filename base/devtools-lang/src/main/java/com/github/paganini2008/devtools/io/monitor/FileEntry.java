package com.github.paganini2008.devtools.io.monitor;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

/**
 * 
 * FileEntry
 * 
 * @author Fred Feng
 * @created 2014-07
 * @revised 2019-12
 * @version 1.0
 */
public class FileEntry implements Serializable {

	private static final long serialVersionUID = 3996349246044345894L;

	private final FileEntry parent;
	private transient final File file;
	private String name;
	private boolean exists;
	private boolean directory;
	private long lastModified;
	private long length;
	private boolean hidden;
	private Map<String, FileEntry> childEntries;

	public FileEntry(File file) {
		this(null, file);
	}

	public FileEntry(FileEntry parent, File file) {
		if (file == null) {
			throw new IllegalArgumentException("File must not be null.");
		}
		this.file = file;
		this.parent = parent;
	}

	public void refresh() {
		this.name = file.getName();
		this.exists = file.exists();
		this.directory = exists ? file.isDirectory() : false;
		this.lastModified = exists ? file.lastModified() : 0;
		this.length = exists && !directory ? file.length() : 0;
		this.hidden = exists ? file.isHidden() : false;
	}

	public int getDepth() {
		return parent == null ? 0 : parent.getDepth() + 1;
	}

	public FileEntry newChildEntry(File file) {
		FileEntry entry = new FileEntry(this, file);
		entry.refresh();
		return entry;
	}

	public boolean compareTo(FileEntry otherEntry) {
		boolean otherExists = otherEntry.isExists();
		boolean otherDirectory = otherExists ? otherEntry.isDirectory() : false;
		long otherLastModified = otherExists ? otherEntry.getLastModified() : 0;
		long otherLength = otherDirectory ? 0 : otherEntry.getLength();
		boolean otherHidden = otherExists ? otherEntry.isHidden() : false;
		return (exists == otherExists)
				&& (lastModified != otherLastModified || directory != otherDirectory || length != otherLength || hidden != otherHidden);
	}

	public Map<String, FileEntry> getChildEntries() {
		return childEntries;
	}

	public void setChildEntries(Map<String, FileEntry> childEntries) {
		this.childEntries = childEntries;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public FileEntry getParent() {
		return parent;
	}

	public File getFile() {
		return file;
	}

	public String toString() {
		return "[FileEntry] file=" + file + ", name=" + name + ", exists=" + exists + ", directory=" + directory + ", lastModified="
				+ lastModified + ", length=" + length + ", hidden=" + hidden;
	}

}
