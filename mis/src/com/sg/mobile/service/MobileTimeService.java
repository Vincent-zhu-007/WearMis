package com.sg.mobile.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.sg.mobile.dao.MobileTimeDao;
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.MobileTime;
import com.sg.mobile.entity.MobileTimeItem;
import com.sg.mobile.entity.MobileUser;
import com.sg.service.BaseEntityManager;
import com.sg.service.CodeService;
import com.sg.util.SendMqttUtil;

@Service
public class MobileTimeService implements BaseEntityManager<MobileTime> {
	@Resource
	private MobileTimeDao mobileTimeDao;
	
	@Autowired
	private MobileTimeItemService mobileTimeItemService;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Autowired
	private CodeService codeService;

	@Override
	public void create(MobileTime entity) {
		// TODO Auto-generated method stub
		mobileTimeDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileTimeDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileTime> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileTimeDao.find(params, pageParams);
	}

	@Override
	public MobileTime get(String id) {
		// TODO Auto-generated method stub
		return mobileTimeDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileTime> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileTimeDao.findForUnPage(params);
	}

	@Override
	public void update(MobileTime entity) {
		// TODO Auto-generated method stub
		mobileTimeDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileTimeDao.getTotalCount(params);
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<MobileTime> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
			
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("Mobile时间列表");
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
				MobileTime model = (MobileTime) list.get(i);
				
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
	
	public String getMobileTimeByOwnerUriAndListTypeJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String userName = "";
		if(jsonObject.containsKey("userName")){
			userName = jsonObject.opt("userName").toString();
		}
		
		String listType = "";
		if(jsonObject.containsKey("listType")){
			listType = jsonObject.opt("listType").toString();
		}
		
		JSONObject jsonObjectResult = new JSONObject();
		
		if(userName != null && !userName.equals("") && listType != null && !listType.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByUserName(userName);
			
			if(mobileUser != null){
				String ownerUri = mobileUser.getOwnerUri();
				
				String listFileName = ownerUri + "_" + listType;
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ownerUri", ownerUri);
				params.put("listFileName", listFileName);
				params.put("status", "Y");
				List<MobileTime> list = findForUnPage(params);
				
				if(list != null && list.size() > 0){
					MobileTime mobileTime = list.get(0);
					
					JSONObject jsonObjectData = new JSONObject();
					jsonObjectData.put("ownerUri", mobileTime.getOwnerUri());
					jsonObjectData.put("listFileName", mobileTime.getListFileName());
					jsonObjectData.put("listType", mobileTime.getListType());
					jsonObjectData.put("etag", mobileTime.getEtag());
					
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
	
	public String getMobileTimeItemByOwnerUriAndListTypeJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String userName = "";
		if(jsonObject.containsKey("userName")){
			userName = jsonObject.opt("userName").toString();
		}
		
		String listType = "";
		if(jsonObject.containsKey("listType")){
			listType = jsonObject.opt("listType").toString();
		}
		
		JSONObject jsonObjectResult = new JSONObject();
		
		if(userName != null && !userName.equals("") && listType != null && !listType.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByUserName(userName);
			
			if(mobileUser != null){
				String ownerUri = mobileUser.getOwnerUri();
				
				String listFileName = ownerUri + "_" + listType;
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ownerUri", ownerUri);
				params.put("listFileName", listFileName);
				List<MobileTimeItem> list = mobileTimeItemService.findForUnPage(params);
				
				if(list != null && list.size() > 0){
					JSONArray jsonArray = new JSONArray();
					
					for (MobileTimeItem mobileTimeItem : list) {
						JSONObject jsonObjectData = new JSONObject();
						jsonObjectData.put("ownerUri", mobileTimeItem.getOwnerUri());
						jsonObjectData.put("startTime", mobileTimeItem.getStartTime() == null ? "" : mobileTimeItem.getStartTime());
						jsonObjectData.put("endTime", mobileTimeItem.getEndTime() == null ? "" : mobileTimeItem.getEndTime());
						jsonObjectData.put("level", mobileTimeItem.getLevel() == null ? "" : mobileTimeItem.getLevel());
						jsonObjectData.put("listFileName", mobileTimeItem.getListFileName());
						
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
	
	public void runMqttUpdateMobileTime(String userName, MobileCompany mobileCompany){
		String mqttHost = mobileCompany.getRpcHost();
		String mqttPort = mobileCompany.getRpcPort();
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("m", "UpdateMobileTime");
		
		String topic = "host/" + userName;
		String content = jsonObject.toString();
		String broker = mqttHost + ":" + mqttPort;
		
		SendMqttUtil.send(topic, content, broker);
	}
	
	public void runMqttPushMobileTimeItem(String mobileTimeId, String listType, String userName, MobileCompany mobileCompany){
		JSONObject jsonObjectData = new JSONObject();
		
		MobileTime mobileTime = get(mobileTimeId);
		
		jsonObjectData.put("cmd", listType);
		
		if(mobileTime != null){
			String ownerUri = mobileTime.getOwnerUri();
			String listFileName = mobileTime.getListFileName();
			
			Map<String, Object> mobileTimeItemParams = new HashMap<String, Object>();
			mobileTimeItemParams.put("ownerUri", ownerUri);
			mobileTimeItemParams.put("listFileName", listFileName);
			List<MobileTimeItem> mobileTimeItems = mobileTimeItemService.findForUnPage(mobileTimeItemParams);
			
			if(mobileTimeItems != null && mobileTimeItems.size() > 0){
				JSONArray jsonArray = new JSONArray();
				
				for (MobileTimeItem mobileTimeItem : mobileTimeItems) {
					JSONObject jsonObjectMobileTimeItem = new JSONObject();
					
					jsonObjectMobileTimeItem.put("startTime", mobileTimeItem.getStartTime());
					jsonObjectMobileTimeItem.put("endTime", mobileTimeItem.getEndTime());
					jsonObjectMobileTimeItem.put("level", mobileTimeItem.getLevel());
					
					jsonArray.add(jsonObjectMobileTimeItem);
					
					jsonObjectData.put("data", jsonArray.toString());
				}
			}else {
				jsonObjectData.put("data", "");
			}
		}else {
			jsonObjectData.put("data", "");
		}
		
		String mqttHost = mobileCompany.getRpcHost();
		String mqttPort = mobileCompany.getRpcPort();
		
		String topic = "host/" + userName;
		String content = jsonObjectData.toString();
		String broker = mqttHost + ":" + mqttPort;
		
		SendMqttUtil.send(topic, content, broker);
	}
	
	public void deleteMobileTime(String ownerUri, String listType){
		Map<String, Object> mobileTimeParams = new HashMap<String, Object>();
		mobileTimeParams.put("ownerUri", ownerUri);
		mobileTimeParams.put("listType", listType);
		
		List<MobileTime> mobileTimes = findForUnPage(mobileTimeParams);
		
		if(mobileTimes != null && mobileTimes.size() > 0){
			MobileTime mobileTime = mobileTimes.get(0);
			
			String listFileName = mobileTime.getListFileName();
			
			Map<String, Object> mobileTimeItemParams = new HashMap<String, Object>();
			mobileTimeItemParams.put("ownerUri", ownerUri);
			mobileTimeItemParams.put("listFileName", listFileName);
			
			List<MobileTimeItem> mobileTimeItems = mobileTimeItemService.findForUnPage(mobileTimeItemParams);
			
			if(mobileTimeItems != null && mobileTimeItems.size() > 0){
				for (MobileTimeItem mobileTimeItem : mobileTimeItems) {
					mobileTimeItemService.delete(mobileTimeItem.getId());
				}
			}
			
			delete(mobileTime.getId());
		}
	}
	
	public void deleteMobileTimeItemByOwnerUri(String ownerUri, String listType){
		Map<String, Object> mobileTimeParams = new HashMap<String, Object>();
		mobileTimeParams.put("ownerUri", ownerUri);
		mobileTimeParams.put("listType", listType);
		
		List<MobileTime> mobileTimes = findForUnPage(mobileTimeParams);
		
		if(mobileTimes != null && mobileTimes.size() > 0){
			MobileTime mobileTime = mobileTimes.get(0);
			
			String listFileName = mobileTime.getListFileName();
			
			Map<String, Object> mobileTimeItemParams = new HashMap<String, Object>();
			mobileTimeItemParams.put("ownerUri", ownerUri);
			mobileTimeItemParams.put("listFileName", listFileName);
			
			List<MobileTimeItem> mobileTimeItems = mobileTimeItemService.findForUnPage(mobileTimeItemParams);
			
			if(mobileTimeItems != null && mobileTimeItems.size() > 0){
				for (MobileTimeItem mobileTimeItem : mobileTimeItems) {
					mobileTimeItemService.delete(mobileTimeItem.getId());
				}
			}
		}
	}
	
	public String getServerTimeJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String userName = "";
		if(jsonObject.containsKey("user")){
			userName = jsonObject.opt("user").toString();
		}
		
		JSONObject jsonObjectResult = new JSONObject();
		
		if(userName != null && !userName.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByUserName(userName);
			
			if(mobileUser != null){
				Date now = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strDate = dateFormat.format(now);
				
				jsonObjectResult.put("result", "1");
				jsonObjectResult.put("data", strDate);
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
}
