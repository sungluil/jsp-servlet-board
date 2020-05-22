package org.sdf.util;

public class SimCrypto {
	private static int defaultKey = -9;

	public static String encrypt(String s) {
		return encrypt(s, defaultKey);
	}

	public static String decrypt(String s) {
		return decrypt(s, defaultKey);
	}

	public static String encrypt(String s, int key) {
		if (s == null)
			return s;
		String rtn = "";
		try {
			byte[] cc = s.getBytes();
			for (int i = 0; i < cc.length; i++) {
				cc[i] = (byte) (cc[i] - key);
			}
			rtn = new String(cc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String s1 = key(s) + rtn;
		return s1;
	}

	public static String decrypt(String s, int key) {
		if (s == null)
			return s;
		String rtn = "";
		s = s.substring(10);
		try {
			byte[] cc = s.getBytes();
			for (int i = 0; i < cc.length; i++) {
				cc[i] = (byte) (cc[i] + key);
			}
			rtn = new String(cc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}

	protected static String key(String s) {

		long sum = s.length();
		for (int i = 0; i < s.length(); i++) {
			sum += (int) s.charAt(i);
		}

		java.util.Calendar cal = java.util.Calendar.getInstance();
		String code = Long.toHexString(cal.getTime().getTime() * sum)
				.toUpperCase();
		return code.substring(4, 8) + "-" + code.substring(0, 4) + "-";
	}
}
