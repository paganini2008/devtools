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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * NetUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class NetUtilsTest {

	@Test
	public void testIsValidPort() {
		assertFalse(NetUtils.isValidPort(0));
		assertFalse(NetUtils.isValidPort(1023));
		// MIN_PORT itself is excluded (kept for backward compatibility)
		assertFalse(NetUtils.isValidPort(1024));
		assertTrue(NetUtils.isValidPort(1025));
		assertTrue(NetUtils.isValidPort(8080));
		assertTrue(NetUtils.isValidPort(65535));
		assertFalse(NetUtils.isValidPort(65536));
	}

	@Test
	public void testIsValidAddress() {
		assertTrue(NetUtils.isValidAddress("127.0.0.1:8080"));
		assertTrue(NetUtils.isValidAddress("192.168.1.10:80"));
		assertFalse(NetUtils.isValidAddress("127.0.0.1"));
		assertFalse(NetUtils.isValidAddress("localhost:80"));
		assertFalse(NetUtils.isValidAddress(""));
		assertFalse(NetUtils.isValidAddress(null));
	}

	@Test
	public void testHosts() {
		assertTrue(NetUtils.isLocalHost("127.0.0.1"));
		assertTrue(NetUtils.isLocalHost("127.1.2.3"));
		assertTrue(NetUtils.isLocalHost("LOCALHOST"));
		assertFalse(NetUtils.isLocalHost("192.168.1.1"));
		assertFalse(NetUtils.isLocalHost(null));
		assertTrue(NetUtils.isAnyHost("0.0.0.0"));
		assertFalse(NetUtils.isAnyHost("127.0.0.1"));
		assertFalse(NetUtils.isAnyHost(null));
	}

	@Test
	public void testLocalAddress() {
		String host = NetUtils.getLocalHost();
		assertNotNull(host);
		assertFalse(host.isEmpty());
		InetAddress address = NetUtils.getLocalAddress();
		if (address != null) {
			assertNotNull(address.getHostAddress());
		}
		assertNotNull(NetUtils.getHostName());
		String mac = NetUtils.getMacAddressString();
		byte[] bytes = NetUtils.getMacAddress();
		if (bytes == null) {
			assertEquals("", mac);
		} else {
			assertTrue(mac.matches("[0-9a-f]{2}(-[0-9a-f]{2})*"), mac);
			assertEquals(bytes.length * 3 - 1, mac.length());
		}
	}

	@Test
	public void testInterfaceScanning() throws Exception {
		assertNull(NetUtils.findValidAddress(null));
		assertNull(NetUtils.findValidAddress(Collections.<NetworkInterface>emptyEnumeration()));
		assertNull(NetUtils.findHardwareAddress(null));
		assertNull(NetUtils.findHardwareAddress(Collections.<NetworkInterface>emptyEnumeration()));
		InetAddress address = NetUtils.findValidAddress(NetworkInterface.getNetworkInterfaces());
		if (address != null) {
			assertFalse(address.isLoopbackAddress());
		}
		byte[] mac = NetUtils.findHardwareAddress(NetworkInterface.getNetworkInterfaces());
		if (mac != null) {
			assertTrue(mac.length > 0);
		}
		// only the loopback interface: nothing valid is found
		NetworkInterface loopback = NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
		if (loopback != null) {
			assertNull(NetUtils.findValidAddress(Collections.enumeration(Collections.singletonList(loopback))));
			assertNull(NetUtils.findHardwareAddress(Collections.enumeration(Collections.singletonList(loopback))));
		}
	}

	@Test
	public void testGetHostName() {
		assertEquals("127.0.0.1", InetAddress.getLoopbackAddress().getHostAddress());
		assertNotNull(NetUtils.getHostName("127.0.0.1:8080"));
		// an invalid input is returned as it is
		assertEquals(null, NetUtils.getHostName(null));
	}

	@Test
	public void testAddressConversion() {
		InetSocketAddress address = NetUtils.toAddress("127.0.0.1:8080");
		assertEquals(8080, address.getPort());
		assertEquals("127.0.0.1:8080", NetUtils.toAddressString(address));
		InetSocketAddress noPort = NetUtils.toAddress("127.0.0.1");
		assertEquals(0, noPort.getPort());
		assertEquals("127.0.0.1:0", NetUtils.toAddressString(noPort));
		assertEquals("", NetUtils.toAddressString(null));
		// unresolved addresses do not cause NullPointerException
		assertEquals("example.invalid:80", NetUtils.toAddressString(InetSocketAddress.createUnresolved("example.invalid", 80)));
		assertThrows(NumberFormatException.class, () -> NetUtils.toAddress("127.0.0.1:abc"));
	}

	@Test
	public void testPorts() throws IOException {
		int port = NetUtils.getAvailablePort();
		assertTrue(port > 0 && port <= NetUtils.MAX_PORT);
		try (ServerSocket ss = new ServerSocket(0)) {
			int used = ss.getLocalPort();
			assertFalse(NetUtils.isAvailablePort(used));
			if (used >= NetUtils.MIN_PORT) {
				int found = NetUtils.getAvailablePort(used, NetUtils.MAX_PORT);
				assertTrue(found > used, String.valueOf(found));
				assertTrue(NetUtils.isAvailablePort(found));
				// the only port in the range is occupied
				assertEquals(-1, NetUtils.getAvailablePort(used, used));
			}
		}
		int random = NetUtils.getRandomPort();
		assertTrue(random >= NetUtils.MIN_PORT && random <= NetUtils.MAX_PORT);
		int ranged = NetUtils.getRandomPort(50000, 50010);
		assertTrue(ranged >= 50000 && ranged < 50010);
		assertEquals(NetUtils.MIN_PORT, NetUtils.getRandomPort(0, NetUtils.MIN_PORT));
		assertThrows(IllegalArgumentException.class, () -> NetUtils.getRandomPort(60000, 50000));
	}

	@Test
	public void testPing() {
		// the result depends on the environment, only check it does not fail
		NetUtils.ping("127.0.0.1", 200);
		assertFalse(NetUtils.ping("[::zz]", 200));
	}

	@Test
	public void testParseCookies() {
		assertTrue(NetUtils.parseCookies(null).isEmpty());
		assertTrue(NetUtils.parseCookies(" ").isEmpty());
		List<HttpCookie> cookies = NetUtils.parseCookies("Set-Cookie: sid=abc; Path=/; HttpOnly");
		assertEquals(1, cookies.size());
		assertEquals("sid", cookies.get(0).getName());
		assertEquals("abc", cookies.get(0).getValue());
		assertEquals("/", cookies.get(0).getPath());
	}

}
