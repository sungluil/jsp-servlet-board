package org.sdf.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	public static byte[] key = "0987654321098765".getBytes();

	public static byte[] hex2byte(String hex) {
		if (hex == null || hex.length() == 0) {
			return null;
		}

		byte[] ba = new byte[hex.length() / 2];
		for (int i = 0; i < ba.length; i++) {
			ba[i] = (byte) Integer
					.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return ba;
	}

	/**
	 * byte[] to hex : unsigned byte(바이트) 배열을 16진수 문자열로 바꾼다.
	 * 
	 * @param ba
	 *            byte[]
	 * @return
	 */
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

	protected static SecretKeySpec getSecretKeySpec() throws Exception {
		/*
		 * try{
		 * 
		 * KeyGenerator kgen = KeyGenerator.getInstance("AES"); kgen.init(128);
		 * SecretKey skey = kgen.generateKey(); byte[] raw = skey.getEncoded();
		 * SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES"); return
		 * skeySpec; }catch(Exception e){}
		 */
		return new SecretKeySpec(key, "AES");
	}

	/**
	 * AES 방식의 암호화
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String message) throws Exception {
		SecretKeySpec skeySpec = getSecretKeySpec();
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		byte[] encrypted = cipher.doFinal(message.getBytes());
		String s = byte2hex(encrypted);
		return s.toUpperCase();
	}

	/**
	 * AES 방식의 복호화
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encrypted) throws Exception {
		if (!StringUtil.valid(encrypted))
			return "";
		try {
			encrypted = encrypted.toLowerCase();

			SecretKeySpec skeySpec = getSecretKeySpec();
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] original = cipher.doFinal(hex2byte(encrypted));
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
		}
		return "";
	}

	public static void main(String[] args) throws Exception {

		String message = "1";

		for (int i = 0; i < 10; i++) {
			// message += (char)('A' +(i));
			String key = message;
			System.out.print(key);
			String enc = AES.encrypt(key);
			String dec = AES.decrypt(enc);
			System.out.print("> " + enc);
			System.out.println("> " + dec);

		}

		String enc = AES.encrypt("");
		String dec = AES.decrypt(enc);
		System.out.print("> " + enc);
		System.out.println("> " + dec);
	}
}