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
package com.github.paganini2008.devtools.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.io.IOUtils;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

/**
 * UrlsTlsTest checks certificate validation of the HTTPS overloads against a local server with a self-signed
 * certificate.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(60)
public class UrlsTlsTest {

	private static final char[] PASSWORD = "changeit".toCharArray();

	@TempDir
	static File tempDir;

	private static HttpsServer server;
	private static String baseUrl;

	@BeforeAll
	public static void startServer() throws Exception {
		File keystore = new File(tempDir, "server.p12");
		String keytool = new File(System.getProperty("java.home"), "bin/keytool").getAbsolutePath();
		Process process = new ProcessBuilder(keytool, "-genkeypair", "-alias", "server", "-keyalg", "RSA", "-keysize", "2048",
				"-validity", "2", "-dname", "CN=localhost", "-storetype", "PKCS12", "-keystore", keystore.getAbsolutePath(),
				"-storepass", new String(PASSWORD), "-keypass", new String(PASSWORD)).redirectErrorStream(true).start();
		IOUtils.toByteArray(process.getInputStream());
		if (!process.waitFor(30, TimeUnit.SECONDS) || process.exitValue() != 0) {
			throw new IllegalStateException("keytool failed");
		}
		KeyStore ks = KeyStore.getInstance("PKCS12");
		try (InputStream in = new FileInputStream(keystore)) {
			ks.load(in, PASSWORD);
		}
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, PASSWORD);
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(kmf.getKeyManagers(), null, null);

		server = HttpsServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.setHttpsConfigurator(new HttpsConfigurator(context));
		server.createContext("/hello", exchange -> {
			byte[] body = "hello".getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(200, body.length);
			try (OutputStream out = exchange.getResponseBody()) {
				out.write(body);
			}
		});
		server.start();
		baseUrl = "https://127.0.0.1:" + server.getAddress().getPort();
	}

	@AfterAll
	public static void stopServer() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test
	public void openStream_trustsSelfSignedCertificateByDefault() throws IOException {
		try (InputStream in = Urls.openStream(baseUrl + "/hello")) {
			assertEquals("hello", new String(IOUtils.toByteArray(in), StandardCharsets.UTF_8));
		}
		try (InputStream in = Urls.openStream(new URL(baseUrl + "/hello"), false)) {
			assertEquals("hello", new String(IOUtils.toByteArray(in), StandardCharsets.UTF_8));
		}
	}

	@Test
	public void openStream_rejectsSelfSignedCertificateWhenValidating() {
		assertThrows(SSLException.class, () -> Urls.openStream(baseUrl + "/hello", true).close());
		assertThrows(SSLException.class, () -> Urls.openStream(new URL(baseUrl + "/hello").openConnection(), true).close());
	}

	@Test
	public void testConnection_validatesOnlyWhenAsked() throws IOException {
		assertEquals(200, Urls.testConnection(baseUrl + "/hello"));
		assertEquals(200, Urls.testConnection(new URL(baseUrl + "/hello"), false));
		assertThrows(SSLException.class, () -> Urls.testConnection(new URL(baseUrl + "/hello"), true));
	}
}
