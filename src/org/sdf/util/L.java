package org.sdf.util;

import java.net.InetAddress;
import java.security.Provider;
import java.security.Security;
import java.util.Enumeration;

import org.sdf.log.Log;

public class L {
	static String prefix = "EU";
	static String suffix = "NA";
	static String represent = "eGene40";
	static int predays = 90;

	public static String encode(String src) {
		src = represent + src;
		try {
			String tmp = AES.encrypt(src);

			return prefix + tmp + suffix;
		} catch (Exception e) {

		}
		return null;
	}

	public static String decode(String key) {
		key = key.substring(prefix.length());
		key = key.substring(0, key.length() - suffix.length());
		try {
			String tmp = AES.decrypt(key);
			// System.out.println(key + ":" + tmp);

			return tmp.substring(represent.length());
		} catch (Exception e) {

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

			org.sdf.lang.Time tm = new org.sdf.lang.Time();
			tm.addDay(predays);
			String src = tm.fm_dttm();

			return encode(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean matchTrialKey(String key) {
		Log.cfg.info("Match TrialKey Start... : " + key);
		try {
			String dt = decode(key);
			Log.cfg.info("dt : " + dt);
			if (dt == null || dt.length() != 14)
				return false;
			int n = 0;
			try {
				n = Integer.parseInt(dt.substring(0, 4));
				if (n > 2020)
					return false;
			} catch (Exception e) {
				return false;
			}
			try {
				n = Integer.parseInt(dt.substring(4, 6));
				if (n > 12)
					return false;
			} catch (Exception e) {
				return false;
			}
			try {
				n = Integer.parseInt(dt.substring(6, 8));
				if (n > 31)
					return false;
			} catch (Exception e) {
				return false;
			}
			try {
				n = Integer.parseInt(dt.substring(8, 10));
				if (n > 23)
					return false;
			} catch (Exception e) {
				return false;
			}
			try {
				n = Integer.parseInt(dt.substring(10, 12));
				if (n > 59)
					return false;
			} catch (Exception e) {
				return false;
			}
			try {
				n = Integer.parseInt(dt.substring(12, 14));
				if (n > 59)
					return false;
			} catch (Exception e) {
				return false;
			}

			org.sdf.lang.Time tm = new org.sdf.lang.Time(dt);
			org.sdf.lang.Time ctm = new org.sdf.lang.Time();
			// Log.cfg.info("License :[" + ctm.fm_ldttm() + ":" + tm.fm_ldttm()
			// + "] --> " + (ctm.getTime() > tm.getTime()) );
			if (ctm.getTime() > tm.getTime()) {
				Log.cfg.info("Trial L" + "i" + "c" + "e" + "n" + "s"
						+ "e expired. [" + tm.fm_ldttm() + "]");
				return false;
			}

			Log.cfg.info("Trial L" + "i" + "c" + "e" + "n" + "s" + "e valid. ["
					+ tm.fm_ldttm() + "]");

			return true;
			//
		} catch (Exception e) {
			Log.cfg.err("Match TrialKey Error : " + e);
		}
		return false;
	}

	public static boolean verify(String key) {

		try {
			String host = "a";
			try{
			InetAddress adr = InetAddress.getLocalHost();
			 host = adr.getHostAddress();
			}catch(Exception e){}
			return verify(key, host);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.cfg.err("L" + "i" + "c" + "e" + "n" + "s" + "e" + " " + "i" + "n"
				+ "v" + "a" + "l" + "i" + "d" + "." + key);
		return false;
	}

	public static boolean verify(String key, String host) {
		boolean result = false;

		try {

			String src = host;
			String ekey = L.encode(src);
			result = ekey.equals(key);
			// System.out.println(src + ":" + ekey + ":" + key);
			Log.cfg.info("L" + "i" + "c" + "e" + "n" + "s" + "e : [IP:" + src
					+ "] --> " + result);
			if (result) {
				return result;
			}else {
				return matchTrialKey(key);
			}
		} catch (Exception e) {
			Log.cfg.err("verify Error : " + e);
		}
		Log.cfg.err("L" + "i" + "c" + "e" + "n" + "s" + "e" + " " + "i" + "n"
				+ "v" + "a" + "l" + "i" + "d" + "." + key);
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
			L.printProvider();
		else if ("-trial".equals(args[0])) {
			System.out
					.println("--------------------------------------------------");
			System.out.println("30 day Trial Key : " + L.trial());
			System.out
					.println("--------------------------------------------------");
		} else if ("-mt".equals(args[0]))
			System.out.println(L.verify(args[1]));
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
			System.out.println("Key : " + L.encode(src));
			System.out
					.println("--------------------------------------------------");
		}
	}
}
