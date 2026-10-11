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
package com.github.paganini2008.devtools.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.EscapeUtils;
import com.github.paganini2008.devtools.codec.Base64Utils;
import com.github.paganini2008.devtools.codec.HexUtils;
import com.github.paganini2008.devtools.codec.URLCodecUtils;
import com.github.paganini2008.devtools.crypto.DigestUtils;
import com.github.paganini2008.devtools.crypto.HmacUtils;
import com.github.paganini2008.devtools.id.IdUtils;
import com.github.paganini2008.devtools.id.SnowflakeIdGenerator;
import com.github.paganini2008.devtools.io.ZipUtils;
import com.github.paganini2008.devtools.progress.ProgressBar;
import com.github.paganini2008.devtools.progress.ProgressRenderers;
import com.github.paganini2008.devtools.time.StopWatch;
import com.github.paganini2008.devtools.validation.ValidationUtils;

/**
 * NewModulesUsageTest exercises typical usage of the modules added in 3.0.0.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class NewModulesUsageTest {

	@Test
	public void codecs() {
		assertEquals("e4b8ade69687", HexUtils.encodeHex("中文"));
		assertEquals("Zm9vYmFy", Base64Utils.encode("foobar"));
		assertEquals("a%20b%2Bc", URLCodecUtils.encodeComponent("a b+c"));
		Map<String, List<String>> multi = new LinkedHashMap<String, List<String>>();
		multi.put("a", Arrays.asList("1", "3"));
		multi.put("b", Arrays.asList("2"));
		assertEquals(multi, URLCodecUtils.parseQueryMulti("a=1&b=2&a=3"));
	}

	@Test
	public void digests() {
		assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", DigestUtils.sha256Hex("abc"));
		assertEquals(3421780262L, DigestUtils.crc32("123456789"));
		assertEquals("f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8",
				HmacUtils.hmacSha256Hex("key", "The quick brown fox jumps over the lazy dog"));
	}

	@Test
	public void escaping() {
		assertEquals("&lt;a href=&quot;x&quot;&gt;Tom &amp; Jerry&#39;s&lt;/a&gt;",
				EscapeUtils.escapeHtml("<a href=\"x\">Tom & Jerry's</a>"));
		assertEquals("He said \\\"hi\\\"\\n", EscapeUtils.escapeJson("He said \"hi\"\n"));
		assertEquals("\"say \"\"hi\"\"\"", EscapeUtils.escapeCsv("say \"hi\""));
	}

	@Test
	public void zipAndGzip(@TempDir File dir) throws IOException {
		File docs = new File(dir, "docs");
		new File(docs, "sub").mkdirs();
		Files.write(new File(docs, "a.txt").toPath(), "a".getBytes(StandardCharsets.UTF_8));
		Files.write(new File(docs, "sub/b.bin").toPath(), new byte[] { 1, 2, 3 });

		File zip = new File(dir, "backup/docs.zip");
		ZipUtils.zip(docs, zip);
		assertEquals(Arrays.asList("docs/", "docs/a.txt", "docs/sub/", "docs/sub/b.bin"), ZipUtils.listEntries(zip));

		File evil = new File(dir, "evil.zip");
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(evil))) {
			out.putNextEntry(new ZipEntry("../evil.txt"));
			out.write("x".getBytes(StandardCharsets.UTF_8));
			out.closeEntry();
		}
		File target = new File(dir, "out");
		IOException e = assertThrows(IOException.class, () -> ZipUtils.unzip(evil, target));
		assertTrue(e.getMessage().startsWith("Zip entry is outside of the target directory: ../evil.txt"), e.getMessage());
		assertFalse(new File(dir, "evil.txt").exists());

		assertEquals("hello 中文", ZipUtils.ungzipToString(ZipUtils.gzip("hello 中文")));
	}

	@Test
	public void ids() {
		SnowflakeIdGenerator generator = new SnowflakeIdGenerator(3, 7);
		long id = generator.nextId();
		assertEquals(3, SnowflakeIdGenerator.parse(id).getWorkerId());
		assertEquals(7, SnowflakeIdGenerator.parse(id).getDatacenterId());
		assertTrue(IdUtils.simpleUuid().matches("[0-9a-f]{32}"));
		assertTrue(IdUtils.shortId().matches("[0-9A-Za-z]{22}"));
	}

	@Test
	public void validation() {
		assertTrue(ValidationUtils.isChineseIdCard("11010519491231002X"));
		assertFalse(ValidationUtils.isChineseIdCard("110105194912310021"));
		assertTrue(ValidationUtils.isUSCC("91350100M000100Y43"));
		assertTrue(ValidationUtils.isBankCard("4111111111111111"));
		assertTrue(ValidationUtils.isIPv6("::ffff:192.0.2.128"));
		assertTrue(ValidationUtils.isMobile("13812345678"));
	}

	@Test
	public void stopWatch() {
		final long[] now = { 0L };
		StopWatch watch = new StopWatch("import") {
			@Override
			protected long nanoTime() {
				return now[0];
			}
		};
		watch.start("load");
		now[0] += 100_000_000L;
		watch.stop();
		watch.start("parse");
		now[0] += 300_000_000L;
		watch.stop();
		assertEquals(400L, watch.getTime());
		String expected = "StopWatch 'import': running time = 400000000 ns (400 ms)\n"
				+ "---------------------------------------------\n"
				+ "               ns        %  Task name\n"
				+ "---------------------------------------------\n"
				+ "        100000000   25.00%  load\n"
				+ "        300000000   75.00%  parse\n";
		assertEquals(expected, watch.prettyPrint().replace("\r\n", "\n"));
	}

	@Test
	public void progressBar() {
		ProgressBar manual = ProgressBar.builder().taskName("Import").total(100).renderer(ProgressRenderers.noop()).build();
		try (ProgressBar bar = manual) {
			for (int i = 0; i < 100; i++) {
				bar.step();
			}
		}
		assertEquals(100, manual.getCompleted());
		assertEquals(100, manual.getPercent());
		assertTrue(manual.isFinished());

		ProgressBar ranged = ProgressBar.builder().taskName("Process").renderer(ProgressRenderers.noop()).build();
		int sum = 0;
		for (int i : ranged.range(0, 4)) {
			sum += i;
		}
		assertEquals(6, sum);
		assertTrue(ranged.isFinished());

		List<String> users = Arrays.asList("alice", "bob", "carol");
		ProgressBar wrapped = ProgressBar.builder().taskName("Dump users").unit("rows").renderer(ProgressRenderers.noop()).build();
		int count = 0;
		for (String user : wrapped.wrap(users)) {
			count += user.isEmpty() ? 0 : 1;
		}
		assertEquals(3, count);
		assertEquals(3, wrapped.getTotal());
		assertTrue(wrapped.isFinished());

		ProgressBar each = ProgressBar.builder().taskName("Send mail").renderer(ProgressRenderers.noop()).build();
		final StringBuilder sent = new StringBuilder();
		each.forEach(users, user -> sent.append(user.charAt(0)));
		assertEquals("abc", sent.toString());
	}
}
