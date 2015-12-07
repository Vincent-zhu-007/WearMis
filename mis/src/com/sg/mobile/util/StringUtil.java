package com.sg.mobile.util;

public class StringUtil {
	public static String getUserNameByOwnerUri(String ownerUri){
		String userName = "";
		
		if(ownerUri != null && !ownerUri.equals("")){
			int i = ownerUri.indexOf(":");
			int j = ownerUri.lastIndexOf("@");
			
			userName = ownerUri.substring(i + 1, j);
		}
		
		return userName;
	}
}
