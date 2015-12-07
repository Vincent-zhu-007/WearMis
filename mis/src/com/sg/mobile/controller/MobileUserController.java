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
import com.sg.mobile.entity.MobileApp;
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileContact;
import com.sg.mobile.entity.MobileMessage;
import com.sg.mobile.entity.MobileOrgStructure;
import com.sg.mobile.entity.MobileRpcParamenter;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.entity.MobileUserFile;
import com.sg.mobile.entity.MobileUserFileShare;
import com.sg.mobile.service.MobileAppService;
import com.sg.mobile.service.MobileCompanyService;
import com.sg.mobile.service.MobileContactService;
import com.sg.mobile.service.MobileMessageService;
import com.sg.mobile.service.MobileOrgStructureService;
import com.sg.mobile.service.MobileSyncDataService;
import com.sg.mobile.service.MobileUserFileService;
import com.sg.mobile.service.MobileUserFileShareService;
import com.sg.mobile.service.MobileUserService;
import com.sg.mobile.util.StringUtil;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;
import com.sg.util.FileUtil;
import com.sg.util.MD5;

@Controller
@RequestMapping("/mobileUser.do")
public class MobileUserController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileUser.class);

	@Autowired
	private MobileUserService mobileUserService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileOrgStructureService mobileOrgStructureService;
	
	@Autowired
	private MobileSyncDataService mobileSyncDataService;
	
	@Autowired
	private MobileCompanyService mobileCompanyService;
	
	@Autowired
	private MobileContactService mobileContactService;
	
	@Autowired
	private MobileAppService mobileAppService;
	
	@Autowired
	private MobileMessageService mobileMessageService;
	
	@Autowired
	private MobileUserFileService mobileUserFileService;
	
	@Autowired
	private MobileUserFileShareService mobileUserFileShareService;
	
	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobileuser_list";
	}

	/**
	 * mobileUser.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileUser entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());
		params.put("userName", entity.getUserName());
		params.put("displayName", entity.getDisplayName());
		params.put("trueName", entity.getTrueName());

		List<MobileUser> list = mobileUserService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> genderMap = codeService.getCodeCacheMapByCategory("GENDER");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (MobileUser mobileUser : list) {
			String gender = "";
			if(mobileUser.getGender() != null && !mobileUser.getGender().equals("")){
				gender = genderMap.get(mobileUser.getGender()).toString();
			}
			mobileUser.setGender(gender);
			
			String status = statusMap.get(mobileUser.getStatus()).toString();
			mobileUser.setStatus(status);
		}

		int totalCount = mobileUserService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileUser entity) {
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
			
			String ownerUri = entity.getOwnerUri();
			String listType = "WhiteList";
			String listFileName = ownerUri + "_" + listType;
			
			MobileContact mobileContact = new MobileContact();
			mobileContact.setId(UUID.randomUUID().toString());
			mobileContact.setOwnerUri(ownerUri);
			mobileContact.setListFileName(listFileName);
			mobileContact.setListType(listType);
			mobileContact.setEtag(UUID.randomUUID().toString());
			mobileContact.setStatus("Y");
			mobileContact.setCreator(sessionUserName);
			mobileContactService.create(mobileContact);
			
			MobileApp mobileApp = new MobileApp();
			mobileApp.setId(UUID.randomUUID().toString());
			mobileApp.setOwnerUri(ownerUri);
			mobileApp.setListFileName(listFileName);
			mobileApp.setListType(listType);
			mobileApp.setEtag(UUID.randomUUID().toString());
			mobileApp.setStatus("Y");
			mobileApp.setCreator(sessionUserName);
			mobileAppService.create(mobileApp);
			
			mobileUserService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileUser exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileUser entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileUser model = mobileUserService.get(entity.getId());
			
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
			
			model.setMobileRole(entity.getMobileRole() == null ? "" : entity.getMobileRole());
			/*model.setWebSiteRole(entity.getWebSiteRole() == null ? "" : entity.getWebSiteRole());*/
			
			mobileUserService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileUser exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileUser entity = mobileUserService.get(id);
		
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
		
		String mobileRole = entity.getMobileRole() == null ? "" : entity.getMobileRole();
		map.put("mobileRole", mobileRole);
		
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
				MobileUser mobileUser = mobileUserService.get(id);
				
				if(mobileUser != null){
					mobileContactService.deleteMobileContact(mobileUser.getOwnerUri(), "WhiteList");
					mobileContactService.deleteMobileContactMemberByContactMemberUri(mobileUser.getOwnerUri());
					
					mobileAppService.deleteMobileApp(mobileUser.getOwnerUri(), "WhiteList");
					
					mobileUserService.delete(mobileUser.getId());
				}
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileUser exception reason:" + e.getMessage());
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
		
		List<MobileUser> list = mobileUserService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileUserService.ExpExcel(list, path);
				
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
	@RequestMapping(params = "method=getmobileuserhtmloptions")
	@ResponseBody
	public String getMobileUserHtmlOptions(String targetOwnerUri){
		Map<String, Object> mobileUserMap = mobileUserService.getMobileUserMap(targetOwnerUri);
		
		String result = "";
		
		for (Map.Entry entry : mobileUserMap.entrySet()) {
			String userName = StringUtil.getUserNameByOwnerUri(entry.getKey().toString());
			
			result += "<option value='"+entry.getKey()+"'>"+ userName + " | " +entry.getValue()+"</option>";
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=ownerUriIsExist")
	@ResponseBody
	public boolean ownerUriIsExist(HttpServletRequest request, String ownerUri) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		
		List<MobileUser> list = mobileUserService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=ownerUriIsNotExist")
	@ResponseBody
	public boolean ownerUriIsNotExist(HttpServletRequest request, String ownerUri) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		
		List<MobileUser> list = mobileUserService.findForUnPage(params);
		if(list.size() == 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=userNameIsExist")
	@ResponseBody
	public boolean userNameIsExist(HttpServletRequest request, String userName) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		
		List<MobileUser> list = mobileUserService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=buildcreatemobileuserorgstructuretree")
	@ResponseBody
	public JSONArray buildCreateMobileUserOrgStructureTree(HttpServletRequest request) {
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
	
	@RequestMapping(params = "method=buildeditmobileuserorgstructuretree")
	@ResponseBody
	public JSONArray buildEditMobileUserOrgStructureTree(HttpServletRequest request) {
		
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
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=initlistboxmeiremove")
	@ResponseBody
	public String initListboxMeiRemove(HttpServletRequest request, String fileName) {
		
		String result = "";
		
		if(fileName == null || fileName.equals("")){
			result = "0&请选择文件";
			return result;
		}
		
		int i = fileName.lastIndexOf('.');
		if(i != -1){
			String suffix = fileName.substring(i + 1, fileName.length());
			
			if(!suffix.equals("txt")){
				result = "0&上传文件必须为txt文件";
				return result;
			}
		}
		
		String basePath = request.getRealPath("/");
		String filePath = fileName.replace("/mis/", "").replace("/", "\\");
		String fullPath = basePath + filePath;
		
		String strArray = FileUtil.readTxtFile(fullPath);
		
		if(strArray != null && !strArray.equals("")){
			int index = strArray.lastIndexOf(',');
			if(index != -1){
				strArray = strArray.substring(0, index);
			}
			
			String[] array = strArray.split(",");
			
			String sqlIn = "";
			for (String meiNo : array) {
				String meiNoTrim = meiNo.trim();
				
				sqlIn += "'"+meiNoTrim+"',";
			}
			
			int indexTemp = sqlIn.lastIndexOf(',');
			if(indexTemp != -1){
				sqlIn = sqlIn.substring(0, indexTemp);
			}
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userName", sqlIn);
			
			List<MobileUser> mobileUsers = mobileUserService.findUnPageIn(params);
			
			result = "1&";
			for (String meiNo : array) {
				
				String meiNoTrim = meiNo.trim();
				
				String message = "可以导入";
				if(meiNoTrim.length() != 15){
					message = "串号位数错误";
				}
				
				if(mobileUsers != null && mobileUsers.size() > 0){
					for (MobileUser mobileUser : mobileUsers) {
						if(mobileUser.getUserName().equals(meiNoTrim)){
							message = "串号已经占用";
							break;
						}
					}
				}
				
				result += "<option value='"+meiNoTrim+"'>"+meiNoTrim+" | "+message+"</option>";
			}
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=addmeinousers")
	@ResponseBody
	public JsonResult addMeiNoUsers(HttpServletRequest request) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String meiNos = request.getParameter("meiNos");
			String sipHostName = request.getParameter("sipHostName");
			
			String password = request.getParameter("import_password");
			String md5Password = MD5.GetMD5Code(password);
			
			String orgStructure = request.getParameter("import_orgStructure");
			
			String[] array = meiNos.split(",");
			
			String sqlIn = "";
			for (String meiNo : array) {
				String meiNoTrim = meiNo.trim();
				
				sqlIn += "'"+meiNoTrim+"',";
			}
			
			int indexTemp = sqlIn.lastIndexOf(',');
			if(indexTemp != -1){
				sqlIn = sqlIn.substring(0, indexTemp);
			}
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userName", sqlIn);
			
			List<MobileUser> mobileUsers = mobileUserService.findUnPageIn(params);
			
			for (String meiNo : array) {
				
				String meiNoTrim = meiNo.trim();
				
				if(meiNoTrim.length() != 15){
					continue;
				}
				
				if(mobileUsers != null && mobileUsers.size() > 0){
					boolean isContainMeiNo = false;
					
					for (MobileUser mobileUser : mobileUsers) {
						if(mobileUser.getUserName().equals(meiNoTrim)){
							isContainMeiNo = true;
							break;
						}
					}
					
					if(isContainMeiNo){
						continue;
					}
				}
				
				MobileUser entity = new MobileUser();
				
				String id = UUID.randomUUID().toString();
				entity.setId(id);
				entity.setExtensionId(id);
				
				String ownerUri = "sip:" + meiNoTrim + "@" + sipHostName;
				entity.setOwnerUri(ownerUri);
				
				entity.setUserName(meiNoTrim);
				entity.setPassword(md5Password);
				entity.setDisplayName(meiNoTrim);
				
				String companyCode = orgStructure == null ? "" : orgStructure.split("_")[0];
				entity.setCompanyCode(companyCode);
				
				entity.setOrgStructure(orgStructure);
				
				entity.setMobileRole("");
				
				entity.setStatus("Y");
				entity.setCreator(sessionUserName);
				
				String listType = "WhiteList";
				String listFileName = ownerUri + "_" + listType;
				
				MobileContact mobileContact = new MobileContact();
				mobileContact.setId(UUID.randomUUID().toString());
				mobileContact.setOwnerUri(ownerUri);
				mobileContact.setListFileName(listFileName);
				mobileContact.setListType(listType);
				mobileContact.setEtag(UUID.randomUUID().toString());
				mobileContact.setStatus("Y");
				mobileContact.setCreator(sessionUserName);
				mobileContactService.create(mobileContact);
				
				MobileApp mobileApp = new MobileApp();
				mobileApp.setId(UUID.randomUUID().toString());
				mobileApp.setOwnerUri(ownerUri);
				mobileApp.setListFileName(listFileName);
				mobileApp.setListType(listType);
				mobileApp.setEtag(UUID.randomUUID().toString());
				mobileApp.setStatus("Y");
				mobileApp.setCreator(sessionUserName);
				mobileAppService.create(mobileApp);
				
				mobileUserService.create(entity);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("import MobileUser by meino exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=sendmobilecontrol")
	@ResponseBody
	public JsonResult sendMobileControl(HttpServletRequest request, String ownerUri, String permission) {
		JsonResult result = new JsonResult();
		
		try {
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
			
			MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByComapnyCode(mobileUser.getCompanyCode());
			
			List<MobileRpcParamenter> mobileRpcParamenters = new ArrayList<MobileRpcParamenter>();
			MobileRpcParamenter mobileRpcParamenter1 = new MobileRpcParamenter();
			
			mobileRpcParamenter1.setParaName("OwnerUri");
			mobileRpcParamenter1.setParaType("String");
			mobileRpcParamenter1.setParaValue(ownerUri);
			mobileRpcParamenter1.setParaSort(1);
			mobileRpcParamenters.add(mobileRpcParamenter1);
			
			MobileRpcParamenter mobileRpcParamenter2 = new MobileRpcParamenter();
			mobileRpcParamenter2.setParaName("Permission");
			mobileRpcParamenter2.setParaType("String");
			mobileRpcParamenter2.setParaValue(permission);
			mobileRpcParamenter2.setParaSort(1);
			mobileRpcParamenters.add(mobileRpcParamenter2);
			
			mobileSyncDataService.SyncData("MobileControl", mobileRpcParamenters, mobileCompany);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		
		return result;
	}
	
	@RequestMapping(params = "method=sendmobilelistening")
	@ResponseBody
	public JsonResult sendMobileListening(HttpServletRequest request, String id) {
		JsonResult result = new JsonResult();
		
		try {
			MobileUser mobileUser = mobileUserService.get(id);
			
			if(mobileUser != null){
				String ownerUri = mobileUser.getOwnerUri();
				String userName = mobileUser.getUserName();
				
				MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
				
				mobileUserService.runMqttPushMobileListening(ownerUri, "WhiteList", userName, mobileCompany);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		
		return result;
	}
	
	@RequestMapping(params = "method=sendmobilereportlocation")
	@ResponseBody
	public JsonResult sendMobileReportLocation(HttpServletRequest request, String id) {
		JsonResult result = new JsonResult();
		
		try {
			MobileUser mobileUser = mobileUserService.get(id);
			
			if(mobileUser != null){
				String ownerUri = mobileUser.getOwnerUri();
				String userName = mobileUser.getUserName();
				
				MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
				
				mobileUserService.runMqttPushMobileReportLocation(userName, mobileCompany);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getremoveandaddmobilemessageuserhtmloptions")
	@ResponseBody
	public String getRemoveAndAddMobileMessageUserHtmlOptions(String id){
		String result = "";
		
		MobileMessage mobileMessage = mobileMessageService.get(id);
		String ownerUri = mobileMessage.getOwnerUri();
		String targetUris = mobileMessage.getTargetUris();
		String[] array = targetUris.split(",");
		
		Map<String, Object> mobileUserMap = mobileUserService.getMobileUserMap(ownerUri);
		
		String listboxAddOptions = "";
		if(array != null && array.length > 0){
			for (String targetUri : array) {
				String displayName = "";
				
				if(mobileUserMap.containsKey(targetUri)){
					displayName = mobileUserMap.get(targetUri).toString();
				}
				
				mobileUserMap.remove(targetUri);
				
				String userName = StringUtil.getUserNameByOwnerUri(targetUri);
				
				listboxAddOptions += "<option value='"+targetUri+"'>" + userName + " | " + displayName +"</option>";
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
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getremoveandaddmobileuserfilesharehtmloptions")
	@ResponseBody
	public String getRemoveAndAddMobileUserFileShareHtmlOptions(String id){
		String result = "";
		
		MobileUserFile mobileUserFile = mobileUserFileService.get(id);
		
		Map<String, Object> mobileUserMap = mobileUserService.getMobileUserMap(mobileUserFile.getOwnerUri());
		
		String mobileUserFileId = mobileUserFile.getId();
		
		Map<String, Object> mobileUserFileShareParams = new HashMap<String, Object>();
		mobileUserFileShareParams.put("mobileUserFileId", mobileUserFileId);
		List<MobileUserFileShare> mobileUserFileShares = mobileUserFileShareService.findForUnPage(mobileUserFileShareParams);
		
		String listboxAddOptions = "";
		if(mobileUserFileShares != null && mobileUserFileShares.size() > 0){
			for (MobileUserFileShare mobileUserFileShare : mobileUserFileShares) {
				String displayName = "";
				if(mobileUserMap.containsKey(mobileUserFileShare.getTargetMemberUri())){
					displayName = mobileUserMap.get(mobileUserFileShare.getTargetMemberUri()).toString();
				}
				
				mobileUserMap.remove(mobileUserFileShare.getTargetMemberUri());
				
				if(mobileUserFileShare.getTargetMemberUri().equals(mobileUserFile.getOwnerUri())){
					continue;
				}
				
				String userName = StringUtil.getUserNameByOwnerUri(mobileUserFileShare.getTargetMemberUri());
				
				listboxAddOptions += "<option value='"+mobileUserFileShare.getTargetMemberUri()+"'>" + userName + " | " + displayName +"</option>";
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
}
