package com.sg.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sg.entity.UserInfo;
import com.sg.entity.UserRemind;
import com.sg.service.CodeService;
import com.sg.service.RoleService;
import com.sg.service.UserInfoService;
import com.sg.service.UserRemindService;
import com.sg.util.DataGridModel;
import com.sg.util.MD5;
import com.sg.util.StringUtil;

@Controller
@RequestMapping("/user.do")
public class UserInfoController extends AbstractController {

	private final Logger log = Logger.getLogger(UserInfo.class);

	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserRemindService userRemindService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "system/user_list";
	}

	/**
	 * user.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, UserInfo entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", entity.getUserName());

		List<UserInfo> list = userInfoService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (UserInfo userInfo : list) {
			String status = statusMap.get(userInfo.getStatus()).toString();
			userInfo.setStatus(status);
		}

		int totalCount = userInfoService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	/**
	 * user.do?method=add 请求以 HTTP POST 方式提交时，create()被调用
	 * 
	 * @param request
	 * @param entity
	 * @return
	 */
	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, UserInfo entity) {
		JsonResult result = new JsonResult();

		try {
			String uuid = UUID.randomUUID().toString();
			entity.setId(uuid);
			entity.setUserInRoleId(uuid);
			
			String md5Password = MD5.GetMD5Code(entity.getPassword());
			entity.setPassword(md5Password);
			
			entity.setCreator("admin");
			
			userInfoService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create UserInfo exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, UserInfo entity) {
		JsonResult result = new JsonResult();

		try {
			UserInfo model = userInfoService.get(entity.getId());
			
			String md5Password = MD5.GetMD5Code(entity.getPassword());
			if(model.getPassword().equals(entity.getPassword()) || model.getPassword().equals(md5Password)){
				
			}else {
				model.setPassword(md5Password);
			}
			
			model.setDescription(entity.getDescription());
			model.setStatus(entity.getStatus());
			model.setUpdator("admin");
			
			model.setRole(entity.getRole());
			
			userInfoService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update UserInfo exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		UserInfo entity = userInfoService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("password", entity.getPassword());
		map.put("description", entity.getDescription());
		map.put("status", entity.getStatus());
		map.put("role", entity.getRole() == null ? "" : entity.getRole());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=isExist")
	@ResponseBody
	public boolean isExist(HttpServletRequest request, String userName) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		
		List<UserInfo> list = userInfoService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=login")
	@ResponseBody
	public JsonResult login(HttpServletRequest request, UserInfo entity) {
		JsonResult result = new JsonResult();
		
		String message = "用户名或密码错误";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", entity.getUserName());
		params.put("status", "Y");
		
		List<UserInfo> list = userInfoService.findForUnPage(params);
		
		if(list.size() == 1){
			UserInfo model = list.get(0);
			if(model != null){
				String md5Password = MD5.GetMD5Code(entity.getPassword());
				if(model.getPassword().equals(md5Password)){
					String sql = "";
					String[] array = model.getRole().split(",");
					for (String roleCode : array) {
						sql += "'" + roleCode + "',";
					}
					
					sql = StringUtil.rTrim(sql, ",");
					
					Map<String, Object> roleParams = new HashMap<String, Object>();
					roleParams.put("codeName", sql);
					
					Map<String, Object> permission = roleService.findPermissionByRoles(roleParams);
					
					HttpSession session = request.getSession();
					session.setAttribute("user", model);
					session.setAttribute("permission", permission);
					
					message = AbstractController.AJAX_SUCCESS_CODE;
				}
			}
		}
		
		result.setMessage(message);
		
		return result;
	}
	
	@RequestMapping(params = "method=getusernamebysession")
	@ResponseBody
	public JSONObject getUserNameBySession(HttpServletRequest request){
		JSONObject jsonObject = new JSONObject();
		HttpSession session = request.getSession();
		if(session != null && session.getAttribute("user") != null){
			
			UserInfo entity = (UserInfo)session.getAttribute("user");
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userName", entity.getUserName());
			
			jsonObject = JSONObject.fromObject(map);
		}
		
		return jsonObject;
	}
	
	@RequestMapping(params = "method=logon")
	@ResponseBody
	public JsonResult logon(HttpServletRequest request){
		JsonResult result = new JsonResult();
		
		String message = "";
		
		HttpSession session = request.getSession();
		if(session != null){
			if(session.getAttribute("user") != null){
				session.setAttribute("user","");
			}
			
			if(session.getAttribute("permission") != null){
				session.setAttribute("permission","");
			}
			
			message = AbstractController.AJAX_SUCCESS_CODE;
		}
		
		result.setMessage(message);
		
		return result;
	}
	
	@RequestMapping(params = "method=passwodiscorrect")
	@ResponseBody
	public boolean passwodIsCorrect(HttpServletRequest request, String password) {
		boolean result = false;
		
		HttpSession session = request.getSession();
		if(session != null && session.getAttribute("user") != null){
			UserInfo entity = (UserInfo)session.getAttribute("user");
			String md5Password = MD5.GetMD5Code(password);
			
			if(entity.getPassword().equals(md5Password)){
				result = true;
			}
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=changepassword")
	@ResponseBody
	public JsonResult changePassword(HttpServletRequest request, String newPassword) {
		JsonResult result = new JsonResult();

		try {
			HttpSession session = request.getSession();
			if(session != null && session.getAttribute("user") != null){
				UserInfo entity = (UserInfo)session.getAttribute("user");
				
				String md5Password = MD5.GetMD5Code(newPassword);
				UserInfo model = userInfoService.get(entity.getId());
				
				model.setPassword(md5Password);
				
				userInfoService.update(model);
				result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
				
			}else {
				result.setMessage(null);
				log.error("update changePassword exception reason: session is null");
			}
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update changePassword exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(params = "method=getuserhtmloptions")
	@ResponseBody
	public String getCodeHtmlOptionsByCategory(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", "Y");
		
		List<UserInfo> list = userInfoService.findForUnPage(params);
		
		String result = "";
		
		for (UserInfo userInfo : list) {
			result += "<option value='"+userInfo.getUserName()+"'>"+userInfo.getUserName()+"</option>";
		}
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getremoveandadduserhtmloptions")
	@ResponseBody
	public String getRemoveAndAddCodeHtmlOptions(String id){
		String result = "";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", "Y");
		
		List<UserInfo> list = userInfoService.findForUnPage(params);
		
		Map<String, Object> map = new HashMap<String, Object>();
		for (UserInfo userInfo : list) {
			map.put(userInfo.getUserName(), userInfo.getDescription());
		}
		
		UserRemind model = userRemindService.get(id);
		String user = model.getTargetUser();
		String[] array = user.split(",");
		
		String listboxAddOptions = "";
		for (String key : array) {
			if(map.containsKey(key)){
				listboxAddOptions += "<option value='"+key+"'>"+key+"</option>";
			}
		}
		
		for (String key : array) {
			map.remove(key);
		}
		
		String listboxRemoveOptions = "";
		for (Map.Entry entry : map.entrySet()) {
			listboxRemoveOptions += "<option value='"+entry.getKey()+"'>"+entry.getKey()+"</option>";
		}
		
		result = listboxAddOptions + "&" + listboxRemoveOptions;
		
		return result;
	}
	
	@RequestMapping(params = "method=delete")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, String ids) {
		JsonResult result = new JsonResult();

		try {
			String[] array = ids.split(",");
			
			for (String id : array) {
				userInfoService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete UserInfo exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String userName = request.getParameter("username");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		
		List<UserInfo> list = userInfoService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = userInfoService.ExpExcel(list, path);
				
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
