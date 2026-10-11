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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * ImageUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ImageUtilsTest {

	@TempDir
	File tempDir;

	private BufferedImage image;
	private byte[] png;
	private File pngFile;

	@BeforeEach
	public void setUp() throws IOException {
		image = new BufferedImage(4, 3, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, 0xFF0000);
		image.setRGB(1, 0, 0x00FF00);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "png", out);
		png = out.toByteArray();
		pngFile = new File(tempDir, "img.png");
		Files.write(pngFile.toPath(), png);
	}

	private static BufferedImage read(byte[] bytes) throws IOException {
		BufferedImage result = ImageIO.read(new ByteArrayInputStream(bytes));
		assertNotNull(result);
		return result;
	}

	private static boolean isGray(BufferedImage img) {
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int rgb = img.getRGB(x, y);
				int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
				if (Math.abs(r - g) > 1 || Math.abs(g - b) > 1) {
					return false;
				}
			}
		}
		return true;
	}

	@Test
	public void toByteArray_encodesImageFromAllSources() throws IOException {
		byte[] fromImage = ImageUtils.toByteArray(image, "PNG");
		assertEquals(4, read(fromImage).getWidth());
		assertEquals(3, read(ImageUtils.toByteArray(new ByteArrayInputStream(png), "png")).getHeight());
		assertEquals(4, read(ImageUtils.toByteArray(pngFile, "png")).getWidth());
		assertEquals(4, read(ImageUtils.toByteArray(pngFile.toURI().toURL(), "png")).getWidth());
	}

	@Test
	public void toByteArray_failsForUnknownTypeOrInvalidData() {
		IOException e = assertThrows(IOException.class, () -> ImageUtils.toByteArray(image, "nosuchformat"));
		assertTrue(e.getMessage().contains("nosuchformat"));
		assertThrows(IOException.class, () -> ImageUtils.toByteArray(new ByteArrayInputStream(new byte[] { 1, 2, 3 }), "png"));
		assertThrows(IOException.class, () -> ImageUtils.toByteArray(new URL("file:" + new File(tempDir, "none").getAbsolutePath()), "png"));
		assertThrows(IOException.class, () -> ImageUtils.toByteArray(new File(tempDir, "none"), "png"));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.toByteArray((BufferedImage) null, "png"));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.toByteArray(image, " "));
	}

	@Test
	public void encodeAndDecode_roundTripDataUri() throws IOException {
		String encoded = ImageUtils.encode(image, "PNG");
		assertTrue(encoded.startsWith("data:image/png;base64,"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageUtils.decode(encoded, out);
		assertEquals(4, read(out.toByteArray()).getWidth());

		String fromStream = ImageUtils.encode(new ByteArrayInputStream(png), "png");
		String fromFile = ImageUtils.encode(pngFile, "png");
		String fromUrl = ImageUtils.encode(pngFile.toURI().toURL(), "png");
		assertEquals(fromStream, fromFile);
		assertEquals(fromStream, fromUrl);

		File output = new File(tempDir, "decoded/out.png");
		ImageUtils.decode(Base64.getEncoder().encodeToString(png), output);
		assertArrayEquals(png, Files.readAllBytes(output.toPath()));
	}

	@Test
	public void encode_wrapsFailures() {
		assertThrows(IOException.class, () -> ImageUtils.encode(new ByteArrayInputStream(new byte[] { 9 }), "png"));
		assertThrows(IOException.class, () -> ImageUtils.encode(new File(tempDir, "none"), "png"));
		assertThrows(IOException.class, () -> ImageUtils.encode(new URL("file:" + new File(tempDir, "none").getAbsolutePath()), "png"));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.encode((File) null, "png"));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.decode(null, new ByteArrayOutputStream()));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.decode("abc", (java.io.OutputStream) null));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.decode("abc", (File) null));
		assertThrows(IOException.class, () -> ImageUtils.decode("AAAA", new IoTestSupport.FailingOutputStream()));
	}

	@Test
	public void fade_convertsToGrayscale() throws IOException {
		File out1 = new File(tempDir, "g1.png");
		ImageUtils.fade(pngFile, "png", out1);
		assertTrue(isGray(ImageIO.read(out1)));

		File out2 = new File(tempDir, "g2.png");
		ImageUtils.fade(png, "png", out2);
		assertTrue(isGray(ImageIO.read(out2)));

		File out3 = new File(tempDir, "g3.png");
		ImageUtils.fade(pngFile.toURI().toURL(), "png", out3);
		assertTrue(isGray(ImageIO.read(out3)));

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageUtils.fade(image, "png", out);
		assertTrue(isGray(read(out.toByteArray())));
	}

	@Test
	public void fade_rejectsInvalidArguments() {
		assertThrows(IOException.class, () -> ImageUtils.fade(image, "nosuchformat", new ByteArrayOutputStream()));
		assertThrows(IOException.class, () -> ImageUtils.fade(new byte[] { 1 }, "png", new File(tempDir, "x.png")));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.fade(pngFile, "png", (File) null));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.fade((File) null, "png", new File(tempDir, "y.png")));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.fade((URL) null, "png", new File(tempDir, "y.png")));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.fade((byte[]) null, "png", new File(tempDir, "y.png")));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.fade((BufferedImage) null, "png", new ByteArrayOutputStream()));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.fade(image, "png", null));
		assertThrows(IllegalArgumentException.class, () -> ImageUtils.fade(image, "", new ByteArrayOutputStream()));
	}
}
