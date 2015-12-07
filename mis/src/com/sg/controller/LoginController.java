package com.sg.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sg.entity.UserInfo;
import com.sg.service.RoleService;
import com.sg.service.UserInfoService;
import com.sg.util.MD5;
import com.sg.util.StringUtil;

@Controller
@RequestMapping("/login.do")
public class LoginController {
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private RoleService roleService;
	
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
}
