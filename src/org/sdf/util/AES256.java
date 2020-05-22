package org.sdf.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import org.apache.commons.codec.binary.Base64;

import com.oreilly.servlet.Base64Encoder;

public class AES256 {

	private static volatile AES256 INSTANCE;

	 final static String secretKey   = "EGENESecurity12#EGENESecurity34#"; //32bit
	 static byte[] IV                = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00}; //16bit

	public static AES256 getInstance() {
		if (INSTANCE == null) {
			synchronized (AES256.class) {
				if (INSTANCE == null)
					INSTANCE = new AES256();
			}
		}
		return INSTANCE;
	}

	// 암호화
	public static String AES_Encode(String str)
			throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyData = secretKey.getBytes();

		SecretKey secureKey = new SecretKeySpec(keyData, "AES");

		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV));

		byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
		String enStr = new String(Base64.encodeBase64(encrypted));

		return enStr;
	}

	// 복호화
	public static String AES_Decode(String str)
			throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyData = secretKey.getBytes();
		SecretKey secureKey = new SecretKeySpec(keyData, "AES");
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV));
		//c.init(Cipher.DECRYPT_MODE, secureKey);
		byte[] byteStr = Base64.decodeBase64(str.getBytes());

		return new String(c.doFinal(byteStr), "UTF-8");
	}
	
	public static void main(String[] args) {
		AES256 a256 = AES256.getInstance();
		// 16 글자 = 16 x 8 = 128 bit
		String text = "jdbc:oracle:thin:@localhost:1521:egene";
		//SQ7qcLkRBlailhG2Vg55KdAz7OnV9mAtls56+air20c=
		String enc_text = "";
		
		
		String s = "1|2|3";
 		String[] str_arr;
 		str_arr = StringUtil.getArrays(s, "|");
 		for (int i = 0; i < str_arr.length; i++) {
 			System.out.println("length : "+str_arr[i]);
		}
		/*try {
			System.out.println("-- 평문 -----------------------------------");
			System.out.println("plainText : " + text);
			// 암호화
			System.out.println("-- 암호문 --------------------------------");
			String encText = AES_Encode(text);
			System.out.println("AES_Encode : " + encText);
			String encText01 = Base64Encoder.encode(encText);
			System.out.println("Base64Encoder : " + encText01);			
			String encText02 = java.net.URLEncoder.encode(encText,"UTF-8");
			System.out.println("URLEncoder : " + encText02);
			
			// 복호화
			System.out.println("-- 복호문 --------------------------------");
			String decText01 = java.net.URLDecoder.decode((encText02), "UTF-8");
			System.out.println("decText01 : " + decText01);
			System.out.println("decText : " + AES_Decode(decText01));
		}catch(Exception e){
			System.out.println(e);
		}*/
	}
}