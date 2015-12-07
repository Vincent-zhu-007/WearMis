package com.sg.mobile.controller;

import java.util.ArrayList;
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
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileContact;
import com.sg.mobile.service.MobileAppConfigService;
import com.sg.mobile.service.MobileAppItemService;
import com.sg.mobile.service.MobileAppService;
import com.sg.mobile.service.MobileCompanyService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileApp.do")
public class MobileAppController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileContact.class);

	@Autowired
	private MobileAppService mobileAppService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileAppConfigService mobileAppConfigService;
	
	@Autowired
	private MobileAppItemService mobileAppItemService;
	
	@Autowired
	private MobileCompanyService mobileCompanyService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobileapp_list";
	}

	/**
	 * mobileApp.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileContact entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());
		
		List<MobileApp> list = mobileAppService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> listTypeMap = codeService.getCodeCacheMapByCategory("MOBILEAPPLISTTYPE");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (MobileApp mobileApp : list) {
			if(mobileApp.getListType() != null && !mobileApp.getListType().equals("")){
				String listType = listTypeMap.get(mobileApp.getListType()).toString();
				mobileApp.setListType(listType);
			}
			
			String status = statusMap.get(mobileApp.getStatus()).toString();
			mobileApp.setStatus(status);
		}

		int totalCount = mobileAppService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileApp entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String listFileName = entity.getOwnerUri() + "_" + entity.getListType();
			
			List<MobileAppConfig> mobileAppConfigs = mobileAppConfigService.getMobileAppConfigCache();
			
			List<MobileAppItem> mobileAppItems = new ArrayList<MobileAppItem>();
			
			String mobileAppItemAppCodeNames = request.getParameter("mobileAppItemAppCodeNames");
			String[] array = mobileAppItemAppCodeNames.split(",");
			for (String mobileAppItemAppCodeName : array) {
				
				MobileAppItem model = new MobileAppItem();
				model.setId(UUID.randomUUID().toString());
				model.setOwnerUri(entity.getOwnerUri());
				
				boolean isFindAppCodeName = false;
				for (MobileAppConfig mobileAppConfig : mobileAppConfigs) {
					if(mobileAppConfig.getCodeName().equals(mobileAppItemAppCodeName)){
						isFindAppCodeName = true;
						break;
					}else {
						continue;
					}
				}
				
				if(!isFindAppCodeName){
					continue;
				}
				
				model.setAppCodeName(mobileAppItemAppCodeName);
				
				model.setListFileName(listFileName);
				
				mobileAppItems.add(model);
			}
			
			if(mobileAppItems != null && mobileAppItems.size() > 0){
				for (MobileAppItem mobileAppItem : mobileAppItems) {
					mobileAppItemService.create(mobileAppItem);
				}
			}
			
			entity.setId(UUID.randomUUID().toString());
			entity.setListFileName(listFileName);
			
			entity.setEtag(UUID.randomUUID().toString());
			entity.setCreator(sessionUserName);
			
			mobileAppService.create(entity);
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileApp exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileContact entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileApp model = mobileAppService.get(entity.getId());
			
			String ownerUri = model.getOwnerUri();
			String listFileName = model.getListFileName();
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ownerUri", ownerUri);
			params.put("listFileName", listFileName);
			
			List<MobileAppItem> mobileAppItems = mobileAppItemService.findForUnPage(params);
			
			if(mobileAppItems != null && mobileAppItems.size() > 0){
				for (MobileAppItem mobileAppItem : mobileAppItems) {
					mobileAppItemService.delete(mobileAppItem.getId());
				}
			}
			
			String mobileAppItemAppCodeNames = request.getParameter("mobileAppItemAppCodeNames");
			
			List<MobileAppConfig> mobileAppConfigs = mobileAppConfigService.getMobileAppConfigCache();
			
			String[] array = mobileAppItemAppCodeNames.split(",");
			for (String mobileAppItemAppCodeName : array) {
				
				MobileAppItem mobileAppItem = new MobileAppItem();
				mobileAppItem.setId(UUID.randomUUID().toString());
				mobileAppItem.setOwnerUri(ownerUri);
				
				boolean isFindAppCodeName = false;
				for (MobileAppConfig mobileAppConfig : mobileAppConfigs) {
					if(mobileAppConfig.getCodeName().equals(mobileAppItemAppCodeName)){
						isFindAppCodeName = true;
						break;
					}else {
						continue;
					}
				}
				
				if(!isFindAppCodeName){
					continue;
				}
				
				mobileAppItem.setAppCodeName(mobileAppItemAppCodeName);
				
				mobileAppItem.setListFileName(listFileName);
				
				mobileAppItemService.create(mobileAppItem);
			}
			
			String etag = UUID.randomUUID().toString();
			model.setEtag(etag);
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
			
			mobileAppService.runCmdUpdateApp(ownerUri, mobileCompany);
			
			mobileAppService.update(model);
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileApp exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileApp entity = mobileAppService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("ownerUri", entity.getOwnerUri());
		map.put("listFileName", entity.getListFileName());
		map.put("listType", entity.getListType());
		map.put("status", entity.getStatus());
		
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
				MobileApp mobileApp = mobileAppService.get(id);
				
				if(mobileApp != null){
					String ownerUri = mobileApp.getOwnerUri();
					String listFileName = mobileApp.getListFileName();
					
					Map<String, Object> mobileAppItemParams = new HashMap<String, Object>();
					mobileAppItemParams.put("ownerUri", ownerUri);
					mobileAppItemParams.put("listFileName", listFileName);
					List<MobileAppItem> mobileAppItems = mobileAppItemService.findForUnPage(mobileAppItemParams);
					
					if(mobileAppItems != null && mobileAppItems.size() > 0){
						for (MobileAppItem mobileAppItem : mobileAppItems) {
							mobileAppItemService.delete(mobileAppItem.getId());
						}
					}
					
					MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
					
					mobileAppService.runCmdDeleteApp(ownerUri, mobileCompany);
					
					mobileAppService.delete(id);
				}
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileApp exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String ownerUri = request.getParameter("ownerUri");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		
		List<MobileApp> list = mobileAppService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileAppService.ExpExcel(list, path);
				
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
	
	@RequestMapping(params = "method=ownermobileappisexist")
	@ResponseBody
	public boolean ownerMobileAppIsExist(HttpServletRequest request, String ownerUri) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		
		List<MobileApp> list = mobileAppService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
}
