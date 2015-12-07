package com.sg.mobile.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sg.controller.AbstractController;
import com.sg.controller.JsonResult;
import com.sg.entity.UserInfo;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.service.MobileServerConfigService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileServerConfig.do")
public class MobileServerConfigController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileServerConfig.class);

	@Autowired
	private MobileServerConfigService mobileServerConfigService;
	
	@Autowired
	private CodeService codeService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobileserverconfig_list";
	}

	/**
	 * mobileServerConfig.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileServerConfig entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("companyCode", entity.getCompanyCodeSearch());
		params.put("description", entity.getDescription());

		List<MobileServerConfig> list = mobileServerConfigService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> httpMethodMap = codeService.getCodeCacheMapByCategory("HTTPMETHOD");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (MobileServerConfig mobileServerConfig : list) {
			
			String httpMethod = httpMethodMap.get(mobileServerConfig.getHttpMethod()).toString();
			mobileServerConfig.setHttpMethod(httpMethod);
			
			String status = statusMap.get(mobileServerConfig.getStatus()).toString();
			mobileServerConfig.setStatus(status);
		}

		int totalCount = mobileServerConfigService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileServerConfig entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			entity.setId(UUID.randomUUID().toString());
			entity.setCreator(sessionUserName);
			
			mobileServerConfigService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileServerConfig exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileServerConfig entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileServerConfig model = mobileServerConfigService.get(entity.getId());
			
			model.setCompanyCode(entity.getCompanyCode());
			model.setHost(entity.getHost());
			model.setPort(entity.getPort());
			model.setAppName(entity.getAppName());
			model.setUserAgent(entity.getUserAgent());
			/*model.setX3GPPIntendedIdentity(entity.getX3GPPIntendedIdentity());*/
			model.setContentType(entity.getContentType());
			model.setDescription(entity.getDescription());
			model.setHttpMethod(entity.getHttpMethod());
			model.setUrlPart1(entity.getUrlPart1());
			model.setUrlPart2(entity.getUrlPart2());
			/*model.setUrlPart3(entity.getUrlPart3());
			model.setUrlPart4(entity.getUrlPart4());*/
			model.setResponseXmlFile(entity.getResponseXmlFile());
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			mobileServerConfigService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileServerConfig exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileServerConfig entity = mobileServerConfigService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		String companyCode = entity.getCompanyCode() == null ? "" : entity.getCompanyCode();
		map.put("companyCode", companyCode);
		
		map.put("id", entity.getId());
		map.put("host", entity.getHost());
		map.put("port", entity.getPort());
		map.put("appName", entity.getAppName());
		
		String userAgent = entity.getUserAgent() == null ? "" : entity.getUserAgent();
		map.put("userAgent", userAgent);
		
		String x3GPPIntendedIdentity = entity.getX3GPPIntendedIdentity() == null ? "" : entity.getX3GPPIntendedIdentity();
		map.put("x3GPPIntendedIdentity", x3GPPIntendedIdentity);
		
		map.put("contentType", entity.getContentType());
		map.put("description", entity.getDescription());
		map.put("httpMethod", entity.getHttpMethod());
		map.put("urlPart1", entity.getUrlPart1());
		map.put("urlPart2", entity.getUrlPart2());
		
		String urlPart3 = entity.getUrlPart3() == null ? "" : entity.getUrlPart3();
		map.put("urlPart3", urlPart3);
		
		String urlPart4 = entity.getUrlPart4() == null ? "" : entity.getUrlPart4();
		map.put("urlPart4", urlPart4);
		
		map.put("responseXmlFile", entity.getResponseXmlFile());
		map.put("status", entity.getStatus());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=getmobileserverconfigcache")
	@ResponseBody
	public List<MobileServerConfig> getMobileServerConfigCache(){
		return mobileServerConfigService.getMobileServerConfigCache();
	}
	
	@RequestMapping(params = "method=clearmobileserverconfigcache")
	@ResponseBody
	public JsonResult clearMobileServerConfigCache(){
		JsonResult result = new JsonResult();
		
		try {
			mobileServerConfigService.clearMobileServerConfigCache();
			mobileServerConfigService.getMobileServerConfigCache();
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
			result.setMessage(null);
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=delete")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, String ids) {
		JsonResult result = new JsonResult();

		try {
			String[] array = ids.split(",");
			
			for (String id : array) {
				mobileServerConfigService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileServerConfig exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String companyCodeSearch = request.getParameter("companyCodeSearch");
		String description = request.getParameter("description");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("companyCode", companyCodeSearch);
		params.put("description", description);
		
		List<MobileServerConfig> list = mobileServerConfigService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileServerConfigService.ExpExcel(list, path);
				
			} catch (Exception e) {
				// TODO: handle exception
				System.out.print(e.getMessage());
			}
		}
		
		try {
			filePath = response.encodeURL(filePath);
			response.getWriter().write(filePath);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
	}
	
	@RequestMapping(params = "method=copy")
	@ResponseBody
	public JsonResult copy(HttpServletRequest request) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String sourceCompanyCode = request.getParameter("sourceCompanyCode");
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("companyCode", sourceCompanyCode);
			
			List<MobileServerConfig> list = mobileServerConfigService.findForUnPage(params);
			
			if(list.size() > 0) {
				String companyCode = request.getParameter("companyCode");
				String host = request.getParameter("host");
				String port = request.getParameter("port");
				String appName = request.getParameter("appName");
				String userAgent = request.getParameter("userAgent");
				
				for (MobileServerConfig mobileServerConfig : list) {
					mobileServerConfig.setId(UUID.randomUUID().toString());
					mobileServerConfig.setCompanyCode(companyCode);
					mobileServerConfig.setHost(host);
					mobileServerConfig.setPort(port);
					mobileServerConfig.setAppName(appName);
					mobileServerConfig.setUserAgent(userAgent);
					mobileServerConfig.setCreator(sessionUserName);
					
					mobileServerConfigService.create(mobileServerConfig);
				}
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("copy MobileServerConfig exception reason：" + e.getMessage());
		}
		return result;
	}
}
