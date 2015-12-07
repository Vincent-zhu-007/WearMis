package com.sg.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sg.dao.CodeDao;
import com.sg.entity.Code;
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileRpcParamenter;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.service.MobileSyncDataService;
import com.sg.util.MemCached;

@Service
public class CodeService implements BaseEntityManager<Code> {
	@Resource
	private CodeDao codeDao;
	
	@Autowired
	private MobileSyncDataService mobileSyncDataService;
	
	@Override
	public void create(Code entity) {
		// TODO Auto-generated method stub
		codeDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Code> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return codeDao.find(params, pageParams);
	}

	@Override
	public Code get(String id) {
		// TODO Auto-generated method stub
		return codeDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Code> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return codeDao.findForUnPage(params);
	}

	@Override
	public void update(Code entity) {
		// TODO Auto-generated method stub
		codeDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return codeDao.getTotalCount(params);
	}
	
	@SuppressWarnings("rawtypes")
	public List<Code> GetCodeCache() {
		List<Code> list = new ArrayList<Code>();
		
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("Code");
			if(obj != null && !obj.equals("")) {
				String json = MemCached.getInstance().get("Code").toString();
				
				JSONArray array = JSONArray.fromObject(json);
			    for(Iterator iter = array.iterator(); iter.hasNext();){
			        JSONObject jsonObject = (JSONObject)iter.next();
			        list.add((Code)JSONObject.toBean(jsonObject, Code.class));
			    }
			}else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("status", "Y");
				list = this.findForUnPage(params);
				
				JSONArray jsonObject = JSONArray.fromObject(list);
				
				MemCached.getInstance().set("Code", jsonObject);
			}
		}
		
		return list;
	}
	
	public List<Map<String, Object>> getCodeCacheByCategory(String category) {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		List<Code> list = GetCodeCache();
		for (Code entity : list) {
			if(entity.getCategory().equals(category)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", entity.getCodeName());
				map.put("text", entity.getDescription());
				maps.add(map);
			}
		}
		return maps;
	}
	
	public List<Map<String, Object>> getDdlCodeCacheByCategory(String category) {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		List<Code> list = GetCodeCache();
		
		Map<String, Object> emptyMap = new HashMap<String, Object>();
		emptyMap.put("id", "");
		emptyMap.put("text", "--请选择--");
		maps.add(emptyMap);
		
		for (Code entity : list) {
			if(entity.getCategory().equals(category)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", entity.getCodeName());
				map.put("text", entity.getDescription());
				maps.add(map);
			}
		}
		return maps;
	}
	
	public Map<String, Object> getCodeCacheMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Code> list = GetCodeCache();
		for (Code entity : list) {
			map.put(entity.getCodeName(), entity.getDescription());
		}
		return map;
	}
	
	public Map<String, Object> getCodeCacheMapByCategory(String category) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Code> list = GetCodeCache();
		for (Code entity : list) {
			if(entity.getCategory().equals(category)) {
				map.put(entity.getCodeName(), entity.getDescription());
			}
		}
		return map;
	}
	
	public String getCodeDescriptionByCategory(String codeName, String category){
		String result = "";
		
		if(codeName != null && !codeName.equals("")){
			Map<String, Object> map = getCodeCacheMapByCategory(category);
			
			if(map != null && map.size() > 0){
				result = map.get(codeName).toString();
			}
		}
		
		return result;
	}
	
	public void clearCodeCache() {
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("Code");
			if(obj != null && !obj.equals("")) {
				MemCached.getInstance().set("Code", "");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<Code> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("用户列表");
			/*第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short*/
			HSSFRow row = sheet.createRow((int) 0);
			/*第四步，创建单元格，并设置值表头 设置表头居中*/
			HSSFCellStyle style = wb.createCellStyle();
			/*左对齐*/
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT); 

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("编码名称");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("描述");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("编码类型");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("创建日期");
			cell.setCellStyle(style);

			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				Code model = (Code) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getCodeName());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getCategory());
				row.createCell((short) 3).setCellValue(model.getStatus());
				cell = row.createCell((short) 4);
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
	
	public List<Code> getCodeCacheListByCategory(String category) {
		List<Code> resultList = new ArrayList<Code>();
		List<Code> list = GetCodeCache();
		for (Code entity : list) {
			if(entity.getCategory().equals(category)) {
				resultList.add(entity);
			}
		}
		return resultList;
	}
	
	public String getCodeJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		try {
			List<Code> list = GetCodeCache();
			
			if(list != null && list.size() > 0){
				JSONArray jsonArray = new JSONArray();
				
				for (Code code : list) {
					JSONObject jsonObjectData = new JSONObject();
					
					jsonObjectData.put("codeName", code.getCodeName());
					jsonObjectData.put("description", code.getDescription());
					jsonObjectData.put("category", code.getCategory());
					
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
	
	public String getCodeByCategoryJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String category = "";
		if(jsonObject.containsKey("category")){
			category = jsonObject.opt("category").toString();
		}
		
		JSONObject jsonObjectResult = new JSONObject();
		
		try {
			if(category != null && !category.equals("")){
				List<Code> list = getCodeCacheListByCategory(category);
				
				if(list != null && list.size() > 0){
					JSONArray jsonArray = new JSONArray();
					
					for (Code code : list) {
						JSONObject jsonObjectData = new JSONObject();
						
						jsonObjectData.put("codeName", code.getCodeName());
						jsonObjectData.put("description", code.getDescription());
						
						jsonArray.add(jsonObjectData);
					}
					
					jsonObjectResult.put("result", "1");
					jsonObjectResult.put("data", jsonArray);
				}
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "缺少必要参数");
			}
		} catch (Exception e) {
			// TODO: handle exception
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", e.getMessage());
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public void runXcapCmdClearCache(List<MobileUser> mobileUsers, MobileCompany mobileCompany){
		/*runXcapCmd*/
		if(mobileUsers != null && mobileUsers.size() > 0){
			List<MobileRpcParamenter> mobileRpcParamenters = new ArrayList<MobileRpcParamenter>();
			
			int i = 1;
			
			for (MobileUser mobileUser : mobileUsers) {
				i += 1;
				
				String ownerUri = mobileUser.getOwnerUri();
				
				MobileRpcParamenter mobileRpcParamenter1 = new MobileRpcParamenter();
				mobileRpcParamenter1.setParaName("pocRpcParamenter" + String.valueOf(i));
				mobileRpcParamenter1.setParaType("String");
				mobileRpcParamenter1.setParaValue("Clear_Cache");
				mobileRpcParamenter1.setParaSort(i);
				mobileRpcParamenters.add(mobileRpcParamenter1);
				
				MobileRpcParamenter mobileRpcParamenter2 = new MobileRpcParamenter();
				mobileRpcParamenter2.setParaName("pocRpcParamenter" + String.valueOf(i + 1));
				mobileRpcParamenter2.setParaType("String");
				mobileRpcParamenter2.setParaValue(ownerUri);
				mobileRpcParamenter2.setParaSort(i + 1);
				mobileRpcParamenters.add(mobileRpcParamenter2);
			}
			
			mobileSyncDataService.SyncData("runXcapCmd", mobileRpcParamenters, mobileCompany);
		}
	}
}
