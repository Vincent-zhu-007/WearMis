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
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileTime;
import com.sg.mobile.entity.MobileTimeItem;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.service.MobileCompanyService;
import com.sg.mobile.service.MobileTimeItemService;
import com.sg.mobile.service.MobileTimeService;
import com.sg.mobile.service.MobileUserService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileTime.do")
public class MobileTimeController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileTime.class);

	@Autowired
	private MobileTimeService mobileTimeService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileTimeItemService mobileTimeItemService;
	
	@Autowired
	private MobileCompanyService mobileCompanyService;
	
	@Autowired
	private MobileUserService mobileUserService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobiletime_list";
	}

	/**
	 * mobileTime.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileTime entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());
		
		List<MobileTime> list = mobileTimeService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> listTypeMap = codeService.getCodeCacheMapByCategory("MOBILETIMELISTTYPE");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (MobileTime mobileTime : list) {
			if(mobileTime.getListType() != null && !mobileTime.getListType().equals("")){
				String listType = listTypeMap.get(mobileTime.getListType()).toString();
				mobileTime.setListType(listType);
			}
			
			String status = statusMap.get(mobileTime.getStatus()).toString();
			mobileTime.setStatus(status);
		}

		int totalCount = mobileTimeService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}
	
	/**
	 * mobileTimeItem.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=mobiletimeitemlist")
	@ResponseBody
	public JsonResult mobileTimeItemList(HttpServletRequest request, String id,
			DataGridModel dm) {
		MobileTime mobileTime = mobileTimeService.get(id);
		
		String ownerUri = mobileTime.getOwnerUri();
		String listFileName = mobileTime.getListFileName();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		params.put("listFileName", listFileName);
		
		List<MobileTimeItem> list = mobileTimeItemService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> levelMap = codeService.getCodeCacheMapByCategory("MOBILETIMEITEMLEVEL");
		
		for (MobileTimeItem mobileTimeItem : list) {
			if(mobileTimeItem.getLevel() != null && !mobileTimeItem.getLevel().equals("")){
				String level = levelMap.get(mobileTimeItem.getLevel()).toString();
				mobileTimeItem.setLevel(level);
			}
		}

		int totalCount = mobileTimeService.getTotalCount(params).intValue();
		
		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileTime entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String listFileName = entity.getOwnerUri() + "_" + entity.getListType();
			String id = UUID.randomUUID().toString();
			String ownerUri = entity.getOwnerUri();
			String listType = entity.getListType();
			
			entity.setId(id);
			entity.setListFileName(listFileName);
			
			entity.setEtag(UUID.randomUUID().toString());
			entity.setCreator(sessionUserName);
			
			mobileTimeService.create(entity);
			
			MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
			
			mobileTimeService.runMqttPushMobileTimeItem(id, listType, mobileUser.getUserName(), mobileCompany);
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileTime exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(params = "method=addmobiletimeitem")
	@ResponseBody
	public JsonResult createMobileTimeItem(HttpServletRequest request, MobileTimeItem entity) {
		JsonResult result = new JsonResult();

		try {
			String id = request.getParameter("mobileTimeId");
			MobileTime mobileTime = mobileTimeService.get(id);
			
			String ownerUri = mobileTime.getOwnerUri();
			String listFileName = mobileTime.getListFileName();
			String listType = mobileTime.getListType();
			
			entity.setId(UUID.randomUUID().toString());
			entity.setOwnerUri(ownerUri);
			entity.setListFileName(listFileName);
			
			mobileTimeItemService.create(entity);
			
			MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
			
			mobileTimeService.runMqttPushMobileTimeItem(id, listType, mobileUser.getUserName(), mobileCompany);
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileTimeItem exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=updatemobiletimeitem")
	@ResponseBody
	public JsonResult updateMobileTimeItem(HttpServletRequest request, MobileTimeItem entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileTimeItem model = mobileTimeItemService.get(entity.getId());
			String ownerUri = model.getOwnerUri();
			String listFileName = model.getListFileName();
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ownerUri", ownerUri);
			params.put("listFileName", listFileName);
			
			List<MobileTime> mobileTimes = mobileTimeService.findForUnPage(params);
			
			MobileTime mobileTime = mobileTimes.get(0);
			String id = mobileTime.getId();
			String listType = mobileTime.getListType();
			
			String etag = UUID.randomUUID().toString();
			mobileTime.setEtag(etag);
			mobileTime.setUpdator(sessionUserName);
			
			mobileTimeService.update(mobileTime);
			
			model.setStartTime(entity.getStartTime() == null ? "" : entity.getStartTime());
			model.setEndTime(entity.getEndTime() == null ? "" : entity.getEndTime());
			model.setLevel(entity.getLevel() == null ? "" : entity.getLevel());
			
			mobileTimeItemService.update(model);
			
			MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
			
			mobileTimeService.runMqttPushMobileTimeItem(id, listType, mobileUser.getUserName(), mobileCompany);
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileTime exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=getmobiletimeitem")
	@ResponseBody
	public JSONObject getmobiletimeitem(HttpServletRequest request, String id) {
		MobileTimeItem entity = mobileTimeItemService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("ownerUri", entity.getOwnerUri());
		map.put("startTime", entity.getStartTime() == null ? "" : entity.getStartTime());
		map.put("endTime", entity.getEndTime() == null ? "" : entity.getEndTime());
		map.put("level", entity.getLevel() == null ? "" : entity.getLevel());
		map.put("listFileName", entity.getListFileName());
		
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
				MobileTime mobileTime = mobileTimeService.get(id);
				
				if(mobileTime != null){
					String ownerUri = mobileTime.getOwnerUri();
					String listFileName = mobileTime.getListFileName();
					String listType = mobileTime.getListType();
					
					Map<String, Object> mobileTimeItemParams = new HashMap<String, Object>();
					mobileTimeItemParams.put("ownerUri", ownerUri);
					mobileTimeItemParams.put("listFileName", listFileName);
					List<MobileTimeItem> mobileTimeItems = mobileTimeItemService.findForUnPage(mobileTimeItemParams);
					
					if(mobileTimeItems != null && mobileTimeItems.size() > 0){
						for (MobileTimeItem mobileTimeItem : mobileTimeItems) {
							mobileTimeItemService.delete(mobileTimeItem.getId());
						}
					}
					
					mobileTimeService.delete(id);
					
					MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
					MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
					
					mobileTimeService.runMqttPushMobileTimeItem(id, listType, mobileUser.getUserName(), mobileCompany);
				}
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileTime exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(params = "method=deletemobiletimeitem")
	@ResponseBody
	public JsonResult deleteMobileTimeItem(HttpServletRequest request, String ids) {
		JsonResult result = new JsonResult();

		try {
			String[] array = ids.split(",");
			
			String mobileTimeId = "";
			String listType = "";
			
			for (String id : array) {
				MobileTimeItem mobileTimeItem = mobileTimeItemService.get(id);
				
				if(mobileTimeItem != null){
					String ownerUri = mobileTimeItem.getOwnerUri();
					String listFileName = mobileTimeItem.getListFileName();
					
					if(listType == null || listType.equals("")){
						Map<String, Object> mobileTimeParams = new HashMap<String, Object>();
						mobileTimeParams.put("ownerUri", ownerUri);
						mobileTimeParams.put("listFileName", listFileName);
						List<MobileTime> mobileTimes = mobileTimeService.findForUnPage(mobileTimeParams);
						
						if(mobileTimes != null && mobileTimes.size() > 0){
							MobileTime mobileTime = mobileTimes.get(0);
							
							mobileTimeId = mobileTime.getId();
							listType = mobileTime.getListType();
						}
					}
					
					mobileTimeItemService.delete(id);
					
					MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
					MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
					
					mobileTimeService.runMqttPushMobileTimeItem(mobileTimeId, listType, mobileUser.getUserName(), mobileCompany);
				}
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileTime exception reason:" + e.getMessage());
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
		
		List<MobileTime> list = mobileTimeService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileTimeService.ExpExcel(list, path);
				
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
	
	@RequestMapping(params = "method=isexist")
	@ResponseBody
	public boolean ownerMobileTimeIsExist(HttpServletRequest request, String ownerUri, String listType) {
		boolean result = false;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		params.put("listType", listType);
		
		List<MobileTime> list = mobileTimeService.findForUnPage(params);
		if(list.size() > 0) {
			result = true;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=getmobiletimeitemcount")
	@ResponseBody
	public boolean getMobileTimeItemCount(HttpServletRequest request, String id) {
		boolean isCreate = true;
		
		MobileTime mobileTime = mobileTimeService.get(id);
		
		String ownerUri = mobileTime.getOwnerUri();
		String listFileName = mobileTime.getListFileName();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		params.put("listFileName", listFileName);
		
		List<MobileTimeItem> mobileTimeItems = mobileTimeItemService.findForUnPage(params);
		
		if(mobileTimeItems != null && mobileTimeItems.size() > 0 && mobileTimeItems.size() >= 3){
			isCreate = false;
		}
		
		return isCreate;
	}
}
