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
import com.sg.mobile.entity.MobileVersionConfig;
import com.sg.mobile.service.MobileVersionConfigService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileVersionConfig.do")
public class MobileVersionConfigController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileVersionConfig.class);

	@Autowired
	private MobileVersionConfigService mobileVersionConfigService;
	
	@Autowired
	private CodeService codeService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobileversionconfig_list";
	}

	/**
	 * mobileVersionConfig.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileVersionConfig entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", entity.getName());
		params.put("verNo", entity.getVerNo());
		params.put("verCategory", entity.getVerCategorySearch());

		List<MobileVersionConfig> list = mobileVersionConfigService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> verCategoryMap = codeService.getCodeCacheMapByCategory("MOBILEVERSIONCATEGORY");
		Map<String, Object> isMandatoryUpdateMap = codeService.getCodeCacheMapByCategory("MOBILEVERSIONISMANDATORYUPDATE");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (MobileVersionConfig mobileVersionConfig : list) {
			
			String verCategory = verCategoryMap.get(mobileVersionConfig.getVerCategory()).toString();
			mobileVersionConfig.setVerCategory(verCategory);
			
			String isMandatoryUpdate = isMandatoryUpdateMap.get(mobileVersionConfig.getIsMandatoryUpdate()).toString();
			mobileVersionConfig.setIsMandatoryUpdate(isMandatoryUpdate);
			
			String status = statusMap.get(mobileVersionConfig.getStatus()).toString();
			mobileVersionConfig.setStatus(status);
		}

		int totalCount = mobileVersionConfigService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileVersionConfig entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			entity.setId(UUID.randomUUID().toString());
			entity.setCreator(sessionUserName);
			
			mobileVersionConfigService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileVersionConfig exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileVersionConfig entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileVersionConfig model = mobileVersionConfigService.get(entity.getId());
			
			model.setName(entity.getName());
			model.setVerFileAddress(entity.getVerFileAddress());
			model.setVerNo(entity.getVerNo());
			model.setVerCategory(entity.getVerCategory());
			model.setVerSort(entity.getVerSort());
			model.setIsMandatoryUpdate(entity.getIsMandatoryUpdate());
			model.setVerRemark(entity.getVerRemark());
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			mobileVersionConfigService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileVersionConfig exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileVersionConfig entity = mobileVersionConfigService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("name", entity.getName());
		map.put("verFileAddress", entity.getVerFileAddress());
		map.put("verNo", entity.getVerNo());
		map.put("verCategory", entity.getVerCategory());
		map.put("verSort", entity.getVerSort());
		map.put("isMandatoryUpdate", entity.getIsMandatoryUpdate());
		map.put("verRemark", entity.getVerRemark());
		map.put("status", entity.getStatus());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=getmobileversionconfigcache")
	@ResponseBody
	public List<MobileVersionConfig> getMobileVersionConfigCache(){
		return mobileVersionConfigService.getMobileVersionConfigCache();
	}
	
	@RequestMapping(params = "method=clearmobileversionconfigcache")
	@ResponseBody
	public JsonResult clearMobileVersionConfigCache(){
		JsonResult result = new JsonResult();
		
		try {
			mobileVersionConfigService.clearMobileVersionConfigCache();
			mobileVersionConfigService.getMobileVersionConfigCache();
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
				mobileVersionConfigService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileVersionConfig exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String name = request.getParameter("name");
		String verNo = request.getParameter("verNo");
		String verCategory = request.getParameter("verCategory");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("verNo", verNo);
		params.put("verCategory", verCategory);
		
		List<MobileVersionConfig> list = mobileVersionConfigService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileVersionConfigService.ExpExcel(list, path);
				
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
