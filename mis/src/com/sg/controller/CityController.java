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
import com.sg.entity.UserInfo;
import com.sg.service.CityService;
import com.sg.service.CodeService;
import com.sg.service.ProvinceService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/city.do")
public class CityController extends AbstractController {
	private final Logger log = Logger.getLogger(City.class);

	@Autowired
	private CityService cityService;
	
	@Autowired
	private ProvinceService provinceService;
	
	@Autowired
	private CodeService codeService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "system/city_list";
	}

	/**
	 * city.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, City entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("description", entity.getDescription());

		List<City> list = cityService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> provinceCodeNameMap = provinceService.getCityCacheMap();
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (City city : list) {
			
			String provinceCodeName = "";
			if(provinceCodeNameMap.containsKey(city.getProvinceCodeName())){
				provinceCodeName = provinceCodeNameMap.get(city.getProvinceCodeName()).toString();
			}
			
			city.setProvinceCodeName(provinceCodeName);
			
			String status = statusMap.get(city.getStatus()).toString();
			city.setStatus(status);
		}

		int totalCount = cityService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	/**
	 * city.do?method=add 请求以 HTTP POST 方式提交时，create()被调用
	 * 
	 * @param request
	 * @param entity
	 * @return
	 */
	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, City entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			entity.setId(UUID.randomUUID().toString());
			entity.setCreator(sessionUserName);
			
			cityService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create City exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, City entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			City model = cityService.get(entity.getId());
			
			model.setDescription(entity.getDescription());
			model.setProvinceCodeName(entity.getProvinceCodeName());
			model.setDisplaySort(entity.getDisplaySort());
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			cityService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update City exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		City entity = cityService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("description", entity.getDescription());
		map.put("provinceCodeName", entity.getProvinceCodeName());
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
				cityService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete City exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(params = "method=isExist")
	@ResponseBody
	public boolean isExist(HttpServletRequest request, String codeName) {
		boolean result = true;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeName", codeName);
		
		List<City> list = cityService.findForUnPage(params);
		if(list.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	@RequestMapping(params = "method=getcitycachemapbyprovincecodename")
	@ResponseBody
	public List<Map<String, Object>> getCityCacheMapByProvinceCodeName(String provinceCodeName){
		return cityService.getCityCacheListMapByProvinceCodeName(provinceCodeName);
	}
	
	@RequestMapping(params = "method=clearcitycache")
	@ResponseBody
	public JsonResult clearCityCache(){
		JsonResult result = new JsonResult();
		
		try {
			cityService.clearCityCache();
			cityService.getCityCache();
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
		
		List<City> list = cityService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = cityService.ExpExcel(list, path);
				
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
