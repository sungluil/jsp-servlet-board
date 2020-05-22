package org.sdf.util;

import java.io.*;
import java.net.*;

import java.util.*;

import java.security.*;
import sun.misc.*;

public class KeyGenerator {

	public static String encode(String src) {
		try {
			byte[] inputBytes = src.getBytes("UTF8");

			// SHA �˰?��; ����ϴ� ��쿡�� getInstance("SHA");
			MessageDigest md5 = MessageDigest.getInstance("MD5");

			md5.update(inputBytes);

			byte[] digest = md5.digest();

			String base64 = Base64Coder.encodeLines(digest);

			return base64;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void printProvider() {
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			System.out.println(providers[i]);
			for (Enumeration e = providers[i].keys(); e.hasMoreElements();)
				System.out.println("\t" + e.nextElement());
		}
	}

	public static String encodeIP() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String src = addr.getHostAddress();
			return encode(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String trial() {
		try {
			Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("JST")
													// );
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			cal.set(y, m, d, 0, 0, 0);

			// InetAddress addr = InetAddress.getLocalHost();
			// String src = addr.getHostAddress() + " " + addr.getHostName() +
			// cal.getTime().getTime();
			String src = String
					.valueOf((long) (cal.getTime().getTime() / 1000));

			return encode(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean matchTrialKey(String key, int days) {
		try {
			Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("JST")
													// );
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			cal.set(y, m, d, 0, 0, 0);

			// InetAddress addr = InetAddress.getLocalHost();
			// String src = addr.getHostAddress() + " " + addr.getHostName() +
			// cal.getTime().getTime();

			for (int i = 0; i < days; i++) {
				cal.add(cal.DAY_OF_MONTH, i * (-1));

				String src = String
						.valueOf((long) (cal.getTime().getTime() / 1000));
				String tmp = encode(src);
				if (tmp.equals(key))
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out
					.println("Usage: generate Key\n\tjava org.sdf.util.KeyGenerator src-text");
			System.out
					.println("Usage: print Secure Provider\n\tjava org.sdf.util.KeyGenerator -v");
			return;
		}

		if ("-v".equals(args[0]))
			KeyGenerator.printProvider();
		else if ("-trial".equals(args[0])) {
			System.out
					.println("--------------------------------------------------");
			System.out.println("30 day Trial Key : " + KeyGenerator.trial());
			System.out
					.println("--------------------------------------------------");
		} else if ("-mt".equals(args[0]))
			System.out.println(KeyGenerator.matchTrialKey(args[1], 30));
		else {
			String src = "";
			for (int i = 0;; i++) {
				src += args[i];
				if (i >= args.length - 1)
					break;
				src += " ";
			}

			System.out
					.println("--------------------------------------------------");
			System.out.println("Source : *" + src + "*");
			System.out.println("Key : " + KeyGenerator.encode(src));
			System.out
					.println("--------------------------------------------------");
		}
	}
}
