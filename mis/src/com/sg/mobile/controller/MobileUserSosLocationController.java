package com.sg.mobile.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sg.controller.JsonResult;
import com.sg.mobile.entity.MobileUserSosLocation;
import com.sg.mobile.service.MobileUserSosLocationService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileUserSosLocation.do")
public class MobileUserSosLocationController {
	/*private final Logger log = Logger.getLogger(MobileUserSosLocation.class);*/
	
	@Autowired
	private MobileUserSosLocationService mobileUserSosLocationService;
	
	@Autowired
	private CodeService codeService;
	
	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobileusersoslocation_list";
	}

	/**
	 * mobileUserSosLocation.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileUserSosLocation entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());

		List<MobileUserSosLocation> list = mobileUserSosLocationService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (MobileUserSosLocation mobileUserSosLocation : list) {
			String status = statusMap.get(mobileUserSosLocation.getStatus()).toString();
			mobileUserSosLocation.setStatus(status);
		}

		int totalCount = mobileUserSosLocationService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}
}
