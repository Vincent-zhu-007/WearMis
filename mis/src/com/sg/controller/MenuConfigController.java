package com.sg.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sg.entity.MenuConfig;
import com.sg.service.CodeService;
import com.sg.service.MenuConfigService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/menuConfig.do")
public class MenuConfigController extends AbstractController {
	private final Logger log = Logger.getLogger(MenuConfig.class);
	
	@Resource 
	MenuConfigService menuConfigService;
	
	@Autowired
	private CodeService codeService;
	
	@RequestMapping(params = "method=clearmenuconfigcache")
	@ResponseBody
	public JsonResult clearMenuConfigCache(HttpServletRequest request) {
		JsonResult result = new JsonResult();
    	
    	try {
    		menuConfigService.ClearMenuConfigCache();
    		menuConfigService.GetMenuConfigCache();
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
		return "system/menuconfig_list";
	}

	/**
	 * menuConfig.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MenuConfig entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", entity.getCode());
		params.put("description", entity.getDescription());
		params.put("parentCode", entity.getParentCode());

		List<MenuConfig> list = menuConfigService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> hasChildrenMap = codeService.getCodeCacheMapByCategory("CONTAIN");
		Map<String, Object> levelCodeMap = codeService.getCodeCacheMapByCategory("MENUCONFIGLEVEL");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (MenuConfig menuConfig : list) {
			
			String hasChildren = hasChildrenMap.get(menuConfig.getHasChildren()).toString();
			menuConfig.setHasChildren(hasChildren);
			
			if(menuConfig.getLevelCode() != null && !menuConfig.getLevelCode().equals("") && !menuConfig.getLevelCode().equals("Empty")){
				String levelCode = levelCodeMap.get(menuConfig.getLevelCode()).toString();
				menuConfig.setLevelCode(levelCode);
			}
			
			String status = statusMap.get(menuConfig.getStatus()).toString();
			menuConfig.setStatus(status);
		}

		int totalCount = menuConfigService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	/**
	 * menuConfig.do?method=add 请求以 HTTP POST 方式提交时，create()被调用
	 * 
	 * @param request
	 * @param entity
	 * @return
	 */
	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MenuConfig entity) {
		JsonResult result = new JsonResult();

		try {
			entity.setId(UUID.randomUUID().toString());
			
			menuConfigService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MenuConfig exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MenuConfig entity) {
		JsonResult result = new JsonResult();

		try {
			MenuConfig model = menuConfigService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setUrl(entity.getUrl());
			model.setHasChildren(entity.getHasChildren());
			model.setDisplaySort(entity.getDisplaySort());
			model.setLevelCode(entity.getLevelCode());
			model.setParentCode(entity.getParentCode());
			model.setStatus(entity.getStatus());
			
			menuConfigService.update(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MenuConfig exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MenuConfig entity = menuConfigService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("description", entity.getDescription());
		map.put("url", entity.getUrl());
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
		
		List<MenuConfig> list = menuConfigService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=initparentcode")
	@ResponseBody
	public List<Map<String, Object>> initParentCode(String levelCode) {
		List<MenuConfig> list = menuConfigService.GetMenuConfigCache();
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> defaultMap = new HashMap<String, Object>();
		defaultMap.put("id", "Empty");
		defaultMap.put("text", "无");
		maps.add(defaultMap);
		
		for (MenuConfig menuConfig : list) {
			if(!menuConfig.getLevelCode().equals(levelCode)){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", menuConfig.getCode());
				map.put("text", menuConfig.getDescription() + "|" + menuConfig.getLevelCode());
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
				menuConfigService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MenuConfig exception reason:" + e.getMessage());
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
		
		List<MenuConfig> list = menuConfigService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = menuConfigService.ExpExcel(list, path);
				
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
}
