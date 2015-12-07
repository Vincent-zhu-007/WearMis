package com.sg.mobile.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sg.mobile.dao.MobileAppDao;
import com.sg.mobile.entity.MobileApp;
import com.sg.mobile.entity.MobileAppConfig;
import com.sg.mobile.entity.MobileAppItem;
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileRpcParamenter;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.MobileUser;
import com.sg.service.BaseEntityManager;
import com.sg.service.CodeService;

@Service
public class MobileAppService implements BaseEntityManager<MobileApp> {
	@Resource
	private MobileAppDao mobileAppDao;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Autowired
	private MobileAppItemService mobileAppItemService;
	
	@Autowired
	private MobileSyncDataService mobileSyncDataService;
	
	@Autowired
	private MobileAppConfigService mobileAppConfigService;

	@Override
	public void create(MobileApp entity) {
		// TODO Auto-generated method stub
		mobileAppDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileAppDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileApp> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileAppDao.find(params, pageParams);
	}

	@Override
	public MobileApp get(String id) {
		// TODO Auto-generated method stub
		return mobileAppDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileApp> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileAppDao.findForUnPage(params);
	}

	@Override
	public void update(MobileApp entity) {
		// TODO Auto-generated method stub
		mobileAppDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileAppDao.getTotalCount(params);
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<MobileApp> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
			
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("终端应用列表");
			/*第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short*/
			HSSFRow row = sheet.createRow((int) 0);
			/*第四步，创建单元格，并设置值表头 设置表头居中*/
			HSSFCellStyle style = wb.createCellStyle();
			/*左对齐*/
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT); 

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("用户地址");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("文件名");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("创建日期");
			cell.setCellStyle(style);

			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				MobileApp model = (MobileApp) list.get(i);
				
