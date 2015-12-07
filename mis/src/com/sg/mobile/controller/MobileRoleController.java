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
import com.sg.mobile.entity.MobileMenuConfig;
import com.sg.mobile.entity.MobileRole;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.service.MobileMenuConfigService;
import com.sg.mobile.service.MobileMenuService;
import com.sg.mobile.service.MobileRoleService;
import com.sg.mobile.service.MobileUserService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileRole.do")
public class MobileRoleController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileRole.class);

	@Autowired
	private MobileRoleService mobileRoleService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileMenuConfigService mobileMenuConfigService;
	
	@Autowired
	private MobileMenuService mobileMenuService;
	
	@Autowired
	private MobileUserService mobileUserService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobilerole_list";
	}

	/**
	 * mobileRole.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileRole entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", entity.getCodeName());

		List<MobileRole> list = mobileRoleService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (MobileRole mobileRole : list) {
			String status = statusMap.get(mobileRole.getStatus()).toString();
			mobileRole.setStatus(status);
		}

		int totalCount = mobileRoleService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	/**
	 * mobileRole.do?method=add 请求以 HTTP POST 方式提交时，create()被调用
	 * 
	 * @param request
	 * @param entity
	 * @return
	 */
	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileRole entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String uuid = UUID.randomUUID().toString();
			entity.setId(uuid);
			entity.setRoleId(uuid);
			entity.setCategory("MOBILEROLE");
			entity.setCreator(sessionUserName);
			
			mobileRoleService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileRole exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileRole entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileRole model = mobileRoleService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setStatus(entity.getStatus());
			model.setPermission(entity.getPermission());
			model.setUpdator(sessionUserName);
			
			mobileRoleService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileRole exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileRole entity = mobileRoleService.get(id);
		
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
		
		List<MobileRole> list = mobileRoleService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=initmobilemenu")
	@ResponseBody
	public JSONArray InitMobileMenu(HttpServletRequest request) {
		String userName = "";
    	
		List<MobileMenuConfig> mobileMenuConfigs = mobileMenuConfigService.GetMobileMenuConfigCache();
		
		List<MobileMenuConfig> level1MobileMenuConfigs = new ArrayList<MobileMenuConfig>();
		
		for (MobileMenuConfig entity : mobileMenuConfigs) {
			if(entity.getLevelCode().equals("Level1")) {
				level1MobileMenuConfigs.add(entity);
			}
		}
		
		JSONArray jSONArray = mobileMenuService.buildMobileRoleTree(level1MobileMenuConfigs, userName);
		
		System.out.print(jSONArray.toString());
		
		return jSONArray;
	}
	
	@RequestMapping(params = "method=initmobilemenubypermission")
	@ResponseBody
	public JSONArray InitMobileMenuByPermission(HttpServletRequest request, String roleId) {
		String userName = "";
    	
		List<MobileMenuConfig> mobileMenuConfigs = mobileMenuConfigService.GetMobileMenuConfigCache();
		
		List<MobileMenuConfig> level1MobileMenuConfigs = new ArrayList<MobileMenuConfig>();
		
		for (MobileMenuConfig entity : mobileMenuConfigs) {
			if(entity.getLevelCode().equals("Level1")) {
				level1MobileMenuConfigs.add(entity);
			}
		}
		
		Map<String, Object> permission = new HashMap<String, Object>();
		
		MobileRole mobileRole = mobileRoleService.get(roleId);
		
		String strPermission = mobileRole.getPermission();
		
		String[] array = strPermission.split(",");
		
		for (String key : array) {
			if(!permission.containsKey(key)){
				permission.put(key, key);
			}
		}
		
		JSONArray jSONArray = mobileMenuService.buildMobileRoleTreeByPermission(level1MobileMenuConfigs, userName, permission);
		
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
				mobileRoleService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileRole exception reason:" + e.getMessage());
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
		
		List<MobileRole> list = mobileRoleService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileRoleService.ExpExcel(list, path);
				
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
	
	@RequestMapping(params = "method=initmobileusermenubypermission")
	@ResponseBody
	public JSONArray InitMobileUserMenuByPermission(HttpServletRequest request, String id) {
		MobileUser mobileUser = mobileUserService.get(id);
		
		String mobileRoleCodeName = mobileUser.getMobileRole();
		
		if(mobileRoleCodeName != null && !mobileRoleCodeName.equals("")){
			String userName = "";
	    	
			List<MobileMenuConfig> mobileMenuConfigs = mobileMenuConfigService.GetMobileMenuConfigCache();
			
			List<MobileMenuConfig> level1MobileMenuConfigs = new ArrayList<MobileMenuConfig>();
			
			for (MobileMenuConfig entity : mobileMenuConfigs) {
				if(entity.getLevelCode().equals("Level1")) {
					level1MobileMenuConfigs.add(entity);
				}
			}
			
			Map<String, Object> permission = new HashMap<String, Object>();
			
			MobileRole mobileRole = mobileRoleService.getMobileRoleByCodeName(mobileRoleCodeName);
			
			String strPermission = mobileRole.getPermission();
			
			String[] array = strPermission.split(",");
			
			for (String key : array) {
				if(!permission.containsKey(key)){
					permission.put(key, key);
				}
			}
			
			JSONArray jSONArray = mobileMenuService.buildMobileRoleTreeByPermission(level1MobileMenuConfigs, userName, permission);
			
			return jSONArray;
		}
		
		return null;
	}
}
