package com.sg.mobile.util;

import net.sf.json.JSONObject;

public class JsonUtil {
	public static String getJsonResult(String json){
		String str = "";
		
		if(json != null && !json.equals("")){
			JSONObject jsonObject = JSONObject.fromObject(json);
			
			if(jsonObject.containsKey("result")){
				String result = jsonObject.get("result").toString();
				if(result != null && !result.equals("") && result.equals("1")){
					if(jsonObject.containsKey("data")){
						String data = jsonObject.get("data").toString();
						
						str = data;
					}
				}
			}
		}
		
		return str;
	}
}
