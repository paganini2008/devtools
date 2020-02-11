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
import java.io.Serializable;
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

	public static <T> T copy(T serializable) {
		byte[] bytes = serialize(serializable, false);
		return (T) deserialize(bytes, false);
	}

	public static <T> List<T> copyMany(T serializable, int count) {
		List<T> list = new ArrayList<T>(count);
		for (int i = 0; i < count; i++) {
			list.add(copy(serializable));
		}
		return list;
	}

	public static void writeObject(Object serializable, File file, boolean compress) {
		Assert.isNull(file, "Undefined output file.");
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

	public static byte[] serialize(Object serializable, boolean compress) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		writeObject(serializable, output, compress);
		return output.toByteArray();
	}

	public static Object deserialize(byte[] data, boolean compress) {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		return readObject(bis, compress);
	}

	public static void writeObject(Object object, OutputStream os, boolean compress) {
		if (!(object instanceof Serializable)) {
			throw new SerializationException("Not serialiazble object");
		}
		Assert.isNull(os, "OutputStream must not be null.");
		ObjectOutputStream oos = null;
		try {
			oos = IOUtils.getObjectOutputStream(compress ? IOUtils.getGZIPOutputStream(os) : os);
			oos.writeObject(object);
		} catch (IOException e) {
			throw new SerializationException(e);
		} finally {
			IOUtils.flushAndCloseQuietly(oos);
		}
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
		} finally {
			IOUtils.closeQuietly(ois);
		}
	}

	public static Object readObject(File file, boolean compress) {
		Assert.isNull(file, "Undefined input file.");
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
