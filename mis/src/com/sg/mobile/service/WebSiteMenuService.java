package com.sg.mobile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.WebSiteMenu;
import com.sg.mobile.entity.WebSiteMenuConfig;
import com.sg.util.StringUtil;

@Service
public class WebSiteMenuService {
	@Autowired
	private WebSiteMenuConfigService webSiteMenuConfigService;
	
	@Autowired
	private WebSiteRoleService webSiteRoleService;
	
	public List<WebSiteMenu> getWebSiteMenus(List<WebSiteMenuConfig> webSiteMenuConfigs, String userName) {
		
		List<WebSiteMenu> menus = new ArrayList<WebSiteMenu>();
		
		List<WebSiteMenuConfig> level1WebSiteMenuConfigs = new ArrayList<WebSiteMenuConfig>();
		for (WebSiteMenuConfig webSiteMenuConfig : webSiteMenuConfigs) {
			if(webSiteMenuConfig.getLevelCode().equals("Level1")) {
				level1WebSiteMenuConfigs.add(webSiteMenuConfig);
			}
		}
		
		for (WebSiteMenuConfig webSiteMenuConfig : level1WebSiteMenuConfigs) {
			menus.add(buildWebSiteMenu(webSiteMenuConfig, webSiteMenuConfigs));
		}
		
		return menus;
    }

    public WebSiteMenu buildWebSiteMenu(WebSiteMenuConfig webSiteMenuConfig, List<WebSiteMenuConfig> webSiteMenuConfigs) {
    	WebSiteMenu menu = new WebSiteMenu();
        
        menu.setMenuId(webSiteMenuConfig.getCode());
        menu.setMenuName(webSiteMenuConfig.getDescription());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("parentCode", webSiteMenuConfig.getCode());
        
        List<WebSiteMenuConfig> childWebSiteMenus = webSiteMenuConfigService.findForUnPage(params);
        
        if(childWebSiteMenus != null && childWebSiteMenus.size() > 0) {
        	
        	List<WebSiteMenu> childWebSiteMenu = new ArrayList<WebSiteMenu>();
        	menu.setChildWebSiteMenu(childWebSiteMenu);
        	
        	for (WebSiteMenuConfig mc : childWebSiteMenus) {
        		menu.getChildWebSiteMenu().add((buildWebSiteMenu(mc, webSiteMenuConfigs)));
			}
        }
        
        return menu;
    }
    
