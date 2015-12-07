package com.sg.mobile.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.service.MobileServerConfigService;
import com.sg.mobile.util.UrlUtil;
import com.sg.mobile.util.XmlUtil;

public class MobileFilter implements Filter {
	
	private MobileServerConfigService mobileServerConfigService;  
	
	
	public MobileServerConfigService getMobileServerConfigService() {
		return mobileServerConfigService;
	}

	public void setMobileServerConfigService(
			MobileServerConfigService mobileServerConfigService) {
		this.mobileServerConfigService = mobileServerConfigService;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest httpRequest = ((HttpServletRequest) request);
		HttpServletResponse httpResponse = ((HttpServletResponse) response);
		
		httpRequest.setCharacterEncoding("UTF-8");

		String url = httpRequest.getRequestURI();
		int index = url.lastIndexOf("/");
		String lastPart = url.substring(index + 1);
		
		if(lastPart.equals("") || lastPart.indexOf(".do") > 0 || lastPart.indexOf(".jsp") > 0 || lastPart.indexOf(".css") > 0 || lastPart.indexOf(".js") > 0){
			chain.doFilter(request, response);
			return;
		}
		
		Map<String, Object> mobileFileTypeMap = mobileServerConfigService.getMobileFileTypeMap();
		for (Map.Entry entry : mobileFileTypeMap.entrySet()) {
			String types = entry.getValue().toString();
			String[] array = types.split(",");
			for (String type : array) {
				if(lastPart.indexOf("." + type) > 0){
					chain.doFilter(request, response);
					return;
				}
			}
		}
		
		String contextPath = httpRequest.getContextPath();
		String doMain =  request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
		if(StringUtils.isNotEmpty(contextPath)){
			doMain = doMain + contextPath;
		}
		
		List<MobileServerConfig> mobileServerConfigs = mobileServerConfigService.getMobileServerConfigCache();
		
		String httpContentType = httpRequest.getContentType();
		
		if(mobileServerConfigs != null && mobileServerConfigs.size() > 0){
			/*System.out.println(url);*/
			
			boolean isMobileUrl = UrlUtil.isMobileUrl(httpRequest, mobileServerConfigs);
			
			if(isMobileUrl){
				/*System.out.println("begin get currentMobileServerConfig");*/
				
				MobileServerConfig model = UrlUtil.getCurrentMobileServerConfig(httpRequest, mobileServerConfigs);
				
				if(model != null){
					/*System.out.println("begin process mobile request");*/
					
					MobileCompany mobileCompany = mobileServerConfigService.getMobileCompanyByComapnyCode(model.getCompanyCode());
					
					if(mobileCompany == null){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("ErrorCode", "MobileCompany is null");
						responseError(httpRequest, httpResponse, httpContentType, map);
					}
					
					String fullUrl = "http://" + model.getHost() + ":" + model.getPort() + url;
					model.setUrl(fullUrl);
					
					String userAgent = httpRequest.getServletContext().getRealPath("/");
					model.setUserAgent(userAgent);
					
					String httpMethod = model.getHttpMethod();
					String contentType = model.getContentType();
					
					String xml = "";
					
					if(httpMethod.equals("GET")){
						
					}else if (httpMethod.equals("PUT")) {
						
					}else if (httpMethod.equals("DELETE")) {
						
					}else if (httpMethod.equals("POST")) {
						String urlPara = UrlUtil.getUrlPara(httpRequest);
						/*System.out.println(urlPara);*/
						
						if(model.getContentType().equals("text/json")){
							String json = "";
							
							String requestXml = getXmlByReader(httpRequest);
							
							if(requestXml != null && !requestXml.equals("")){
								urlPara = requestXml;
							}
							
							json = mobileServerConfigService.serverPostProcess(model, requestXml, urlPara);
							responseJson(httpRequest, httpResponse, json);
						}else if (model.getContentType().equals("file")) {
							String savePath = httpRequest.getServletContext().getRealPath("/") + "attached/";
							InputStream is = getFileByHttpRequest(httpRequest);
							
							String json = mobileServerConfigService.serverPostFileProcess(model, is, urlPara, savePath);
							
							is.close();
							
							responseStatus(httpRequest,httpResponse,"text/json", json);
						}else {
							String requestXml = getXmlByReader(httpRequest);
							/*System.out.println(requestXml);*/
							
							xml = mobileServerConfigService.serverPostProcess(model, requestXml, urlPara);
							
							/*System.out.println("Post cust xml : " + xml);*/
							
							responseStatus(httpRequest,httpResponse,contentType, xml);
						}
					}
				}else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("ErrorCode", "E001");
					responseError(httpRequest, httpResponse, httpContentType, map);
				}
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ErrorCode", "E002");
				responseError(httpRequest, httpResponse, httpContentType, map);
			}
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ErrorCode", "E003");
			responseError(httpRequest, httpResponse, httpContentType, map);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub
		ServletContext context = config.getServletContext();  
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);  
        mobileServerConfigService = (MobileServerConfigService) ctx.getBean("mobileServerConfigService");
	}
	
	private void responseXml(HttpServletRequest request, HttpServletResponse response, String xml, String contentType) throws IOException {
		response.setContentType(contentType + ";charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		PrintWriter out = response.getWriter();
		out.write(xml);
	}
	
	private void responseStatus(HttpServletRequest request, HttpServletResponse response, String contentType, String responseStatus) throws IOException {
		response.setContentType(contentType + ";charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		PrintWriter out = response.getWriter();
		out.write(responseStatus);
	}
	
	@SuppressWarnings("rawtypes")
	private void responseErrorXml(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map){
		String contentType = "text/xml; charset=UTF-8";
		
		String mobileXmlPath = "/com/sg/mobile/xml/";
		String fileName = "error.xml";
		String fileFullName = mobileXmlPath + fileName;
		Document doc = XmlUtil.getXmlDoc(fileFullName);
		Element rootElement = doc.getRootElement();
		Element errorElement = (Element)rootElement.selectSingleNode("error");
		
		String key = "";
		String value = "";
		for (Map.Entry entry : map.entrySet()) {
			key = entry.getKey().toString();
			value = entry.getValue().toString();
			break;
		}
		
		errorElement.setText(key + ":" + value);
		String xml = doc.asXML();
		
		try {
			responseXml(request, response, xml, contentType);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
	}
	
	private String getXmlByReader(HttpServletRequest httpRequest){
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader reader = httpRequest.getReader();
			String line = null;
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
			
			reader.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		
		return sb.toString();
	}
	
	private InputStream getFileByHttpRequest(HttpServletRequest httpRequest){
		InputStream is = null;
		
		try {
			is = httpRequest.getInputStream();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		
		return is;
	}
	
	private void responseJson(HttpServletRequest request, HttpServletResponse response, String json) throws IOException {
		String CONTENT_TYPE = "text/json; charset=UTF-8";
		
		response.setContentType(CONTENT_TYPE);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		PrintWriter out = response.getWriter();
		out.write(json);
	}
	
	private void responseErrorJson(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map){
		
		String json = "";
		JSONArray jsonArray = JSONArray.fromObject(map);
		json = jsonArray.toString();
		try {
			responseJson(request, response, json);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
	}
	
	private void responseError(HttpServletRequest request, HttpServletResponse response, String contentType, Map<String, Object> map){
		if(contentType != null && !contentType.equals("")){
			if(contentType.equals("text/json")){
				responseErrorJson(request, response, map);
			}else {
				responseErrorXml(request, response, map);
			}
		}else {
			responseErrorXml(request, response, map);
		}
	}
}
