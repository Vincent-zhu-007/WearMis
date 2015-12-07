package com.sg.mobile.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sg.controller.AbstractController;
import com.sg.controller.JsonResult;
import com.sg.entity.Code;
import com.sg.entity.UserInfo;
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileOrgStructure;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.service.MobileCompanyService;
import com.sg.mobile.service.MobileOrgStructureService;
import com.sg.mobile.service.MobileUserService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileOrgStructure.do")
public class MobileOrgStructureController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileOrgStructure.class);
	
	@Autowired
	private MobileOrgStructureService mobileOrgStructureService;
	
	@Autowired
	private CodeService codeService;
	
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
		return "mobile/mobileorgstructure_list";
	}

	/**
	 * mobileOrgStructure.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileOrgStructure entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", entity.getCodeName());
		params.put("category", "MOBILECOMPANY");

		List<MobileOrgStructure> list = mobileOrgStructureService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (MobileOrgStructure mobileOrgStructure : list) {
			String status = statusMap.get(mobileOrgStructure.getStatus()).toString();
			mobileOrgStructure.setStatus(status);
		}

		int totalCount = mobileOrgStructureService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileOrgStructure entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String uuid = UUID.randomUUID().toString();
			entity.setId(uuid);
			entity.setOrgStructureId(uuid);
			entity.setCategory("MOBILEORGSTRUCTURE");
			
			entity.setCreator(sessionUserName);
			
			/*mobileOrgStructureService.create(entity);*/
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileOrgStructure exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(params = "method=addnode")
	@ResponseBody
	public JSONObject createNode(HttpServletRequest request, MobileOrgStructure entity) {
		
		JSONObject jsonObject = new JSONObject();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String uuid = UUID.randomUUID().toString();
			entity.setId(uuid);
			entity.setOrgStructureId(uuid);
			entity.setCategory("MOBILEORGSTRUCTURE");
			
			entity.setCreator(sessionUserName);
			
			mobileOrgStructureService.create(entity);
			
			MobileOrgStructure model = mobileOrgStructureService.get(uuid);
			
			if(model != null){
				jsonObject.put("message", AbstractController.AJAX_SUCCESS_CODE);
				jsonObject.put("codeName", model.getCodeName());
				jsonObject.put("description", model.getDescription());
				
				if(model.getUpdateTime() == null){
					model.setUpdateTime(new Date());
				}
				
				if(model.getUpdator() == null){
					model.setUpdator("");
				}
				
				JSONObject jsonObject1 = JSONObject.fromObject(model);
				jsonObject.put("attributes", jsonObject1);
			}
			
		} catch (Exception e) {
			jsonObject.put("message", null);
			log.error("create MobileOrgStructure exception reason:" + e.getMessage());
		}
		
		return jsonObject;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileOrgStructure entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileOrgStructure model = mobileOrgStructureService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setStatus(entity.getStatus());
			model.setHasChildren(entity.getHasChildren());
			model.setDisplaySort(entity.getDisplaySort());
			model.setLevelNum(entity.getLevelNum());
			model.setParentCode(entity.getParentCode());
			
			model.setUpdator(sessionUserName);
			
			/*mobileOrgStructureService.update(model);*/
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileOrgStructure exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(params = "method=updatenode")
	@ResponseBody
	public JSONObject updateNode(HttpServletRequest request, MobileOrgStructure entity) {
		JSONObject jsonObject = new JSONObject();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileOrgStructure model = mobileOrgStructureService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setStatus(entity.getStatus());
			model.setHasChildren(entity.getHasChildren());
			model.setDisplaySort(entity.getDisplaySort());
			model.setLevelNum(entity.getLevelNum());
			model.setParentCode(entity.getParentCode());
			
			model.setUpdator(sessionUserName);
			
			mobileOrgStructureService.update(model);
			
			jsonObject.put("message", AbstractController.AJAX_SUCCESS_CODE);
			jsonObject.put("codeName", model.getCodeName());
			jsonObject.put("description", model.getDescription());
			
			if(model.getUpdateTime() == null){
				model.setUpdateTime(new Date());
			}
			
			if(model.getUpdator() == null){
				model.setUpdator("");
			}
			
			JSONObject jsonObject1 = JSONObject.fromObject(model);
			jsonObject.put("attributes", jsonObject1);
		} catch (Exception e) {
			jsonObject.put("message", null);
			log.error("update MobileOrgStructure exception reason:" + e.getMessage());
		}
		
		return jsonObject;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileOrgStructure entity = mobileOrgStructureService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("description", entity.getDescription());
		map.put("status", entity.getStatus());
		map.put("hasChildren", entity.getHasChildren());
		map.put("displaySort", entity.getDisplaySort());
		map.put("levelNum", entity.getLevelNum());
		map.put("parentCode", entity.getParentCode());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=isExist")
	@ResponseBody
	public boolean isExist(HttpServletRequest request, String codeName) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", codeName);
		params.put("category", "MOBILEORGSTRUCTURE");
		
		List<Code> list = codeService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=delete")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, String ids) {
		JsonResult result = new JsonResult();

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			List<MobileOrgStructure> allMobileOrgStructures = mobileOrgStructureService.findForUnPage(params);
			
			String[] array = ids.split(",");
			
			for (String id : array) {
				List<MobileOrgStructure> mobileOrgStructures = new ArrayList<MobileOrgStructure>();
				
				MobileOrgStructure mobileOrgStructure = mobileOrgStructureService.get(id);
				
				mobileOrgStructureService.getChildMobileOrgStructure(mobileOrgStructure, mobileOrgStructures, allMobileOrgStructures);
				
				if(mobileOrgStructures != null && mobileOrgStructures.size() > 0){
					for (MobileOrgStructure childMobileOrgStructure : mobileOrgStructures) {
						mobileOrgStructureService.delete(childMobileOrgStructure.getId());
					}
				}
				
				/*将该架构下的人移动至根架构下*/
				String orgStructure = mobileOrgStructure.getCodeName();
				String parentCode = mobileOrgStructure.getParentCode();
				Map<String, Object> mobileUserParams = new HashMap<String, Object>();
				mobileUserParams.put("orgStructure", orgStructure);
				
				List<MobileUser> mobileUsers = mobileUserService.findForUnPage(mobileUserParams);
				if(mobileUsers != null && mobileUsers.size() > 0){
					for (MobileUser mobileUser : mobileUsers) {
						mobileUser.setOrgStructure(parentCode);
						
						mobileUserService.update(mobileUser);
					}
				}
				
				mobileOrgStructureService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileOrgStructure exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(params = "method=getmobileorgstructurecache")
	@ResponseBody
	public List<MobileOrgStructure> getMobileOrgStructureCache(){
		return mobileOrgStructureService.getMobileOrgStructureCache();
	}
	
	@RequestMapping(params = "method=clearmobileorgstructurecache")
	@ResponseBody
	public JsonResult clearMobileOrgStructureCache(){
		JsonResult result = new JsonResult();
		
		try {
			mobileOrgStructureService.clearMobileOrgStructureCache();
			mobileOrgStructureService.getMobileOrgStructureCache();
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
			result.setMessage(null);
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=inittreedata")
	@ResponseBody
	public JSONArray initTreeData(HttpServletRequest request) {
		String companyId = request.getParameter("companyid");
		
		MobileCompany mobileCompany = mobileCompanyService.get(companyId);
		String companyCode = mobileCompany.getCodeName();
		
		Map<String, Object> params = new HashMap<String, Object>();
		List<MobileOrgStructure> allMobileOrgStructures = mobileOrgStructureService.findForUnPage(params);
		
		List<MobileOrgStructure> level1MenuMobileOrgStructures = new ArrayList<MobileOrgStructure>();
		
		for (MobileOrgStructure entity : allMobileOrgStructures) {
			if(entity.getLevelNum() == 1 && entity.getCodeName().equals(companyCode)) {
				level1MenuMobileOrgStructures.add(entity);
			}
		}
		
		JSONArray jSONArray = mobileOrgStructureService.buildMobileOrgStructureTree(level1MenuMobileOrgStructures, allMobileOrgStructures);
		
		return jSONArray;
	}
	
	@RequestMapping(params = "method=initnode")
	@ResponseBody
	public JSONObject initNode(HttpServletRequest request, String id) {
		MobileOrgStructure entity = mobileOrgStructureService.get(id);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("parentCode", entity.getCodeName());
		
		List<MobileOrgStructure> childMobileOrgStructures = mobileOrgStructureService.findForUnPage(params);
		String strDisplaySort = "";
		if(childMobileOrgStructures != null && childMobileOrgStructures.size() > 0){
			MobileOrgStructure maxDisplaySortMobileOrgStructure = childMobileOrgStructures.get(0);
			strDisplaySort = String.valueOf(maxDisplaySortMobileOrgStructure.getDisplaySort());
		}
		
		int levelNum = entity.getLevelNum() + 1;
		String uuid = UUID.randomUUID().toString().replace("-", "");
		String codeName = entity.getCodeName() + "_Level" + levelNum + "-" + uuid;
		int displaySort = 1;
		if(strDisplaySort != null && !strDisplaySort.equals("")){
			displaySort = Integer.parseInt(strDisplaySort) + 1;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", "");
		map.put("orgStructureId", "");
		map.put("codeName", codeName);
		map.put("description", "");
		map.put("status", "Y");
		map.put("hasChildren", "N");
		map.put("displaySort", displaySort);
		map.put("levelNum", levelNum);
		map.put("parentCode", entity.getCodeName());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=initupdatenode")
	@ResponseBody
	public JSONObject initUpdateNode(HttpServletRequest request, String id) {
		MobileOrgStructure entity = mobileOrgStructureService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("orgStructureId", entity.getOrgStructureId());
		map.put("codeName", entity.getCodeName());
		map.put("description", entity.getDescription());
		map.put("status", entity.getStatus());
		map.put("hasChildren", entity.getHasChildren());
		map.put("displaySort", entity.getDisplaySort());
		map.put("levelNum", entity.getLevelNum());
		map.put("parentCode", entity.getParentCode());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
}
