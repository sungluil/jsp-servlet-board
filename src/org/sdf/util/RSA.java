package org.sdf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import org.sdf.log.Log;
import org.sdf.slim.config.GlobalResource;

public class RSA {
	static int KEY_SIZE = 1024;
	private static PublicKey publicKey;
	private static PrivateKey privateKey;
	
	private static String pubfile = "PublicKey.ser";
	private static String prifile = "PrivateKey.ser";
	static{
			loadKey();
	}
	
	static void loadKey(){
		try{
			File file = new File(GlobalResource.getBaseDir(), "WEB-INF/rsa/" + pubfile);
			
			if(!file.exists()){
				makeKey();
			}
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
	        publicKey = (PublicKey)ois.readObject();
	        ois.close();
	        
	        file = new File(GlobalResource.getBaseDir(), "WEB-INF/rsa/" + prifile);
			
			ois = new ObjectInputStream(new FileInputStream(file));
	        privateKey = (PrivateKey)ois.readObject();
	        ois.close();
      
		}catch(Exception e){
			Log.biz.err("RSA Key load failed.",e);
		}

	}
	
	static void makeKey(){
		try{
	        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	        kpg.initialize(KEY_SIZE);
	        KeyPair kp = kpg.genKeyPair();
	        PrivateKey privateKey = kp.getPrivate();
	        PublicKey publicKey = kp.getPublic();
	        File file = new File(GlobalResource.getBaseDir(), "WEB-INF/rsa/" + prifile);
	        
	        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
	        out.writeObject(privateKey);
	        out.close();
	        
	        file = new File(GlobalResource.getBaseDir(), "WEB-INF/rsa/" + pubfile);
	        if(file.exists()){
	        	file.delete();
	        }
	        
	        out = new ObjectOutputStream(new FileOutputStream(file));
	        out.writeObject(publicKey);
	        out.close();
		}catch(Exception e){}
   
	}
	
	/*
	static RSAPublicKeySpec getPublicSpec() throws Exception{
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(KEY_SIZE);
		
		KeyPair keyPair = generator.genKeyPair();
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();
		//session.setAttribute("__rsaPrivateKey__", privateKey);
		RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
		    
		return publicSpec;
	}
	*/
	
	public static String encrypt( String s){
		if(publicKey == null){
			Log.biz.err("WEB-INF/rsa/" + pubfile + " not found.");
		}
		try{
			
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);  
			
			  //the log  plainText encrypted by public key 
			byte[] encrypted = rsaCipher.doFinal(s.getBytes());
			
			String str = byte2hex(encrypted);
			return str.toUpperCase();
		}catch(Exception e){e.printStackTrace();}
		return "";
	}
	
	 public static String decrypt( String securedValue)  {
		 if(privateKey == null){
				Log.biz.err("WEB-INF/rsa/" + privateKey + " not found.");
		 }
		 try{
	        Cipher cipher = Cipher.getInstance("RSA");
	        byte[] encryptedBytes = hex2byte(securedValue);
	        cipher.init(Cipher.DECRYPT_MODE, privateKey);
	        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
	        String decryptedValue = new String(decryptedBytes, "utf-8"); // ¹®Z NŚµ�Ů
	        return decryptedValue;
		 }catch(Exception e){e.printStackTrace();}
		return "";
	 }
	 
	 private static String byte2hex(byte[] ba) {
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
	 
	 private static byte[] hex2byte(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return new byte[]{};
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            byte value = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }
        return bytes;
	 }

		   
    public  byte[] hexToByteArrayBI(String hexString) {
        return new BigInteger(hexString, 16).toByteArray();
    }
	
    
    public static void main(String[] args){
    	//RSA.makeKey();
/*    	
    	for(int i=0; i<10; i++){
    		String pw = "aaa12" + i;
    		String enc = RSA.encrypt(pw);
    		String dec = RSA.decrypt(enc);
    		System.out.println(i + ")" + pw + ":" + enc + ":" + dec);
    	}
    	String enc = "1E46A7EFFFED0C2AEDF99014DFC8E291679A752D7BDB018A37B73222C612C0B9125A9D243A4A6EABEF0374041D14F77540F2F292F2292E11CCFB65FAD2414B2B3CCF8382360FCFA8446EB2520395037EDA1DB53827DE4158352CE2061FEE8F90BA0CBAAA68F2AE6EC35F1F0FC638022171E743C8A2E0117CB033DDA8189DCBDC";
    	System.out.println( "-->"+RSA.decrypt(enc) );
    	*/
    	
    	System.out.println("pass : "+RSA.encrypt("aaa1234!"));
    	
    }
}
