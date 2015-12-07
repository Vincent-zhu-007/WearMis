package com.sg.mobile.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.stereotype.Service;
import com.sg.mobile.dao.MobileAppConfigDao;
import com.sg.mobile.entity.MobileAppConfig;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.service.BaseEntityManager;
import com.sg.util.MemCached;

@Service
public class MobileAppConfigService implements BaseEntityManager<MobileAppConfig> {
	@Resource
	private MobileAppConfigDao mobileAppConfigDao;

	@Override
	public void create(MobileAppConfig entity) {
		// TODO Auto-generated method stub
		mobileAppConfigDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileAppConfigDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileAppConfig> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileAppConfigDao.find(params, pageParams);
	}

	@Override
	public MobileAppConfig get(String id) {
		// TODO Auto-generated method stub
		return mobileAppConfigDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileAppConfig> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileAppConfigDao.findForUnPage(params);
	}

	@Override
	public void update(MobileAppConfig entity) {
		// TODO Auto-generated method stub
		mobileAppConfigDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileAppConfigDao.getTotalCount(params);
	}
	
	@SuppressWarnings("rawtypes")
	public List<MobileAppConfig> getMobileAppConfigCache() {
		List<MobileAppConfig> list = new ArrayList<MobileAppConfig>();
		
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("MobileAppConfig");
			if(obj != null && !obj.equals("")) {
				String json = MemCached.getInstance().get("MobileAppConfig").toString();
				
				JSONArray array = JSONArray.fromObject(json);
			    for(Iterator iter = array.iterator(); iter.hasNext();){
			        JSONObject jsonObject = (JSONObject)iter.next();
			        list.add((MobileAppConfig)JSONObject.toBean(jsonObject, MobileAppConfig.class));
			    }
			}else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("status", "Y");
				list = this.findForUnPage(params);
				
				JSONArray jsonObject = JSONArray.fromObject(list);
				
				MemCached.getInstance().set("MobileAppConfig", jsonObject);
			}
		}
		
		return list;
	}
	
	public void clearMobileAppConfigCache() {
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("MobileAppConfig");
			if(obj != null && !obj.equals("")) {
				MemCached.getInstance().set("MobileAppConfig", "");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<MobileAppConfig> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
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
			cell.setCellValue("编码");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("描述");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("包名");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("文件地址");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 5);
			cell.setCellValue("创建日期");
			cell.setCellStyle(style);

			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				MobileAppConfig model = (MobileAppConfig) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getCodeName());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getPackageName());
				row.createCell((short) 3).setCellValue(model.getFileUrl() == null ? "" : model.getFileUrl());
				row.createCell((short) 4).setCellValue(model.getStatus());
				cell = row.createCell((short) 5);
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
	
	public Map<String, Object> getMobileAppConfigMap(){
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<MobileAppConfig> mobileAppConfigs = getMobileAppConfigCache();
		
		for (MobileAppConfig mobileAppConfig : mobileAppConfigs) {
			map.put(mobileAppConfig.getCodeName(), mobileAppConfig.getDescription());
		}
		
		return map;
	}
	
	public String getMobileAppConfigCacheJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		/*String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);*/
		
		JSONObject jsonObjectResult = new JSONObject();
		
		try {
			List<MobileAppConfig> list = getMobileAppConfigCache();
			
			if(list != null && list.size() > 0){
				JSONArray jsonArray = new JSONArray();
				
				for (MobileAppConfig mobileAppConfig : list) {
					JSONObject jsonObjectData = new JSONObject();
					
					jsonObjectData.put("codeName", mobileAppConfig.getCodeName());
					jsonObjectData.put("description", mobileAppConfig.getDescription());
					
					jsonArray.add(jsonObjectData);
				}
				
				jsonObjectResult.put("result", "1");
				jsonObjectResult.put("data", jsonArray);
			}
		} catch (Exception e) {
			// TODO: handle exception
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", e.getMessage());
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public String clearMobileAppConfigCacheJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		/*String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);*/
		
		JSONObject jsonObjectResult = new JSONObject();
		
		try {
			clearMobileAppConfigCache();
			getMobileAppConfigCache();
			
			jsonObjectResult.put("result", "1");
			jsonObjectResult.put("data", "200");
		} catch (Exception e) {
			// TODO: handle exception
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", e.getMessage());
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
}
