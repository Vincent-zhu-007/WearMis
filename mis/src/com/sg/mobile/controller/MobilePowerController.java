package com.sg.mobile.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sg.controller.AbstractController;
import com.sg.controller.JsonResult;
import com.sg.mobile.entity.MobilePower;
import com.sg.mobile.service.MobilePowerService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobilePower.do")
public class MobilePowerController extends AbstractController {
	/*private final Logger log = Logger.getLogger(MobilePower.class);*/
	
	@Autowired
	private MobilePowerService mobilePowerService;
	
	@Autowired
	private CodeService codeService;
	
	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobilepower_list";
	}

	/**
	 * mobilePower.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobilePower entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());

		List<MobilePower> list = mobilePowerService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (MobilePower mobilePower : list) {
			String status = statusMap.get(mobilePower.getStatus()).toString();
			mobilePower.setStatus(status);
		}

		int totalCount = mobilePowerService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}
}
