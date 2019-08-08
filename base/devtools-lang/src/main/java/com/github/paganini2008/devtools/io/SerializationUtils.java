package com.github.paganini2008.devtools.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.Assert;

/**
 * 
 * SerializationUtils
 * 
 * @author Fred Feng
 * @revised 2019-08
 * @created 2013-05
 * @version 1.0
 */
@SuppressWarnings("all")
public abstract class SerializationUtils {

	public static <T> T clone(T serializable) {
		byte[] bytes = toByteArray(serializable);
		return readObject(bytes);
	}

	public static <T> List<T> cloneMany(T serializable, int count) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < count; i++) {
			list.add(clone(serializable));
		}
		return list;
	}

	public static void writeObject(Object serializable, File file) {
		writeObject(serializable, file, false);
	}

	public static void writeObject(Object serializable, File file, boolean compress) {
		Assert.isNull(file, "Unspecified the output file.");
		FileOutputStream fos = null;
		try {
			fos = FileUtils.openOutputStream(file);
			writeObject(serializable, fos, compress);
		} catch (IOException e) {
			throw new SerializationException(e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	public static byte[] toByteArray(Object serializable) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		writeObject(serializable, output);
		return output.toByteArray();
	}

	public static void writeObject(Object serializable, OutputStream os) {
		writeObject(serializable, os, false);
	}

	public static void writeObject(Object serializable, OutputStream os, boolean compress) {
		Assert.isNull(serializable);
		Assert.isNull(os, "OutputStream must not be null.");
		ObjectOutputStream oos = null;
		try {
			oos = IOUtils.getObjectOutputStream(compress ? IOUtils.getGZIPOutputStream(os) : os);
			oos.writeObject(serializable);
		} catch (IOException e) {
			throw new SerializationException(e);
		} finally {
			IOUtils.flushQuietly(oos);
		}
	}

	public static <T> T readObject(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		return (T) readObject(bis);
	}

	public static Object readObject(InputStream is) {
		return readObject(is, false);
	}

	public static Object readObject(InputStream is, boolean compress) {
		Assert.isNull(is, "InputStream must not be null.");
		ObjectInputStream ois = null;
		try {
			ois = IOUtils.getObjectInputStream(compress ? IOUtils.getGZIPInputStream(is) : is);
			return ois.readObject();
		} catch (IOException e) {
			throw new SerializationException(e);
		} catch (ClassNotFoundException e) {
			throw new SerializationException(e);
		}
	}

	public static Object readObject(File file) {
		return readObject(file, false);
	}

	public static Object readObject(File file, boolean compress) {
		Assert.isNull(file, "Unspecified the input file.");
		FileInputStream fis = null;
		try {
			fis = FileUtils.openInputStream(file);
			return readObject(fis, compress);
		} catch (IOException e) {
			throw new SerializationException(e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

}