    public JSONArray buildWebSiteMenuTree(List<WebSiteMenuConfig> webSiteMenuConfigs, Map<String, Object> permission) {
		JSONArray jSONArray = new JSONArray();
		
		if (webSiteMenuConfigs != null && webSiteMenuConfigs.size() > 0) {
			for (int i = 0; i < webSiteMenuConfigs.size(); i++) {
				
				if(!permission.containsKey(webSiteMenuConfigs.get(i).getCode())){
					continue;
				}
				
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", webSiteMenuConfigs.get(i).getId());
				jsonObject1.put("text", webSiteMenuConfigs.get(i).getDescription());
				
				JSONObject jsonObject = JSONObject.fromObject(webSiteMenuConfigs.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				List<WebSiteMenuConfig> webSiteMenuConfigCache = webSiteMenuConfigService.GetWebSiteMenuConfigCache();
				List<WebSiteMenuConfig> childWebSiteMenuConfig = new ArrayList<WebSiteMenuConfig>();
				for (WebSiteMenuConfig entity : webSiteMenuConfigCache) {
					if(entity.getParentCode().equals(webSiteMenuConfigs.get(i).getCode()) && !entity.getLevelCode().equals("Button")) {
						childWebSiteMenuConfig.add(entity);
					}
				}
				
				if (childWebSiteMenuConfig != null && childWebSiteMenuConfig.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildWebSiteMenuTree(childWebSiteMenuConfig, permission));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
    
    public JSONArray buildWebSiteRoleTree(List<WebSiteMenuConfig> webSiteMenuConfigs, String userName) {
		JSONArray jSONArray = new JSONArray();
		
		if (webSiteMenuConfigs != null && webSiteMenuConfigs.size() > 0) {
			for (int i = 0; i < webSiteMenuConfigs.size(); i++) {
				
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", webSiteMenuConfigs.get(i).getId());
				jsonObject1.put("text", webSiteMenuConfigs.get(i).getDescription());
				
				JSONObject jsonObject = JSONObject.fromObject(webSiteMenuConfigs.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				List<WebSiteMenuConfig> webSiteMenuConfigCache = webSiteMenuConfigService.GetWebSiteMenuConfigCache();
				List<WebSiteMenuConfig> childWebSiteMenuConfig = new ArrayList<WebSiteMenuConfig>();
				for (WebSiteMenuConfig entity : webSiteMenuConfigCache) {
					if(entity.getParentCode().equals(webSiteMenuConfigs.get(i).getCode())) {
						childWebSiteMenuConfig.add(entity);
					}
				}
				
				if (childWebSiteMenuConfig != null && childWebSiteMenuConfig.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildWebSiteRoleTree(childWebSiteMenuConfig, userName));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
    
    public JSONArray buildWebSiteRoleTreeByPermission(List<WebSiteMenuConfig> webSiteMenuConfigs, String userName, Map<String, Object> permission) {
		JSONArray jSONArray = new JSONArray();
		
		if (webSiteMenuConfigs != null && webSiteMenuConfigs.size() > 0) {
			for (int i = 0; i < webSiteMenuConfigs.size(); i++) {
				
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", webSiteMenuConfigs.get(i).getId());
				jsonObject1.put("text", webSiteMenuConfigs.get(i).getDescription());
				
				JSONObject jsonObject = JSONObject.fromObject(webSiteMenuConfigs.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				if(permission.containsKey(webSiteMenuConfigs.get(i).getCode())){
					jsonObject1.put("checked", true);
				}
				
				List<WebSiteMenuConfig> webSiteMenuConfigCache = webSiteMenuConfigService.GetWebSiteMenuConfigCache();
				List<WebSiteMenuConfig> childWebSiteMenuConfig = new ArrayList<WebSiteMenuConfig>();
				for (WebSiteMenuConfig entity : webSiteMenuConfigCache) {
					if(entity.getParentCode().equals(webSiteMenuConfigs.get(i).getCode())) {
						childWebSiteMenuConfig.add(entity);
					}
				}
				
				if (childWebSiteMenuConfig != null && childWebSiteMenuConfig.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildWebSiteRoleTreeByPermission(childWebSiteMenuConfig, userName, permission));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
    
    public String initWebSiteMenuJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara) {
    	String paraJson = urlPara;
    	JSONObject jsonObjectPara = JSONObject.fromObject(paraJson);
    	
    	String webSiteRole = "";
		if(jsonObjectPara.containsKey("webSiteRole")){
			webSiteRole = jsonObjectPara.opt("webSiteRole").toString();
		}
		
		String sql = "";
		String[] array = webSiteRole.split(",");
		for (String webSiteRoleCode : array) {
			sql += "'" + webSiteRoleCode + "',";
		}
		
		sql = StringUtil.rTrim(sql, ",");
		
		Map<String, Object> roleParams = new HashMap<String, Object>();
		roleParams.put("codeName", sql);
		
		Map<String, Object> permission = webSiteRoleService.findPermissionByRoles(roleParams);
    	
		List<WebSiteMenuConfig> webSiteMenuConfigs = webSiteMenuConfigService.GetWebSiteMenuConfigCache();
		
		for (WebSiteMenuConfig entity : webSiteMenuConfigs) {
			if(permission.containsKey(entity.getCode())){
				if(!permission.containsKey(entity.getParentCode())){
					permission.put(entity.getParentCode(), entity.getParentCode());
				}
			}
		}
		
		List<WebSiteMenuConfig> level1WebSiteMenuConfigs = new ArrayList<WebSiteMenuConfig>();
		
		for (WebSiteMenuConfig entity : webSiteMenuConfigs) {
			if(entity.getLevelCode().equals("Level1")) {
				level1WebSiteMenuConfigs.add(entity);
			}
		}
		
		JSONArray jSONArray = buildWebSiteMenuTree(level1WebSiteMenuConfigs, permission);
		
		return jSONArray.toString();
	}
}
