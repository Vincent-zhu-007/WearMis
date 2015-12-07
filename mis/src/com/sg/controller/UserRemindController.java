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

import com.sg.entity.UserInfo;
import com.sg.entity.UserRemind;
import com.sg.service.CodeService;
import com.sg.service.UserRemindService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/userRemind.do")
public class UserRemindController extends AbstractController {
	private final Logger log = Logger.getLogger(UserRemind.class);

	@Autowired
	private UserRemindService userRemindService;
	
	@Autowired
	private CodeService codeService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "system/userremind_list";
	}

	/**
	 * userRemind.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, UserRemind entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", entity.getTitle());

		List<UserRemind> list = userRemindService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> processTagMap = codeService.getCodeCacheMapByCategory("TARGETUSER");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (UserRemind userRemind : list) {
			
			if(userRemind.getProcessTag() != null && !userRemind.getProcessTag().equals("")){
				String processTag = processTagMap.get(userRemind.getProcessTag()).toString();
				userRemind.setProcessTag(processTag);
			}
			
			String status = statusMap.get(userRemind.getStatus()).toString();
			userRemind.setStatus(status);
		}

		int totalCount = userRemindService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	/**
	 * userRemind.do?method=add 请求以 HTTP POST 方式提交时，create()被调用
	 * 
	 * @param request
	 * @param entity
	 * @return
	 */
	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, UserRemind entity) {
		JsonResult result = new JsonResult();

		try {
			entity.setId(UUID.randomUUID().toString());
			entity.setCreator("admin");
			
			userRemindService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create UserRemind exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, UserRemind entity) {
		JsonResult result = new JsonResult();

		try {
			UserRemind model = userRemindService.get(entity.getId());
			
			model.setTitle(entity.getTitle());
			model.setContent(entity.getContent());
			model.setTargetUser(entity.getTargetUser());
			model.setProcessTag(entity.getProcessTag());
			model.setStatus(entity.getStatus());
			model.setUpdator("admin");
			
			userRemindService.update(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update UserRemind exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		UserRemind entity = userRemindService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("title", entity.getTitle());
		map.put("content", entity.getContent());
		map.put("targetUser", entity.getTargetUser());
		map.put("processTag", entity.getProcessTag());
		map.put("status", entity.getStatus());
		
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	
	@RequestMapping(params = "method=getremindsbyuser")
	@ResponseBody
	public JsonResult getRemindsByUser(HttpServletRequest request, UserRemind entity,
			DataGridModel dm) {
		UserInfo user = (UserInfo)request.getSession().getAttribute("user");
		
		String userName = user.getUserName();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("targetUser", userName);

		List<UserRemind> list = userRemindService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> processTagMap = codeService.getCodeCacheMapByCategory("TARGETUSER");
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		for (UserRemind userRemind : list) {
			
			if(userRemind.getProcessTag() != null && !userRemind.getProcessTag().equals("")){
				String processTag = processTagMap.get(userRemind.getProcessTag()).toString();
				userRemind.setProcessTag(processTag);
			}
			
			String status = statusMap.get(userRemind.getStatus()).toString();
			userRemind.setStatus(status);
		}

		int totalCount = userRemindService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}
	
	@RequestMapping(params = "method=delete")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, String ids) {
		JsonResult result = new JsonResult();

		try {
			String[] array = ids.split(",");
			
			for (String id : array) {
				userRemindService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete UserRemind exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String title = request.getParameter("title");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", title);
		
		List<UserRemind> list = userRemindService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = userRemindService.ExpExcel(list, path);
				
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
