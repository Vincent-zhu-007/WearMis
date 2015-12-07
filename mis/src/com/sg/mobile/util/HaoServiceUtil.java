package com.sg.mobile.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HaoServiceUtil {
	public static String getLocationByMix(String requestData, String type, String key) throws MalformedURLException, IOException {
		String url = "http://api.haoservice.com/api/viplbs?requestdata="+requestData+"&type="+type+"&key="+key+"";
		
	    HttpURLConnection urlConnection = (HttpURLConnection)(new URL(url).openConnection());
	    urlConnection.connect();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
	    
	    StringBuffer sb = new StringBuffer();
	    
	    String line = null;
		while((line = reader.readLine()) != null){
			sb.append(line);
		}
	    
	    reader.close(); 
	    urlConnection.disconnect();
	    return sb.toString();
	}
	
	public static String getLocationByGps(String latlng, String type, String key) throws MalformedURLException, IOException {
		String url = " http://api.haoservice.com/api/getLocationinfor?latlng="+latlng+"&type="+type+"&key="+key+"";
		
	    HttpURLConnection urlConnection = (HttpURLConnection)(new URL(url).openConnection());
	    urlConnection.connect();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
	    
	    StringBuffer sb = new StringBuffer();
	    
	    String line = null;
		while((line = reader.readLine()) != null){
			sb.append(line);
		}
	    
	    reader.close(); 
	    urlConnection.disconnect();
	    return sb.toString();
	}
}
