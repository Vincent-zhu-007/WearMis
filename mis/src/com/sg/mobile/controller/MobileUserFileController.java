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
import com.sg.mobile.entity.MobileUserFile;
import com.sg.mobile.entity.MobileUserFileShare;
import com.sg.mobile.service.MobileUserFileService;
import com.sg.mobile.service.MobileUserFileShareService;
import com.sg.service.CodeService;
import com.sg.util.DataGridModel;

@Controller
@RequestMapping("/mobileUserFile.do")
public class MobileUserFileController extends AbstractController {
	private final Logger log = Logger.getLogger(MobileUserFile.class);
	
	@Autowired
	private MobileUserFileService mobileUserFileService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileUserFileShareService mobileUserFileShareService;

	/**
	 * 菜单栏页面展示
	 * 
	 * @return 页面路径
	 */
	@RequestMapping
	public String management() {
		return "mobile/mobileuserfile_list";
	}

	/**
	 * mobileUserFile.do?method=list 提交时，list()被调用
	 */
	@RequestMapping(params = "method=list")
	@ResponseBody
	public JsonResult list(HttpServletRequest request, MobileUserFile entity,
			DataGridModel dm) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", entity.getOwnerUri());
		params.put("description", entity.getDescription());
		
		List<MobileUserFile> list = mobileUserFileService.findForPage(params,
				dm.getStartRow(), dm.getRows());
		
		Map<String, Object> fileTypeMap = mobileUserFileService.getMobileUserFileFileTypeDescriptionMap();
		Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
		
		for (MobileUserFile mobileUserFile : list) {
			String fileType = fileTypeMap.get(mobileUserFile.getFileType()).toString();
			mobileUserFile.setFileType(fileType);
			
			String status = statusMap.get(mobileUserFile.getStatus()).toString();
			mobileUserFile.setStatus(status);
		}

		int totalCount = mobileUserFileService.getTotalCount(params).intValue();

		JsonResult jsonResult = new JsonResult();
		jsonResult.setTotal(totalCount);
		jsonResult.setRows(list);
		return jsonResult;
	}

	@RequestMapping(params = "method=add")
	@ResponseBody
	public JsonResult create(HttpServletRequest request, MobileUserFile entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			String id = UUID.randomUUID().toString();
			 
			String targetMemberUris = request.getParameter("targetMemberUri");
			
			if(targetMemberUris != null && !targetMemberUris.equals("")){
				List<MobileUserFileShare> mobileUserFileShares = new ArrayList<MobileUserFileShare>();
				String[] array = targetMemberUris.split(",");
				for (String targetMemberUri : array) {
					MobileUserFileShare model = new MobileUserFileShare();
					model.setId(UUID.randomUUID().toString());
					model.setMobileUserFileId(id);
					model.setTargetMemberUri(targetMemberUri);
					
					mobileUserFileShares.add(model);
				}
				
				if(mobileUserFileShares != null && mobileUserFileShares.size() > 0){
					for (MobileUserFileShare mobileUserFileShare : mobileUserFileShares) {
						mobileUserFileShareService.create(mobileUserFileShare);
					}
				}
			}
			
			entity.setId(id);
			
			String fileName = "";
			String fileType = "";
			
			String fileUrl = entity.getFileUrl();
			
			int index = fileUrl.lastIndexOf("/");
			
			if(index != -1){
				fileName = fileUrl.substring(index + 1);
				
				int i = fileName.lastIndexOf(".");
				
				if(i != -1){
					String suffix = fileName.substring(i + 1);
					fileType = mobileUserFileService.getMobileUserFileFileTypeBySuffix(suffix);
				}
			}
			
			entity.setFileName(fileName);
			entity.setFileType(fileType);
			
			entity.setLocation("");
			entity.setAddress("");
			
			entity.setCreator(sessionUserName);
			
			mobileUserFileService.create(entity);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("create MobileUserFile exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=update")
	@ResponseBody
	public JsonResult update(HttpServletRequest request, MobileUserFile entity) {
		JsonResult result = new JsonResult();

		try {
			String sessionUserName = ((UserInfo)request.getSession().getAttribute("user")).getUserName();
			
			Map<String, Object> mobileUserFileShareParams = new HashMap<String, Object>();
			mobileUserFileShareParams.put("mobileUserFileId", entity.getId());
			List<MobileUserFileShare> oldMobileUserFileShares = mobileUserFileShareService.findForUnPage(mobileUserFileShareParams);
			
			if(oldMobileUserFileShares != null && oldMobileUserFileShares.size() > 0){
				for (MobileUserFileShare oldMobileUserFileShare : oldMobileUserFileShares) {
					mobileUserFileShareService.delete(oldMobileUserFileShare.getId());
				}
			}
			
			String targetMemberUris = request.getParameter("targetMemberUri");
			
			if(targetMemberUris != null && !targetMemberUris.equals("")){
				List<MobileUserFileShare> mobileUserFileShares = new ArrayList<MobileUserFileShare>();
				String[] array = targetMemberUris.split(",");
				for (String targetMemberUri : array) {
					MobileUserFileShare model = new MobileUserFileShare();
					model.setId(UUID.randomUUID().toString());
					model.setMobileUserFileId(entity.getId());
					model.setTargetMemberUri(targetMemberUri);
					
					mobileUserFileShares.add(model);
				}
				
				if(mobileUserFileShares != null && mobileUserFileShares.size() > 0){
					for (MobileUserFileShare mobileUserFileShare : mobileUserFileShares) {
						mobileUserFileShareService.create(mobileUserFileShare);
					}
				}
			}
			
			MobileUserFile model = mobileUserFileService.get(entity.getId());
			
			model.setFileSize(entity.getFileSize());
			model.setDescription(entity.getDescription());
			model.setStatus(entity.getStatus());
			model.setUpdator(sessionUserName);
			
			mobileUserFileService.update(model);
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("update MobileUserFile exception reason：" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(params = "method=get")
	@ResponseBody
	public JSONObject get(HttpServletRequest request, String id) {
		MobileUserFile entity = mobileUserFileService.get(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", entity.getId());
		map.put("fileSize", entity.getFileSize() == null ? "" : entity.getFileSize());
		map.put("description", entity.getDescription() == null ? "" : entity.getDescription());
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
				
				Map<String, Object> mobileUserFileShareParams = new HashMap<String, Object>();
				mobileUserFileShareParams.put("mobileUserFileId", id);
				List<MobileUserFileShare> mobileUserFileShares = mobileUserFileShareService.findForUnPage(mobileUserFileShareParams);
				
				if(mobileUserFileShares != null && mobileUserFileShares.size() > 0){
					for (MobileUserFileShare mobileUserFileShare : mobileUserFileShares) {
						mobileUserFileShareService.delete(mobileUserFileShare.getId());
					}
				}
				
				mobileUserFileService.delete(id);
			}
			
			result.setMessage(AbstractController.AJAX_SUCCESS_CODE);
		} catch (Exception e) {
			result.setMessage(null);
			log.error("delete MobileUserFile exception reason:" + e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(params = "method=getfilepath")
	public void getExpFilePath(HttpServletRequest request, HttpServletResponse response) {
		String filePath = "";
		
		String ownerUri = request.getParameter("ownerUri");
		String description = request.getParameter("description");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerUri", ownerUri);
		params.put("description", description);
		
		List<MobileUserFile> list = mobileUserFileService.findForUnPage(params);
		
		if(list.size() > 0) {
			try {
				String path = request.getRealPath("/") + "userfile\\xls";
				
				filePath = mobileUserFileService.ExpExcel(list, path);
				
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
