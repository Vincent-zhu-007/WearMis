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
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileContact;
import com.sg.mobile.entity.MobileContactMember;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.service.MobileCompanyService;
import com.sg.mobile.service.MobileContactMemberService;
import com.sg.mobile.service.MobileContactService;
import com.sg.mobile.service.MobileUserService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileContact.do")
public class MobileContactController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileContact.class);

	@Autowired
	private MobileContactService mobileContactService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileContactMemberService mobileContactMemberService;
	
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
		return "mobile/mobilecontact_list";
	}

	/**
	 * mobileContact.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileContact entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());
		
		List<MobileContact> list = mobileContactService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> listTypeMap = codeService.getCodeCacheMapByCategory("MOBILECONTACTLISTTYPE");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (MobileContact mobileContact : list) {
			if(mobileContact.getListType() != null && !mobileContact.getListType().equals("")){
				String listType = listTypeMap.get(mobileContact.getListType()).toString();
				mobileContact.setListType(listType);
			}
			
			String status = statusMap.get(mobileContact.getStatus()).toString();
			mobileContact.setStatus(status);
		}

		int totalCount = mobileContactService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileContact entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String listFileName = entity.getOwnerUri() + "_" + entity.getListType();
			
			List<MobileContactMember> mobileContactMembers = new ArrayList<MobileContactMember>();
			
			String displayName1 = request.getParameter("displayName1");
			String displayName2 = request.getParameter("displayName2");
			String displayName3 = request.getParameter("displayName3");
			String displayName4 = request.getParameter("displayName4");
			
			String shortNum1 = request.getParameter("shortNum1");
			String shortNum2 = request.getParameter("shortNum2");
			String shortNum3 = request.getParameter("shortNum3");
			String shortNum4 = request.getParameter("shortNum4");
			
			String mobilePhone1 = request.getParameter("mobilePhone1");
			String mobilePhone2 = request.getParameter("mobilePhone2");
			String mobilePhone3 = request.getParameter("mobilePhone3");
			String mobilePhone4 = request.getParameter("mobilePhone4");
			
			if(displayName1 != null && !displayName1.equals("") && mobilePhone1 != null && !mobilePhone1.equals("")){
				MobileContactMember model1 = new MobileContactMember();
				model1.setId(UUID.randomUUID().toString());
				model1.setOwnerUri(entity.getOwnerUri());
				model1.setDisplayName(displayName1);
				model1.setShortNum(shortNum1);
				model1.setMobilePhone(mobilePhone1);
				model1.setNumButton("1");
				model1.setListFileName(listFileName);
				
				mobileContactMembers.add(model1);
			}
			
			if(displayName2 != null && !displayName2.equals("") && mobilePhone2 != null && !mobilePhone2.equals("")){
				MobileContactMember model2 = new MobileContactMember();
				model2.setId(UUID.randomUUID().toString());
				model2.setOwnerUri(entity.getOwnerUri());
				model2.setDisplayName(displayName2);
				model2.setShortNum(shortNum2);
				model2.setMobilePhone(mobilePhone2);
				model2.setNumButton("2");
				model2.setListFileName(listFileName);
				
				mobileContactMembers.add(model2);
			}
			
			if(displayName3 != null && !displayName3.equals("") && mobilePhone3 != null && !mobilePhone3.equals("")){
				MobileContactMember model3 = new MobileContactMember();
				model3.setId(UUID.randomUUID().toString());
				model3.setOwnerUri(entity.getOwnerUri());
				model3.setDisplayName(displayName3);
				model3.setShortNum(shortNum3);
				model3.setMobilePhone(mobilePhone3);
				model3.setNumButton("3");
				model3.setListFileName(listFileName);
				
				mobileContactMembers.add(model3);
			}
			
			if(displayName4 != null && !displayName4.equals("") && mobilePhone4 != null && !mobilePhone4.equals("")){
				MobileContactMember model4 = new MobileContactMember();
				model4.setId(UUID.randomUUID().toString());
				model4.setOwnerUri(entity.getOwnerUri());
				model4.setDisplayName(displayName4);
				model4.setShortNum(shortNum4);
				model4.setMobilePhone(mobilePhone4);
				model4.setNumButton("4");
				model4.setListFileName(listFileName);
				
				mobileContactMembers.add(model4);
			}
			
			if(mobileContactMembers != null && mobileContactMembers.size() > 0){
				for (MobileContactMember mobileContactMember : mobileContactMembers) {
					mobileContactMemberService.create(mobileContactMember);
				}
			}
			
			entity.setId(UUID.randomUUID().toString());
			entity.setListFileName(listFileName);
			
			entity.setEtag(UUID.randomUUID().toString());
			entity.setCreator(sessionUserName);
			
			mobileContactService.create(entity);
			
			MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(entity.getOwnerUri());
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(entity.getOwnerUri());
			
			mobileContactService.runMqttPushMobileContactMember(entity.getOwnerUri(), entity.getListType(), mobileUser.getUserName(), mobileCompany);
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileContact exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileContact entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileContact model = mobileContactService.get(entity.getId());
			
			String ownerUri = model.getOwnerUri();
			String listFileName = model.getListFileName();
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ownerUri", ownerUri);
			params.put("listFileName", listFileName);
			
			List<MobileContactMember> mobileContactMembers = mobileContactMemberService.findForUnPage(params);
			
			if(mobileContactMembers != null && mobileContactMembers.size() > 0){
				for (MobileContactMember mobileContactMember : mobileContactMembers) {
					mobileContactMemberService.delete(mobileContactMember.getId());
				}
			}
			
			List<MobileContactMember> newMobileContactMembers = new ArrayList<MobileContactMember>();
			
			String displayName1 = request.getParameter("displayName1");
			String displayName2 = request.getParameter("displayName2");
			String displayName3 = request.getParameter("displayName3");
			String displayName4 = request.getParameter("displayName4");
			
			String shortNum1 = request.getParameter("shortNum1");
			String shortNum2 = request.getParameter("shortNum2");
			String shortNum3 = request.getParameter("shortNum3");
			String shortNum4 = request.getParameter("shortNum4");
			
			String mobilePhone1 = request.getParameter("mobilePhone1");
			String mobilePhone2 = request.getParameter("mobilePhone2");
			String mobilePhone3 = request.getParameter("mobilePhone3");
			String mobilePhone4 = request.getParameter("mobilePhone4");
			
			if(displayName1 != null && !displayName1.equals("") && mobilePhone1 != null && !mobilePhone1.equals("")){
				MobileContactMember model1 = new MobileContactMember();
				model1.setId(UUID.randomUUID().toString());
				model1.setOwnerUri(model.getOwnerUri());
				model1.setDisplayName(displayName1);
				model1.setShortNum(shortNum1);
				model1.setMobilePhone(mobilePhone1);
				model1.setNumButton("1");
				model1.setListFileName(listFileName);
				
				newMobileContactMembers.add(model1);
			}
			
			if(displayName2 != null && !displayName2.equals("") && mobilePhone2 != null && !mobilePhone2.equals("")){
				MobileContactMember model2 = new MobileContactMember();
				model2.setId(UUID.randomUUID().toString());
				model2.setOwnerUri(model.getOwnerUri());
				model2.setDisplayName(displayName2);
				model2.setShortNum(shortNum2);
				model2.setMobilePhone(mobilePhone2);
				model2.setNumButton("2");
				model2.setListFileName(listFileName);
				
				newMobileContactMembers.add(model2);
			}
			
			if(displayName3 != null && !displayName3.equals("") && mobilePhone3 != null && !mobilePhone3.equals("")){
				MobileContactMember model3 = new MobileContactMember();
				model3.setId(UUID.randomUUID().toString());
				model3.setOwnerUri(model.getOwnerUri());
				model3.setDisplayName(displayName3);
				model3.setShortNum(shortNum3);
				model3.setMobilePhone(mobilePhone3);
				model3.setNumButton("3");
				model3.setListFileName(listFileName);
				
				newMobileContactMembers.add(model3);
			}
			
			if(displayName4 != null && !displayName4.equals("") && mobilePhone4 != null && !mobilePhone4.equals("")){
				MobileContactMember model4 = new MobileContactMember();
				model4.setId(UUID.randomUUID().toString());
				model4.setOwnerUri(model.getOwnerUri());
				model4.setDisplayName(displayName4);
				model4.setShortNum(shortNum4);
				model4.setMobilePhone(mobilePhone4);
				model4.setNumButton("4");
				model4.setListFileName(listFileName);
				
				newMobileContactMembers.add(model4);
			}
			
			if(newMobileContactMembers != null && newMobileContactMembers.size() > 0){
				for (MobileContactMember mobileContactMember : newMobileContactMembers) {
					mobileContactMemberService.create(mobileContactMember);
				}
			}
			
			String etag = UUID.randomUUID().toString();
			model.setEtag(etag);
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			mobileContactService.update(model);
			
			MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
			
			mobileContactService.runMqttPushMobileContactMember(ownerUri, model.getListType(), mobileUser.getUserName(), mobileCompany);
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileContact exception reason:" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileContact entity = mobileContactService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("ownerUri", entity.getOwnerUri());
		map.put("listFileName", entity.getListFileName());
		map.put("listType", entity.getListType());
		map.put("status", entity.getStatus());
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());
		params.put("listFileName", entity.getListFileName());
		
		List<MobileContactMember> mobileContactMembers = mobileContactMemberService.findForUnPage(params);
		
		if(mobileContactMembers != null && mobileContactMembers.size() > 0){
			int i = 0;
			
			for (MobileContactMember mobileContactMember : mobileContactMembers) {
				i++;
				
				map.put("displayName" + i, mobileContactMember.getDisplayName() == null ? "" : mobileContactMember.getDisplayName());
				map.put("shortNum" + i, mobileContactMember.getShortNum() == null ? "" : mobileContactMember.getShortNum());
				map.put("mobilePhone" + i, mobileContactMember.getMobilePhone() == null ? "" : mobileContactMember.getMobilePhone());
			}
		}
		
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
				MobileContact mobileContact = mobileContactService.get(id);
				
				if(mobileContact != null){
					String ownerUri = mobileContact.getOwnerUri();
					String listFileName = mobileContact.getListFileName();
					
					Map<String, Object> mobileContactMemberParams = new HashMap<String, Object>();
					mobileContactMemberParams.put("ownerUri", ownerUri);
					mobileContactMemberParams.put("listFileName", listFileName);
					List<MobileContactMember> mobileContactMembers = mobileContactMemberService.findForUnPage(mobileContactMemberParams);
					
					if(mobileContactMembers != null && mobileContactMembers.size() > 0){
						for (MobileContactMember mobileContactMember : mobileContactMembers) {
							mobileContactMemberService.delete(mobileContactMember.getId());
						}
					}
					
					mobileContactService.delete(id);
					
					MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByOwnerUri(ownerUri);
					MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
					
					mobileContactService.runMqttPushMobileContactMember(ownerUri, mobileContact.getListType(), mobileUser.getUserName(), mobileCompany);
				}
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileContact exception reason:" + e.getMessage());
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
		
		List<MobileContact> list = mobileContactService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileContactService.ExpExcel(list, path);
				
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
	
	@RequestMapping(params = "method=ownermobilecontactisexist")
	@ResponseBody
	public boolean ownerMobileContactIsExist(HttpServletRequest request, String ownerUri) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		
		List<MobileContact> list = mobileContactService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
}
