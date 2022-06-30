/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

/**
 * NetUtils
 * 
 * @author Fred Feng
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

	private static final Pattern IP_PATTERN = RegexUtils.IP_PATTERN;

	public static boolean isValidPort(int port) {
		return port > MIN_PORT && port <= MAX_PORT;
	}

	public static boolean isValidAddress(String address) {
		return ADDRESS_PATTERN.matcher(address).matches();
	}

	public static boolean isLocalHost(String host) {
		return host != null && (LOCAL_IP_PATTERN.matcher(host).matches() || host.equalsIgnoreCase("localhost"));
	}

	public static boolean isAnyHost(String host) {
		return "0.0.0.0".equals(host);
	}

	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress()) {
			return false;
		}
		String name = address.getHostAddress();
		return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
	}

	public static String getHostName() {
		return getHostName(getLocalHost());
	}

	public static String getLocalHost() {
		InetAddress address = getLocalAddress();
		return address == null ? LOCALHOST : address.getHostAddress();
	}

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
			content.append(s.length() == 1 ? 0 + s : s);
		}
		return content.toString();
	}

	public static byte[] getMacAddress() {
		byte[] mac = null;
		try {
			final InetAddress localHost = InetAddress.getLocalHost();
			try {
				NetworkInterface localInterface = NetworkInterface.getByInetAddress(localHost);
				if (localInterface != null && !localInterface.isLoopback() && localInterface.isUp()) {
					mac = localInterface.getHardwareAddress();
				}
				if (mac == null) {
					final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
					if (networkInterfaces != null) {
						while (networkInterfaces.hasMoreElements() && mac == null) {
							final NetworkInterface nic = networkInterfaces.nextElement();
							if (nic != null && !nic.isLoopback() && nic.isUp()) {
								mac = nic.getHardwareAddress();
							}
						}
					}
				}
			} catch (final SocketException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		} catch (final UnknownHostException ignored) {
		}
		return mac;
	}

	public static InetAddress getLocalAddress() {
		InetAddress localAddress;
		try {
			localAddress = InetAddress.getLocalHost();
			if (isValidAddress(localAddress)) {
				return localAddress;
			}
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
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
		return localAddress;
	}

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

	public static String toAddressString(InetSocketAddress address) {
		if (address != null) {
			return address.getAddress().getHostAddress() + ":" + address.getPort();
		}
		return "";
	}

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

	public static int getAvailablePort(int from, int to) {
		from = Math.max(MIN_PORT, from);
		to = Math.min(MAX_PORT, to);
		int i;
		for (i = from; i < to; i++) {
			if (isAvailablePort(i)) {
				break;
			}
		}
		return i;
	}

	public static int getRandomPort() {
		return getRandomPort(MIN_PORT, MAX_PORT);
	}

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

	public static boolean ping(String ip, int timeout) {
		try {
			return InetAddress.getByName(ip).isReachable(timeout);
		} catch (Exception ex) {
			return false;
		}
	}

	public static List<HttpCookie> parseCookies(String cookieStr) {
		if (StringUtils.isBlank(cookieStr)) {
			return Collections.emptyList();
		}
		return HttpCookie.parse(cookieStr);
	}

	public static void main(String[] args) {

	}

}
