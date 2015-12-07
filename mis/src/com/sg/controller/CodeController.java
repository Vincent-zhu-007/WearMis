package com.sg.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sg.entity.Code;
import com.sg.entity.UserInfo;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.service.MobileUserService;
import com.sg.service.CodeService;
import com.sg.service.UserInfoService;
import com.sg.util.DataGridModel;
import com.sg.weixin.entity.WeiXinUser;
import com.sg.weixin.service.WeiXinUserService;

@Controller
@RequestMapping("/code.do")
public class CodeController extends AbstractController {
	private final Logger log = Logger.getLogger(Code.class);

	@Autowired
	private CodeService codeService;
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Autowired
	private WeiXinUserService weiXinUserService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "system/code_list";
	}

	/**
	 * code.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, Code entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", entity.getCodeName());

		List<Code> list = codeService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (Code code : list) {
			String status = statusMap.get(code.getStatus()).toString();
			code.setStatus(status);
		}

		int totalCount = codeService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	/**
	 * code.do?method=add 请求以 HTTP POST 方式提交时，create()被调用
	 * 
	 * @param request
	 * @param entity
	 * @return
	 */
	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, Code entity) {
		JsonResult result = new JsonResult();

		try {
			entity.setId(UUID.randomUUID().toString());
			entity.setCreator("admin");
			
			codeService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create Code exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, Code entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			Code model = codeService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			codeService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update Code exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		Code entity = codeService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("description", entity.getDescription());
		map.put("status", entity.getStatus());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=isExist")
	@ResponseBody
	public boolean isExist(HttpServletRequest request, String codeName, String category) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", codeName);
		params.put("category", category);
		
		List<Code> list = codeService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=getcodecache")
	@ResponseBody
	public List<Map<String, Object>> getCodeCacheByCategory(String category){
		return codeService.getCodeCacheByCategory(category);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getcodehtmloptionsbycategory")
	@ResponseBody
	public String getCodeHtmlOptionsByCategory(String category){
		Map<String, Object> map = codeService.getCodeCacheMapByCategory(category);
		String result = "";
		
		for (Map.Entry entry : map.entrySet()) {
			result += "<option value='"+entry.getKey()+"'>"+entry.getValue()+"</option>";
		}
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getremoveandaddcodehtmloptionsbycategory")
	@ResponseBody
	public String getRemoveAndAddCodeHtmlOptionsByCategory(String category, String id){
		String result = "";
		
		Map<String, Object> map = codeService.getCodeCacheMapByCategory(category);
		
		UserInfo model = userInfoService.get(id);
		String role = model.getRole();
		
		String listboxAddOptions = "";
		
		if(role != null && !role.equals("")){
			String[] array = role.split(",");
			
			
			for (String key : array) {
				if(map.containsKey(key)){
					listboxAddOptions += "<option value='"+key+"'>"+map.get(key)+"</option>";
				}
			}
			
			for (String key : array) {
				map.remove(key);
			}
		}
		
		String listboxRemoveOptions = "";
		for (Map.Entry entry : map.entrySet()) {
			listboxRemoveOptions += "<option value='"+entry.getKey()+"'>"+entry.getValue()+"</option>";
		}
		
		result = listboxAddOptions + "&" + listboxRemoveOptions;
		
		return result;
	}
	
	@RequestMapping(params = "method=clearcodecache")
	@ResponseBody
	public JsonResult clearCodeCache(){
		JsonResult result = new JsonResult();
		
		try {
			codeService.clearCodeCache();
			codeService.GetCodeCache();
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
			result.setMessage(null);
		}
		
		/*codeService.runXcapCmdClearCache()*/
		
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String codeName = request.getParameter("codename");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", codeName);
		
		List<Code> list = codeService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = codeService.ExpExcel(list, path);
				
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
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getremoveandaddcodehtmloptionsbymobilerolecategory")
	@ResponseBody
	public String getRemoveAndAddCodeHtmlOptionsByMobileRoleCategory(String category, String id){
		String result = "";
		
		Map<String, Object> map = codeService.getCodeCacheMapByCategory(category);
		
		MobileUser model = mobileUserService.get(id);
		String mobileRole = model.getMobileRole();
		
		String listboxAddOptions = "";
		if(mobileRole != null && !mobileRole.equals("")){
			String[] array = mobileRole.split(",");
			
			for (String key : array) {
				if(map.containsKey(key)){
					listboxAddOptions += "<option value='"+key+"'>"+map.get(key)+"</option>";
				}
			}
			
			for (String key : array) {
				map.remove(key);
			}
		}
		
		String listboxRemoveOptions = "";
		for (Map.Entry entry : map.entrySet()) {
			listboxRemoveOptions += "<option value='"+entry.getKey()+"'>"+entry.getValue()+"</option>";
		}
		
		result = listboxAddOptions + "&" + listboxRemoveOptions;
		
		return result;
	}
	
	@RequestMapping(params = "method=getddlcodecache")
	@ResponseBody
	public List<Map<String, Object>> getDdlCodeCacheByCategory(String category){
		return codeService.getDdlCodeCacheByCategory(category);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getremoveandaddcodehtmloptionsbywebsiterolecategory")
	@ResponseBody
	public String getRemoveAndAddCodeHtmlOptionsByWebSiteRoleCategory(String category, String id){
		String result = "";
		
		Map<String, Object> map = codeService.getCodeCacheMapByCategory(category);
		
		WeiXinUser model = weiXinUserService.get(id);
		String webSiteRole = model.getWebSiteRole();
		
		String listboxAddOptions = "";
		if(webSiteRole != null && !webSiteRole.equals("")){
			String[] array = webSiteRole.split(",");
			
			for (String key : array) {
				if(map.containsKey(key)){
					listboxAddOptions += "<option value='"+key+"'>"+map.get(key)+"</option>";
				}
			}
			
			for (String key : array) {
				map.remove(key);
			}
		}
		
		String listboxRemoveOptions = "";
		for (Map.Entry entry : map.entrySet()) {
			listboxRemoveOptions += "<option value='"+entry.getKey()+"'>"+entry.getValue()+"</option>";
		}
		
		result = listboxAddOptions + "&" + listboxRemoveOptions;
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getremoveandaddcodehtmloptionsbyweixinrolecategory")
	@ResponseBody
	public String getRemoveAndAddCodeHtmlOptionsByWeiXinRoleCategory(String category, String id){
		String result = "";
		
		Map<String, Object> map = codeService.getCodeCacheMapByCategory(category);
		
		WeiXinUser model = weiXinUserService.get(id);
		String weiXinRole = model.getWeiXinRole();
		
		String listboxAddOptions = "";
		if(weiXinRole != null && !weiXinRole.equals("")){
			String[] array = weiXinRole.split(",");
			
			for (String key : array) {
				if(map.containsKey(key)){
					listboxAddOptions += "<option value='"+key+"'>"+map.get(key)+"</option>";
				}
			}
			
			for (String key : array) {
				map.remove(key);
			}
		}
		
		String listboxRemoveOptions = "";
		for (Map.Entry entry : map.entrySet()) {
			listboxRemoveOptions += "<option value='"+entry.getKey()+"'>"+entry.getValue()+"</option>";
		}
		
		result = listboxAddOptions + "&" + listboxRemoveOptions;
		
		return result;
	}
}
