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

import com.sg.entity.City;
import com.sg.entity.Province;
import com.sg.entity.UserInfo;
import com.sg.service.CityService;
import com.sg.service.CodeService;
import com.sg.service.ProvinceService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/province.do")
public class ProvinceController extends AbstractController {
	private final Logger log = Logger.getLogger(Province.class);

	@Autowired
	private ProvinceService provinceService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private CityService cityService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "system/province_list";
	}

	/**
	 * province.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, Province entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("description", entity.getDescription());

		List<Province> list = provinceService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (Province province : list) {
			String status = statusMap.get(province.getStatus()).toString();
			province.setStatus(status);
		}

		int totalCount = provinceService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	/**
	 * province.do?method=add 请求以 HTTP POST 方式提交时，create()被调用
	 * 
	 * @param request
	 * @param entity
	 * @return
	 */
	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, Province entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			entity.setId(UUID.randomUUID().toString());
			entity.setCreator(sessionUserName);
			
			provinceService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create Province exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, Province entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			Province model = provinceService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setDisplaySort(entity.getDisplaySort());
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			provinceService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update Province exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		Province entity = provinceService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("description", entity.getDescription());
		map.put("displaySort", entity.getDisplaySort());
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
				Province province = provinceService.get(id);
				
				if(province != null){
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("provinceCodeName", province.getCodeName());
					
					List<City> citys = cityService.findForUnPage(params);
					
					if(citys != null && citys.size() > 0){
						for (City city : citys) {
							cityService.delete(city.getId());
						}
					}
				}
				
				provinceService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete Province exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(params = "method=isExist")
	@ResponseBody
	public boolean isExist(HttpServletRequest request, String codeName) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", codeName);
		
		List<Province> list = provinceService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=getprovincecachemap")
	@ResponseBody
	public List<Map<String, Object>> getProvinceCacheMap(){
		return provinceService.getProvinceCacheListMap();
	}
	
	@RequestMapping(params = "method=clearprovincecache")
	@ResponseBody
	public JsonResult clearProvinceCache(){
		JsonResult result = new JsonResult();
		
		try {
			provinceService.clearProvinceCache();
			provinceService.getProvinceCache();
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
			result.setMessage(null);
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
		
		List<Province> list = provinceService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = provinceService.ExpExcel(list, path);
				
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
