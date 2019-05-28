package com.github.paganini2008.devtools.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.MessageDigest;

import com.github.paganini2008.devtools.io.FileUtils;

public class TeatMain {

	public static String md5(byte[] source) throws Exception {
		int bufferSize = 4096;
		byte[] buffer = new byte[4096];
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		int remain = source.length;
		while (remain > 0) {
			int len = (remain > bufferSize) ? bufferSize : remain;
			System.arraycopy(source, source.length - remain, buffer, 0, len);
			remain = remain - len;
			md5.update(buffer, 0, len);
		}

		return byte2Hex(md5.digest());
	}

	public static String byte2Hex(byte[] bytes) throws Exception {
		final String HEX = "0123456789abcdef";

		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			result += HEX.charAt(bytes[i] >> 4 & 0x0F);
			result += HEX.charAt(bytes[i] & 0x0F);
		}

		return new String(result);
	}
	
    private static String getmd5Str(long ptime, String key) throws Exception {
        return SecureUtil.md5(key + "_" + ptime + "_" + key);
    }

	public static void main33(String[] args) throws Exception {
		String url = "https://10.0.43.221/b/overdueClassify";
		Connection c = Connection.get(url);
		c.validateTLSCertificates(true);
		c.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
		c.timeout(30 * 1000);
		c.ignoreContentType(true);
		c.data("phone", "15827885727");
		//c.data("idCard", "");
		c.data("pname", "test");
		
		long ptime = System.currentTimeMillis();
		c.data("vkey", getmd5Str(ptime, "32334eb304cb3986860acbd840da99df"));
		c.data("ptime", String.valueOf(ptime));
		c.charset("utf-8");
		//c.data("call_details", "testDetail", FileUtils.openInputStream(new File("d:/testDetail.txt")));
		HttpResponse response = c.execute();
		System.out.println(response);
		System.out.println(response.body());
		System.out.println(c.elapsedTime());
		System.out.println("TeatMain.main()");
	}
	
	private static final String callDetails = "[{'duration':'30','contact_addr':'本地','called_type':'本地','contact':'15982107158','call_time':'2017-06-01 16:56:14','call_model':'被叫','call_addr':'成都资阳眉山三地'},{'duration':'101','contact_addr':'','called_type':'本地','contact':'15982107158','call_time':'2017-06-01 19:36:58','call_model':'被叫','call_addr':'成都资阳眉山三地'}]";

	public static void main2(String[] args) throws Exception {
		String url = "http://localhost:9200/gs/upload";
		Connection c = Connection.post(url);
		c.validateTLSCertificates(true);
		c.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
		c.timeout(60 * 1000);
		c.ignoreContentType(true);
//		c.data("vkey", "20170414021537200");
//		c.data("number", "01050895282");
//		c.data("phone", "13367333336");
//		c.data("name", "易敏娜");
//		c.data("idcard", "");
//		c.data("version", "v1");
//		c.data("apply_date", "2017-07-31");
		c.charset("utf-8");
		c.data("call_details", "testDetail", FileUtils.openInputStream(new File("f:/符传兴_15120607210_460022199110213213.txt")));
		//c.data("call_details", "testDetail", new ByteArrayInputStream(callDetails.getBytes("UTF-8")));
		HttpResponse response = c.execute();
		System.out.println(response);
		System.out.println(response.body());
		System.out.println(c.elapsedTime());
		System.out.println("TeatMain.main()");

	}
	
	public static void main55(String[] args) throws Exception {
		String url = "http://192.168.108.92:5180/service?t=dun_number_mark";
		Connection c = Connection.get(url);
		c.validateTLSCertificates(true);
		//c.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
		c.timeout(120 * 1000);
		c.ignoreContentType(true);
		c.data("vkey", "20170809034100871");
		c.data("number", "01050895282");
		c.data("phone", "13202973328");
		c.data("name", "原秀");
		c.data("idcard", "");
		c.data("version", "v1");
		c.data("apply_date", "2017-07-31");
		c.charset("utf-8");
		//c.data("call_details", "testDetail", FileUtils.openInputStream(new File("f:/符传兴_15120607210_460022199110213213.txt")));
		//c.data("call_details", "testDetail", new ByteArrayInputStream(callDetails.getBytes("UTF-8")));
		HttpResponse response = c.execute();
		System.out.println(response);
		System.out.println(response.body());
		System.out.println(c.elapsedTime());
		System.out.println("TeatMain.main()");

	}
	
	public static void main(String[] args) throws Exception{
		String url = "http://192.168.108.92:9180/dun/count";
		Connection c = Connection.post(url);
		c.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
		c.timeout(30 * 1000);
		c.ignoreContentType(true);
		HttpResponse response = c.execute();
		System.out.println(response);
		System.out.println(response.body());
		System.out.println(c.elapsedTime());
		System.out.println("TeatMain.main()");
	}

	public static void main222(String[] args) throws Exception {
		String url = "https://10.0.43.221/shixin/find";
		Connection c = Connection.get(url);
		c.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
		c.timeout(30 * 1000);
		c.ignoreContentType(true);
		c.data("pname", "pc1001");
		String pkey = "6a8f87626cb610618a60d742677284cd";
		long now = System.currentTimeMillis();
		c.data("ptime", now + "");
		c.data("vkey", md5((pkey + "_" + now + "_" + pkey).getBytes()));
		c.data("key", md5(("330326198801083225苏牡丹").getBytes()));
		HttpResponse response = c.execute();
		System.out.println(response);
		System.out.println(response.body());
		System.out.println(c.elapsedTime());
		System.out.println("TeatMain.main()");

	}

}
