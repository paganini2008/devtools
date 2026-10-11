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

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.imageio.ImageIO;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.net.Urls;

/**
 * 
 * Image utilities based on {@link ImageIO}: grayscale conversion, encoding to byte arrays and Base64 data URIs.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class ImageUtils {

	/**
	 * Converts the image file to grayscale and writes it to the output file in the given format.
	 *
	 * @throws IOException if the image cannot be read or written
	 */
	public static void fade(File file, String format, File output) throws IOException {
		Assert.isNull(file, "Undefined image source.");
		Assert.isNull(output, "Undefined output file.");
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			fade(in, format, output);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Converts the image at the URL to grayscale and writes it to the output file in the given format.
	 */
	public static void fade(URL src, String format, File output) throws IOException {
		Assert.isNull(src, "Undefined image source.");
		InputStream in = null;
		try {
			in = Urls.openStream(src);
			fade(in, format, output);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Converts the image data to grayscale and writes it to the output file in the given format.
	 */
	public static void fade(byte[] src, String format, File output) throws IOException {
		Assert.isNull(src, "Undefined image source.");
		fade(new ByteArrayInputStream(src), format, output);
	}

	/**
	 * Converts the image read from the stream to grayscale and writes it to the output file. The stream is not closed.
	 */
	public static void fade(InputStream src, String format, File output) throws IOException {
		Assert.isNull(src, "Undefined image source.");
		OutputStream fileOutput = null;
		try {
			fileOutput = new FileOutputStream(output);
			fade(readImage(src), format, fileOutput);
		} finally {
			IOUtils.closeQuietly(fileOutput);
		}
	}

	/**
	 * Converts the image to grayscale and writes it to the stream in the given format.
	 *
	 * @throws IOException if no writer exists for the format or writing fails
	 */
	public static void fade(BufferedImage src, String format, OutputStream output) throws IOException {
		Assert.isNull(src, "Undefined image source.");
		Assert.isNull(output, "Undefined output source.");
		Assert.hasNoText(format, "Undefined image format.");
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		src = op.filter(src, null);
		if (!ImageIO.write(src, format, output)) {
			throw new IOException("No appropriate image writer found for format: " + format);
		}
	}

	private static BufferedImage readImage(InputStream in) throws IOException {
		BufferedImage image = ImageIO.read(in);
		if (image == null) {
			throw new IOException("Unsupported or unrecognized image data.");
		}
		return image;
	}

	/**
	 * Reads the image file and encodes it in the given format (e.g. {@code "png"}).
	 */
	public static byte[] toByteArray(File file, String type) throws IOException {
		Assert.isNull(file, "Unspecified image file.");
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			return toByteArray(in, type);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Encodes the image in the given format (case insensitive, e.g. {@code "PNG"}).
	 *
	 * @throws IOException if no writer exists for the format
	 */
	public static byte[] toByteArray(BufferedImage src, String type) throws IOException {
		Assert.isNull(src, "Unspecified image source.");
		Assert.hasNoText(type);
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			if (!ImageIO.write(src, type.toLowerCase(), bos)) {
				throw new IOException("No appropriate image writer found for type: " + type);
			}
		} finally {
			IOUtils.closeQuietly(bos);
		}
		return bos.toByteArray();
	}

	/**
	 * Reads an image from the stream and encodes it in the given format. The stream is not closed.
	 *
	 * @throws IOException if the data is not a supported image
	 */
	public static byte[] toByteArray(InputStream src, String type) throws IOException {
		Assert.isNull(src, "Unspecified image source.");
		return toByteArray(readImage(src), type);
	}

	/**
	 * Reads the image at the URL and encodes it in the given format.
	 */
	public static byte[] toByteArray(URL url, String type) throws IOException {
		Assert.isNull(url, "Unspecified image source.");
		try (InputStream in = url.openStream()) {
			return toByteArray(in, type);
		} catch (IOException e) {
			throw new IOException("Failed to read image as byte array: " + url, e);
		}
	}

	/**
	 * Encodes the image as a Base64 data URI such as {@code "data:image/png;base64,iVBORw0..."}.
	 */
	public static String encode(BufferedImage src, String type) throws IOException {
		byte[] bytes = toByteArray(src, type);
		Encoder encoder = Base64.getEncoder();
		String img = encoder.encodeToString(bytes);
		img = "data:image/" + type.toLowerCase() + ";base64," + img;
		return img;
	}

	/**
	 * Reads an image from the stream and encodes it as a Base64 data URI. The stream is not closed.
	 */
	public static String encode(InputStream src, String type) throws IOException {
		Assert.isNull(src, "Unspecified image source.");
		try {
			return encode(readImage(src), type);
		} catch (IOException e) {
			throw new IOException("Failed to encode.", e);
		}
	}

	/**
	 * Reads the image at the URL and encodes it as a Base64 data URI.
	 */
	public static String encode(URL src, String type) throws IOException {
		Assert.isNull(src, "Unspecified image source.");
		try (InputStream in = Urls.openStream(src)) {
			return encode(in, type);
		} catch (IOException e) {
			throw new IOException("Failed to encode.", e);
		}
	}

	/**
	 * Reads the image file and encodes it as a Base64 data URI.
	 */
	public static String encode(File file, String type) throws IOException {
		Assert.isNull(file, "Unspecified image source.");
		InputStream in = null;
		try {
			in = FileUtils.openInputStream(file);
			return encode(in, type);
		} catch (IOException e) {
			throw new IOException("Failed to encode image file by path: " + file, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Decodes Base64 data, optionally prefixed with a data URI header as created by {@link #encode(BufferedImage, String)},
	 * and writes the bytes to the stream.
	 *
	 * @throws IOException if writing fails
	 */
	public static void decode(String code, OutputStream output) throws IOException {
		Assert.isNull(code, "Unspecified image source.");
		Assert.isNull(output, "Unspecified output file.");
		try {
			if (code.startsWith("data:")) {
				int index = code.indexOf(',');
				code = index >= 0 ? code.substring(index + 1) : code;
			}
			Decoder decoder = Base64.getDecoder();
			byte[] b = decoder.decode(code.trim());
			output.write(b);
		} catch (IOException e) {
			throw new IOException("Failed to decode image.", e);
		}
	}

	/**
	 * Decodes Base64 image data (optionally a data URI) into the file.
	 */
	public static void decode(String code, File output) throws IOException {
		Assert.isNull(code, "Unspecified image source.");
		Assert.isNull(output, "Unspecified output file.");
		FileOutputStream fos = null;
		try {
			fos = FileUtils.openOutputStream(output);
			decode(code, fos);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}
}
