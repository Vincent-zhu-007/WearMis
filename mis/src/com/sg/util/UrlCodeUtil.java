package com.sg.util;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class UrlCodeUtil {
	public static String urlEnCode(String str){
		if(str != null && !str.equals("")){
			try {
				str = URLEncoder.encode(str, "utf-8");
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
			}
		}
		
		return str;
	}
	
	public static String urlDeCode(String str){
		if(str != null && !str.equals("")){
			try {
				str = URLDecoder.decode(str, "utf-8");
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
			}
		}
		
		return str;
	}
}
