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
import com.sg.mobile.entity.MobileOrgStructure;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.service.MobileOrgStructureService;
import com.sg.mobile.service.MobileUserService;
import com.sg.mobile.util.StringUtil;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;
import com.sg.util.MD5;
import com.sg.weixin.entity.WeiXinInMobile;
import com.sg.weixin.entity.WeiXinUser;
import com.sg.weixin.service.WeiXinInMobileService;
import com.sg.weixin.service.WeiXinUserService;

@Controller
@RequestMapping("/weiXinUser.do")
public class WeiXinUserController extends AbstractController {
	private final Logger log = Logger.getLogger(WeiXinUser.class);

	@Autowired
	private WeiXinUserService weiXinUserService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileOrgStructureService mobileOrgStructureService;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Autowired
	private WeiXinInMobileService weiXinInMobileService;
	
	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "weixin/weixinuser_list";
	}

	/**
	 * weiXinUser.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, WeiXinUser entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());
		params.put("userName", entity.getUserName());
		params.put("displayName", entity.getDisplayName());
		params.put("trueName", entity.getTrueName());

		List<WeiXinUser> list = weiXinUserService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> genderMap = codeService.getCodeCacheMapByCategory("GENDER");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (WeiXinUser weiXinUser : list) {
			String gender = "";
			if(weiXinUser.getGender() != null && !weiXinUser.getGender().equals("")){
				gender = genderMap.get(weiXinUser.getGender()).toString();
			}
			weiXinUser.setGender(gender);
			
			String status = statusMap.get(weiXinUser.getStatus()).toString();
			weiXinUser.setStatus(status);
		}

		int totalCount = weiXinUserService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, WeiXinUser entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String uuid = UUID.randomUUID().toString();
			entity.setId(uuid);
			entity.setExtensionId(uuid);
			String md5Password = MD5.GetMD5Code(entity.getPassword());
			entity.setPassword(md5Password);
			
			String companyCode = entity.getOrgStructure() == null ? "" : entity.getOrgStructure().split("_")[0];
			entity.setCompanyCode(companyCode);
			
			entity.setCreator(sessionUserName);
			
			weiXinUserService.create(entity);
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create WeiXinUser exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, WeiXinUser entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			WeiXinUser model = weiXinUserService.get(entity.getId());
			
			String md5Password = MD5.GetMD5Code(entity.getPassword());
			if(model.getPassword().equals(entity.getPassword()) || model.getPassword().equals(md5Password)){
				
			}else {
				model.setPassword(md5Password);
			}
			
			model.setDisplayName(entity.getDisplayName());
			model.setOrgStructure(entity.getOrgStructure());
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			model.setTrueName(entity.getTrueName());
			model.setMobilePhone(entity.getMobilePhone());
			model.setGender(entity.getGender());
			model.setMail(entity.getMail());
			model.setBirthday(entity.getBirthday());
			model.setHeadPortrait(entity.getHeadPortrait());
			model.setProvince(entity.getProvince());
			model.setCity(entity.getCity());
			model.setSign(entity.getSign());
			model.setIsOpenExtension(entity.getIsOpenExtension());
			
			String companyCode = entity.getOrgStructure() == null ? "" : entity.getOrgStructure().split("_")[0];
			model.setCompanyCode(companyCode);
			
			model.setWeiXinRole(entity.getWeiXinRole() == null ? "" : entity.getWeiXinRole());
			/*model.setWebSiteRole(entity.getWebSiteRole() == null ? "" : entity.getWebSiteRole());*/
			
			weiXinUserService.update(model);
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update WeiXinUser exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		WeiXinUser entity = weiXinUserService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("ownerUri", entity.getOwnerUri());
		map.put("userName", entity.getUserName());
		map.put("password", entity.getPassword());
		String displayName = entity.getDisplayName() == null ? "" : entity.getDisplayName();
		map.put("displayName", displayName);
		String orgStructure = entity.getOrgStructure() == null ? "" : entity.getOrgStructure();
		map.put("orgStructure", orgStructure);
		map.put("status", entity.getStatus());
		
		String trueName = entity.getTrueName() == null ? "" : entity.getTrueName();
		map.put("trueName", trueName);
		
		String mobilePhone = entity.getMobilePhone() == null ? "" : entity.getMobilePhone();
		map.put("mobilePhone", mobilePhone);
		
		String gender = entity.getGender() == null ? "" : entity.getGender();
		map.put("gender", gender);
		
		String mail = entity.getMail() == null ? "" : entity.getMail();
		map.put("mail", mail);
		
		String birthday = entity.getBirthday() == null ? "" : entity.getBirthday();
		map.put("birthday", birthday);
		
		String headPortrait = entity.getHeadPortrait() == null ? "" : entity.getHeadPortrait();
		map.put("headPortrait", headPortrait);
		
		String province = entity.getProvince() == null ? "" : entity.getProvince();
		map.put("province", province);
		
		String city = entity.getCity() == null ? "" : entity.getCity();
		map.put("city", city);
		
		String sign = entity.getSign() == null ? "" : entity.getSign();
		map.put("sign", sign);
		
		String weiXinRole = entity.getWeiXinRole() == null ? "" : entity.getWeiXinRole();
		map.put("weiXinRole", weiXinRole);
		
		String webSiteRole = entity.getWebSiteRole() == null ? "" : entity.getWebSiteRole();
		map.put("webSiteRole", webSiteRole);
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=delete")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, String ids) {
		JsonResult result = new JsonResult();

		try {
			String[] array = ids.split(",");
			
			for (String id : array) {
				WeiXinUser weiXinUser = weiXinUserService.get(id);
				
				if(weiXinUser != null){
					weiXinUserService.delete(weiXinUser.getId());
				}
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete WeiXinUser exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String ownerUri = request.getParameter("ownerUri");
		String userName = request.getParameter("userName");
		String displayName = request.getParameter("displayName");
		String trueName = request.getParameter("trueName");
		String orgStructureSearch = request.getParameter("orgStructureSearch");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		params.put("userName", userName);
		params.put("trueName", trueName);
		params.put("displayName", displayName);
		params.put("orgStructure", orgStructureSearch);
		
		List<WeiXinUser> list = weiXinUserService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = weiXinUserService.ExpExcel(list, path);
				
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
	
	@RequestMapping(params = "method=owneruriisexist")
	@ResponseBody
	public boolean ownerUriIsExist(HttpServletRequest request, String ownerUri) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		
		List<WeiXinUser> list = weiXinUserService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=owneruriisnotexist")
	@ResponseBody
	public boolean ownerUriIsNotExist(HttpServletRequest request, String ownerUri) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		
		List<WeiXinUser> list = weiXinUserService.findForUnPage(params);
		if(list.size() == 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=usernameisexist")
	@ResponseBody
	public boolean userNameIsExist(HttpServletRequest request, String userName) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		
		List<WeiXinUser> list = weiXinUserService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}

	@RequestMapping(params = "method=addorupdatewebsiterole")
	@ResponseBody
	public JsonResult addOrUpdateWebSiteRole(HttpServletRequest request, WeiXinUser entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			WeiXinUser model = weiXinUserService.get(entity.getId());
			
			model.setUpdator(sessionUserName);
			model.setWebSiteRole(entity.getWebSiteRole() == null ? "" : entity.getWebSiteRole());
			
			weiXinUserService.update(model);
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update WeiXinUserWebSiteRole exception reason：" + e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(params = "method=buildcreateweixinuserorgstructuretree")
	@ResponseBody
	public JSONArray buildCreateWeiXinUserOrgStructureTree(HttpServletRequest request) {
		Map<String, Object> params = new HashMap<String, Object>();
		List<MobileOrgStructure> allMobileOrgStructures = mobileOrgStructureService.findForUnPage(params);
		
		List<MobileOrgStructure> level1MenuMobileOrgStructures = new ArrayList<MobileOrgStructure>();
		
		for (MobileOrgStructure entity : allMobileOrgStructures) {
			if(entity.getLevelNum() == 1) {
				level1MenuMobileOrgStructures.add(entity);
			}
		}

		JSONArray jSONArray = mobileOrgStructureService.buildCreateMobileUserOrgStructureTree(level1MenuMobileOrgStructures, allMobileOrgStructures);
		
		return jSONArray;
	}
	
	@RequestMapping(params = "method=buildeditweixinuserorgstructuretree")
	@ResponseBody
	public JSONArray buildEditWeiXinUserOrgStructureTree(HttpServletRequest request) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		List<MobileOrgStructure> allMobileOrgStructures = mobileOrgStructureService.findForUnPage(params);
		
		List<MobileOrgStructure> level1MenuMobileOrgStructures = new ArrayList<MobileOrgStructure>();
		
		for (MobileOrgStructure entity : allMobileOrgStructures) {
			if(entity.getLevelNum() == 1) {
				level1MenuMobileOrgStructures.add(entity);
			}
		}
		
		JSONArray jSONArray = mobileOrgStructureService.buildEditMobileUserOrgStructureTree(level1MenuMobileOrgStructures, allMobileOrgStructures);
		
		return jSONArray;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getremoveandaddmobileuserhtmloptions")
	@ResponseBody
	public String getRemoveAndAddMobileUserHtmlOptions(String id){
		String result = "";
		
		WeiXinUser weiXinUser = weiXinUserService.get(id);
		String ownerUri = weiXinUser.getOwnerUri();
		String orgStructure = weiXinUser.getOrgStructure();
		
		Map<String, Object> mobileUserMap = mobileUserService.getMobileUserMapByOrgStructure(orgStructure);
		
		Map<String, Object> weiXinInMobileParams = new HashMap<String, Object>();
		weiXinInMobileParams.put("weiXinOwnerUri", ownerUri);
		weiXinInMobileParams.put("status", "Y");
		List<WeiXinInMobile> weiXinInMobiles = weiXinInMobileService.findForUnPage(weiXinInMobileParams);
		
		String listboxAddOptions = "";
		if(weiXinInMobiles != null && weiXinInMobiles.size() > 0){
			for (WeiXinInMobile weiXinInMobile : weiXinInMobiles) {
				String displayName = "";
				if(mobileUserMap.containsKey(weiXinInMobile.getMobileOwnerUri())){
					displayName = mobileUserMap.get(weiXinInMobile.getMobileOwnerUri()).toString();
				}
				
				mobileUserMap.remove(weiXinInMobile.getMobileOwnerUri());
				
				String userName = StringUtil.getUserNameByOwnerUri(weiXinInMobile.getMobileOwnerUri());
				
				listboxAddOptions += "<option value='"+weiXinInMobile.getMobileOwnerUri()+"'>" + userName + " | " + displayName +"</option>";
			}
		}
		
		String listboxRemoveOptions = "";
		for (Map.Entry entry : mobileUserMap.entrySet()) {
			String userName = StringUtil.getUserNameByOwnerUri(entry.getKey().toString());
			
			listboxRemoveOptions += "<option value='"+entry.getKey()+"'>"+ userName + " | " +entry.getValue()+"</option>";
		}
		
		result = listboxAddOptions + "&" + listboxRemoveOptions;
		
		return result;
	}
	
	@RequestMapping(params = "method=addorupdateweixininmobile")
	@ResponseBody
	public JsonResult addOrUpdateWeiXinInMobile(HttpServletRequest request, MobileUser entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			WeiXinUser model = weiXinUserService.get(entity.getId());
			
			String ownerUri = model.getOwnerUri();
			
			Map<String, Object> weiXinInMobileParams = new HashMap<String, Object>();
			weiXinInMobileParams.put("weiXinOwnerUri", ownerUri);
			List<WeiXinInMobile> oldWeiXinInMobiles = weiXinInMobileService.findForUnPage(weiXinInMobileParams);
			
			if(oldWeiXinInMobiles != null && oldWeiXinInMobiles.size() > 0){
				for (WeiXinInMobile weiXinInMobile : oldWeiXinInMobiles) {
					weiXinInMobileService.delete(weiXinInMobile.getId());
				}
			}
			
			String mobileOwnerUris = request.getParameter("mobileOwnerUris");
			
			if(mobileOwnerUris != null && !mobileOwnerUris.equals("")){
				String[] array = mobileOwnerUris.split(",");
				for (String mobileOwnerUri : array) {
					WeiXinInMobile weiXinInMobile = new WeiXinInMobile();
					
					weiXinInMobile.setId(UUID.randomUUID().toString());
					weiXinInMobile.setWeiXinOwnerUri(ownerUri);
					weiXinInMobile.setMobileOwnerUri(mobileOwnerUri);
					weiXinInMobile.setStatus("Y");
					weiXinInMobile.setCreator(sessionUserName);
					
					weiXinInMobileService.create(weiXinInMobile);
				}
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("addOrUpdate WeiXinInMobile exception reason：" + e.getMessage());
		}
		return result;
	}
}
