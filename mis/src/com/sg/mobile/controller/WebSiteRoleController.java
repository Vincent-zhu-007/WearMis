package com.sg.mobile.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sg.controller.AbstractController;
import com.sg.controller.JsonResult;
import com.sg.entity.UserInfo;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.entity.WebSiteMenuConfig;
import com.sg.mobile.entity.WebSiteRole;
import com.sg.mobile.service.MobileUserService;
import com.sg.mobile.service.WebSiteMenuConfigService;
import com.sg.mobile.service.WebSiteMenuService;
import com.sg.mobile.service.WebSiteRoleService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/webSiteRole.do")
public class WebSiteRoleController extends AbstractController {
	private final Logger log = Logger.getLogger(WebSiteRole.class);

	@Autowired
	private WebSiteRoleService webSiteRoleService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private WebSiteMenuConfigService webSiteMenuConfigService;
	
	@Autowired
	private WebSiteMenuService webSiteMenuService;
	
	@Autowired
	private MobileUserService mobileUserService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/websiterole_list";
	}

	/**
	 * webSiteRole.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, WebSiteRole entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", entity.getCodeName());

		List<WebSiteRole> list = webSiteRoleService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (WebSiteRole webSiteRole : list) {
			String status = statusMap.get(webSiteRole.getStatus()).toString();
			webSiteRole.setStatus(status);
		}

		int totalCount = webSiteRoleService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, WebSiteRole entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String uuid = UUID.randomUUID().toString();
			entity.setId(uuid);
			entity.setRoleId(uuid);
			entity.setCategory("WEBSITEROLE");
			entity.setCreator(sessionUserName);
			
			webSiteRoleService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create WebSiteRole exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, WebSiteRole entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			WebSiteRole model = webSiteRoleService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setStatus(entity.getStatus());
			model.setPermission(entity.getPermission());
			model.setUpdator(sessionUserName);
			
			webSiteRoleService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update WebSiteRole exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		WebSiteRole entity = webSiteRoleService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("description", entity.getDescription());
		map.put("status", entity.getStatus());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=isExist")
	@ResponseBody
	public boolean isExist(HttpServletRequest request, String codeName) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", codeName);
		
		List<WebSiteRole> list = webSiteRoleService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=initwebsitemenu")
	@ResponseBody
	public JSONArray InitWebSiteMenu(HttpServletRequest request) {
		String userName = "";
    	
		List<WebSiteMenuConfig> webSiteMenuConfigs = webSiteMenuConfigService.GetWebSiteMenuConfigCache();
		
		List<WebSiteMenuConfig> level1WebSiteMenuConfigs = new ArrayList<WebSiteMenuConfig>();
		
		for (WebSiteMenuConfig entity : webSiteMenuConfigs) {
			if(entity.getLevelCode().equals("Level1")) {
				level1WebSiteMenuConfigs.add(entity);
			}
		}
		
		JSONArray jSONArray = webSiteMenuService.buildWebSiteRoleTree(level1WebSiteMenuConfigs, userName);
		
		System.out.print(jSONArray.toString());
		
		return jSONArray;
	}
	
	@RequestMapping(params = "method=initwebsitemenubypermission")
	@ResponseBody
	public JSONArray InitWebSiteMenuByPermission(HttpServletRequest request, String roleId) {
		String userName = "";
    	
		List<WebSiteMenuConfig> webSiteMenuConfigs = webSiteMenuConfigService.GetWebSiteMenuConfigCache();
		
		List<WebSiteMenuConfig> level1WebSiteMenuConfigs = new ArrayList<WebSiteMenuConfig>();
		
		for (WebSiteMenuConfig entity : webSiteMenuConfigs) {
			if(entity.getLevelCode().equals("Level1")) {
				level1WebSiteMenuConfigs.add(entity);
			}
		}
		
		Map<String, Object> permission = new HashMap<String, Object>();
		
		WebSiteRole webSiteRole = webSiteRoleService.get(roleId);
		
		String strPermission = webSiteRole.getPermission();
		
		String[] array = strPermission.split(",");
		
		for (String key : array) {
			if(!permission.containsKey(key)){
				permission.put(key, key);
			}
		}
		
		JSONArray jSONArray = webSiteMenuService.buildWebSiteRoleTreeByPermission(level1WebSiteMenuConfigs, userName, permission);
		
		System.out.print(jSONArray.toString());
		
		return jSONArray;
	}
	
	@RequestMapping(params = "method=delete")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, String ids) {
		JsonResult result = new JsonResult();

		try {
			String[] array = ids.split(",");
			
			for (String id : array) {
				webSiteRoleService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete WebSiteRole exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String codeName = request.getParameter("codename");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", codeName);
		
		List<WebSiteRole> list = webSiteRoleService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = webSiteRoleService.ExpExcel(list, path);
				
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
	
	@RequestMapping(params = "method=initwebsiteusermenubypermission")
	@ResponseBody
	public JSONArray InitWebSiteUserMenuByPermission(HttpServletRequest request, String id) {
		MobileUser mobileUser = mobileUserService.get(id);
		
		String webSiteRoleCodeName = mobileUser.getWebSiteRole();
		
		if(webSiteRoleCodeName != null && !webSiteRoleCodeName.equals("")){
			String userName = "";
	    	
			List<WebSiteMenuConfig> webSiteMenuConfigs = webSiteMenuConfigService.GetWebSiteMenuConfigCache();
			
			List<WebSiteMenuConfig> level1WebSiteMenuConfigs = new ArrayList<WebSiteMenuConfig>();
			
			for (WebSiteMenuConfig entity : webSiteMenuConfigs) {
				if(entity.getLevelCode().equals("Level1")) {
					level1WebSiteMenuConfigs.add(entity);
				}
			}
			
			Map<String, Object> permission = new HashMap<String, Object>();
			
			WebSiteRole webSiteRole = webSiteRoleService.getWebSiteRoleByCodeName(webSiteRoleCodeName);
			
			String strPermission = webSiteRole.getPermission();
			
			String[] array = strPermission.split(",");
			
			for (String key : array) {
				if(!permission.containsKey(key)){
					permission.put(key, key);
				}
			}
			
			JSONArray jSONArray = webSiteMenuService.buildWebSiteRoleTreeByPermission(level1WebSiteMenuConfigs, userName, permission);
			
			return jSONArray;
		}
		
		return null;
	}
}
