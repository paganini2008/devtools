package com.github.paganini2008.devtools.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class NewUtils {

	private NewUtils() {
	}

	public static final String LOCALHOST = "127.0.0.1";

	public static final String ANYHOST = "0.0.0.0";

	public static final int MIN_PORT = 5001;

	public static final int MAX_PORT = 65535;

	private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

	public static void main(String[] args){
		System.out.println(getLocalAddress());
	}
	
	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress())
			return false;
		String name = address.getHostAddress();
		return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
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

}
