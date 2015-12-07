package com.sg.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 {
	
	public static String convertSHA1(String strObj) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(strObj.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0){
					i += 256;
				}
				
				if (i < 16){
					buf.append("0");
					buf.append(Integer.toHexString(i));
				}
			}
		
			result = buf.toString();
			//System.out.println("result32: " + buf.toString());// 32位的加密
			//System.out.println("result16: " + buf.toString().substring(8, 24));// 16位的加密
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
