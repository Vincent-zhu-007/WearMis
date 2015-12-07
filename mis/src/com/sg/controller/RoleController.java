package com.sg.controller;

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
import com.sg.entity.MenuConfig;
import com.sg.entity.Role;
import com.sg.service.CodeService;
import com.sg.service.MenuConfigService;
import com.sg.service.MenuService;
import com.sg.service.RoleService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/role.do")
public class RoleController extends AbstractController {
	private final Logger log = Logger.getLogger(Role.class);

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MenuConfigService menuConfigService;
	
	@Autowired
	private MenuService menuService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "system/role_list";
	}

	/**
	 * role.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, Role entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", entity.getCodeName());

		List<Role> list = roleService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (Role role : list) {
			String status = statusMap.get(role.getStatus()).toString();
			role.setStatus(status);
		}

		int totalCount = roleService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	/**
	 * role.do?method=add 请求以 HTTP POST 方式提交时，create()被调用
	 * 
	 * @param request
	 * @param entity
	 * @return
	 */
	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, Role entity) {
		JsonResult result = new JsonResult();

		try {
			String uuid = UUID.randomUUID().toString();
			entity.setId(uuid);
			entity.setRoleId(uuid);
			entity.setCategory("ROLE");
			entity.setCreator("admin");
			
			roleService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create Role exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, Role entity) {
		JsonResult result = new JsonResult();

		try {
			Role model = roleService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setStatus(entity.getStatus());
			model.setPermission(entity.getPermission());
			model.setUpdator("admin");
			
			roleService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update Role exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		Role entity = roleService.get(id);
		
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
		
		List<Role> list = roleService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=initmenu")
	@ResponseBody
	public JSONArray InitMenu(HttpServletRequest request) {
		String userName = "";
    	
		List<MenuConfig> menuConfigs = menuConfigService.GetMenuConfigCache();
		
		List<MenuConfig> level1MenuConfigs = new ArrayList<MenuConfig>();
		
		for (MenuConfig entity : menuConfigs) {
			if(entity.getLevelCode().equals("Level1")) {
				level1MenuConfigs.add(entity);
			}
		}
		
		JSONArray jSONArray = menuService.buildRoleTree(level1MenuConfigs, userName);
		
		System.out.print(jSONArray.toString());
		
		return jSONArray;
	}
	
	@RequestMapping(params = "method=initmenubypermission")
	@ResponseBody
	public JSONArray InitMenuByPermission(HttpServletRequest request, String roleId) {
		String userName = "";
    	
		List<MenuConfig> menuConfigs = menuConfigService.GetMenuConfigCache();
		
		List<MenuConfig> level1MenuConfigs = new ArrayList<MenuConfig>();
		
		for (MenuConfig entity : menuConfigs) {
			if(entity.getLevelCode().equals("Level1")) {
				level1MenuConfigs.add(entity);
			}
		}
		
		Map<String, Object> permission = new HashMap<String, Object>();
		
		Role role = roleService.get(roleId);
		
		String strPermission = role.getPermission();
		
		String[] array = strPermission.split(",");
		
		for (String key : array) {
			if(!permission.containsKey(key)){
				permission.put(key, key);
			}
		}
		
		JSONArray jSONArray = menuService.buildRoleTreeByPermission(level1MenuConfigs, userName, permission);
		
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
				roleService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete Role exception reason:" + e.getMessage());
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
		
		List<Role> list = roleService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = roleService.ExpExcel(list, path);
				
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
