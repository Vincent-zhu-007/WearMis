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
import com.sg.mobile.entity.MobileCardReader;
import com.sg.mobile.service.MobileCardReaderService;
import com.sg.mobile.service.MobileMenuConfigService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileCardReader.do")
public class MobileCardReaderController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileCardReader.class);

	@Autowired
	private MobileCardReaderService mobileCardReaderService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileMenuConfigService mobileMenuConfigService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobilecardreader_list";
	}

	/**
	 * mobileCardReader.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileCardReader entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("equipmentNo", entity.getEquipmentNo());
		params.put("description", entity.getDescription());

		List<MobileCardReader> list = mobileCardReaderService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> categoryCodeMap = codeService.getCodeCacheMapByCategory("MOBILECARDREADERCATEGORYCODE");
		Map<String, Object> areaCodeMap = mobileMenuConfigService.getLevel1MobileMenuConfigCacheMap();
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (MobileCardReader mobileCardReader : list) {
			if(mobileCardReader.getCategoryCode() != null && !mobileCardReader.getCategoryCode().equals("")){
				String categoryCode = categoryCodeMap.get(mobileCardReader.getCategoryCode()).toString();
				mobileCardReader.setCategoryCode(categoryCode);
			}
			
			if(mobileCardReader.getAreaCode() != null && !mobileCardReader.getAreaCode().equals("")){
				String areaCode = areaCodeMap.get(mobileCardReader.getAreaCode()).toString();
				mobileCardReader.setAreaCode(areaCode);
			}
			
			String status = statusMap.get(mobileCardReader.getStatus()).toString();
			mobileCardReader.setStatus(status);
		}

		int totalCount = mobileCardReaderService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	/**
	 * mobileCardReader.do?method=add 请求以 HTTP POST 方式提交时，create()被调用
	 * 
	 * @param request
	 * @param entity
	 * @return
	 */
	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileCardReader entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			entity.setId(UUID.randomUUID().toString());
			entity.setCreator(sessionUserName);
			
			mobileCardReaderService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileCardReader exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileCardReader entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileCardReader model = mobileCardReaderService.get(entity.getId());
			
			model.setEquipmentNo(entity.getEquipmentNo());
			model.setDescription(entity.getDescription());
			model.setCategoryCode(entity.getCategoryCode());
			model.setAreaCode(entity.getAreaCode());
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			mobileCardReaderService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileCardReader exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileCardReader entity = mobileCardReaderService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("id", entity.getId());
		map.put("equipmentNo", entity.getEquipmentNo());
		map.put("description", entity.getDescription());
		map.put("categoryCode", entity.getCategoryCode());
		map.put("areaCode", entity.getAreaCode());
		map.put("status", entity.getStatus());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=getmobilecardreadercache")
	@ResponseBody
	public List<MobileCardReader> getMobileCardReaderCache(){
		return mobileCardReaderService.getMobileCardReaderCache();
	}
	
	@RequestMapping(params = "method=clearmobilecardreadercache")
	@ResponseBody
	public JsonResult clearMobileCardReaderCache(){
		JsonResult result = new JsonResult();
		
		try {
			mobileCardReaderService.clearMobileCardReaderCache();
			mobileCardReaderService.getMobileCardReaderCache();
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
				mobileCardReaderService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileCardReader exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String equipmentNo = request.getParameter("equipmentNo");
		String description = request.getParameter("description");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("equipmentNo", equipmentNo);
		params.put("description", description);
		
		List<MobileCardReader> list = mobileCardReaderService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileCardReaderService.ExpExcel(list, path);
				
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