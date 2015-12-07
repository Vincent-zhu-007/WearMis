package com.sg.mobile.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sg.controller.AbstractController;
import com.sg.controller.JsonResult;
import com.sg.mobile.entity.MobileCardReaderReport;
import com.sg.mobile.service.MobileCardReaderReportService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileCardReaderReport.do")
public class MobileCardReaderReportController extends AbstractController {
	/*private final Logger log = Logger.getLogger(MobileCardReaderReport.class);*/
	
	@Autowired
	private MobileCardReaderReportService mobileCardReaderReportService;
	
	@Autowired
	private CodeService codeService;
	
	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobilecardreaderreport_list";
	}

	/**
	 * mobileCardReaderReport.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileCardReaderReport entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());
		params.put("equipmentNo", entity.getEquipmentNo());

		List<MobileCardReaderReport> list = mobileCardReaderReportService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		Map<String, Object> inOutStatusMap = codeService.getCodeCacheMapByCategory("MOBILECARDREADERREPORTINOUTSTATUS");
		
		for (MobileCardReaderReport mobileCardReaderReport : list) {
			if(mobileCardReaderReport.getInOutStatus() != null && !mobileCardReaderReport.getInOutStatus().equals("")){
				String inOutStatus = inOutStatusMap.get(mobileCardReaderReport.getInOutStatus()).toString();
				mobileCardReaderReport.setInOutStatus(inOutStatus);
			}
			
			String status = statusMap.get(mobileCardReaderReport.getStatus()).toString();
			mobileCardReaderReport.setStatus(status);
		}

		int totalCount = mobileCardReaderReportService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String ownerUri = request.getParameter("ownerUri");
		String equipmentNo = request.getParameter("equipmentNo");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		params.put("equipmentNo", equipmentNo);
		
		List<MobileCardReaderReport> list = mobileCardReaderReportService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileCardReaderReportService.ExpExcel(list, path);
				
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