				String status = statusMap.get(model.getStatus()).toString();
				
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getOwnerUri());
				row.createCell((short) 1).setCellValue(model.getListFileName());
				row.createCell((short) 2).setCellValue(status);
				cell = row.createCell((short) 3);
				cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(model.getCreateTime()));
			}
			
			/*第六步，将文件存到指定位置*/
			try
			{
				String fileName = UUID.randomUUID() + ".xls";
				String tempFilePath = path + "\\" + fileName;
				
				FileOutputStream fout = new FileOutputStream(tempFilePath);
				wb.write(fout);
				fout.close();
				
				filePath = tempFilePath;
			}
			catch (Exception e)
			{
				System.out.print(e.getMessage());
			}
		}
		
		return filePath;
	}
	
	public String getMobileAppByOwnerUriAndListTypeJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String ownerUri = "";
		if(jsonObject.containsKey("ownerUri")){
			ownerUri = jsonObject.opt("ownerUri").toString();
		}
		
		String listType = "";
		if(jsonObject.containsKey("listType")){
			listType = jsonObject.opt("listType").toString();
		}
		
		JSONObject jsonObjectResult = new JSONObject();
		
		if(ownerUri != null && !ownerUri.equals("") && listType != null && !listType.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUriAndCompanyCode(ownerUri, mobileServerConfig.getCompanyCode());
			
			if(mobileUser != null){
				String listFileName = ownerUri + "_" + listType;
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ownerUri", ownerUri);
				params.put("listFileName", listFileName);
				params.put("status", "Y");
				List<MobileApp> list = findForUnPage(params);
				
				if(list != null && list.size() > 0){
					MobileApp mobileApp = list.get(0);
					
					JSONObject jsonObjectData = new JSONObject();
					jsonObjectData.put("ownerUri", mobileApp.getOwnerUri());
					jsonObjectData.put("listFileName", mobileApp.getListFileName());
					jsonObjectData.put("listType", mobileApp.getListType());
					jsonObjectData.put("etag", mobileApp.getEtag());
					
					jsonObjectResult.put("result", "1");
					jsonObjectResult.put("data", jsonObjectData);
					
				}else {
					jsonObjectResult.put("result", "1");
					jsonObjectResult.put("data", "");
				}
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "没有相关的用户");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public String getMobileAppItemByOwnerUriAndListTypeJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String ownerUri = "";
		if(jsonObject.containsKey("ownerUri")){
			ownerUri = jsonObject.opt("ownerUri").toString();
		}
		
		String listType = "";
		if(jsonObject.containsKey("listType")){
			listType = jsonObject.opt("listType").toString();
		}
		
		JSONObject jsonObjectResult = new JSONObject();
		
		if(ownerUri != null && !ownerUri.equals("") && listType != null && !listType.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUriAndCompanyCode(ownerUri, mobileServerConfig.getCompanyCode());
			
			if(mobileUser != null){
				List<MobileAppConfig> mobileAppConfigs = mobileAppConfigService.getMobileAppConfigCache();
				
				String listFileName = ownerUri + "_" + listType;
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ownerUri", ownerUri);
				params.put("listFileName", listFileName);
				List<MobileAppItem> list = mobileAppItemService.findForUnPage(params);
				
				if(list != null && list.size() > 0){
					JSONArray jsonArray = new JSONArray();
					
					for (MobileAppItem mobileAppItem : list) {
						JSONObject jsonObjectData = new JSONObject();
						jsonObjectData.put("ownerUri", mobileAppItem.getOwnerUri());
						
						String description = "";
						String packageName = "";
						String fileUrl = "";
						for (MobileAppConfig mobileAppConfig : mobileAppConfigs) {
							if(mobileAppItem.getAppCodeName().equals(mobileAppConfig.getCodeName())){
								description = mobileAppConfig.getDescription();
								packageName = mobileAppConfig.getPackageName();
								fileUrl = mobileAppConfig.getFileUrl() == null ? "" : mobileAppConfig.getFileUrl();
								break;
							}else {
								continue;
							}
						}
						
						jsonObjectData.put("appCodeName", mobileAppItem.getAppCodeName());
						jsonObjectData.put("description", description);
						jsonObjectData.put("packageName", packageName);
						jsonObjectData.put("fileUrl", fileUrl);
						
						jsonObjectData.put("listFileName", mobileAppItem.getListFileName());
						
						jsonArray.add(jsonObjectData);
					}
					
					jsonObjectResult.put("result", "1");
					jsonObjectResult.put("data", jsonArray);
					
				}else {
					jsonObjectResult.put("result", "1");
					jsonObjectResult.put("data", "");
				}
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "没有相关的用户");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public void runCmdUpdateApp(String ownerUri, MobileCompany mobileCompany){
		/*runCmd*/
		List<MobileRpcParamenter> mobileRpcParamenters = new ArrayList<MobileRpcParamenter>();
		
		MobileRpcParamenter mobileRpcParamenter1 = new MobileRpcParamenter();
		mobileRpcParamenter1.setParaName("mobileRpcParamenter1");
		mobileRpcParamenter1.setParaType("String");
		mobileRpcParamenter1.setParaValue("UpdateApp");
		mobileRpcParamenter1.setParaSort(1);
		mobileRpcParamenters.add(mobileRpcParamenter1);
		
		MobileRpcParamenter mobileRpcParamenter2 = new MobileRpcParamenter();
		mobileRpcParamenter2.setParaName("mobileRpcParamenter2");
		mobileRpcParamenter2.setParaType("String");
		mobileRpcParamenter2.setParaValue(ownerUri);
		mobileRpcParamenter2.setParaSort(2);
		mobileRpcParamenters.add(mobileRpcParamenter2);
		
		mobileSyncDataService.SyncData("runCmd", mobileRpcParamenters, mobileCompany);
	}
	
	public void runCmdDeleteApp(String ownerUri, MobileCompany mobileCompany){
		/*runCmd*/
		List<MobileRpcParamenter> mobileRpcParamenters = new ArrayList<MobileRpcParamenter>();
		
		MobileRpcParamenter mobileRpcParamenter1 = new MobileRpcParamenter();
		mobileRpcParamenter1.setParaName("mobileRpcParamenter1");
		mobileRpcParamenter1.setParaType("String");
		mobileRpcParamenter1.setParaValue("DeleteApp");
		mobileRpcParamenter1.setParaSort(1);
		mobileRpcParamenters.add(mobileRpcParamenter1);
		
		MobileRpcParamenter mobileRpcParamenter2 = new MobileRpcParamenter();
		mobileRpcParamenter2.setParaName("mobileRpcParamenter2");
		mobileRpcParamenter2.setParaType("String");
		mobileRpcParamenter2.setParaValue(ownerUri);
		mobileRpcParamenter2.setParaSort(2);
		mobileRpcParamenters.add(mobileRpcParamenter2);
		
		mobileSyncDataService.SyncData("runCmd", mobileRpcParamenters, mobileCompany);
	}
	
	public void deleteMobileApp(String ownerUri, String listType){
		Map<String, Object> mobileAppParams = new HashMap<String, Object>();
		mobileAppParams.put("ownerUri", ownerUri);
		mobileAppParams.put("listType", listType);
		
		List<MobileApp> mobileApps = findForUnPage(mobileAppParams);
		
		if(mobileApps != null && mobileApps.size() > 0){
			MobileApp mobileApp = mobileApps.get(0);
			
			String listFileName = mobileApp.getListFileName();
			
			Map<String, Object> mobileAppItemParams = new HashMap<String, Object>();
			mobileAppItemParams.put("ownerUri", ownerUri);
			mobileAppItemParams.put("listFileName", listFileName);
			
			List<MobileAppItem> mobileAppItems = mobileAppItemService.findForUnPage(mobileAppItemParams);
			
			if(mobileAppItems != null && mobileAppItems.size() > 0){
				for (MobileAppItem mobileAppItem : mobileAppItems) {
					mobileAppItemService.delete(mobileAppItem.getId());
				}
			}
			
			delete(mobileApp.getId());
		}
	}
	
	public void deleteMobileAppItemByAppCodeName(String appCodeName){
		Map<String, Object> mobileAppItemParams = new HashMap<String, Object>();
		mobileAppItemParams.put("appCodeName", appCodeName);
		
		List<MobileAppItem> mobileAppItems = mobileAppItemService.findForUnPage(mobileAppItemParams);
		
		if(mobileAppItems != null && mobileAppItems.size() > 0){
			for (MobileAppItem mobileAppItem : mobileAppItems) {
				mobileAppItemService.delete(mobileAppItem.getId());
			}
		}
	}
	
	public void deleteMobileAppItemByoOwnerUri(String ownerUri, String listType){
		Map<String, Object> mobileAppParams = new HashMap<String, Object>();
		mobileAppParams.put("ownerUri", ownerUri);
		mobileAppParams.put("listType", listType);
		
		List<MobileApp> mobileApps = findForUnPage(mobileAppParams);
		
		if(mobileApps != null && mobileApps.size() > 0){
			MobileApp mobileApp = mobileApps.get(0);
			
			String listFileName = mobileApp.getListFileName();
			
			Map<String, Object> mobileAppItemParams = new HashMap<String, Object>();
			mobileAppItemParams.put("ownerUri", ownerUri);
			mobileAppItemParams.put("listFileName", listFileName);
			
			List<MobileAppItem> mobileAppItems = mobileAppItemService.findForUnPage(mobileAppItemParams);
			
			if(mobileAppItems != null && mobileAppItems.size() > 0){
				for (MobileAppItem mobileAppItem : mobileAppItems) {
					mobileAppItemService.delete(mobileAppItem.getId());
				}
			}
		}
	}
}
