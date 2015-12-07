package com.sg.mobile.controller;

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
import com.sg.controller.AbstractController;
import com.sg.controller.JsonResult;
import com.sg.entity.UserInfo;
import com.sg.mobile.entity.MobileApp;
import com.sg.mobile.entity.MobileAppConfig;
import com.sg.mobile.entity.MobileAppItem;
import com.sg.mobile.service.MobileAppConfigService;
import com.sg.mobile.service.MobileAppItemService;
import com.sg.mobile.service.MobileAppService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileAppConfig.do")
public class MobileAppConfigController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileAppConfig.class);

	@Autowired
	private MobileAppConfigService mobileAppConfigService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileAppService mobileAppService;
	
	@Autowired
	private MobileAppItemService mobileAppItemService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobileappconfig_list";
	}

	/**
	 * mobileAppConfig.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileAppConfig entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("description", entity.getDescription());

		List<MobileAppConfig> list = mobileAppConfigService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (MobileAppConfig mobileAppConfig : list) {
			String status = statusMap.get(mobileAppConfig.getStatus()).toString();
			mobileAppConfig.setStatus(status);
		}

		int totalCount = mobileAppConfigService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileAppConfig entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			entity.setId(UUID.randomUUID().toString());
			entity.setCreator(sessionUserName);
			
			mobileAppConfigService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileAppConfig exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileAppConfig entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileAppConfig model = mobileAppConfigService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setPackageName(entity.getPackageName());
			model.setFileUrl(entity.getFileUrl() == null ? "" : entity.getFileUrl());
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			mobileAppConfigService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileAppConfig exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileAppConfig entity = mobileAppConfigService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("description", entity.getDescription());
		map.put("packageName", entity.getPackageName());
		map.put("fileUrl", entity.getFileUrl() == null ? "" : entity.getFileUrl());
		map.put("status", entity.getStatus());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=getmobileappconfigcache")
	@ResponseBody
	public List<MobileAppConfig> getMobileAppConfigCache(){
		return mobileAppConfigService.getMobileAppConfigCache();
	}
	
	@RequestMapping(params = "method=clearmobileappconfigcache")
	@ResponseBody
	public JsonResult clearMobileAppConfigCache(){
		JsonResult result = new JsonResult();
		
		try {
			mobileAppConfigService.clearMobileAppConfigCache();
			mobileAppConfigService.getMobileAppConfigCache();
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
			result.setMessage(null);
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=delete")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, String ids) {
		JsonResult result = new JsonResult();

		try {
			String[] array = ids.split(",");
			
			for (String id : array) {
				MobileAppConfig mobileAppConfig = mobileAppConfigService.get(id);
				
				if(mobileAppConfig != null){
					String appCodeName = mobileAppConfig.getCodeName();
					
					mobileAppService.deleteMobileAppItemByAppCodeName(appCodeName);
					
					mobileAppConfigService.delete(id);
				}
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileAppConfig exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String description = request.getParameter("description");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("description", description);
		
		List<MobileAppConfig> list = mobileAppConfigService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileAppConfigService.ExpExcel(list, path);
				
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
	
	@RequestMapping(params = "method=isExist")
	@ResponseBody
	public boolean isExist(HttpServletRequest request, String codeName) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", codeName);
		
		List<MobileAppConfig> list = mobileAppConfigService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getmobileappconfightmloptions")
	@ResponseBody
	public String getMobileAppConfigHtmlOptions(){
		Map<String, Object> mobileUserMap = mobileAppConfigService.getMobileAppConfigMap();
		
		String result = "";
		
		for (Map.Entry entry : mobileUserMap.entrySet()) {
			result += "<option value='"+entry.getKey()+"'>" + entry.getValue() + "</option>";
		}
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(params = "method=getremoveandaddmobileappconfightmloptions")
	@ResponseBody
	public String getRemoveAndAddMobileAppConfigHtmlOptions(String id){
		String result = "";
		
		MobileApp mobileApp = mobileAppService.get(id);
		
		String ownerUri = mobileApp.getOwnerUri();
		String listFileName = mobileApp.getListFileName();
		
		Map<String, Object> mobileAppConfigMap = mobileAppConfigService.getMobileAppConfigMap();
		
		Map<String, Object> mobileAppItemParams = new HashMap<String, Object>();
		mobileAppItemParams.put("ownerUri", ownerUri);
		mobileAppItemParams.put("listFileName", listFileName);
		List<MobileAppItem> mobileAppItems = mobileAppItemService.findForUnPage(mobileAppItemParams);
		
		String listboxAddOptions = "";
		if(mobileAppItems != null && mobileAppItems.size() > 0){
			for (MobileAppItem mobileAppItem : mobileAppItems) {
				
				String description = "";
				if(mobileAppConfigMap.containsKey(mobileAppItem.getAppCodeName())){
					description = mobileAppConfigMap.get(mobileAppItem.getAppCodeName()).toString();
					
					listboxAddOptions += "<option value='"+mobileAppItem.getAppCodeName()+"'>" + description + "</option>";
				}else {
					mobileAppConfigMap.remove(mobileAppItem.getAppCodeName());
					continue;
				}
				
				mobileAppConfigMap.remove(mobileAppItem.getAppCodeName());
			}
		}
		
		String listboxRemoveOptions = "";
		for (Map.Entry entry : mobileAppConfigMap.entrySet()) {
			
			listboxRemoveOptions += "<option value='"+entry.getKey()+"'>" + entry.getValue() + "</option>";
		}
		
		result = listboxAddOptions + "&" + listboxRemoveOptions;
		
		return result;
	}
}
