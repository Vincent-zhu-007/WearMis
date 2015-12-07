package com.sg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import com.sg.dao.MenuConfigDao;
import com.sg.entity.Menu;
import com.sg.entity.MenuConfig;

@Service
public class MenuService {
	@Resource
	private MenuConfigDao menuConfigDao;
	
	@Resource
	private MenuConfigService menuConfigService;
	
	public List<Menu> getMenus(List<MenuConfig> menuConfigs, String userName) {
		
		List<Menu> menus = new ArrayList<Menu>();
		
		List<MenuConfig> level1MenuConfigs = new ArrayList<MenuConfig>();
		for (MenuConfig menuConfig : menuConfigs) {
			if(menuConfig.getLevelCode().equals("Level1")) {
				level1MenuConfigs.add(menuConfig);
			}
		}
		
		for (MenuConfig menuConfig : level1MenuConfigs) {
			menus.add(buildMenu(menuConfig, menuConfigs));
		}
		
		return menus;
    }

    public Menu buildMenu(MenuConfig menuConfig, List<MenuConfig> menuConfigs) {
        Menu menu = new Menu();
        
        menu.setMenuId(menuConfig.getCode());
        menu.setMenuName(menuConfig.getDescription());
        menu.setUrl(menuConfig.getUrl());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("parentCode", menuConfig.getCode());
        
        List<MenuConfig> childMenus = menuConfigDao.findForUnPage(params);
        
        if(childMenus != null && childMenus.size() > 0) {
        	
        	List<Menu> childMenu = new ArrayList<Menu>();
        	menu.setChildMenu(childMenu);
        	
        	for (MenuConfig mc : childMenus) {
        		menu.getChildMenu().add((buildMenu(mc, menuConfigs)));
			}
        }
        
        return menu;
    }
    
    public JSONArray buildMenuTree(List<MenuConfig> menuConfigs, Map<String, Object> permission) {
		JSONArray jSONArray = new JSONArray();
		
		if (menuConfigs != null && menuConfigs.size() > 0) {
			for (int i = 0; i < menuConfigs.size(); i++) {
				
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", menuConfigs.get(i).getId());
				jsonObject1.put("text", menuConfigs.get(i).getDescription());
				
				JSONObject jsonObject = JSONObject.fromObject(menuConfigs.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				List<MenuConfig> menuConfigCache = menuConfigService.GetMenuConfigCache();
				List<MenuConfig> childMenuConfig = new ArrayList<MenuConfig>();
				for (MenuConfig entity : menuConfigCache) {
					if(entity.getParentCode().equals(menuConfigs.get(i).getCode()) && permission.containsKey(entity.getCode())) {
						
						if(entity.getLevelCode().equals("Button")){
							continue;
						}else {
							childMenuConfig.add(entity);
						}
					}
				}
				
				if (childMenuConfig != null && childMenuConfig.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildMenuTree(childMenuConfig, permission));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
    
    public JSONArray buildRoleTree(List<MenuConfig> menuConfigs, String userName) {
		JSONArray jSONArray = new JSONArray();
		
		if (menuConfigs != null && menuConfigs.size() > 0) {
			for (int i = 0; i < menuConfigs.size(); i++) {
				
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", menuConfigs.get(i).getId());
				jsonObject1.put("text", menuConfigs.get(i).getDescription());
				
				JSONObject jsonObject = JSONObject.fromObject(menuConfigs.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				List<MenuConfig> menuConfigCache = menuConfigService.GetMenuConfigCache();
				List<MenuConfig> childMenuConfig = new ArrayList<MenuConfig>();
				for (MenuConfig entity : menuConfigCache) {
					if(entity.getParentCode().equals(menuConfigs.get(i).getCode())) {
						childMenuConfig.add(entity);
					}
				}
				
				if (childMenuConfig != null && childMenuConfig.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildRoleTree(childMenuConfig, userName));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
    
    public JSONArray buildRoleTreeByPermission(List<MenuConfig> menuConfigs, String userName, Map<String, Object> permission) {
		JSONArray jSONArray = new JSONArray();
		
		if (menuConfigs != null && menuConfigs.size() > 0) {
			for (int i = 0; i < menuConfigs.size(); i++) {
				
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", menuConfigs.get(i).getId());
				jsonObject1.put("text", menuConfigs.get(i).getDescription());
				
				JSONObject jsonObject = JSONObject.fromObject(menuConfigs.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				if(permission.containsKey(menuConfigs.get(i).getCode())){
					jsonObject1.put("checked", true);
				}
				
				List<MenuConfig> menuConfigCache = menuConfigService.GetMenuConfigCache();
				List<MenuConfig> childMenuConfig = new ArrayList<MenuConfig>();
				for (MenuConfig entity : menuConfigCache) {
					if(entity.getParentCode().equals(menuConfigs.get(i).getCode())) {
						childMenuConfig.add(entity);
					}
				}
				
				if (childMenuConfig != null && childMenuConfig.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildRoleTreeByPermission(childMenuConfig, userName, permission));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
}
