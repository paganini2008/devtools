/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
 * Java serialization utilities: deep copies, (optionally gzip compressed) byte arrays, streams and files.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class SerializationUtils {

	/**
	 * Returns a deep copy of the object made by serializing and deserializing it.
	 *
	 * @throws SerializationException if the object is not serializable
	 */
	public static <T> T copy(T serializable) {
		byte[] bytes = serialize(serializable, false);
		return (T) deserialize(bytes, false);
	}

	/**
	 * Returns {@code count} independent deep copies of the object (an empty list if {@code count <= 0}).
	 */
	public static <T> List<T> copyMany(T serializable, int count) {
		List<T> list = new ArrayList<T>(Math.max(count, 0));
		if (count <= 0) {
			return list;
		}
		byte[] bytes = serialize(serializable, false);
		for (int i = 0; i < count; i++) {
			list.add((T) deserialize(bytes, false));
		}
		return list;
	}

	/**
	 * Serializes the object into the file (created with its parent directories), optionally gzip compressed.
	 *
	 * @throws SerializationException if the object is not serializable or writing fails
	 */
	public static void writeObject(Object serializable, File file, boolean compress) {
		Assert.isNull(file, "Undefined output file.");
		if (!(serializable instanceof Serializable)) {
			// Fail before the file is created (or an existing one truncated)
			throw new SerializationException("Not serialiazble object");
		}
		try (FileOutputStream fos = FileUtils.openOutputStream(file)) {
			writeObject(serializable, fos, compress);
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Serializes the object into a byte array, optionally gzip compressed.
	 *
	 * @throws SerializationException if the object is not serializable
	 */
	public static byte[] serialize(Object serializable, boolean compress) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		writeObject(serializable, output, compress);
		return output.toByteArray();
	}

	/**
	 * Deserializes an object from the byte array.
	 *
	 * @throws IllegalArgumentException if the data is {@code null}
	 * @throws SerializationException if the data is invalid
	 */
	public static Object deserialize(byte[] data, boolean compress) {
		Assert.isNull(data, "Data must not be null.");
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		return readObject(bis, compress);
	}

	/**
	 * Serializes the object into the stream and <b>closes</b> the stream.
	 *
	 * @throws SerializationException if the object is not serializable (including {@code null}) or writing fails
	 */
	public static void writeObject(Object object, OutputStream os, boolean compress) {
		if (!(object instanceof Serializable)) {
			throw new SerializationException("Not serialiazble object");
		}
		Assert.isNull(os, "OutputStream must not be null.");
		ObjectOutputStream oos = null;
		try {
			oos = IOUtils.getObjectOutputStream(compress ? IOUtils.getGZIPOutputStream(os) : os);
			oos.writeObject(object);
			oos.close();
		} catch (IOException e) {
			throw new SerializationException(e);
		} finally {
			// If the wrapping stream could not be created, close the given stream as documented
			IOUtils.closeQuietly(oos != null ? oos : os);
		}
	}

	/**
	 * Deserializes an object from the stream and <b>closes</b> the stream.
	 *
	 * @throws SerializationException if the data is invalid
	 */
	public static Object readObject(InputStream is) {
		return readObject(is, false);
	}

	/**
	 * Deserializes an object from the (optionally gzip compressed) stream and <b>closes</b> the stream.
	 *
	 * @throws SerializationException if the data is invalid
	 */
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
			// A corrupt header fails in the ObjectInputStream constructor, close the given stream then
			IOUtils.closeQuietly(ois != null ? ois : is);
		}
	}

	/**
	 * Deserializes an object from the file.
	 *
	 * @throws SerializationException if the file cannot be read or the data is invalid
	 */
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
