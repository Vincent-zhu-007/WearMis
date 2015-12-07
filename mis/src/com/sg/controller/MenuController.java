package com.sg.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sg.entity.MenuConfig;
import com.sg.service.MenuConfigService;
import com.sg.service.MenuService;

@Controller
@RequestMapping("/menu.do")
public class MenuController extends AbstractController {
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private MenuConfigService menuConfigService;
	
	@RequestMapping
	public String management() {
		return "west";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "method=initmenu")
	@ResponseBody
	public JSONArray InitMenu(HttpServletRequest request) {
		Map<String, Object> permission = (Map<String, Object>)request.getSession().getAttribute("permission");
		
		List<MenuConfig> menuConfigs = menuConfigService.GetMenuConfigCache();
		
		Map<String, Object> permissionParent = new HashMap<String, Object>();
		for (MenuConfig entity : menuConfigs) {
			if(permission.containsKey(entity.getCode())){
				permissionParent.put(entity.getCode(), entity.getParentCode());
			}
		}
		
		List<MenuConfig> level1MenuConfigs = new ArrayList<MenuConfig>();
		
		for (MenuConfig entity : menuConfigs) {
			if(entity.getLevelCode().equals("Level1")) {
				if(permission.containsKey(entity.getCode()) || permissionParent.containsValue(entity.getCode())){
					level1MenuConfigs.add(entity);
				}
			}
		}
		
		JSONArray jSONArray = menuService.buildMenuTree(level1MenuConfigs, permission);
		
		return jSONArray;
	}
}
