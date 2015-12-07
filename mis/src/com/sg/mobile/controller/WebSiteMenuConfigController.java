package com.sg.mobile.controller;

import java.util.ArrayList;
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
import com.sg.mobile.entity.WebSiteMenuConfig;
import com.sg.mobile.service.WebSiteMenuConfigService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/webSiteMenuConfig.do")
public class WebSiteMenuConfigController extends AbstractController {
private final Logger log = Logger.getLogger(WebSiteMenuConfig.class);
	
	@Autowired 
	WebSiteMenuConfigService webSiteMenuConfigService;
	
	@Autowired
	private CodeService codeService;
	
	@RequestMapping(params = "method=clearwebsitemenuconfigcache")
	@ResponseBody
	public JsonResult clearWebSiteMenuConfigCache(HttpServletRequest request) {
		JsonResult result = new JsonResult();
    	
    	try {
    		webSiteMenuConfigService.ClearWebSiteMenuConfigCache();
    		webSiteMenuConfigService.GetWebSiteMenuConfigCache();
    		result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
			result.setMessage(null);
		}
		
		return result;
	}
	
	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/websitemenuconfig_list";
	}

	/**
	 * webSiteMenuConfig.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, WebSiteMenuConfig entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", entity.getCode());
		params.put("description", entity.getDescription());
		params.put("parentCode", entity.getParentCode());

		List<WebSiteMenuConfig> list = webSiteMenuConfigService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> hasChildrenMap = codeService.getCodeCacheMapByCategory("WEBSITEMENUCONFIGHASCHILDREN");
		Map<String, Object> levelCodeMap = codeService.getCodeCacheMapByCategory("WEBSITEMENUCONFIGLEVELCODE");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (WebSiteMenuConfig webSiteMenuConfig : list) {
			if(webSiteMenuConfig.getHasChildren() != null && !webSiteMenuConfig.getHasChildren().equals("")){
				String hasChildren = hasChildrenMap.get(webSiteMenuConfig.getHasChildren()).toString();
				webSiteMenuConfig.setHasChildren(hasChildren);
			}
			
			if(webSiteMenuConfig.getLevelCode() != null && !webSiteMenuConfig.getLevelCode().equals("") && !webSiteMenuConfig.getLevelCode().equals("Empty")){
				String levelCode = levelCodeMap.get(webSiteMenuConfig.getLevelCode()).toString();
				webSiteMenuConfig.setLevelCode(levelCode);
			}
			
			String status = statusMap.get(webSiteMenuConfig.getStatus()).toString();
			webSiteMenuConfig.setStatus(status);
		}

		int totalCount = webSiteMenuConfigService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, WebSiteMenuConfig entity) {
		JsonResult result = new JsonResult();

		try {
			entity.setId(UUID.randomUUID().toString());
			
			webSiteMenuConfigService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create WebSiteMenuConfig exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, WebSiteMenuConfig entity) {
		JsonResult result = new JsonResult();

		try {
			WebSiteMenuConfig model = webSiteMenuConfigService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setHasChildren(entity.getHasChildren());
			model.setDisplaySort(entity.getDisplaySort());
			model.setUrl(entity.getUrl() == null ? "" : entity.getUrl());
			model.setLevelCode(entity.getLevelCode());
			model.setParentCode(entity.getParentCode());
			model.setStatus(entity.getStatus());
			
			webSiteMenuConfigService.update(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update WebSiteMenuConfig exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		WebSiteMenuConfig entity = webSiteMenuConfigService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("description", entity.getDescription());
		map.put("url", entity.getUrl() == null ? "" : entity.getUrl());
		map.put("hasChildren", entity.getHasChildren());
		map.put("displaySort", entity.getDisplaySort());
		map.put("levelCode", entity.getLevelCode());
		
		String parentCode = "";
		if(entity.getParentCode() != null && entity.getParentCode().equals("")){
			parentCode = entity.getParentCode();
		}else {
			parentCode = "Empty";
		}
		
		map.put("parentCode", parentCode);
		map.put("status", entity.getStatus());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=isExist")
	@ResponseBody
	public boolean isExist(HttpServletRequest request, String code) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		
		List<WebSiteMenuConfig> list = webSiteMenuConfigService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=initparentcode")
	@ResponseBody
	public List<Map<String, Object>> initParentCode(String levelCode) {
		List<WebSiteMenuConfig> list = webSiteMenuConfigService.GetWebSiteMenuConfigCache();
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> defaultMap = new HashMap<String, Object>();
		defaultMap.put("id", "Empty");
		defaultMap.put("text", "无");
		maps.add(defaultMap);
		
		for (WebSiteMenuConfig webSiteMenuConfig : list) {
			if(!webSiteMenuConfig.getLevelCode().equals(levelCode)){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", webSiteMenuConfig.getCode());
				map.put("text", webSiteMenuConfig.getDescription() + "|" + webSiteMenuConfig.getLevelCode());
				maps.add(map);
			}
		}
		
		return maps;
	}
	
	@RequestMapping(params = "method=delete")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, String ids) {
		JsonResult result = new JsonResult();

		try {
			String[] array = ids.split(",");
			
			for (String id : array) {
				webSiteMenuConfigService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete WebSiteMenuConfig exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String code = request.getParameter("code");
		String description = request.getParameter("description");
		String parentCode = request.getParameter("parentcode");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("description", description);
		params.put("parentCode", parentCode);
		
		List<WebSiteMenuConfig> list = webSiteMenuConfigService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = webSiteMenuConfigService.ExpExcel(list, path);
				
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
	
	@RequestMapping(params = "method=getlevel1websitemenuconfigcache")
	@ResponseBody
	public List<Map<String, Object>> getLevel1WebSiteMenuConfigCache(){
		return webSiteMenuConfigService.getLevel1WebSiteMenuConfig();
	}
}
