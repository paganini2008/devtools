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

import javax.imageio.ImageIO;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.net.UrlUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ImageUtils {

	private ImageUtils() {

	}

	public static void main(String[] args) throws Throwable {
		URL url = new URL("https://car2.autoimg.cn/cardfs/product/g12/M0F/E1/52/t_autohomecar__wKjBy1oM8pKAIKqgAAX2RcQYALo899.jpg");
		System.out.println(encode(url, "jpg"));
	}

	public static void fade(File file, String format, File output) throws IOException {
		Assert.isNull(file, "Undefined image source.");
		Assert.isNull(file, "Undefined output file.");
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			fade(in, format, output);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static void fade(URL src, String format, File output) throws IOException {
		Assert.isNull(src, "Undefined image source.");
		InputStream in = null;
		try {
			in = UrlUtils.openStream(src);
			fade(in, format, output);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static void fade(byte[] src, String format, File output) throws IOException {
		Assert.isNull(src, "Undefined image source.");
		fade(new ByteArrayInputStream(src), format, output);
	}

	public static void fade(InputStream src, String format, File output) throws IOException {
		Assert.isNull(src, "Undefined image source.");
		OutputStream fileOutput = null;
		try {
			fileOutput = new FileOutputStream(output);
			fade(ImageIO.read(src), format, fileOutput);
		} finally {
			IOUtils.closeQuietly(fileOutput);
		}
	}

	public static void fade(BufferedImage src, String format, OutputStream output) throws IOException {
		Assert.isNull(src, "Undefined image source.");
		Assert.isNull(output, "Undefined output source.");
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		src = op.filter(src, null);
		ImageIO.write(src, format, output);
	}

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

	public static byte[] toByteArray(BufferedImage src, String type) throws IOException {
		Assert.isNull(src, "Unspecified image source.");
		Assert.hasNoText(type);
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			ImageIO.write(src, type.toLowerCase(), bos);
		} finally {
			IOUtils.closeQuietly(bos);
		}
		return bos.toByteArray();
	}

	public static byte[] toByteArray(InputStream src, String type) throws IOException {
		Assert.isNull(src, "Unspecified image source.");
		return toByteArray(ImageIO.read(src), type);
	}

	public static byte[] toByteArray(URL url, String type) throws IOException {
		Assert.isNull(url, "Unspecified image source.");
		try {
			return toByteArray(url.openStream(), type);
		} catch (IOException e) {
			throw new IOException("Failed to the byte array.", e);
		}
	}

	public static String encode(BufferedImage src, String type) throws IOException {
		byte[] bytes = toByteArray(src, type);
		BASE64Encoder encoder = new BASE64Encoder();
		String img = encoder.encode(bytes);
		img = "data:image/" + type.toLowerCase() + ";base64," + img;
		return img;
	}

	public static String encode(InputStream src, String type) throws IOException {
		Assert.isNull(src, "Unspecified image source.");
		try {
			return encode(ImageIO.read(src), type);
		} catch (IOException e) {
			throw new IOException("Failed to encode.", e);
		}
	}

	public static String encode(URL src, String type) throws IOException {
		Assert.isNull(src, "Unspecified image source.");
		try {
			return encode(UrlUtils.openStream(src), type);
		} catch (IOException e) {
			throw new IOException("Failed to encode.", e);
		}
	}

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

	public static void decode(String code, OutputStream output) throws IOException {
		Assert.isNull(code, "Unspecified image source.");
		Assert.isNull(output, "Unspecified output file.");
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] b = decoder.decodeBuffer(code);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			output.write(b);
		} catch (IOException e) {
			throw new IOException("Failed to decode image.", e);
		}
	}

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
