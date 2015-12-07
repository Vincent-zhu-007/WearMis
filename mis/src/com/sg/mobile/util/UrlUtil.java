package com.sg.mobile.util;

import java.net.URLDecoder;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.sg.mobile.entity.MobileServerConfig;

public class UrlUtil {
	public static boolean isMobileUrl(HttpServletRequest httpRequest, List<MobileServerConfig> mobileServerConfigs){
		boolean result = false;
		
		String host = httpRequest.getServerName();
		/*System.out.println("httpRequest host :" + host);*/
		int port = httpRequest.getServerPort();
		/*System.out.println("httpRequest port :" + port);*/
		String httpMethod = httpRequest.getMethod();
		/*System.out.println("httpRequest httpMethod :" + httpMethod);*/
		String appName = httpRequest.getContextPath();
		appName = appName.substring(1);
		/*System.out.println("httpRequest appName :" + appName);*/
		/*String contentType = httpRequest.getContentType();*/
		
		String uri = httpRequest.getRequestURI().toString();
		
		uri = uri.substring(1);
		int index = uri.indexOf("/~~/");
		
		if(index > 0){
			uri = uri.substring(0, index);
		}
		
		String[] array = uri.split("/");
		
		String urlPart1 = array[1].toString();
		/*System.out.println(urlPart1);*/
		
		String urlPart2 = array[2].toString();
		/*System.out.println(urlPart2);*/
		
		for (MobileServerConfig mobileServerConfig : mobileServerConfigs) {
			String mobileHost = mobileServerConfig.getHost();
			int mobilePort = Integer.parseInt(mobileServerConfig.getPort());
			String mobileHttpMethod = mobileServerConfig.getHttpMethod();
			String mobileAppName = mobileServerConfig.getAppName();
			/*String mobileContentType = mobileServerConfig.getContentType();*/
			
			boolean hostIsPass = false;
			if(mobileHost.equals(host)){
				hostIsPass = true;
			}
			
			boolean portIsPass = false;
			if(mobilePort == port){
				portIsPass = true;
			}
			
			boolean httpMethodIsPass = false;
			if(mobileHttpMethod.equals(httpMethod)){
				httpMethodIsPass = true;
			}
			
			boolean appNameIsPass = false;
			if(mobileAppName.equals(appName)){
				appNameIsPass = true;
			}
			
			boolean urlPart1Pass = false;
			if(mobileServerConfig.getUrlPart1().equals(urlPart1)){
				urlPart1Pass = true;
			}
			
			boolean urlPart2Pass = false;
			if(mobileServerConfig.getUrlPart2().equals(urlPart2)){
				urlPart2Pass = true;
			}
			
			if(hostIsPass && portIsPass && httpMethodIsPass && appNameIsPass && urlPart1Pass && urlPart2Pass){
				/*System.out.println("isMobileUrl true");*/
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public static MobileServerConfig getCurrentMobileServerConfig(HttpServletRequest httpRequest, List<MobileServerConfig> mobileServerConfigs){
		String companyCode = httpRequest.getHeader("CompanyCode");
		
		System.out.println(companyCode);
		
		String httpMethod = httpRequest.getMethod();
		
		String uri = httpRequest.getRequestURI().toString();
		
		uri = uri.substring(1);
		int index = uri.indexOf("/~~/");
		
		if(index > 0){
			uri = uri.substring(0, index);
		}
		
		String[] array = uri.split("/");
		/*String appName = array[0].toString();*/
		String urlPart1 = array[1].toString();
		String urlPart2 = array[2].toString();
		
		String urlPart3 = "";
		if(array.length >= 4){
			urlPart3 = array[3].toString();
		}
		
		String urlPart4 = "";
		if(array.length >= 5){
			urlPart4 = array[4].toString();
		}
		 
		for (MobileServerConfig mobileServerConfig : mobileServerConfigs) {
			if(companyCode != null && !companyCode.equals("")){
				if(mobileServerConfig.getHttpMethod().equals(httpMethod) && mobileServerConfig.getUrlPart1().equals(urlPart1) && mobileServerConfig.getUrlPart2().equals(urlPart2) && mobileServerConfig.getCompanyCode().equals(companyCode)){
					mobileServerConfig.setUrlPart3(urlPart3);
					mobileServerConfig.setUrlPart4(urlPart4);
					return mobileServerConfig;
				}
			}else {
				if(mobileServerConfig.getHttpMethod().equals(httpMethod) && mobileServerConfig.getUrlPart1().equals(urlPart1) && mobileServerConfig.getUrlPart2().equals(urlPart2)){
					mobileServerConfig.setUrlPart3(urlPart3);
					mobileServerConfig.setUrlPart4(urlPart4);
					return mobileServerConfig;
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static String getUrlPara(HttpServletRequest httpRequest){
		String urlPara = "";
		
		String uri = httpRequest.getRequestURI().toString();
		
		uri = uri.substring(1);
		int index = uri.indexOf("/~~/");
		
		if(index > 0){
			urlPara = uri.substring(index);
			urlPara = urlPara.replace("/~~/", "").trim();
			urlPara = URLDecoder.decode(urlPara);
		}
		
		return urlPara;
	}
	
	public static String getUrlParaValue(String partUrl){
		String value = "";
		
		String[] array = partUrl.split("=");
		String temp = array[1];
		
		value = temp.substring(1, temp.length() - 2);
		
		return value;
	}
	
	@SuppressWarnings("deprecation")
	public static String getUrlPara(String uri){
		String urlPara = "";
		
		uri = uri.substring(1);
		int index = uri.indexOf("/~~/");
		
		if(index > 0){
			urlPara = uri.substring(index);
			urlPara = urlPara.replace("/~~/", "").trim();
			urlPara = URLDecoder.decode(urlPara);
		}
		
		return urlPara;
	}
}
