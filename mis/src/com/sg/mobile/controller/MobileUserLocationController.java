package com.sg.mobile.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sg.controller.AbstractController;
import com.sg.controller.JsonResult;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.entity.MobileUserLocation;
import com.sg.mobile.service.MobileUserLocationService;
import com.sg.mobile.service.MobileUserService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileUserLocation.do")
public class MobileUserLocationController extends AbstractController {
	/*private final Logger log = Logger.getLogger(MobileUserLocation.class);*/
	
	@Autowired
	private MobileUserLocationService mobileUserLocationService;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Autowired
	private CodeService codeService;
	
	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobileuserlocation_list";
	}

	/**
	 * mobileUserLocation.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileUserLocation entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());

		List<MobileUserLocation> list = mobileUserLocationService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (MobileUserLocation mobileUserLocation : list) {
			String status = statusMap.get(mobileUserLocation.getStatus()).toString();
			mobileUserLocation.setStatus(status);
		}

		int totalCount = mobileUserLocationService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}
	
	@RequestMapping(params = "method=getmobileuserlocations")
	@ResponseBody
	public JSONArray getPocUserLocations(HttpServletRequest request, String id) {
		JSONArray jsonArray = new JSONArray();
		
		MobileUser mobileUser = mobileUserService.get(id);
		String ownerUri = mobileUser.getOwnerUri();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		
		List<MobileUserLocation> mobileUserLocations = mobileUserLocationService.findForUnPage(params);
		
		if(mobileUserLocations != null && mobileUserLocations.size() > 0){
			for (MobileUserLocation mobileUserLocation : mobileUserLocations) {
				String address = mobileUserLocation.getAddress();
				
				if(address != null && !address.equals("")){
					
				}else {
					address = "抱歉，未获得详细地址 ";
				}
				
				mobileUserLocation.setAddress(address);
				
				mobileUserLocation.setUpdateTime(new Date());
				mobileUserLocation.setUpdator("");
				
				JSONObject jsonObject = JSONObject.fromObject(mobileUserLocation);
				
				jsonArray.add(jsonObject);
			}
		}
		
		return jsonArray;
	}
}
