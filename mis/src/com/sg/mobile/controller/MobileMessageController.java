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
import com.sg.mobile.entity.MobileMessage;
import com.sg.mobile.entity.MobileRpcParamenter;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.service.MobileCompanyService;
import com.sg.mobile.service.MobileMessageService;
import com.sg.mobile.service.MobileSyncDataService;
import com.sg.mobile.service.MobileUserService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileMessage.do")
public class MobileMessageController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileMessage.class);

	@Autowired
	private MobileMessageService mobileMessageService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Autowired
	private MobileSyncDataService mobileSyncDataService;
	
	@Autowired
	private MobileCompanyService mobileCompanyService;
	
	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobilemessage_list";
	}

	/**
	 * mobileMessage.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileMessage entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());

		List<MobileMessage> list = mobileMessageService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> processStatusMap = codeService.getCodeCacheMapByCategory("MOBILEMESSAGEPROCESSSTATUS");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (MobileMessage mobileMessage : list) {
			if(mobileMessage.getProcessStatus() != null && !mobileMessage.getProcessStatus().equals("")){
				String processStatus = processStatusMap.get(mobileMessage.getProcessStatus()).toString();
				mobileMessage.setProcessStatus(processStatus);
			}
			
			String status = statusMap.get(mobileMessage.getStatus()).toString();
			mobileMessage.setStatus(status);
		}

		int totalCount = mobileMessageService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileMessage entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			entity.setId(UUID.randomUUID().toString());
			entity.setCreator(sessionUserName);
			
			mobileMessageService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileMessage exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileMessage entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			MobileMessage model = mobileMessageService.get(entity.getId());
			
			model.setTargetUris(entity.getTargetUris());
			model.setContent(entity.getContent());
			model.setProcessStatus(entity.getProcessStatus());
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			mobileMessageService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileMessage exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileMessage entity = mobileMessageService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("ownerUri", entity.getOwnerUri());
		map.put("targetUris", entity.getTargetUris());
		map.put("content", entity.getContent());
		map.put("processStatus", entity.getProcessStatus());
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
				mobileMessageService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileMessage exception reason:" + e.getMessage());
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
		
		List<MobileMessage> list = mobileMessageService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileMessageService.ExpExcel(list, path);
				
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
	
	@RequestMapping(params = "method=sendmobilemessage")
	@ResponseBody
	public JsonResult sendMobileMessage(HttpServletRequest request, String id) {
		JsonResult result = new JsonResult();
		
		try {
			MobileMessage mobileMessage = mobileMessageService.get(id);
			
			String ownerUri = mobileMessage.getOwnerUri();
			
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
			
			if(mobileMessage != null && mobileUser != null){
				String targetUris = mobileMessage.getTargetUris();
				String[] array = targetUris.split(",");
				
				String content = mobileMessage.getContent();
				
				MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByComapnyCode(mobileUser.getCompanyCode());
				
				for (String targetUri : array) {
					List<MobileRpcParamenter> mobileRpcParamenters = new ArrayList<MobileRpcParamenter>();
					
					MobileRpcParamenter mobileRpcParamenter1 = new MobileRpcParamenter();
					mobileRpcParamenter1.setParaName("mobileRpcParamenter1");
					mobileRpcParamenter1.setParaType("String");
					mobileRpcParamenter1.setParaValue("SendMobileMessage");
					mobileRpcParamenter1.setParaSort(1);
					mobileRpcParamenters.add(mobileRpcParamenter1);
					
					MobileRpcParamenter mobileRpcParamenter2 = new MobileRpcParamenter();
					mobileRpcParamenter2.setParaName("mobileRpcParamenter2");
					mobileRpcParamenter2.setParaType("String");
					mobileRpcParamenter2.setParaValue(targetUri);
					mobileRpcParamenter2.setParaSort(2);
					mobileRpcParamenters.add(mobileRpcParamenter2);
					
					MobileRpcParamenter mobileRpcParamenter3 = new MobileRpcParamenter();
					mobileRpcParamenter3.setParaName("mobileRpcParamenter3");
					mobileRpcParamenter3.setParaType("String");
					mobileRpcParamenter3.setParaValue(content);
					mobileRpcParamenter3.setParaSort(3);
					mobileRpcParamenters.add(mobileRpcParamenter3);
					
					mobileSyncDataService.SyncData("runCmd", mobileRpcParamenters, mobileCompany);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		
		return result;
	}
}
