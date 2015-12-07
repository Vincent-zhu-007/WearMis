package com.sg.util;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class StringUtil {
	public static String rTrim(String str, String sign){
		String result = str;
		
		int index = str.lastIndexOf(sign);
		int lastIndex = str.length() - 1;
		
		if(index > 0 && lastIndex == index){
			result = str.substring(0, index);
		}
		
		return result;
	}
	
	public static String ecodeBase64(byte[] buf) {  
        return (new BASE64Encoder()).encode(buf);  
    }  
  
    public static byte[] decodeBase64(String buf) {  
        try {  
            return (new BASE64Decoder()).decodeBuffer(buf);  
        } catch (IOException e) {  
        }  
        return null;  
    }  
  
    public static String remove(String str) {  
        return str.substring(0, str.length() - 1);  
    }
    
    public static String getRandomNum(int num){
		double PLUS = Math.pow(10, num);
		int random = (int) (Math.random()*PLUS);
		String result = String.valueOf(random);
		return result;
	}
}
