package com.zp.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			URL url = new URL(
					"http://ip.taobao.com/service/getIpInfo.php?ip=218.1.64.33");
			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();
			byte[] cache = new byte[512];
			String json = "";
			do {
				int read_length = is.read(cache);
				if (read_length == -1) {
					break;
				} else {
					json += new String(cache, 0, read_length);
				}
			} while (true);
			Gson son = new Gson();
			StringMap data = (StringMap)son.fromJson(json, Map.class).get("data");
			System.out.println(data.get("city"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String c = "\u4e2d\u56fd";
		String r = "\u534e\u4e2d";
		String city = "\u90d1\u5dde\u5e02";
		String isp = "\u8054\u901a";
		System.out.println(c + r + city + isp);
	}

	public static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					System.out.println("--------------");
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					System.out.println("----------" + (char) value);
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();

	}
}
