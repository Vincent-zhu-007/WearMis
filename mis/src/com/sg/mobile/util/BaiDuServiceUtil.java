package com.sg.mobile.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BaiDuServiceUtil {
	public String getBaiDuLocation(float x, float y) throws MalformedURLException, IOException {
	    String url = String.format("http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=%f&y=%f", x, y);
	    HttpURLConnection urlConnection = (HttpURLConnection)(new URL(url).openConnection());
	    urlConnection.connect();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    String lines = reader.readLine();
	    reader.close(); 
	    urlConnection.disconnect();
	    return lines;
	}
	
	public String getBaiDuAddress(double x, double y) throws MalformedURLException, IOException {
		String xEncode = URLEncoder.encode(String.valueOf(x), "UTF-8");
		String yEncode = URLEncoder.encode(String.valueOf(y), "UTF-8");
		
		String url = "http://api.map.baidu.com/geocoder?location="+yEncode+","+xEncode+"&output=xml&key=XL9I0GDfqGsiU3pGMjeF0Mnx";
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
	
	public static String getBaiDuAddressByGps(String x, String y) throws MalformedURLException, IOException {
		
		String url = "http://api.map.baidu.com/geocoder/v2/?ak=XL9I0GDfqGsiU3pGMjeF0Mnx&callback=renderReverse&location=" + x + "," + y + "&coordtype=wgs84ll&output=xml&pois=0";
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
