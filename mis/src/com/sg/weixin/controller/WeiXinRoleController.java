package com.sg.weixin.controller;

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
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;
import com.sg.weixin.entity.WeiXinChannel;
import com.sg.weixin.entity.WeiXinRole;
import com.sg.weixin.entity.WeiXinUser;
import com.sg.weixin.service.WeiXinChannelService;
import com.sg.weixin.service.WeiXinMenuService;
import com.sg.weixin.service.WeiXinRoleService;
import com.sg.weixin.service.WeiXinUserService;

@Controller
@RequestMapping("/weiXinRole.do")
public class WeiXinRoleController extends AbstractController {
	private final Logger log = Logger.getLogger(WeiXinRole.class);

	@Autowired
	private WeiXinRoleService weiXinRoleService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private WeiXinChannelService weiXinChannelService;
	
	@Autowired
	private WeiXinMenuService weiXinMenuService;
	
	@Autowired
	private WeiXinUserService weiXinUserService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "weixin/weixinrole_list";
	}

	/**
	 * weiXinRole.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, WeiXinRole entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", entity.getCodeName());

		List<WeiXinRole> list = weiXinRoleService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (WeiXinRole weiXinRole : list) {
			String status = statusMap.get(weiXinRole.getStatus()).toString();
			weiXinRole.setStatus(status);
		}

		int totalCount = weiXinRoleService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, WeiXinRole entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String uuid = UUID.randomUUID().toString();
			entity.setId(uuid);
			entity.setRoleId(uuid);
			entity.setCategory("WEIXINROLE");
			entity.setCreator(sessionUserName);
			
			weiXinRoleService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create WeiXinRole exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, WeiXinRole entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			WeiXinRole model = weiXinRoleService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setStatus(entity.getStatus());
			model.setPermission(entity.getPermission());
			model.setUpdator(sessionUserName);
			
			weiXinRoleService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update WeiXinRole exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		WeiXinRole entity = weiXinRoleService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("description", entity.getDescription());
		map.put("status", entity.getStatus());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=isexist")
	@ResponseBody
	public boolean isExist(HttpServletRequest request, String codeName) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", codeName);
		
		List<WeiXinRole> list = weiXinRoleService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=initweixinmenu")
	@ResponseBody
	public JSONArray InitWeiXinMenu(HttpServletRequest request) {
		String userName = "";
    	
		List<WeiXinChannel> weiXinChannels = weiXinChannelService.GetWeiXinChannelCache();
		
		List<WeiXinChannel> level1WeiXinChannels = new ArrayList<WeiXinChannel>();
		
		for (WeiXinChannel entity : weiXinChannels) {
			if(entity.getChannelLevelCode().equals("Level1")) {
				level1WeiXinChannels.add(entity);
			}
		}
		
		JSONArray jSONArray = weiXinMenuService.buildWeiXinRoleTree(level1WeiXinChannels, userName);
		
		return jSONArray;
	}
	
	@RequestMapping(params = "method=initweixinmenubypermission")
	@ResponseBody
	public JSONArray InitWeiXinMenuByPermission(HttpServletRequest request, String roleId) {
		String userName = "";
    	
		List<WeiXinChannel> weiXinChannels = weiXinChannelService.GetWeiXinChannelCache();
		
		List<WeiXinChannel> level1WeiXinChannels = new ArrayList<WeiXinChannel>();
		
		for (WeiXinChannel entity : weiXinChannels) {
			if(entity.getChannelLevelCode().equals("Level1")) {
				level1WeiXinChannels.add(entity);
			}
		}
		
		Map<String, Object> permission = new HashMap<String, Object>();
		
		WeiXinRole weiXinRole = weiXinRoleService.get(roleId);
		
		String strPermission = weiXinRole.getPermission();
		
		String[] array = strPermission.split(",");
		
		for (String key : array) {
			if(!permission.containsKey(key)){
				permission.put(key, key);
			}
		}
		
		JSONArray jSONArray = weiXinMenuService.buildWeiXinRoleTreeByPermission(level1WeiXinChannels, userName, permission);
		
		return jSONArray;
	}
	
	@RequestMapping(params = "method=delete")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, String ids) {
		JsonResult result = new JsonResult();

		try {
			String[] array = ids.split(",");
			
			for (String id : array) {
				weiXinRoleService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete WeiXinRole exception reason:" + e.getMessage());
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
		
		List<WeiXinRole> list = weiXinRoleService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = weiXinRoleService.ExpExcel(list, path);
				
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

		}
	}
	
	@RequestMapping(params = "method=initweixinusermenubypermission")
	@ResponseBody
	public JSONArray InitWeiXinUserMenuByPermission(HttpServletRequest request, String id) {
		WeiXinUser weiXinUser = weiXinUserService.get(id);
		
		String weiXinRoleCodeName = weiXinUser.getWeiXinRole();
		
		if(weiXinRoleCodeName != null && !weiXinRoleCodeName.equals("")){
			String userName = "";
	    	
			List<WeiXinChannel> weiXinChannels = weiXinChannelService.GetWeiXinChannelCache();
			
			List<WeiXinChannel> level1WeiXinChannels = new ArrayList<WeiXinChannel>();
			
			for (WeiXinChannel entity : weiXinChannels) {
				if(entity.getChannelLevelCode().equals("Level1")) {
					level1WeiXinChannels.add(entity);
				}
			}
			
			Map<String, Object> permission = new HashMap<String, Object>();
			
			WeiXinRole weiXinRole = weiXinRoleService.getWeiXinRoleByCodeName(weiXinRoleCodeName);
			
			String strPermission = weiXinRole.getPermission();
			
			String[] array = strPermission.split(",");
			
			for (String key : array) {
				if(!permission.containsKey(key)){
					permission.put(key, key);
				}
			}
			
			JSONArray jSONArray = weiXinMenuService.buildWeiXinRoleTreeByPermission(level1WeiXinChannels, userName, permission);
			
			return jSONArray;
		}
		
		return null;
	}
}
