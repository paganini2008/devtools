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

import java.io.IOException;
import java.net.HttpCookie;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.regex.RegexUtils;
import com.github.paganini2008.devtools.validation.ValidationUtils;

/**
 * Network utilities: local addresses, ports, host names and cookies.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class NetUtils {

	public static final String LOCALHOST = "127.0.0.1";

	public static final String ANYHOST = "0.0.0.0";

	public static final int MIN_PORT = 1024;

	public static final int MAX_PORT = 65535;

	private static final int RANDOM_PORT_TRY_TIMES = 10;

	private static final Pattern ADDRESS_PATTERN = RegexUtils.ADDRESS_PATTERN;

	private static final Pattern LOCAL_IP_PATTERN = RegexUtils.LOCAL_IP_PATTERN;


	/**
	 * Checks whether the port is in the range of non-privileged ports (MIN_PORT, MAX_PORT].
	 *
	 * <pre>
	 * NetUtils.isValidPort(1023)  = false
	 * NetUtils.isValidPort(1024)  = false
	 * NetUtils.isValidPort(8080)  = true
	 * NetUtils.isValidPort(65536) = false
	 * </pre>
	 */
	public static boolean isValidPort(int port) {
		return port > MIN_PORT && port <= MAX_PORT;
	}

	/**
	 * Checks whether the string is an IPv4 address with port.
	 *
	 * <pre>
	 * NetUtils.isValidAddress(null)             = false
	 * NetUtils.isValidAddress("127.0.0.1")      = false
	 * NetUtils.isValidAddress("127.0.0.1:8080") = true
	 * NetUtils.isValidAddress("localhost:80")   = false
	 * </pre>
	 */
	public static boolean isValidAddress(String address) {
		return address != null && ADDRESS_PATTERN.matcher(address).matches();
	}

	/**
	 * Checks whether the host is a loopback IPv4 address or "localhost" (case insensitive).
	 *
	 * <pre>
	 * NetUtils.isLocalHost(null)          = false
	 * NetUtils.isLocalHost("127.0.0.1")   = true
	 * NetUtils.isLocalHost("LOCALHOST")   = true
	 * NetUtils.isLocalHost("192.168.1.1") = false
	 * </pre>
	 */
	public static boolean isLocalHost(String host) {
		return host != null && (LOCAL_IP_PATTERN.matcher(host).matches() || host.equalsIgnoreCase("localhost"));
	}

	/**
	 * Checks whether the host is the wildcard address "0.0.0.0". Returns <code>false</code> for <code>null</code>.
	 */
	public static boolean isAnyHost(String host) {
		return "0.0.0.0".equals(host);
	}

	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress()) {
			return false;
		}
		String name = address.getHostAddress();
		return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && ValidationUtils.isIPv4(name));
	}

	/**
	 * Returns the host name of the local address.
	 */
	public static String getHostName() {
		return getHostName(getLocalHost());
	}

	/**
	 * Returns the IP of the first non-loopback local address, or {@link #LOCALHOST} if none is found.
	 */
	public static String getLocalHost() {
		InetAddress address = getLocalAddress();
		return address == null ? LOCALHOST : address.getHostAddress();
	}

	/**
	 * Returns the MAC address of the local host as lower case hex bytes joined by "-", e.g. "a4-83-e7-12-34-56",
	 * or an empty string if it cannot be determined.
	 */
	public static String getMacAddressString() {
		byte[] mac = getMacAddress();
		if (mac == null) {
			return "";
		}
		StringBuilder content = new StringBuilder();
		String s;
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				content.append("-");
			}
			s = Integer.toHexString(mac[i] & 0xFF);
			content.append(s.length() == 1 ? "0" + s : s);
		}
		return content.toString();
	}

	/**
	 * Returns the hardware address of the network interface of the local host, or of the first non-loopback interface
	 * which is up, <code>null</code> if none is available.
	 *
	 * @throws IllegalStateException if the network interfaces cannot be read
	 */
	public static byte[] getMacAddress() {
		byte[] mac = null;
		try {
			NetworkInterface localInterface = null;
			try {
				localInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			} catch (final UnknownHostException ignored) {
				// fall through to scan the network interfaces
			}
			if (localInterface != null && !localInterface.isLoopback() && localInterface.isUp()) {
				mac = localInterface.getHardwareAddress();
			}
			if (mac == null) {
				mac = findHardwareAddress(NetworkInterface.getNetworkInterfaces());
			}
		} catch (final SocketException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return mac;
	}

	static byte[] findHardwareAddress(Enumeration<NetworkInterface> networkInterfaces) throws SocketException {
		byte[] mac = null;
		if (networkInterfaces != null) {
			while (networkInterfaces.hasMoreElements() && mac == null) {
				final NetworkInterface nic = networkInterfaces.nextElement();
				if (nic != null && !nic.isLoopback() && nic.isUp()) {
					mac = nic.getHardwareAddress();
				}
			}
		}
		return mac;
	}

	/**
	 * Returns the first non-loopback IPv4 address of the local host. Falls back to {@link InetAddress#getLocalHost()} (which may
	 * be a loopback address), and returns <code>null</code> if even that cannot be resolved.
	 *
	 * @throws IllegalStateException if the network interfaces cannot be read
	 */
	public static InetAddress getLocalAddress() {
		InetAddress localAddress = null;
		try {
			localAddress = InetAddress.getLocalHost();
			if (isValidAddress(localAddress)) {
				return localAddress;
			}
		} catch (UnknownHostException ignored) {
			// fall through to scan the network interfaces
		}
		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		InetAddress address = findValidAddress(interfaces);
		return address != null ? address : localAddress;
	}

	static InetAddress findValidAddress(Enumeration<NetworkInterface> interfaces) {
		if (interfaces != null) {
			while (interfaces.hasMoreElements()) {
				NetworkInterface network = interfaces.nextElement();
				Enumeration<InetAddress> addresses = network.getInetAddresses();
				if (addresses != null) {
					while (addresses.hasMoreElements()) {
						InetAddress address = addresses.nextElement();
						if (isValidAddress(address)) {
							return address;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Resolves the host name of an address like "host" or "host:port". The input is returned as it is if it cannot be
	 * resolved, and <code>null</code> returns <code>null</code>.
	 */
	public static String getHostName(String address) {
		try {
			int i = address.indexOf(':');
			if (i > 0) {
				address = address.substring(0, i);
			}
			InetAddress inetAddress = InetAddress.getByName(address);
			if (inetAddress != null) {
				return inetAddress.getHostName();
			}
		} catch (Throwable e) {
		}
		return address;
	}

	/**
	 * Formats the socket address as "ip:port". An unresolved address uses its host name instead of the IP, and
	 * <code>null</code> returns an empty string.
	 */
	public static String toAddressString(InetSocketAddress address) {
		if (address != null) {
			InetAddress inetAddress = address.getAddress();
			String host = inetAddress != null ? inetAddress.getHostAddress() : address.getHostString();
			return host + ":" + address.getPort();
		}
		return "";
	}

	/**
	 * Parses "host:port" or "host" (port 0) into a socket address.
	 *
	 * @throws NumberFormatException if the port is not a number
	 */
	public static InetSocketAddress toAddress(String address) {
		int i = address.indexOf(':');
		String host;
		int port;
		if (i > 0) {
			host = address.substring(0, i);
			port = Integer.parseInt(address.substring(i + 1));
		} else {
			host = address;
			port = 0;
		}
		return new InetSocketAddress(host, port);
	}

	/**
	 * Returns a free port chosen by the operating system, or a random port if that fails.
	 */
	public static int getAvailablePort() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket();
			ss.bind(null);
			return ss.getLocalPort();
		} catch (IOException e) {
			return getRandomPort();
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Checks whether a server socket can be bound to the port.
	 */
	public static boolean isAvailablePort(int port) {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(port);
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Returns the first free port in the range [from, to]; the range is clipped to [MIN_PORT, MAX_PORT].
	 *
	 * @return the first free port, or -1 if no port in the range is free
	 */
	public static int getAvailablePort(int from, int to) {
		from = Math.max(MIN_PORT, from);
		to = Math.min(MAX_PORT, to);
		for (int i = from; i <= to; i++) {
			if (isAvailablePort(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns a random port in [MIN_PORT, MAX_PORT), see {@link #getRandomPort(int, int)}.
	 */
	public static int getRandomPort() {
		return getRandomPort(MIN_PORT, MAX_PORT);
	}

	/**
	 * Returns a random port in [from, to) (clipped to [MIN_PORT, MAX_PORT]), retrying a few times to find a free one.
	 * The returned port is not guaranteed to be free.
	 */
	public static int getRandomPort(int from, int to) {
		int port;
		int i = 0;
		from = Math.max(MIN_PORT, from);
		to = Math.min(MAX_PORT, to);
		do {
			port = RandomUtils.randomInt(from, to);
		} while (!isAvailablePort(port) && (i++) < RANDOM_PORT_TRY_TIMES);
		return port;
	}

	/**
	 * Checks whether the host is reachable within the timeout in milliseconds. Returns <code>false</code> on any error.
	 */
	public static boolean ping(String ip, int timeout) {
		try {
			return InetAddress.getByName(ip).isReachable(timeout);
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Parses a Set-Cookie (or Set-Cookie2) header value, see {@link HttpCookie#parse(String)}.
	 *
	 * @return the cookies, an empty list for a blank string
	 */
	public static List<HttpCookie> parseCookies(String cookieStr) {
		if (StringUtils.isBlank(cookieStr)) {
			return Collections.emptyList();
		}
		return HttpCookie.parse(cookieStr);
	}
}
