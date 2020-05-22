package org.sdf.util;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class SimpleAES {

	public static Key skey = makeAESKey("0987654321098765");

	public static String padding16(String t){
		int nCount = 16 - (t.length() % 16);
		for (int i=0;i<nCount;i++)
			t += ' ';
		return t;
	}

	public static String encrypt(String text){
		byte[] enc = aesEncode(padding16(text).getBytes(), skey);
		return byte2hex(enc);
	}

	public static String decrypt(String encrypted) throws Exception {
		if (!StringUtil.valid(encrypted))
			return "";
		try {
			encrypted = encrypted.toLowerCase();

			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			byte[] original = cipher.doFinal(hex2byte(encrypted));
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
		}
		return "";
	}

	public static void main(String[] args) {
		// 16 글자 = 16 x 8 = 128 bit
		Key skey = makeAESKey("0987654321098765");
		String text = "1234";
		System.out.println("-- 평문 -----------------------------------");
		System.out.println("plainText : " + text);

		// 암호화
		System.out.println("-- 암호문 --------------------------------");
		System.out.println("encText : " + encrypt(text));

		// 복호화
		System.out.println("-- 복호문 --------------------------------");
		try{
			System.out.println("decText : " + decrypt(encrypt(text)));
		}catch(Exception e){

		}
	}

	public static String byte2hex (byte []b) {
		String hs ="";
		String stmp ="";
		for (int n =0 ;n <b.length ;n ++) {
			stmp =(Integer.toHexString (b [n ]&0XFF ));
			if (stmp.length ()==1 )hs =hs +"0"+stmp ;
			else hs =hs +stmp ;
			if (n <b.length -1 )hs = hs;
		}
		return hs;
	}

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
	public static Key makeAESKey(String sKey) {
		final byte[] key = sKey.getBytes();
		return new SecretKeySpec(key, "AES");
	}

	public static byte[] aesEncode(byte[] src, Key skey) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			return cipher.doFinal(src);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
