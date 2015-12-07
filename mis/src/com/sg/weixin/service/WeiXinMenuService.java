package com.sg.weixin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sg.weixin.entity.WeiXinChannel;
import com.sg.weixin.entity.WeiXinMenu;

@Service
public class WeiXinMenuService {
	@Autowired
	private WeiXinChannelService weiXinChannelService;
	
	public List<WeiXinMenu> getWeiXinMenus(List<WeiXinChannel> weiXinChannels, String userName) {
		
		List<WeiXinMenu> menus = new ArrayList<WeiXinMenu>();
		
		List<WeiXinChannel> level1WeiXinChannels = new ArrayList<WeiXinChannel>();
		for (WeiXinChannel weiXinChannel : weiXinChannels) {
			if(weiXinChannel.getChannelLevelCode().equals("Level1")) {
				level1WeiXinChannels.add(weiXinChannel);
			}
		}
		
		for (WeiXinChannel weiXinChannel : level1WeiXinChannels) {
			menus.add(buildWeiXinMenu(weiXinChannel, weiXinChannels));
		}
		
		return menus;
    }

    public WeiXinMenu buildWeiXinMenu(WeiXinChannel weiXinChannel, List<WeiXinChannel> weiXinChannels) {
    	WeiXinMenu menu = new WeiXinMenu();
        
        menu.setMenuId(weiXinChannel.getChannelCodeName());
        menu.setMenuName(weiXinChannel.getDescription());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("channelParentCode", weiXinChannel.getChannelCodeName());
        
        List<WeiXinChannel> childWeiXinMenus = weiXinChannelService.findForUnPage(params);
        
        if(childWeiXinMenus != null && childWeiXinMenus.size() > 0) {
        	
        	List<WeiXinMenu> childWeiXinMenu = new ArrayList<WeiXinMenu>();
        	menu.setChildWeiXinMenu(childWeiXinMenu);
        	
        	for (WeiXinChannel wc : childWeiXinMenus) {
        		menu.getChildWeiXinMenu().add((buildWeiXinMenu(wc, weiXinChannels)));
			}
        }
        
        return menu;
    }
    
    public JSONArray buildWeiXinMenuTree(List<WeiXinChannel> weiXinChannels, Map<String, Object> permission) {
		JSONArray jSONArray = new JSONArray();
		
		if (weiXinChannels != null && weiXinChannels.size() > 0) {
			for (int i = 0; i < weiXinChannels.size(); i++) {
				
				if(!permission.containsKey(weiXinChannels.get(i).getChannelCodeName())){
					continue;
				}
				
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", weiXinChannels.get(i).getId());
				jsonObject1.put("text", weiXinChannels.get(i).getDescription());
				
				JSONObject jsonObject = JSONObject.fromObject(weiXinChannels.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				List<WeiXinChannel> weiXinChannelCache = weiXinChannelService.GetWeiXinChannelCache();
				List<WeiXinChannel> childWeiXinChannel = new ArrayList<WeiXinChannel>();
				for (WeiXinChannel entity : weiXinChannelCache) {
					if(entity.getChannelParentCode().equals(weiXinChannels.get(i).getChannelCodeName()) && !entity.getChannelLevelCode().equals("Button")) {
						childWeiXinChannel.add(entity);
					}
				}
				
				if (childWeiXinChannel != null && childWeiXinChannel.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildWeiXinMenuTree(childWeiXinChannel, permission));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
    
    public JSONArray buildWeiXinRoleTree(List<WeiXinChannel> weiXinChannels, String userName) {
		JSONArray jSONArray = new JSONArray();
		
		if (weiXinChannels != null && weiXinChannels.size() > 0) {
			for (int i = 0; i < weiXinChannels.size(); i++) {
				
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", weiXinChannels.get(i).getId());
				jsonObject1.put("text", weiXinChannels.get(i).getDescription());
				
				JSONObject jsonObject = JSONObject.fromObject(weiXinChannels.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				List<WeiXinChannel> weiXinChannelCache = weiXinChannelService.GetWeiXinChannelCache();
				List<WeiXinChannel> childWeiXinChannel = new ArrayList<WeiXinChannel>();
				for (WeiXinChannel entity : weiXinChannelCache) {
					if(entity.getChannelParentCode().equals(weiXinChannels.get(i).getChannelCodeName())) {
						childWeiXinChannel.add(entity);
					}
				}
				
				if (childWeiXinChannel != null && childWeiXinChannel.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildWeiXinRoleTree(childWeiXinChannel, userName));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
    
    public JSONArray buildWeiXinRoleTreeByPermission(List<WeiXinChannel> weiXinChannels, String userName, Map<String, Object> permission) {
		JSONArray jSONArray = new JSONArray();
		
		if (weiXinChannels != null && weiXinChannels.size() > 0) {
			for (int i = 0; i < weiXinChannels.size(); i++) {
				
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", weiXinChannels.get(i).getId());
				jsonObject1.put("text", weiXinChannels.get(i).getDescription());
				
				JSONObject jsonObject = JSONObject.fromObject(weiXinChannels.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				if(permission.containsKey(weiXinChannels.get(i).getChannelCodeName())){
					jsonObject1.put("checked", true);
				}
				
				List<WeiXinChannel> weiXinChannelCache = weiXinChannelService.GetWeiXinChannelCache();
				List<WeiXinChannel> childWeiXinChannel = new ArrayList<WeiXinChannel>();
				for (WeiXinChannel entity : weiXinChannelCache) {
					if(entity.getChannelParentCode().equals(weiXinChannels.get(i).getChannelCodeName())) {
						childWeiXinChannel.add(entity);
					}
				}
				
				if (childWeiXinChannel != null && childWeiXinChannel.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildWeiXinRoleTreeByPermission(childWeiXinChannel, userName, permission));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
}
