package org.sdf.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA {
	static String ALGO =  "SHA-256";
	public static String encrypt(String str) throws Exception {
		MessageDigest md;
		String enc = "";
        try {
            md= MessageDigest.getInstance(ALGO);
 
            md.update(str.getBytes());
            byte[] mb = md.digest();
            enc = byte2hex(mb);
            
        } catch (NoSuchAlgorithmException e) {
        }
        
        return enc;

	}
	
	public static String byte2hex(byte[] ba) {
		if (ba == null || ba.length == 0) {
			return null;
		}

		StringBuffer sb = new StringBuffer(ba.length * 2);
		String hexNumber;
		for (int x = 0; x < ba.length; x++) {
			hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

			sb.append(hexNumber.substring(hexNumber.length() - 2));
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		
		try {
			String t = encrypt("a#DT031452");
			System.out.println(t);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
